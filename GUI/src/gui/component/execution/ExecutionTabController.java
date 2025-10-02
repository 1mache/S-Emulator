package gui.component.execution;

import engine.api.SLanguageEngine;
import engine.api.dto.debug.DebugEndResult;
import engine.api.dto.debug.DebugHandle;
import engine.api.dto.ProgramExecutionResult;
import engine.api.dto.debug.DebugStepPeek;
import gui.component.execution.event.DebugStateChange;
import gui.component.execution.event.DebugStopOnLine;
import gui.component.variable.table.VariableTableController;
import gui.utility.CssClasses;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.geometry.Insets;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.*;

public class ExecutionTabController implements Initializable {
    @FXML
    private VariableTableController variableTableController;

    @FXML
    private GridPane inputVariableGrid;

    @FXML
    private Label inputVarsLabel;

    @FXML
    private Label cyclesLabel;

    @FXML
    private ChoiceBox<RunMode> modeChoiceBox;

    @FXML
    private Button mainActionButton;

    @FXML
    private Button stepOverButton;

    @FXML
    private Button stepBackButton;

    @FXML
    private Button continueButton;

    private List<Button> debugControls;

    private SLanguageEngine engine;
    private final Map<String, TextField> inputFields = new HashMap<>();
    private final BooleanProperty inputsValidProperty = new SimpleBooleanProperty(true);
    private final IntegerProperty expansionDegreeProperty = new SimpleIntegerProperty(0);

    private enum RunMode {
        EXECUTION("Execution"),
        DEBUG("Debug");

        private final String name;

        RunMode(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private RunMode runMode = RunMode.EXECUTION; // Default

    private DebugHandle debugHandle;

    private final Set<EventHandler<DebugStopOnLine>> debugLineChangeListeners = new HashSet<>();
    private final DebugStateMachine debugStateMachine = new DebugStateMachine();

    private final Set<Integer> breakpoints = new HashSet<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        inputVariableGrid.getChildren().clear();

        inputsValidProperty.addListener(
                (v, was, now) ->
                        onInputsValidityChange(now)
        );

        initializeModeChoiceBox();

        initDebugRelated();
    }

    public void setEngine(SLanguageEngine engine){
        if(engine == null)
            throw new AssertionError("engine is null");

        this.engine = engine;
    }

    public void runProgramAction() {
        validateInputs();
        if(!inputsValidProperty.get()) return;

        setCyclesText(0);
        disableInputs(true);

        ProgramExecutionResult result;
        if(runMode == RunMode.EXECUTION){
            result = engine.runProgram(
                    getInputsFromTextFields(),
                    expansionDegreeProperty.get(),
                    true
            );
            variableTableController.setVariableEntries(result.variableMap());
            cyclesLabel.setText("Cycles: " + result.cycles());
        }
        else if(runMode == RunMode.DEBUG){
            debugHandle = engine.debugProgram(
                    getInputsFromTextFields(),
                    expansionDegreeProperty.get(),
                    true
            );

            breakpoints.forEach(
                    lineId -> debugHandle.addBreakpoint(lineId)
            );

            debugStateMachine.transitionTo(DebugState.RUNNING);
            if(debugHandle.startDebug())
                debugStateMachine.transitionTo(DebugState.END);
            else // execution was paused on breakpoint
                debugStateMachine.transitionTo(DebugState.ON_INSTRUCTION);
        }
    }

    public void stopDebugAction() {
        debugHandle.stopDebug();
        debugStateMachine.transitionTo(DebugState.END);
    }

    // --------------- debug api ------------------

    @FXML
    public void onMainAction(ActionEvent event){
        DebugState state = debugStateMachine.getCurrentState();
        if (state == DebugState.NOT_IN_DEBUG || state == DebugState.END) {
            // run program or start debug
            runProgramAction();
        } else if (state == DebugState.RUNNING || state == DebugState.ON_INSTRUCTION) {
            // stop debug
            stopDebugAction();
        }
    }

