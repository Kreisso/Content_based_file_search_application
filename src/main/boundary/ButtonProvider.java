package main.boundary;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import main.controllers.JsonService;
import main.entity.FindeFile;
import main.entity.LCS;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ButtonProvider {
    public static final String NO_FILE_EXTENSION_SELECTED_ERROR_MESSAGE = "No file extension selected";
    public static final String APPLICATION_NAME = "Content based file search application";
    public static final String SOLLUTION = "Select the one or more extension from the right side bar";
    public static final String SAVE_TITLE = "Save File";
    public static final String SAVE_HEADER = "Do you want save this file?";
    public static final String SAVE_MESSAGE = "Content will by override";
    private final String MESSAGE_IN_ABOUT = "Proudly developed by Maciej Polak \n" +
            "Engineering work: \n" +
            "APLIKACJA DO WYSZUKIWANIA PLIKÓW PO ZAWARTOŚCI \n" +
            "CONTENT BASED FILE SEARCH APPLICATION \n\n" +
            "Doctor Finder 1.0";
    private final DoctorFinderProvider doctorFinderProvider;

    private final JsonService jsonService = new JsonService();

    public ButtonProvider(DoctorFinderProvider doctorFinderProvider) {
        this.doctorFinderProvider = doctorFinderProvider;
    }

    public void setSearchButton() {
        doctorFinderProvider.searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                FindeFile.findWordLine = new LinkedList<String>();
                doctorFinderProvider.loadTreeItems();
                doctorFinderProvider.fileContent.getHtmlText()
                        .replace("<html><head></head><body>", "")
                        .replace("</body></html>", "")
                        .isEmpty();
                System.out.println("sample: " + doctorFinderProvider.key.getText());
                addSampleToHistory(doctorFinderProvider.key.getText());
                List<String> checkedBoxes = doctorFinderProvider.getCheckedBoxes();
                if (!checkedBoxes.isEmpty()) {
                    FileUtils.getFiles(doctorFinderProvider);
                } else {
                    AlertBox.displayErrorAlert(NO_FILE_EXTENSION_SELECTED_ERROR_MESSAGE, SOLLUTION);
                }
            }
        });
    }

    public void setMenuItem() {
        doctorFinderProvider.deleteHistory.setOnAction(e -> {
            doctorFinderProvider.areaChart.getData().clear();
            JsonService.clearHistory();
            saveHistory();
            AlertBox.display("Delete history", "History correctly removed", "");

        });

        doctorFinderProvider.about.setOnAction(e -> {
            AlertBox.display("About", APPLICATION_NAME, MESSAGE_IN_ABOUT);
        });
    }

    public void setSaveButton() {
        doctorFinderProvider.saveButton.setOnAction(e -> {
            boolean isConfirmed = AlertBox.displayConfirmAlert(SAVE_TITLE, SAVE_HEADER, SAVE_MESSAGE);
            if (!isConfirmed) {
                return;
            }
            if (doctorFinderProvider.getPathFile().equals("")) {
                return;
            }
            String stringHtml = doctorFinderProvider.fileContent.getHtmlText();
            try {
                saveFile(stringHtml, doctorFinderProvider.getPathFile());
            } catch (IOException ex) {
                Logger.getLogger(DoctorFinderProvider.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
            System.out.println("plik : " + doctorFinderProvider.getPathFile() + " zapisany");
            AlertBox.display("Save File", "File saved correctly", doctorFinderProvider.getPathFile());
        });
    }

    private void saveFile(String content, String pathFile) throws IOException {
        File file = new File(pathFile);

        FileWriter fileWriter = null;

        fileWriter = new FileWriter(file);
        fileWriter.write(content);
        fileWriter.close();
    }

    private void saveHistory() {
        jsonService.saveJson();
    }

    public void setSearchPrecision() {
        doctorFinderProvider.percentPrecision.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                LCS.MATCHING_PERCENT = new_val.doubleValue() / 100;
                System.out.println(LCS.MATCHING_PERCENT);
            }
        });
    }


    private void addSampleToHistory(String key) {
        JsonService.addWord(key);
        doctorFinderProvider.loadChart();
        saveHistory();

    }

}