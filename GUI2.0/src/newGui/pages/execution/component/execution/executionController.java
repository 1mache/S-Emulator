package newGui.pages.execution.component.execution;

import dto.InstructionPeek;
import dto.ProgramExecutionResult;
import dto.ProgramPeek;
import dto.server.request.RunRequest;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.util.converter.LongStringConverter;
import newGui.pages.execution.component.primary.mainExecutionController;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
    @FXML private TableColumn<String, Long> valueInput;
    // fields in the controller:
    private final Map<String, Long> inputValues = new HashMap<>();


    // Variables State Table
    @FXML private TableView<String> variableTable;
    @FXML private TableColumn<String, String> variableState;
    @FXML private TableColumn<String, Long> valueState;
    // Holds the latest values for ALL variables (inputs + works) shown in variableTable
    private final Map<String, Long> variableValues = new HashMap<>();



    // History Table
    @FXML private TableView<InstructionPeek> historyTable;
    @FXML private TableColumn<InstructionPeek, Long> Index;
    @FXML private TableColumn<InstructionPeek, Long> cycles;
    @FXML private TableColumn<InstructionPeek, List<Long>> inputs;
    @FXML private TableColumn<InstructionPeek, Long> level;
    @FXML private TableColumn<InstructionPeek, Long> output;

    @FXML private ComboBox<String> architectureSelection;

    @FXML private TextField CyclesCounter;

    public void setMainExecutionController(mainExecutionController mainExecutionController) {
        this.mainExecutionController = mainExecutionController;
    }

    public void setProgramPeek(ProgramPeek programPeek) {
        // Extract data from the given ProgramPeek
        //List<InstructionPeek> instructions = programPeek.instructions();
        List<String> inputs = new ArrayList<>(programPeek.inputVariables());
        List<String> works = new ArrayList<>(programPeek.workVariables());

        // ====== INPUT TABLE ======
        inputTable.getItems().clear(); // clear old data

        // Each row represents a variable name (String)
        variableInput.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));

        // Initialize map to hold values for each input variable
        inputValues.clear();
        for (String var : inputs) {
            inputValues.put(var, 0L); // default value 0
        }

        // Make the table editable
        inputTable.setEditable(true);
        valueInput.setEditable(true);

        // Display the current value (from map, default 0L)
        valueInput.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(inputValues.getOrDefault(data.getValue(), 0L)));

        // Make the value column editable using a TextField
        valueInput.setCellFactory(TextFieldTableCell.forTableColumn(new LongStringConverter()));

        // When user edits a cell → save the new value in the map
        valueInput.setOnEditCommit(event -> {
            String varName = event.getRowValue(); // variable name (String)
            Long newValue = event.getNewValue();  // user input parsed as Long
            inputValues.put(varName, newValue);   // update map
            inputTable.refresh();                 // refresh row display
        });

        // Add all input variable names as rows
        inputTable.getItems().addAll(inputs);


        // ====== VARIABLE STATE TABLE ======
        List<String> allVariables = new ArrayList<>();
        allVariables.addAll(inputs);
        allVariables.addAll(works);

        // init backing map for state table (default 0L)
        variableValues.clear();
        for (String v : allVariables) {
            variableValues.put(v, 0L);
        }


        variableTable.getItems().clear();
        variableState.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        // show the value from variableValues (default 0L)
        valueState.setCellValueFactory(data ->
                new ReadOnlyObjectWrapper<>(variableValues.getOrDefault(data.getValue(), 0L)));

        variableTable.getItems().addAll(allVariables);
    }

    public static List<Long> sortKeysBySubstring(Map<String, Long> map) {
        // Sort the keys by their substring starting from index 1
        return map.keySet().stream()
                .sorted(Comparator.comparing(key -> key.substring(1)))
                .map(map::get) // take the value of each key
                .collect(Collectors.toList());
    }


        @FXML
    void backListener(ActionEvent event) {
        mainExecutionController.returnToDashboard();

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
        List<Long> inputs = sortKeysBySubstring(inputValues);
        int extensionDegree = mainExecutionController.getSelectedDgree();
        String programName = mainExecutionController.getProgramName();

        Request runRequest = requests.RunRequest.build(new RunRequest(programName,extensionDegree, inputs));
        final ProgramExecutionResult[] result = new ProgramExecutionResult[1];

        HttpClientUtil.runAsync(runRequest, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Server Bad Response : " + e.getMessage());
                requests.RunRequest.onFailure(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                ProgramExecutionResult res = requests.RunRequest.onResponse(response);
                if (res == null) {
                    return;
                }

                // Update maps and tables on the JavaFX Application Thread
                Platform.runLater(() -> {
                    // Update the variable-values map for the variableTable
                    Map<String, Long> outMap = res.getVariableMap(); // or res.variableMap() if you use record accessors
                    variableValues.clear();
                    variableValues.putAll(outMap);

                    variableTable.refresh();

                    // Optional: update history table and other UI parts if present in result
                    /// I what to update history table every 2 seconds in the future
                });
            }
        });
    }


    @FXML
    void stepOverDebugListener(ActionEvent event) {

    }

    @FXML
    void stopDebugListener(ActionEvent event) {

    }



}
