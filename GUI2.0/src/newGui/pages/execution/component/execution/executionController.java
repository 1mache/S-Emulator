package newGui.pages.execution.component.execution;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class executionController {

    // Won't Use
    @FXML private VBox right;
    @FXML private Label CyclesLabel;
    @FXML private Label History;
    @FXML private Label InputsLabel;
    @FXML private Label VariablesLabel;
    @FXML private Label debuggerLabel;
    @FXML private Label executionLabel;

    // Buttons
    @FXML private Button reRun;
    @FXML private Button resumeDebugButton;
    @FXML private Button show;
    @FXML private Button startButton;
    @FXML private Button startDebugButton;
    @FXML private Button stepOverDebugButton;
    @FXML private Button stopDebugButton;
    @FXML private Button backToDashboard;
    @FXML private Button initButton;

    // Inputs Table
    @FXML private TableView<?> inputTable;
    @FXML private TableColumn<?, ?> variableInput;
    @FXML private TableColumn<?, ?> valueInput;

    // Variables State Table
    @FXML private TableView<?> variableTable;
    @FXML private TableColumn<?, ?> valueState;
    @FXML private TableColumn<?, ?> variableState;


    // History Table
    @FXML private TableView<?> historyTable;
    @FXML private TableColumn<?, ?> Index;
    @FXML private TableColumn<?, ?> cycles;
    @FXML private TableColumn<?, ?> inputs;
    @FXML private TableColumn<?, ?> level;
    @FXML private TableColumn<?, ?> output;

    @FXML private ComboBox<?> architectureSelection;

    @FXML private TextField CyclesCounter;






    @FXML
    void backListener(ActionEvent event) {

    }

    @FXML
    void initListener(ActionEvent event) {

    }

    @FXML
    void reRunListener(ActionEvent event) {

    }

    @FXML
    void resumeDebugListener(ActionEvent event) {

    }

    @FXML
    void showHistoryListener(ActionEvent event) {

    }

    @FXML
    void startDebugListener(ActionEvent event) {

    }

    @FXML
    void startListener(ActionEvent event) {

    }

    @FXML
    void stepOverDebugListener(ActionEvent event) {

    }

    @FXML
    void stopDebugListener(ActionEvent event) {

    }

}
