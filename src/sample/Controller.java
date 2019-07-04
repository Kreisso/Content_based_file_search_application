package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.controlsfx.control.CheckListView;

import java.util.ArrayList;

public class Controller {

    public TreeView<String> filesTree;
    public CheckListView<String> typeFiles;
    public AreaChart areaChart;
    ArrayList<XYChart.Series<Number, Number>> seriesContainer = new ArrayList();//Use an ArrayList to hold new Series!


    public void initialize() {
        loadTreeItems("initial 1", "initial 2", "initial 3");
        loadCheckListItems("TXT", "RTF", "DOC/DOCX", "ODT", "CSS", "HTML", "HTM", "XML", "WPS");
        loadChart();
    }

    public void loadCheckListItems(String... checkItems) {
        ObservableList<String> strings = FXCollections.observableArrayList();
        strings.addAll(checkItems);
        typeFiles.setItems(strings);


    }

    public void loadChart() {
        areaChart.setTitle("Search word\n");
        areaChart.getYAxis().setLabel("number of occurrences");
        areaChart.getXAxis().setLabel("File name");
//
//        XYChart.Series<Number, Number> series = new XYChart.Series<>();
//        double test = 33;
//
//        for (int i = 10; i <= 120; i++) {
//           // series.getData().add(new XYChart.Data(i, test));
//        }
        ////
        XYChart.Series seriesApril= new XYChart.Series();
        seriesApril.setName("April");
        seriesApril.getData().add(new XYChart.Data(1, 4));
        seriesApril.getData().add(new XYChart.Data(3, 10));
        seriesApril.getData().add(new XYChart.Data(6, 15));
        seriesApril.getData().add(new XYChart.Data(9, 8));
        seriesApril.getData().add(new XYChart.Data(12, 5));
        seriesApril.getData().add(new XYChart.Data(15, 18));
        seriesApril.getData().add(new XYChart.Data(18, 15));
        seriesApril.getData().add(new XYChart.Data(21, 13));
        seriesApril.getData().add(new XYChart.Data(24, 19));
        seriesApril.getData().add(new XYChart.Data(27, 21));
        seriesApril.getData().add(new XYChart.Data(30, 21));

        XYChart.Series seriesMay = new XYChart.Series();
        seriesMay.setName("May");
        seriesMay.getData().add(new XYChart.Data(1, 20));
        seriesMay.getData().add(new XYChart.Data(3, 15));
        seriesMay.getData().add(new XYChart.Data(6, 13));
        seriesMay.getData().add(new XYChart.Data(9, 12));
        seriesMay.getData().add(new XYChart.Data(12, 14));
        seriesMay.getData().add(new XYChart.Data(15, 18));
        seriesMay.getData().add(new XYChart.Data(18, 25));
        seriesMay.getData().add(new XYChart.Data(21, 25));
        seriesMay.getData().add(new XYChart.Data(24, 23));
        seriesMay.getData().add(new XYChart.Data(27, 26));
        seriesMay.getData().add(new XYChart.Data(31, 26));

        seriesContainer.add(seriesMay);
        seriesContainer.add(seriesApril);


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
    }
}
