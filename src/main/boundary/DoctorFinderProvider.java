package main.boundary;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.web.HTMLEditor;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import main.controllers.JsonService;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.controlsfx.control.CheckListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DoctorFinderProvider {

    private final ButtonProvider buttonProvider = new ButtonProvider(this);
    private JsonService jsonService = new JsonService();
    public TreeView<String> filesTree;
    public CheckListView<String> typeFiles;
    public AreaChart areaChart;
    public Button searchButton, chooseDirectoryButton, saveButton;
    public TextField key, pathTextField;
    public HTMLEditor fileContent;
    public MenuItem deleteHistory, about;
    private ArrayList<XYChart.Series<String, Number>> seriesContainer = new ArrayList<>();

    private String pathFile;

    public void initialize() {
        jsonService.loadJson();
        loadTreeItems();
        loadCheckListItems("TXT", "RTF", "DOC", "DOCX", "ODT", "CSS", "HTML", "HTM", "XML", "WPS", "PAGES");
        loadChart();
        buttonProvider.setSearchButton();
        buttonProvider.setSaveButton();
        buttonProvider.setMenuItem();
        addDirectoryChooser();
    }

    public String getPathFile() {
        return pathFile;
    }

    private void loadCheckListItems(String... checkItems) {
        ObservableList<String> strings = FXCollections.observableArrayList();
        strings.addAll(checkItems);
        typeFiles.setItems(strings);
    }

    private void loadFileContentToTextArea(String filePath) {

        StringBuilder content = new StringBuilder();
        Path path = Paths.get(filePath);
        Optional<String> optionalFileType = FileUtils.getExtensionByStringHandling(filePath);
        if (optionalFileType.isEmpty()) {
            return;
        }
        String fileType = optionalFileType.get();

        if ("docx".equals(fileType) || "doc".equals(fileType)) {
            try {
                FileInputStream fis = new FileInputStream(pathFile);
                XWPFDocument docx = new XWPFDocument(fis);

                XWPFWordExtractor we = new XWPFWordExtractor(docx);
                content.append(we.getText());

            } catch (FileNotFoundException ignored) {

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                content = new StringBuilder(new String(Files.readAllBytes(path)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        fileContent.setHtmlText(content.toString());
    }

    private void addDirectoryChooser() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        chooseDirectoryButton.setOnAction(e -> {
            File selectedDirectory = directoryChooser.showDialog(Stage.getWindows().filtered(Window::isShowing).get(0));
            pathTextField.setText(selectedDirectory.getAbsolutePath());

        });
    }

    public List<String> getCheckedBoxes() {
        return typeFiles.getCheckModel().getCheckedItems();
    }


    public void loadChart() {
        areaChart.setTitle("Search words\n");
        areaChart.getYAxis().setLabel("Number of use");
        areaChart.getXAxis().setLabel("Word");

        XYChart.Series historyChart = new XYChart.Series();
        historyChart.setName("History");

        Map<String, Long> wordsToCount = JsonService.getWordsToCount();
        List<XYChart.Data> chartData = wordsToCount.entrySet().stream()
                .map(wordToCount -> new XYChart.Data(wordToCount.getKey(), wordToCount.getValue()))
                .collect(Collectors.toList());

        historyChart.getData().addAll(chartData);
        seriesContainer.add(historyChart);


        for (XYChart.Series<String, Number> numberNumberSeries : seriesContainer) {
            try {
                //noinspection unchecked
                areaChart.getData().add(numberNumberSeries);
            } catch (IllegalArgumentException e) {
                System.out.println("Refresh error:" + e.getMessage());
            }
        }
    }

    public void loadTreeItems(String... rootItems) {
        pathFile = "";
        filesTree.refresh();
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

    public String getKey() {
        return key.getText();
    }
}
