package gui.component.primary;

import engine.api.SLanguageEngine;
import engine.api.dto.InstructionPeek;
import engine.loader.exception.NotXMLException;
import engine.loader.exception.UnknownLabelException;
import gui.component.execution.DebugState;
import gui.component.execution.ExecutionTabController;
import gui.component.instruction.table.InstructionTableController;
import gui.task.ProgramLoadTask;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;
import java.util.List;

public class PrimaryController implements Initializable {
    @FXML
    private BorderPane rootBorderPane;

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
    private ExecutionTabController executionTabController;

    private SLanguageEngine engine;

    private final IntegerProperty expansionDegreeProperty = new SimpleIntegerProperty(0);

    private final BooleanProperty programLoadedProperty = new SimpleBooleanProperty(false);
    private final StringProperty  programPathProperty   = new SimpleStringProperty("None");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playSoundTheme();

        initInstructionTables();

        initExpansionDegreeSelection();

        initDebugRelated();

        bindToProgramLoaded();
    }

    @FXML
    public void openFileButtonAction(ActionEvent event) {
        //get the stage from one of the components
        Stage stage = (Stage) openFileButton.getScene().getWindow();

        // file chooser dialog window
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open S Language File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        File fileChosen = fileChooser.showOpenDialog(stage);

        if(fileChosen == null) return;
        loadProgram(fileChosen);
    }

    public void setEngine(SLanguageEngine engine) {
        if(engine == null)
            throw new AssertionError("engine is null");

        this.engine = engine;
        executionTabController.setEngine(engine);
    }

    // ===================== private =======================
    private void initExpansionDegreeSelection() {
        // what happens on expansion degree change
        expansionDegreeProperty.bind(expansionChoiceBox.getSelectionModel().selectedItemProperty());
        expansionChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (v, old, now) ->{
                    mainInstructionTableController.setInstructions(
                            engine.getExpandedProgramPeek(expansionDegreeProperty.get()).instructions());
                    expansionTableController.clear();
                }
        );
        // bind the expansion deg. property to execution tab controller
        executionTabController.getExpansionDegreeProperty().bind(expansionDegreeProperty);
    }

    private void initInstructionTables() {
        mainInstructionTableController.setPlaceholderMessage("No program loaded");
        expansionTableController.setPlaceholderMessage("Pick instruction to see its source");

        // on row click, show expansion chain in the expansion table
        mainInstructionTableController.addRowClickListener(rowClickAction ->
                showExpansionChain(rowClickAction.getRowData())
        );

        // breakpoints inside instruction table
        mainInstructionTableController.disableBreakpoints(false);
        mainInstructionTableController.addBreakpointChangeListener(
                breakpointChangeAction -> {
                    Integer lineId = breakpointChangeAction.getSource();

                    if(breakpointChangeAction.isBreakpointSet())
                        executionTabController.addBreakPoint(lineId);
                    else
                        executionTabController.removeBreakPoint(lineId);
                }
        );
    }

    private void initDebugRelated() {
        // on debug state change
        executionTabController.addDebugStateListener(
                debugStateChange ->
                        onDebugStateChange(debugStateChange.getSource())
        );

        executionTabController.addDebugLineChangeListener(
                debugLineChangeAction ->
                        onDebugStoppedOnLine(debugLineChangeAction.getSource())
        );
    }

    private void bindToProgramLoaded(){
        // file name label
        filenameLabel.textProperty().bind(
                Bindings.format("Loaded file: %s", programPathProperty)
        );

        // expansion choice box
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
        programLoadedProperty.addListener(
                (v, was, now) -> {
                    if(now) {
                        expansionChoiceBox.setDisable(false);
                        expansionChoiceBox.setValue(0); // reset to 0 when new program loaded
                    }
                }
        );

        // disable other menus if no program loaded
        rootBorderPane.getCenter().disableProperty().bind(Bindings.not(programLoadedProperty));
        rootBorderPane.getRight().disableProperty().bind(Bindings.not(programLoadedProperty));

        // miscellaneous reactions to program loaded
        programLoadedProperty.addListener(
                (v, was, now) -> {
                    if (now){
                        mainInstructionTableController.setInstructions(engine.getProgramPeek().instructions());

                        executionTabController.reset();
                        executionTabController.buildInputGrid();
                    }
                }
        );
    }

    private void onDebugStateChange(DebugState debugState) {
        expansionChoiceBox.setDisable(debugState != DebugState.NOT_IN_DEBUG); // we don't want to allow changing expansions while debugging

        if(debugState == DebugState.END)
            mainInstructionTableController.resetDebugHighlight();
    }

    private void onDebugStoppedOnLine(int lineId) {
        // highlight the line the debugger stopped on
        mainInstructionTableController.setDebugHighlight(lineId);
    }

    private void loadProgram(File fileChosen) {
        programLoadedProperty.set(false);

        // create a task to load the program
        var loaderTask = new ProgramLoadTask(engine, fileChosen.getAbsolutePath());
        Thread th = new Thread(loaderTask);
        th.setDaemon(true);

        // create a ProgressBar and bind its progress to the task
        ProgressBar progressBar = new ProgressBar(0);
        progressBar.progressProperty().bind(loaderTask.progressProperty());
        Stage dialog = getProgressDialogWindow(progressBar);

        // when the task succeeded
        loaderTask.setOnSucceeded(e -> Platform.runLater(() -> {
            Platform.runLater(() -> {
                rootBorderPane.setDisable(false);
                dialog.close();
            });
            programLoadedProperty.set(true); // succeeded to load program
            programPathProperty.setValue(fileChosen.getAbsolutePath());
        }));

        // when the task failed
        loaderTask.setOnFailed(e -> {
            if(!engine.programNotLoaded()){
                programLoadedProperty.set(true); // if we failed but engine had a program loaded before
            }

            Platform.runLater(() -> {
                dialog.close();
                rootBorderPane.setDisable(false);
            });

            Throwable ex = loaderTask.getException();
            switch (ex) {
                case NotXMLException ignored ->
                        Platform.runLater(() -> showError("Error. File not an XML"));
                case FileNotFoundException ignored ->
                        Platform.runLater(() -> showError("Error. File not found"));
                case UnknownLabelException ignored ->
                        Platform.runLater(() -> showError(ex.getMessage()));
                default ->
                        Platform.runLater(() -> showError("Unhandled error: " + ex.getMessage()));
            }
        });

        // start the loading task
        th.start();
    }

    private Stage getProgressDialogWindow(ProgressBar progressBar) {
        // create a dialog/window to show the progress bar
        Stage dialog = new Stage();
        VBox box = new VBox(10, new Label("Loading..."), progressBar);
        box.setPadding(new Insets(20));
        dialog.setScene(new Scene(box));
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.show();
        dialog.setResizable(false);
        dialog.setAlwaysOnTop(true);

        dialog.setOnCloseRequest(Event::consume);

        rootBorderPane.setDisable(true);
        return dialog;
    }

    private void showExpansionChain(InstructionPeek instruction){
        int currentExpansionDegree = expansionDegreeProperty.get();

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
        System.out.println(s);
        //filenameLabel.setText(s); //TODO:fix this
    }

    private void playSoundTheme() {
        final String MAMALIGA_SOUND_PATH = "/sound/magic_mamaliga.mp3";
        boolean DEBUG = true;

        var mamaligaUrl = getClass().getResource(MAMALIGA_SOUND_PATH);
        if(mamaligaUrl != null && !DEBUG) {
            Media mamaligaSound = new Media(mamaligaUrl.toExternalForm());
            MediaPlayer player = new MediaPlayer(mamaligaSound);
            player.setCycleCount(MediaPlayer.INDEFINITE);
            player.play();
        }
    }
}