    @FXML
    public void onNewRunAction(ActionEvent event){
        if(debugStateMachine.getCurrentState() == DebugState.ON_INSTRUCTION){
            stopDebugAction();
        }

        // enable inputs
        disableInputs(false);
        // clear variable table
        variableTableController.clear();
        // clear input text field
        inputFields.values().forEach(textField ->  textField.setText("0"));
        setCyclesText(0);

        debugStateMachine.transitionTo(DebugState.NOT_IN_DEBUG);
    }

    @FXML
    public void stepOverAction(ActionEvent event) {
        // if debug finished this button will not be active so no check needed
        DebugStepPeek delta = debugHandle.stepOver();
        if(delta.variable().isPresent()){
            String varName = delta.variable().get();
            variableTableController.addVariableEntries(Map.of(varName, delta.newValue()));
            variableTableController.highlightVariable(varName);
        }

        setCyclesText(debugHandle.getCurrentCycles()); // update cycles

        debugHandle.whichLine()
                .ifPresentOrElse(
                        this::fireDebugStoppedOnLine,
                        // if not present then we reached the end
                        () -> debugStateMachine.transitionTo(DebugState.END)
                );
    }

    @FXML
    public void stepBackAction(ActionEvent event) {
        // if debug finished this button will not be active so no check needed
        System.out.println("I am stepping back");
    }

    @FXML
    public void continueAction(ActionEvent event) {
        debugStateMachine.transitionTo(DebugState.RUNNING);
        if(debugHandle.resume())
            debugStateMachine.transitionTo(DebugState.END);
        else
            debugStateMachine.transitionTo(DebugState.ON_INSTRUCTION);
    }

    public void addDebugLineChangeListener(EventHandler<DebugStopOnLine> listener){
        debugLineChangeListeners.add(listener);
    }

    public void addDebugStateListener(EventHandler<DebugStateChange> listener){
        debugStateMachine.addListener(listener);
    }

    public void addBreakPoint(int lineId){
        breakpoints.add(lineId);
        if(debugStateMachine.getCurrentState() == DebugState.ON_INSTRUCTION)
            debugHandle.addBreakpoint(lineId); // adds breakpoint mid run
    }

    public void removeBreakPoint(int lineId){
        breakpoints.remove(lineId);
        if(debugStateMachine.getCurrentState() == DebugState.ON_INSTRUCTION)
            debugHandle.removeBreakpoint(lineId); // adds breakpoint mid run
    }

    // --------------- other controller communication ------------------
    public void reset(){
        runMode = RunMode.EXECUTION;
        modeChoiceBox.setValue(runMode);
        debugStateMachine.transitionTo(DebugState.NOT_IN_DEBUG);
        variableTableController.clear();
        buildInputGrid();
        disableInputs(false);
    }

    public IntegerProperty getExpansionDegreeProperty() {
        return expansionDegreeProperty;
    }

    public void buildInputGrid() {
        List<String> variableNames = engine.getProgramPeek().inputVariables();

        inputVariableGrid.getChildren().clear();
        inputVariableGrid.getRowConstraints().clear();
        inputFields.clear(); // reset storage

        int row = 0;
        for (String varName : variableNames) {
            Label label = new Label(varName);
            GridPane.setRowIndex(label, row);
            GridPane.setColumnIndex(label, 0);
            GridPane.setMargin(label, new Insets(0, 0, 0, 10));

            TextField textField = new TextField("0");
            GridPane.setRowIndex(textField, row);
            GridPane.setColumnIndex(textField, 1);
            GridPane.setMargin(textField, new Insets(0, 0, 0, 5));

            inputVariableGrid.getChildren().addAll(label, textField);

            // keep reference
            inputFields.put(varName, textField);

            row++;
        }

        // add row constraint
        RowConstraints rc = new RowConstraints();
        rc.setMinHeight(10);
        rc.setPrefHeight(30);
        rc.setVgrow(Priority.SOMETIMES);
        inputVariableGrid.getRowConstraints().add(rc);
    }

