package main.boundary;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import main.controllers.JsonService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ButtonProvider {
    public static final String NO_FILE_EXTENSION_SELECTED_ERROR_MESSAGE = "No file extension selected";
    public static final String APPLICATION_NAME = "Content based file search application";
    public static final String SOLLUTION = "Select the extension from the right side bar";
    private final String MESSAGE_IN_ABOUT = "Maciej Polak \n" +
            "Praca inżynierska";
    private final DoctorFinderProvider doctorFinderProvider;

    private final JsonService jsonService = new JsonService();

    public ButtonProvider(DoctorFinderProvider doctorFinderProvider) {
        this.doctorFinderProvider = doctorFinderProvider;
    }

    public void setSearchButton() {
        doctorFinderProvider.searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
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
        });

        doctorFinderProvider.about.setOnAction(e -> {
            AlertBox.display("About", APPLICATION_NAME, MESSAGE_IN_ABOUT);
        });
    }

    public void setSaveButton() {
        doctorFinderProvider.saveButton.setOnAction(e -> {
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
            AlertBox.display("Save File", "File saved correctly: \n" + doctorFinderProvider.getPathFile(), "");
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


    private void addSampleToHistory(String key) {
        JsonService.addWord(key);
        doctorFinderProvider.loadChart();
        saveHistory();

    }

}