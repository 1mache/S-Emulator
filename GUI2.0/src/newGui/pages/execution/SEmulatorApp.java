package newGui.pages.execution;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SEmulatorApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Path of the main dashboard layout FXML file
        final String LAYOUT_FXML_PATH = "/newGui/pages/dashboard/component/primary/dashboard.fxml";

//        // Create a new engine to be used in the new GUI (in this exercise the engine in the server)
//        SLanguageEngine engine = SLanguageEngine.getInstance();

        var fxmlUrl = getClass().getResource(LAYOUT_FXML_PATH);
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(fxmlUrl);
        Parent root = loader.load();

//        PrimaryController controller = loader.getController();
//        controller.setEngine(engine);

        Scene scene = new Scene(root);
        loadStyles(scene);
        primaryStage.setScene(scene);
        primaryStage.setTitle("S-Emulator – Dashboard");
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    private void loadStyles(Scene scene){
        final String STYLES_CSS_PATH = "/newGui/pages/dashboard/styles.css";

        var stylesUrl = getClass().getResource(STYLES_CSS_PATH);
        // Add the stylesheet to the scene if found`
        if (stylesUrl != null) {
            scene.getStylesheets().add(stylesUrl.toExternalForm());
        } else {
            System.out.println("Unable to locate " + STYLES_CSS_PATH);
        }
    }
}
