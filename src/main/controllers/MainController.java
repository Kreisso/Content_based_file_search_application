package main.controllers;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.web.HTMLEditor;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import main.boundary.FileScanner;
import main.boundary.JsonService;
import main.boundary.PathFinder;
import main.entity.DirectoryTree;
import org.controlsfx.control.CheckListView;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainController {

    private static final int THREADS = 5;
    public static final String PATH_TO_HISTORY = "../../resources/json/history.json";
    public TreeView<String> filesTree;
    public CheckListView<String> typeFiles;
    public AreaChart areaChart;
    public Button searchButton, chooseDirectoryButton, saveButton;
    public TextField key, pathTextField;
    public HTMLEditor fileContent;
    private ArrayList<XYChart.Series<String, Number>> seriesContainer = new ArrayList<>();
    private Multimap<String, String> multimap = ArrayListMultimap.create();
    private String pathFile;
    private InputStream inputStream;

    public void initialize() {
        loadTreeItems();
        loadCheckListItems("TXT", "RTF", "DOC", "DOCX", "ODT", "CSS", "HTML", "HTM", "XML", "WPS", "PAGES");
        loadJson();
        loadChart();
        setSearchButton();
        setSaveButton();
        addDirectoryChooser();
    }

    private void loadCheckListItems(String... checkItems) {
        ObservableList<String> strings = FXCollections.observableArrayList();
        strings.addAll(checkItems);
        typeFiles.setItems(strings);
    }

    private void loadFileContentToTextArea(String filePath) {
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileContent.setHtmlText(content);
    }

    private void setSearchButton() {
        searchButton.setOnAction(new EventHandler<ActionEvent>() {
           @Override public void handle(ActionEvent e) {
               loadTreeItems();
               fileContent.getHtmlText()
                       .replace("<html><head></head><body>", "")
                       .replace("</body></html>", "")
                       .isEmpty();
               System.out.println("sample: " + key.getText());
               addSampleToHistory(key.getText());
               List<String> checkedBoxes = getCheckedBoxes();
               if (!checkedBoxes.isEmpty()) {
                   getFiles(key.getText(), checkedBoxes);
               }
           }
        });
    }

    private void addSampleToHistory(String key) {
        JsonService.addWord(key);
        loadChart();
        saveHistory();

    }

    private void saveHistory() {
        saveJson();
    }

    private void addDirectoryChooser() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        chooseDirectoryButton.setOnAction(e -> {
            File selectedDirectory = directoryChooser.showDialog(Stage.getWindows().filtered(window -> window.isShowing()).get(0));
            pathTextField.setText(selectedDirectory.getAbsolutePath());

        });
    }

    private void setSaveButton() {
        saveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (pathFile.equals("")) {
                    return;
                }
                String stringHtml = fileContent.getHtmlText();
                saveFile(stringHtml);
                System.out.println("plik : " + pathFile + " zapisany");
            }
        });
    }

    private void saveFile(String content) {
        File file = new File(pathFile);
        try {
            FileWriter fileWriter = null;

            fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    private List<String> getCheckedBoxes() {
        return typeFiles.getCheckModel().getCheckedItems();
    }

    private void loadChart() {
        areaChart.setTitle("Search words\n");
        areaChart.getYAxis().setLabel("Number of use");
        areaChart.getXAxis().setLabel("Word");

        XYChart.Series historyChart = new XYChart.Series();
        historyChart.setName("History");

        Map<String, Long> wordsToCount = JsonService.getWordsToCount();

        wordsToCount.entrySet().stream()
                .forEach(object -> historyChart.getData().add(new XYChart.Data(object.getKey(), object.getValue())));

        seriesContainer.add(historyChart);

        for (XYChart.Series<String, Number> numberNumberSeries : seriesContainer) {
            try {
                areaChart.getData().add(numberNumberSeries);
            } catch (IllegalArgumentException e) {
                System.out.println("Refresh error:" + e.getMessage());
            }
        }
    }

    private void loadJson() {
        inputStream = getClass().getResourceAsStream(PATH_TO_HISTORY);
        JSONParser jsonParser = new JSONParser();
        try {
            JSONArray jsonArray = (JSONArray) jsonParser.parse(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            JsonService.setWordsToCount(jsonArray);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void saveJson() {
        try {
            FileWriter writer = new FileWriter(
                    new File(this.getClass().getResource(PATH_TO_HISTORY).getPath()));
            writer.write(JsonService.prepareJsonToSave().toJSONString());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTreeItems(String... rootItems) {
        pathFile = "";
        TreeItem<String> root = new TreeItem<String>("Results");
        root.setExpanded(true);
        for (String itemString : rootItems) {
            root.getChildren().add(new TreeItem<String>(itemString));
        }

        filesTree.setRoot(root);
        //noinspection unchecked
        filesTree.getSelectionModel().selectedItemProperty().addListener((ChangeListener) (observable, oldValue, newValue) -> {
            StringBuilder pathBuilder = new StringBuilder();
            TreeItem<String> selectedItem = (TreeItem<String>) newValue;
            for (TreeItem<String> item = selectedItem;
                 item != null; item = item.getParent()) {

                pathBuilder.insert(0, item.getValue());
                pathBuilder.insert(0, "\\");
            }
            pathBuilder.deleteCharAt(0);

            pathFile = pathBuilder.toString().replace("\\", "/");
            System.out.println(pathFile);

            loadFileContentToTextArea(pathFile);
        });
    }

    private void getFiles(String sample, List<String> typeFiles)
    {

        String path = System.getProperty("user.home");
        if(!pathTextField.getText().isEmpty()) {
            path = pathTextField.getText();
        }
        File file = new File(path);

        BlockingQueue<File> fileQueue = new ArrayBlockingQueue<File>(50);

        new Thread(new PathFinder(fileQueue, file, typeFiles)).start();

        ExecutorService executorService = Executors.newFixedThreadPool(THREADS);
        for (int i = 0; i < THREADS; i++)
            executorService.execute(new FileScanner(fileQueue, sample, multimap, typeFiles));//;  new Thread().start();

        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
            DirectoryTree.createNewTree(path, getCheckedBoxes(), multimap);
            filesTree.setRoot(DirectoryTree.getTreeItem());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
