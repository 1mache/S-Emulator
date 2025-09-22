package gui.component.execution;

import engine.api.SLanguageEngine;
import engine.api.dto.debug.DebugEndResult;
import engine.api.dto.debug.DebugHandle;
import engine.api.dto.ExecutionResult;
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
    private Button runProgramButton;

    @FXML
    private Button stepOverButton;

    @FXML
    private Button stepBackButton;

    @FXML
    private Button stopDebugButton;

    @FXML
    private Button continueButton;

    private List<Button> debugControls;

    private SLanguageEngine engine;
    private final Map<String, TextField> inputFields = new HashMap<>();
    private final BooleanProperty inputsValidProperty = new SimpleBooleanProperty(true);
    private final IntegerProperty expansionDegreeProperty = new SimpleIntegerProperty(0);

    private RunMode runMode = RunMode.EXECUTION; // Default

    private DebugHandle debugHandle;

    private final ObjectProperty<DebugState> debugState = new SimpleObjectProperty<>(DebugState.NOT_IN_DEBUG);
    private final Set<EventHandler<DebugStateChange>> debugStateListeners = new HashSet<>();
    private final Set<EventHandler<DebugStopOnLine>> debugLineChangeListeners = new HashSet<>();

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

    @FXML
    public void runButtonAction(ActionEvent event) {
        validateInputs();
        if(!inputsValidProperty.get()) return;

        disableInputs(true);

        ExecutionResult result;
        if(runMode == RunMode.EXECUTION){
            result = engine.runProgram(
                    inputFields.values().stream().map(TextField::getText).map(Long::parseLong).toList(),
                    expansionDegreeProperty.get(),
                    true
            );
            variableTableController.setVariableEntries(result.variableMap());
            cyclesLabel.setText("Cycles: " + result.cycles());
        }
        else if(runMode == RunMode.DEBUG){
            debugHandle = engine.debugProgram(
                    inputFields.values().stream().map(TextField::getText).map(Long::parseLong).toList(),
                    expansionDegreeProperty.get(),
                    true
            );

            breakpoints.forEach(
                    lineId -> debugHandle.addBreakpoint(lineId)
            );

            debugState.set(DebugState.RUNNING);
            if(debugHandle.startDebug())
                debugState.set(DebugState.END);
            else
                debugState.set(DebugState.ON_INSTRUCTION);
        }
    }

    // --------------- debug api ------------------

    @FXML
    public void onNewRunAction(ActionEvent event){
        // enable inputs
        disableInputs(false);
        // clear variable table
        variableTableController.clear();
        // clear input text field
        inputFields.values().forEach(textField ->  textField.setText("0"));
        cyclesLabel.setText("Cycles: " + 0);

        if(debugHandle != null && debugState.get() == DebugState.ON_INSTRUCTION){
            debugHandle.stopDebug();
            debugState.set(DebugState.NOT_IN_DEBUG);
        }
    }

    @FXML
    public void stepOverAction(ActionEvent event) {
        // if debug finished this button will not be active so no check needed
        DebugStepPeek delta = debugHandle.stepOver();
        if(delta.variable().isPresent())
            variableTableController.addVariableEntries(Map.of(delta.variable().get(), delta.newValue()));

        debugHandle.whichLine()
                .ifPresentOrElse(
                        this::fireDebugStoppedOnLine,
                        // if not present then we reached the end
                        () -> debugState.set(DebugState.END)
                );
    }

    @FXML
    public void stepBackAction(ActionEvent event) {
        // if debug finished this button will not be active so no check needed
        System.out.println("I am stepping back");
    }

    @FXML
    public void stopDebugAction(ActionEvent event) {
        debugHandle.stopDebug();
        debugState.set(DebugState.END);
    }

    @FXML
    public void continueAction(ActionEvent event) {
        debugState.set(DebugState.RUNNING);
        if(debugHandle.resume())
            debugState.set(DebugState.END);
        else
            debugState.set(DebugState.ON_INSTRUCTION);
    }

    public void addDebugLineChangeListener(EventHandler<DebugStopOnLine> listener){
        debugLineChangeListeners.add(listener);
    }

    public void addDebugStateListener(EventHandler<DebugStateChange> listener){
        debugStateListeners.add(listener);
    }

    public void addBreakPoint(int lineId){
        breakpoints.add(lineId);
        if(debugState.get() == DebugState.ON_INSTRUCTION)
            debugHandle.addBreakpoint(lineId);
    }

    public void removeBreakPoint(int lineId){
        breakpoints.remove(lineId);
        if(debugState.get() == DebugState.ON_INSTRUCTION)
            debugHandle.removeBreakpoint(lineId);
    }

    // --------------- other controller communication ------------------
    public void reset(){
        runMode = RunMode.EXECUTION;
        modeChoiceBox.setValue(runMode);
        debugState.set(DebugState.NOT_IN_DEBUG);
        variableTableController.clear();
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
        debugControls = List.of(stepOverButton, stepBackButton, stopDebugButton, continueButton);

        // when debug finishes:
        debugState.addListener(
                (observable, oldValue, newValue) ->
                        onDebugStateChange(newValue)
        );
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
        fireDebugStateChange();

        switch (newValue) {
            case NOT_IN_DEBUG, RUNNING -> {
                debugControls.forEach(control -> control.setDisable(true));
                runProgramButton.setDisable(false); // doesnt matter in RUNNING because we never long enough at this state
            }
            case ON_INSTRUCTION -> {
                fireDebugStoppedOnLine(debugHandle.whichLine().orElseThrow());
                debugControls.forEach(control -> control.setDisable(false));
                runProgramButton.setDisable(true);
                variableTableController.setVariableEntries(debugHandle.getResult().variableMap());
            }
            case END -> {
                DebugEndResult result = debugHandle.getResult();
                variableTableController.setVariableEntries(result.variableMap());
                cyclesLabel.setText("Cycles: " + result.cycles());
                runProgramButton.setDisable(false);

                debugState.set(DebugState.NOT_IN_DEBUG);
            }
            case null, default -> {
            }
        }
    }

    private void fireDebugStateChange() {
        debugStateListeners.forEach(
                listener ->
                        listener.handle(new DebugStateChange(debugState.get()))
        );
    }

    private void fireDebugStoppedOnLine(int breakpointLine) {
        debugLineChangeListeners.forEach(
                listener -> listener.handle(new DebugStopOnLine(breakpointLine))
        );
    }
}
