package newGui.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import newGui.pages.primary.mainClientAppController;


import java.net.URL;

import static util.Constants.*;

// login page for an S-Emulator client
public class SEmulatorClient extends Application {

    private mainClientAppController mainClientController;


    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(600);
        primaryStage.setTitle("S-Emulator – Login");

        URL loginPage = getClass().getResource(MAIN_PAGE_FXML_RESOURCE_LOCATION);
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(loginPage);
            Parent root = loader.load();
            mainClientController = loader.getController();

            Scene scene = new Scene(root);
            loadStyles(scene);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadStyles(Scene scene){

        var stylesUrl = getClass().getResource(MAIN_PAGE_STYLE_RESOURCE_LOCATION);
        // Add the stylesheet to the scene if found`
        if (stylesUrl != null) {
            scene.getStylesheets().add(stylesUrl.toExternalForm());
        } else {
            System.out.println("Unable to locate " + MAIN_PAGE_STYLE_RESOURCE_LOCATION);
        }
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
