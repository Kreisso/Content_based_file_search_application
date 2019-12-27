package main.boundary;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import main.controllers.JsonService;
import main.controllers.MainController;

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
    private final MainController mainController;

    private final JsonService jsonService = new JsonService();

    public ButtonProvider(MainController mainController) {
        this.mainController = mainController;
    }

    public void setSearchButton() {
        mainController.searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                mainController.loadTreeItems();
                mainController.fileContent.getHtmlText()
                        .replace("<html><head></head><body>", "")
                        .replace("</body></html>", "")
                        .isEmpty();
                System.out.println("sample: " + mainController.key.getText());
                addSampleToHistory(mainController.key.getText());
                List<String> checkedBoxes = mainController.getCheckedBoxes();
                if (!checkedBoxes.isEmpty()) {
                    FileUtils.getFiles(mainController);
                } else {
                    AlertBox.displayErrorAlert(NO_FILE_EXTENSION_SELECTED_ERROR_MESSAGE, SOLLUTION);
                }
            }
        });
    }

    public void setMenuItem() {
        mainController.deleteHistory.setOnAction(e -> {
            mainController.areaChart.getData().clear();
            JsonService.clearHistory();
            saveHistory();
        });

        mainController.about.setOnAction(e -> {
            AlertBox.display("About", APPLICATION_NAME, MESSAGE_IN_ABOUT);
        });
    }

    public void setSaveButton() {
        mainController.saveButton.setOnAction(e -> {
            if (mainController.getPathFile().equals("")) {
                return;
            }
            String stringHtml = mainController.fileContent.getHtmlText();
            try {
                saveFile(stringHtml, mainController.getPathFile());
            } catch (IOException ex) {
                Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
            System.out.println("plik : " + mainController.getPathFile() + " zapisany");
            AlertBox.display("Save File", "File saved correctly: \n" + mainController.getPathFile(), "");
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
        mainController.loadChart();
        saveHistory();

    }

}