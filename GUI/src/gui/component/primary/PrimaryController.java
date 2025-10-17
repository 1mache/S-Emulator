package gui.component.primary;

import dto.ProgramIdentifier;
import dto.InstructionPeek;
import dto.ProgramExecutionResult;
import dto.ProgramPeek;
import engine.api.SLanguageEngine;
import engine.loader.exception.NotXMLException;
import gui.component.execution.DebugState;
import gui.component.execution.ExecutionTabController;
import gui.component.history.HistoryTableController;
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Insets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    private HBox programControlsHbox;

    @FXML
    private ChoiceBox<String> programChoiceBox;

    @FXML
    private ChoiceBox<Integer> expansionChoiceBox;

    @FXML
    private ComboBox<String> highlightChoiceBox;

    @FXML
    private InstructionTableController mainInstructionTableController;

    @FXML
    private TextField summaryLineField;

    @FXML
    private InstructionTableController expansionTableController;

    @FXML
    private ExecutionTabController executionTabController;

    private SLanguageEngine engine;

    private final IntegerProperty expansionDegreeProperty = new SimpleIntegerProperty(0);

    private final BooleanProperty programLoadedProperty       = new SimpleBooleanProperty(false);
    private final StringProperty  selectedProgramNameProperty = new SimpleStringProperty("");
    private final StringProperty  programPathProperty         = new SimpleStringProperty("");

    private final ObservableList<String> avaliablePrograms = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        initInstructionTables();

        initExpansionDegreeSelection();

        initHighlightSelection();

        initProgramSelection();

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

    @FXML
    public void showHistoryAction(ActionEvent event) {
        final String HISTORY_LAYOUT_PATH = "/gui/component/history/history_tab.fxml";

        FXMLLoader loader = new FXMLLoader();
        URL historyTableUrl = getClass().getResource(HISTORY_LAYOUT_PATH);
        loader.setLocation(historyTableUrl);

        try {
            Parent root = loader.load();
            Stage dialog = new Stage();
            dialog.setTitle("Run History");
            dialog.initModality(Modality.APPLICATION_MODAL);
            Scene dialogScene = new Scene(root);
            dialogScene.getStylesheets().addAll(rootBorderPane.getStylesheets());

            dialog.setScene(dialogScene);

            HistoryTableController controller = loader.getController();
            controller.setItems(
                    executionTabController.getRunHistory()
                            .getExecutionsOf(selectedProgramNameProperty.get())
            );
            controller.addReRunButtonListener(e -> {
                dialog.close();
                executionTabController.reset();
                ProgramExecutionResult selectedRun = controller.getSelectedLine();
                executionTabController.setInputsInTextFields(selectedRun.inputs());
                expansionChoiceBox.setValue(selectedRun.expansionDegree());
            });

            dialog.showAndWait();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
                    if(engine.programNotLoaded(selectedProgramNameProperty.get())) return;
                    showCurrentProgramWithSelectedExpansion();
                }
        );
        // bind the expansion deg. property to execution tab controller
        executionTabController.getExpansionDegreeProperty().bind(expansionDegreeProperty);
    }

    private void initProgramSelection(){
        programChoiceBox.setItems(avaliablePrograms);
        programChoiceBox.getSelectionModel().selectedIndexProperty().addListener(
                (v, old, now) ->{
                    if(now.intValue() < 0) return; // no selection

                    selectedProgramNameProperty.set(
                            engine.getAvaliablePrograms()
                                    // select by index.
                                    .get(now.intValue())
                                    .name()
                    );

                    resetProgramView();
                    showCurrentProgramWithSelectedExpansion();
                }
        );
    }

    private void initHighlightSelection(){
        highlightChoiceBox.setValue(null);
        highlightChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (v, old, now) ->{
                    if(engine.programNotLoaded(selectedProgramNameProperty.get())) return;

                    if(now == null) {
                        mainInstructionTableController.resetLinesHighlight();
                        return;
                    }

                    mainInstructionTableController.setLinesHighlight(
                            engine.getInstructionsIdsThatUse(
                                    selectedProgramNameProperty.get(),
                                    expansionDegreeProperty.get(),
                                    now
                            )
                    );
                }
        );
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
        // other program name properties
        executionTabController.bindProgramNameProperty(selectedProgramNameProperty);

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
                                int maxDegree = engine.getMaxExpansionDegree(selectedProgramNameProperty.get());
                                for (int i = 0; i <= maxDegree; i++)
                                    degrees.add(i);
                            }

                            // 0 expansion degree by default
                            Platform.runLater(() -> expansionChoiceBox.setValue(0));

                            return degrees;
                        },
                        programLoadedProperty, programChoiceBox.valueProperty()
                )
        );

        // highlight choice box
        highlightChoiceBox.itemsProperty().bind(
                Bindings.createObjectBinding(
                        () -> {
                            ObservableList<String> symbols = FXCollections.observableArrayList();
                            if (programLoadedProperty.get()) {
                                symbols.add(null); // null highlight, meaning dont highlight
                                ProgramPeek programPeek = engine.getProgramPeek(
                                        selectedProgramNameProperty.get(),
                                        expansionDegreeProperty.get()
                                );

                                symbols.addAll(
                                        programPeek.inputVariables()
                                );

                                symbols.addAll(
                                        programPeek.workVariables()
                                );

                                symbols.addAll(
                                        programPeek.labelsUsed()
                                );
                            }

                            Platform.runLater(() ->  highlightChoiceBox.setValue(null));

                            return symbols;
                        },
                        programLoadedProperty, programChoiceBox.valueProperty(), expansionDegreeProperty
                )
        );

        // function selection
        programLoadedProperty.addListener(
                (v, was, now) -> {
                    if(now) {
                        programControlsHbox.setDisable(false);
                        avaliablePrograms.setAll(
                                engine.getAvaliablePrograms().stream()
                                        .map(ProgramIdentifier::userString)
                                        .toList()
                        );
                        String first = avaliablePrograms.getFirst();
                        avaliablePrograms.set(0, first + " (main)");
                        // program choice box option by default
                        programChoiceBox.setValue(programChoiceBox.getItems().getFirst());
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
                        resetProgramView();

                        mainInstructionTableController.clear();

                        mainInstructionTableController.setInstructions(
                                engine.getProgramPeek(
                                        selectedProgramNameProperty.get(),
                                        expansionDegreeProperty.get()
                                ).instructions()
                        );
                        summaryLineField.setText(buildSummaryLine());
                    }
                }
        );
    }

    private void showCurrentProgramWithSelectedExpansion() {
        mainInstructionTableController.setInstructions(
                engine.getProgramPeek(
                        selectedProgramNameProperty.get(),
                        expansionDegreeProperty.get()
                ).instructions());

        expansionTableController.clear(); // reset the bottom table
        summaryLineField.setText(buildSummaryLine());
    }

    private void onDebugStateChange(DebugState debugState) {
        // we don't want to allow changing expansions or functions while debugging
        if(debugState != DebugState.NOT_IN_DEBUG) {
            programControlsHbox.setDisable(true);
            resetHighlight();
        }

        if(debugState == DebugState.END || debugState == DebugState.NOT_IN_DEBUG){
            mainInstructionTableController.resetDebugHighlight();
            programControlsHbox.setDisable(false);
        }
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

            selectedProgramNameProperty.set(loaderTask.getValue()); // main program name
            programLoadedProperty.set(true); // succeeded to load program
            programPathProperty.setValue(fileChosen.getAbsolutePath());
        }));

        // when the task failed
        loaderTask.setOnFailed(e -> {
            if(!engine.programNotLoaded(selectedProgramNameProperty.get())){
                programLoadedProperty.set(true); // if we failed but engine had a program loaded before
            }

            Platform.runLater(() -> {
                dialog.close();
                rootBorderPane.setDisable(false);
            });

            Throwable ex = loaderTask.getException();
            switch (ex) {
                case NotXMLException ignored -> {}
                case FileNotFoundException ignored -> {}
                default ->
                        Platform.runLater(() -> showError(ex.getMessage()));
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

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error while loading file");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void resetProgramView() {
        // reset expansion
        expansionChoiceBox.setValue(0);

        resetHighlight();

        executionTabController.reset();
    }

    private void resetHighlight(){
        highlightChoiceBox.setValue(null);
        mainInstructionTableController.resetLinesHighlight();
    }

    private String buildSummaryLine(){
        List<InstructionPeek> instructions = mainInstructionTableController.getInstructions();

        final int[] baseInstructionsCount = {0};
        final int[] syntheticInstructionsCount = {0};
        instructions.forEach(instruction -> {
            if(instruction.isSynthetic())
                syntheticInstructionsCount[0]++;
            else
                baseInstructionsCount[0]++;
        });

        return String.format("B: %d | S: %d | Total: %d",
                baseInstructionsCount[0], syntheticInstructionsCount[0], instructions.size());
    }
}
