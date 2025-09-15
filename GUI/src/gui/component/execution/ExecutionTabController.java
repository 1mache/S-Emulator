package gui.component.execution;

import engine.api.SLanguageEngine;
import gui.component.variable.table.VariableTableController;
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
    @FXML
    private VariableTableController variableTableController;

    @FXML
    private GridPane inputVariableGrid;

    @FXML
    private Button runProgramButton;

    @FXML
    private Button debugProgramButton;

    private SLanguageEngine engine;
    private final Map<String, TextField> inputFields = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        inputVariableGrid.getChildren().clear();
    }

    public void setEngine(SLanguageEngine engine){
        if(engine == null)
            throw new AssertionError("engine is null");

        this.engine = engine;
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
}