    // --------- private: ------------
    private void initializeModeChoiceBox() {
        modeChoiceBox.setItems(FXCollections.observableArrayList(
                RunMode.EXECUTION,
                RunMode.DEBUG
        ));

        modeChoiceBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(RunMode option) {
                return option == null ? "" : option.getName(); // use your getName()
            }

            @Override
            public RunMode fromString(String s) {
                return null; // not needed
            }
        });

        // mode choice box:
        modeChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) ->
                        runMode = newValue
        );
        modeChoiceBox.setValue(runMode);
    }

    private void initDebugRelated() {
        // define debug controls
        debugControls = List.of(stepOverButton, stepBackButton, continueButton);

        // when debug finishes:
        debugStateMachine.addListener(
                newValue ->
                        onDebugStateChange(newValue.getSource())
        );
    }

    private void setCyclesText(long cycles) {
        cyclesLabel.setText("Cycles: " + cycles);
    }

    private void setDebugControlsDisabled(boolean disabled) {
        debugControls.forEach(control -> control.setDisable(disabled));
    }

    private void validateInputs() {
        boolean allValid = true;
        for (TextField textField : inputFields.values()) {
            String text = textField.getText();
            try {
                var num = Long.parseLong(text);
                if(num < 0)
                    throw new NumberFormatException("Negative number");

                textField.getStyleClass().remove(CssClasses.ERROR_FIELD);
            } catch (NumberFormatException e) {
                if (!textField.getStyleClass().contains(CssClasses.ERROR_FIELD)) {
                    textField.getStyleClass().add(CssClasses.ERROR_FIELD);
                }
                allValid = false;
            }
        }
        inputsValidProperty.set(allValid);
    }

    private List<Long> getInputsFromTextFields() {
        return inputFields.values().stream().map(TextField::getText).map(Long::parseLong).toList();
    }

    private void disableInputs(boolean disable) {
        inputFields.values().forEach(inputField -> inputField.setDisable(disable));
    }

    private void onInputsValidityChange(Boolean now) {
        if (now) {
            String DEFAULT_LABEL_TEXT = "Input Variables (positive integers)";
            inputVarsLabel.setText(DEFAULT_LABEL_TEXT);
            inputVarsLabel.getStyleClass().remove(CssClasses.ERROR_FIELD);
        } else {
            inputVarsLabel.setText("Please correct invalid inputs");
            if (!inputVarsLabel.getStyleClass().contains(CssClasses.ERROR_FIELD)) {
                inputVarsLabel.getStyleClass().add(CssClasses.ERROR_FIELD);
            }
        }
    }

    private void onDebugStateChange(DebugState newValue) {
        String RUN_PROGRAM_BUTTON_TEXT = "Start Run";
        String STOP_DEBUG_BUTTON_TEXT = "Stop Debug";

        switch (newValue) {
            case NOT_IN_DEBUG-> {
                mainActionButton.setText(RUN_PROGRAM_BUTTON_TEXT);
                variableTableController.resetHighlight();
            }
            case RUNNING -> {
                setDebugControlsDisabled(true);
                mainActionButton.setText(STOP_DEBUG_BUTTON_TEXT);

            }
            case ON_INSTRUCTION -> {
                fireDebugStoppedOnLine(debugHandle.whichLine().orElseThrow());
                setDebugControlsDisabled(false);
                mainActionButton.setText(STOP_DEBUG_BUTTON_TEXT);
                variableTableController.setVariableEntries(debugHandle.getResult().variableMap());
                setCyclesText(debugHandle.getCurrentCycles()); // update cycles
            }
            case END -> {
                DebugEndResult result = debugHandle.getResult();
                variableTableController.setVariableEntries(result.variableMap());
                cyclesLabel.setText("Cycles: " + result.cycles());

                setDebugControlsDisabled(true);
                mainActionButton.setText(RUN_PROGRAM_BUTTON_TEXT);

                variableTableController.resetHighlight();
            }
            case null, default -> {
                throw new IllegalArgumentException("Illegal state passed: " + newValue);
            }
        }
    }

    private void fireDebugStoppedOnLine(int breakpointLine) {
        debugLineChangeListeners.forEach(
                listener -> listener.handle(new DebugStopOnLine(breakpointLine))
        );
    }
}
