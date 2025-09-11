package gui.components.primary;

import engine.api.SLanguageEngine;
import engine.api.dto.InstructionPeek;
import engine.execution.exception.SProgramNotLoadedException;
import engine.jaxb.loader.exception.NotXMLException;
import engine.jaxb.loader.exception.UnknownLabelException;
import gui.components.instruction.table.InstructionTableController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
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

public class PrimaryController implements Initializable {
    private final boolean DEBUG = true;

    private Image catImage;
    private Image monkeImage;

    private SLanguageEngine engine;

    @FXML
    private ImageView goofyImageView;

    @FXML
    private Label filenameLabel;

    @FXML
    private Button openFileButton;

    @FXML
    private TableView<InstructionPeek> mainInstructionTable;

    @FXML
    private InstructionTableController mainInstructionTableController;

    @FXML
    void openFileButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open S Language File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));

        //get the stage from one of the components
        Stage stage = (Stage) openFileButton.getScene().getWindow();

        File fileChosen = fileChooser.showOpenDialog(stage);
        if(fileChosen == null) return;

        try {
            engine.loadProgram(fileChosen.getAbsolutePath());
            filenameLabel.setText("Loaded file: " + fileChosen.getAbsolutePath());
            filenameLabel.setStyle(filenameLabel.getStylesheets().toString());
            showCatImage();
            mainInstructionTableController.setInstructions(engine.getProgramPeek().instructions());
        } catch (NotXMLException e) {
            showError("Error. File not an XML");
        } catch (FileNotFoundException e) {
            showError("Error. File not found");
        } catch (UnknownLabelException e) {
            showError(e.getMessage());
        } catch (SProgramNotLoadedException e) {
            throw new AssertionError(e); // will never happen, we loaded a program
        }
    }

    public void setEngine(SLanguageEngine engine) {
        this.engine = engine;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        goofyImageView.setOpacity(0.0);

        if(mainInstructionTable == null)
            throw new IllegalStateException("mainInstructionTable was not injected properly");

        final String CAT_IMAGE_PATH = "/images/thmbs_up.jpg";
        final String MONKE_IMAGE_PATH = "/images/monke.jpeg";
        final String MAMALIGA_SOUND_PATH = "/sound/magic_mamaliga.mp3";

        var catUrl = getClass().getResource(CAT_IMAGE_PATH);
        var monkeUrl = getClass().getResource(MONKE_IMAGE_PATH);
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

        var mamaligaUrl = getClass().getResource(MAMALIGA_SOUND_PATH);
        if(mamaligaUrl != null && !DEBUG) {
            Media mamaligaSound = new Media(mamaligaUrl.toExternalForm());
            MediaPlayer player = new MediaPlayer(mamaligaSound);
            player.setVolume(1100000);
            player.play();
        }

        catImage = new Image(catUrl.toExternalForm());
        monkeImage = new Image(monkeUrl.toExternalForm());
        openFileButton.pressedProperty().addListener(
                (v,t1,t2) -> {
                    if(!t1 && t2) goofyImageView.setOpacity(1.0);
                }
        );
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
    }

    private void showMonkeImage(){
        goofyImageView.setImage(monkeImage);
    }

    private void showError(String s) {
        filenameLabel.setText(s);
        filenameLabel.setStyle("-fx-text-fill: red");
        showMonkeImage();
    }
}
