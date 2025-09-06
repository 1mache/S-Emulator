package gui;

import engine.api.SLanguageEngine;
import engine.jaxb.loader.exception.NotXMLException;
import engine.jaxb.loader.exception.UnknownLabelException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

public class SEmulatorController implements Initializable {
    private final String CAT_IMAGE_PATH = "file:/C:/Users/Dmytro/Akademit/Java/S-Emulator/GUI/resources/thmbs_up.jpg";
    private final String MONKE_IMAGE_PATH = "file:/C:/Users/Dmytro/Akademit/Java/S-Emulator/GUI/resources/monke.jpeg";


    private Stage stage;
    private SLanguageEngine engine;

    @FXML
    private ImageView catImage;

    @FXML
    private Label filenameLabel;

    @FXML
    private Button openFileButton;

    @FXML
    void openFileButtonActionListener(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open S Language File");

        File fileChosen = fileChooser.showOpenDialog(stage);
        try {
            engine.loadProgram(fileChosen.getAbsolutePath());
            filenameLabel.setText("Loaded file: " + fileChosen.getName());
            showCatImage();
        } catch (NotXMLException e) {
            filenameLabel.setText("Error. File not an XML");
            showMonkeImage();
        } catch (FileNotFoundException e) {
            filenameLabel.setText("Error. File not found");
            showMonkeImage();
        } catch (UnknownLabelException e) {
            filenameLabel.setText("Error." + e.getMessage());
            showMonkeImage();
        }
    }

    public void setEngine(SLanguageEngine engine) {
        this.engine = engine;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        catImage.setOpacity(0.0);
    }

    private void showCatImage(){
        catImage.setImage(new Image(CAT_IMAGE_PATH));
        catImage.setOpacity(1.0);
    }
    private void showMonkeImage(){
        catImage.setImage(new Image(MONKE_IMAGE_PATH));
        catImage.setOpacity(1.0);
    }
}
