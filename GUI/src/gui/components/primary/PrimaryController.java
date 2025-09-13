package gui.components.primary;

import engine.api.SLanguageEngine;
import engine.api.dto.InstructionPeek;
import engine.execution.exception.SProgramNotLoadedException;
import engine.jaxb.loader.exception.NotXMLException;
import engine.jaxb.loader.exception.UnknownLabelException;
import gui.components.instruction.table.InstructionTableController;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;

public class PrimaryController implements Initializable {
    private final boolean DEBUG = true;

    private SLanguageEngine engine;
    private int currentExpansionDegree = 0;

    private final BooleanProperty programLoadedProperty = new SimpleBooleanProperty(false);
    private final StringProperty  programPathProperty   = new SimpleStringProperty("None");

    @FXML
    private SplitPane centerSplitPane;

    @FXML
    private SplitPane rightSplitPane;

    @FXML
    private Label filenameLabel;

    @FXML
    private Button openFileButton;

    @FXML
    private ChoiceBox<Integer> expansionChoiceBox;

    @FXML
    private InstructionTableController mainInstructionTableController;

    @FXML
    private InstructionTableController expansionTableController;

    @FXML
    public void openFileButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open S Language File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));

        //get the stage from one of the components
        Stage stage = (Stage) openFileButton.getScene().getWindow();

        File fileChosen = fileChooser.showOpenDialog(stage);
        if(fileChosen == null) return;

        try {
            engine.loadProgram(fileChosen.getAbsolutePath());

            programLoadedProperty.set(true);
            programPathProperty.setValue(fileChosen.getAbsolutePath());

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        final String MAMALIGA_SOUND_PATH = "/sound/magic_mamaliga.mp3";

        var mamaligaUrl = getClass().getResource(MAMALIGA_SOUND_PATH);
        if(mamaligaUrl != null && !DEBUG) {
            Media mamaligaSound = new Media(mamaligaUrl.toExternalForm());
            MediaPlayer player = new MediaPlayer(mamaligaSound);
            player.setCycleCount(MediaPlayer.INDEFINITE);
            player.play();
        }

        mainInstructionTableController.setPlaceholderMessage("No program loaded");
        expansionTableController.setPlaceholderMessage("Pick instruction to see its source");

        mainInstructionTableController.addRowClickListener(rowClickAction ->
                showExpansionChain(rowClickAction.getRowData())
        );

        expansionChoiceBox.getSelectionModel().selectedIndexProperty().addListener(
                (v, old, now) ->{
                    if(old.intValue() != now.intValue())
                        onChoiceBoxSelect(now.intValue());
                }
        );

        bindToProgramLoaded();

        expansionChoiceBox.setValue(0); // default expansion degree
    }

    public void setEngine(SLanguageEngine engine) {
        this.engine = engine;
    }

    public void onChoiceBoxSelect(int degreeSelected){
        if( 0 <= degreeSelected && degreeSelected <= engine.getMaxExpansionDegree()) {
            currentExpansionDegree = degreeSelected;
            mainInstructionTableController.setInstructions(
                    engine.getExpandedProgramPeek(currentExpansionDegree).instructions()
            );
        }
    }

    private void bindToProgramLoaded(){
        // file name label
        filenameLabel.textProperty().bind(
                Bindings.format("Loaded file: %s", programPathProperty)
        );

        // expansion choice box
        expansionChoiceBox.disableProperty().bind(Bindings.not(programLoadedProperty));
        expansionChoiceBox.itemsProperty().bind(
                Bindings.createObjectBinding(
                        () -> {
                            ObservableList<Integer> degrees = FXCollections.observableArrayList();
                            if (programLoadedProperty.get()) {
                                for (int i = 0; i <= engine.getMaxExpansionDegree(); i++) {
                                    degrees.add(i);
                                }
                            }
                            return degrees;
                        },
                        programLoadedProperty
                )
        );

        // other menus
        centerSplitPane.disableProperty().bind(Bindings.not(programLoadedProperty));
        rightSplitPane.disableProperty().bind(Bindings.not(programLoadedProperty));

        // react to program loaded
        programLoadedProperty.addListener(
                (v, was, now) -> {
                    if (now)
                        mainInstructionTableController.setInstructions(engine.getProgramPeek().instructions());
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

    private void showExpansionChain(InstructionPeek instruction){
        List<InstructionPeek> expansionChain = new ArrayList<>();
        expansionChain.add(instruction);
        instruction = instruction.expandedFrom();
        if(instruction == null) {
            expansionTableController.setPlaceholderMessage("This instruction was in the original program. Was not expanded");
            expansionTableController.clear();
            return;
        }
        if(currentExpansionDegree == 0) {
            expansionTableController.setPlaceholderMessage("The expansion degree is 0. Choose a higher expansion degree.");
            expansionTableController.clear();
            return;
        }

        for (int i = 0; i < currentExpansionDegree && instruction != null; i++) {
            expansionChain.add(instruction);
            instruction = instruction.expandedFrom();
        }

        expansionTableController.setInstructions(expansionChain);
    }

    private void showError(String s) {
        filenameLabel.setText(s);
    }
}
