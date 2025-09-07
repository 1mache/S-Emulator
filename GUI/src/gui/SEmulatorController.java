package gui;

import engine.api.SLanguageEngine;
import engine.jaxb.loader.exception.NotXMLException;
import engine.jaxb.loader.exception.UnknownLabelException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class SEmulatorController implements Initializable {

    private Image catImage;
    private Image monkeImage;

    private Media click;
    private Media success;
    private Media clickReverb;

    private SLanguageEngine engine;

    @FXML
    private ImageView goofyImageView;

    @FXML
    private Label filenameLabel;

    @FXML
    private Button openFileButton;

    @FXML
    void openFileButtonActionListener(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open S Language File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));

        //get the stage from one of the components
        Stage stage = (Stage) openFileButton.getScene().getWindow();

        new MediaPlayer(click).play();
        File fileChosen = fileChooser.showOpenDialog(stage);
        if(fileChosen == null) return;

        try {
            engine.loadProgram(fileChosen.getAbsolutePath());
            filenameLabel.setText("Loaded file: " + fileChosen.getName());
            filenameLabel.setStyle(filenameLabel.getStylesheets().toString());
            showCatImage();
            new MediaPlayer(success).play();
        } catch (NotXMLException e) {
            showError("Error. File not an XML");
        } catch (FileNotFoundException e) {
            showError("Error. File not found");
        } catch (UnknownLabelException e) {
            showError(e.getMessage());
        }
    }

    public void setEngine(SLanguageEngine engine) {
        this.engine = engine;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        goofyImageView.setOpacity(0.0);

        final String CAT_IMAGE_PATH = "/images/thmbs_up.jpg";
        final String MONKE_IMAGE_PATH = "/images/monke.jpeg";

        var catUrl = getClass().getResource(CAT_IMAGE_PATH);
        var monkeUrl = getClass().getResource(MONKE_IMAGE_PATH);
        var clickUrl = getClass().getResource("/sounds/perfect-fart.mp3");
        var clickReverbUrl = getClass().getResource("/sounds/fart-with-reverb.mp3");
        var successSoundUrl = getClass().getResource("/sounds/brain-fart.mp3");
        if(catUrl == null && monkeUrl == null){
            failedToLoadResource("images", CAT_IMAGE_PATH, MONKE_IMAGE_PATH);
            return;
        }
        else if(catUrl == null){
            failedToLoadResource("image", CAT_IMAGE_PATH);
            return;
        }
        else if(monkeUrl == null){
            failedToLoadResource("image", MONKE_IMAGE_PATH);
            return;
        }

        catImage = new Image(catUrl.toExternalForm());
        monkeImage = new Image(monkeUrl.toExternalForm());

        click = new Media(clickUrl.toExternalForm());
        clickReverb = new Media(clickReverbUrl.toExternalForm());
        success = new Media(successSoundUrl.toExternalForm());
    }

    private void failedToLoadResource(String resourceKind, String... resourceNames) {
        System.out.print("Error: Could not load " + resourceKind + " ");
        Arrays.stream(resourceNames)
                .forEach(s ->  System.out.print(s + " "));
        System.out.println();
        Platform.exit();
    }

    private void showCatImage(){
        goofyImageView.setImage(catImage);
        goofyImageView.setOpacity(1.0);
    }

    private void showMonkeImage(){
        goofyImageView.setImage(monkeImage);
        goofyImageView.setOpacity(1.0);
    }

    private void showError(String s) {
        filenameLabel.setText(s);
        filenameLabel.setStyle("-fx-text-fill: red");
        new MediaPlayer(clickReverb).play();
        showMonkeImage();
    }
}
