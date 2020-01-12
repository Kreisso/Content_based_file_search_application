package main.boundary;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AlertBox {

    public static void displayInfoAlert(String title, String header, String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);

        alert.showAndWait();
    }

    public static void displayFoundWordAlert(String title, String header, List<String> messages) {
        messages = messages.stream().map(message -> message = message + "\n").collect(Collectors.toList());
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        VBox dialogPaneContent = new VBox();
        Label label = new Label(header);
        TextArea textArea = new TextArea();
        textArea.setText(messages.toString());
        dialogPaneContent.getChildren().addAll(label, textArea);
        alert.getDialogPane().setContent(dialogPaneContent);

        alert.showAndWait();
    }

    public static void displayErrorAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }

    public static boolean displayConfirmAlert(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.get() == ButtonType.OK;
    }
}