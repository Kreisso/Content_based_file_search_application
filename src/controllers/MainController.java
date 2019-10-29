package controllers;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.controlsfx.control.CheckListView;
import sample.FileScanner;
import sample.PathFinder;
import sample.entity.DirectoryTree;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
public class MainController {

    private static final int THREADS = 5;
    public TreeView<String> filesTree;
    public CheckListView<String> typeFiles;
    public AreaChart areaChart;
    public Button searchButton, chooseDirectoryButton;
    public TextField key,pathTextField;
    public TextArea fileContent;
    ArrayList<XYChart.Series<Number, Number>> seriesContainer = new ArrayList();
    private File file;
    private Multimap<String,String> multimap =  ArrayListMultimap.create();

    public void initialize() {
        loadTreeItems("initial 1", "initial 2", "initial 3");
        loadCheckListItems("TXT", "RTF", "DOC/DOCX", "ODT", "CSS", "HTML", "HTM", "XML", "WPS");
        loadChart();
        setSearchButton();
        addDirectoryChooser();
    }

    public void loadCheckListItems(String... checkItems) {
        ObservableList<String> strings = FXCollections.observableArrayList();
        strings.addAll(checkItems);
        typeFiles.setItems(strings);
    }

    public void loadFileContentToTextArea(String filePath) {
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileContent.setText(content);
    }
    public void setSearchButton() {
        searchButton.setOnAction(new EventHandler<ActionEvent>() {
           @Override public void handle(ActionEvent e) {
               // TODO leci nullpointer gdy nic nie wbiore
               System.out.println("sample: "+ key.getText());
               List<String> checkedBoxes = getCheckedBoxes();
               if(!checkedBoxes.isEmpty()) {
                   getFiles(key.getText(), checkedBoxes);
               }
           }
       });
    }

    private void addDirectoryChooser() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        chooseDirectoryButton.setOnAction(e -> {
            File selectedDirectory = directoryChooser.showDialog(Stage.getWindows().filtered(window -> window.isShowing()).get(0));
            pathTextField.setText(selectedDirectory.getAbsolutePath());

        });
    }

    private List<String> getCheckedBoxes() {
        return typeFiles.getCheckModel().getCheckedItems();
    }

    public void loadChart() {
        areaChart.setTitle("Search word\n");
        areaChart.getYAxis().setLabel("number of occurrences");
        areaChart.getXAxis().setLabel("File name");

        XYChart.Series seriesTest1= new XYChart.Series();
        seriesTest1.setName("Test 1");
        seriesTest1.getData().add(new XYChart.Data(1, 4));
        seriesTest1.getData().add(new XYChart.Data(3, 10));
        seriesTest1.getData().add(new XYChart.Data(6, 15));
        seriesTest1.getData().add(new XYChart.Data(9, 8));
        seriesTest1.getData().add(new XYChart.Data(12, 5));
        seriesTest1.getData().add(new XYChart.Data(15, 18));
        seriesTest1.getData().add(new XYChart.Data(18, 15));
        seriesTest1.getData().add(new XYChart.Data(21, 13));
        seriesTest1.getData().add(new XYChart.Data(24, 19));
        seriesTest1.getData().add(new XYChart.Data(27, 21));
        seriesTest1.getData().add(new XYChart.Data(30, 21));

        XYChart.Series seriesTest2 = new XYChart.Series();
        seriesTest2.setName("Test 2");
        seriesTest2.getData().add(new XYChart.Data(1, 20));
        seriesTest2.getData().add(new XYChart.Data(3, 15));
        seriesTest2.getData().add(new XYChart.Data(6, 13));
        seriesTest2.getData().add(new XYChart.Data(9, 12));
        seriesTest2.getData().add(new XYChart.Data(12, 14));
        seriesTest2.getData().add(new XYChart.Data(15, 18));
        seriesTest2.getData().add(new XYChart.Data(18, 25));
        seriesTest2.getData().add(new XYChart.Data(21, 25));
        seriesTest2.getData().add(new XYChart.Data(24, 23));
        seriesTest2.getData().add(new XYChart.Data(27, 26));
        seriesTest2.getData().add(new XYChart.Data(31, 26));

        seriesContainer.add(seriesTest2);
        seriesContainer.add(seriesTest1);

         for (XYChart.Series<Number, Number> numberNumberSeries : seriesContainer) {
            areaChart.getData().add(numberNumberSeries);
         }
    }

    public void loadTreeItems(String... rootItems) {
        TreeItem<String> root = new TreeItem<String>("Root Node");
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
            String path = pathBuilder.toString();
            System.out.println(path);

            loadFileContentToTextArea(path);
            // do what ever you want
        });
    }

    private void getFiles(String sample, List<String> typeFiles)
    {

        String path = System.getProperty("user.home");
        if(!pathTextField.getText().isEmpty()) {
            path = pathTextField.getText();
        }
        DirectoryTree.createNewTree(path, getCheckedBoxes());
        file = new File(path);

        BlockingQueue<File> fileQueue = new ArrayBlockingQueue<File>(50);

        new Thread(new PathFinder(fileQueue, file, typeFiles)).start();

        ExecutorService executorService = Executors.newFixedThreadPool(THREADS);
        for (int i = 0; i < THREADS; i++)
            executorService.execute(new FileScanner(fileQueue, sample, multimap, typeFiles));//;  new Thread().start();

        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
            filesTree.setRoot(DirectoryTree.getTreeItem());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}