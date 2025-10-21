package newGui.pages.execution.component.execution;

import dto.InstructionPeek;
import dto.ProgramPeek;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
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
import newGui.pages.execution.component.primary.mainExecutionController;

import java.util.List;

public class executionController {

    private mainExecutionController mainExecutionController;
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
    @FXML private TableView<String> inputTable;
    @FXML private TableColumn<String, String> variableInput;
    @FXML private TableColumn<Long, Long> valueInput;

    // Variables State Table
    @FXML private TableView<String> variableTable;
    @FXML private TableColumn<String, String> variableState;
    @FXML private TableColumn<Long, Long> valueState;


    // History Table
    @FXML private TableView<InstructionPeek> historyTable;
    @FXML private TableColumn<InstructionPeek, Long> Index;
    @FXML private TableColumn<InstructionPeek, Long> cycles;
    @FXML private TableColumn<InstructionPeek, List<Long>> inputs;
    @FXML private TableColumn<InstructionPeek, Long> level;
    @FXML private TableColumn<InstructionPeek, Long> output;

    @FXML private ComboBox<?> architectureSelection;

    @FXML private TextField CyclesCounter;

    public void setMainExecutionController(mainExecutionController mainExecutionController) {
        this.mainExecutionController = mainExecutionController;
    }

    public void setProgramPeek(ProgramPeek programPeek) {
        List<InstructionPeek> instructions = programPeek.instructions();

        List<String> inputs =  programPeek.inputVariables();
        List<String> works =  programPeek.workVariables();

        // Inputs Table
        inputTable.getItems().clear();
        variableInput.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        valueInput.setCellValueFactory(data -> {
            Long value = 0L;
            return new SimpleLongProperty(value).asObject();});
        inputTable.getItems().addAll(inputs);


        // Variables State Table
        List<String> allVariables =  List.of();
        allVariables.addAll(inputs);
        allVariables.addAll(works);

        variableTable.getItems().clear();
        variableState.setCellValueFactory(newData -> new SimpleStringProperty(newData.getValue()));
        valueState.setCellValueFactory(data -> {
            Long value = 0L;
            return new SimpleLongProperty(value).asObject();});
        variableTable.getItems().addAll(inputs);




    }


    @FXML
    void backListener(ActionEvent event) {
        //mainExecutionController.getMainController().returnToDashboard(mainExecutionController.);

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
