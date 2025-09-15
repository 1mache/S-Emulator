package gui.component.execution;

import engine.api.SLanguageEngine;
import engine.api.dto.ExecutionResult;
import gui.component.variable.table.VariableTableController;
import gui.utility.CssClasses;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.geometry.Insets;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ExecutionTabController implements Initializable {
    private final String DEFAULT_LABEL_TEXT = "Input Variables (positive integers)";

    @FXML
    private VariableTableController variableTableController;

    @FXML
    private GridPane inputVariableGrid;

    @FXML
    private Button runProgramButton;

    @FXML
    private Button debugProgramButton;

    @FXML
    private Label inputVarsLabel;

    private SLanguageEngine engine;
    private final Map<String, TextField> inputFields = new HashMap<>();
    private final BooleanProperty inputsValidProperty = new SimpleBooleanProperty(true);
    private final IntegerProperty expansionDegreeProperty = new SimpleIntegerProperty(0);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        inputVariableGrid.getChildren().clear();

        inputsValidProperty.addListener(
                (v, old, now) -> {
                    if (now) {
                        inputVarsLabel.setText(DEFAULT_LABEL_TEXT);
                        inputVarsLabel.getStyleClass().remove(CssClasses.ERROR_FIELD);
                    } else {
                        inputVarsLabel.setText("Please correct invalid inputs");
                        if (!inputVarsLabel.getStyleClass().contains(CssClasses.ERROR_FIELD)) {
                            inputVarsLabel.getStyleClass().add(CssClasses.ERROR_FIELD);
                        }
                    }
                }
        );
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
        ExecutionResult result = engine.runProgram(
                inputFields.values().stream().map(TextField::getText).map(Long::parseLong).toList(),
                expansionDegreeProperty.get(),
                true
        );

        variableTableController.setVariableEntries(result.variableMap());
    }

    @FXML
    public void debugButtonAction(ActionEvent event) {
        validateInputs();
        if(!inputsValidProperty.get()) return;
        System.out.println("DEBUG NOT IMPLEMENTED YET");
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

            // add row constraint
            RowConstraints rc = new RowConstraints();
            rc.setMinHeight(10);
            rc.setPrefHeight(30);
            rc.setVgrow(Priority.SOMETIMES);
            inputVariableGrid.getRowConstraints().add(rc);

            row++;
        }
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
}
