package Alerts;

import javafx.scene.control.Alert;

public class Alerts {

    public static Runnable loadField(String ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Load Error");
        alert.setHeaderText(null);
        alert.setContentText("Load Error: " + ex);
        alert.showAndWait();
        return null;
    }

    public static void loadSucceeded() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Load Completed Successfully");
        alert.setHeaderText(null);
        alert.setContentText("Load Completed Successfully");
        alert.showAndWait();
    }

    public static Runnable invalidInput() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Invalid Input");
        alert.setHeaderText(null);
        alert.setContentText("plaese enter valid input (integer number)");
        alert.showAndWait();
        return null;
    }

    public static void creditLoadSucceeded() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Credit Load Completed Successfully");
        alert.setHeaderText(null);
        alert.setContentText("Credit Load Completed Successfully");
        alert.showAndWait();
    }

    public static Runnable dtoTranslate(String ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Server Error");
        alert.setHeaderText(null);
        alert.setContentText("DTO isn't valid : " + ex);
        alert.showAndWait();
        return null;
    }

    // On Failure Alerts
    public static Runnable serverProblamResponse(String ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Server Error");
        alert.setHeaderText(null);
        alert.setContentText("Server Bad Response : " + ex);
        alert.showAndWait();
        return null;
    }

    // On response bad answer Alerts
    public static Runnable serverBadAnswer(String ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Server Error");
        alert.setHeaderText(null);
        alert.setContentText("Server Bad Response : " + ex);
        alert.showAndWait();
        return null;
    }


    public static Runnable badBody(String ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Server Error");
        alert.setHeaderText(null);
        alert.setContentText("Server Missing Body : " + ex);
        alert.showAndWait();
        return null;
    }

}
