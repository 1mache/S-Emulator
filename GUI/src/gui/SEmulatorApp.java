package gui;

import engine.api.SLanguageEngine;
import gui.component.primary.PrimaryController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class SEmulatorApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        final String LAYOUT_FXML = "component/primary/app_layout.fxml";
        SLanguageEngine engine = SLanguageEngine.getInstance();

        var fxmlUrl = getClass().getResource(LAYOUT_FXML);
        if (fxmlUrl == null) {
            System.out.println("Unable to locate " + LAYOUT_FXML);
            Platform.exit();
            return;
        }

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(fxmlUrl);

        BorderPane root = loader.load();

        PrimaryController controller = loader.getController();
        controller.setEngine(engine);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("SEmulator");
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
