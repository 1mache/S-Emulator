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

    @FXML
    private TextField CyclesCounter;

    @FXML
    private Label CyclesLabel;

    @FXML
    private Label History;

    @FXML
    private TableColumn<?, ?> Index;

    @FXML
    private Label InputsLabel;

    @FXML
    private Label VariablesLabel;

    @FXML
    private ComboBox<?> architectureSelection;

    @FXML
    private Button backToDashboard;

    @FXML
    private TableColumn<?, ?> cycles;

    @FXML
    private Label debuggerLabel;

    @FXML
    private Label executionLabel;

    @FXML
    private TableView<?> historyTable;

    @FXML
    private Button initButton;

    @FXML
    private TableView<?> inputTable;

    @FXML
    private TableColumn<?, ?> inputs;

    @FXML
    private TableColumn<?, ?> level;

    @FXML
    private TableColumn<?, ?> output;

    @FXML
    private Button reRun;

    @FXML
    private HBox regulerExecution;

    @FXML
    private Button resumeDebugButton;

    @FXML
    private VBox rght;

    @FXML
    private VBox right;

    @FXML
    private Button show;

    @FXML
    private Button startButton;

    @FXML
    private Button startDebugButton;

    @FXML
    private Button stepOverDebugButton;

    @FXML
    private Button stopDebugButton;

    @FXML
    private TableColumn<?, ?> valueInput;

    @FXML
    private TableColumn<?, ?> valueState;

    @FXML
    private TableColumn<?, ?> variableInput;

    @FXML
    private TableColumn<?, ?> variableState;

    @FXML
    private TableView<?> variableTable;

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
