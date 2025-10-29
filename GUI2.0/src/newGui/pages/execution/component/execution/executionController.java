package newGui.pages.execution.component.execution;

import Alerts.Alerts;
import dto.ProgramExecutionResult;
import dto.ProgramPeek;
import dto.debug.DebugEndResult;
import dto.debug.DebugStepPeek;
import dto.server.request.RunRequest;
import dto.server.response.ProgramData;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.util.converter.LongStringConverter;
import newGui.pages.execution.component.primary.mainExecutionController;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.valueOf;

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

    // Inputs Table
    @FXML private TableView<String> inputTable;
    @FXML private TableColumn<String, String> variableInput;
    @FXML private TableColumn<String, Long> valueInput;
    // Holds the values for ALL inputs variables shown in inputTable
    private final Map<String, Long> inputValues = new HashMap<>();


    // Variables State Table
    @FXML private TableView<String> variableTable;
    @FXML private TableColumn<String, String> variableState;@FXML
    private TableColumn<String, Long> valueState;
    // Holds the latest values for ALL variables (inputs + works) shown in variableTable
    private final Map<String, Long> variableValues = new HashMap<>();


    // History Table
    @FXML private TableView<ProgramExecutionResult> historyTable;
    @FXML private TableColumn<ProgramExecutionResult, Number> Index;
    @FXML private TableColumn<ProgramExecutionResult, Long> cycles;
    @FXML private TableColumn<ProgramExecutionResult, String> inputs;
    @FXML private TableColumn<ProgramExecutionResult, Integer> level;
    @FXML private TableColumn<ProgramExecutionResult, Long> output;

    @FXML private ComboBox<String> architectureSelection;
    @FXML private TextField CyclesCounter;

    private boolean debugModeActive = false;


    @FXML
    private void initialize() {
        // Initialization logic if needed
        resumeDebugButton.setDisable(true);
        stepOverDebugButton.setDisable(true);
        stopDebugButton.setDisable(true);
        architectureSelection.getItems().addAll("I", "II", "III", "IV");
        architectureSelection.setPromptText("Select architecture");
    }

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
        allVariables.add("y"); // output variable

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

    // General
    @FXML
    void backListener(ActionEvent event) {
        mainExecutionController.returnToDashboard();
    }


    // History
    @FXML
    void reRunListener(ActionEvent event) {
        // 1) get selected history row
        ProgramExecutionResult selected = historyTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return; // no selection, do nothing
        }

        // 2) extract past inputs
        List<Long> pastInputs = selected.getInputs();
        if (pastInputs == null || pastInputs.isEmpty()) {
            return;
        }

        // 3) apply inputs to inputValues map
        List<String> sortedNames = getSortedInputNames();
        int n = Math.min(sortedNames.size(), pastInputs.size());
        for (int i = 0; i < n; i++) {
            inputValues.put(sortedNames.get(i), pastInputs.get(i));
        }
        inputTable.refresh();

        // 4) reset variableValues map → all variables = 0
        variableValues.replaceAll((k, v) -> 0L);
        variableTable.refresh();

        // 5) clear cycles counter
        CyclesCounter.setText(0L + "");

        // optional UX cleanup
        inputTable.getSelectionModel().clearSelection();
        variableTable.getSelectionModel().clearSelection();
    }

    @FXML
    void showHistoryListener(ActionEvent event) {
        // Get selected history row
        ProgramExecutionResult selected = historyTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return; // No selection, do nothing
        }

        // Get sorted input variable names
        List<String> inputNames = getSortedInputNames();

        // Take past inputs from the selected execution
        List<Long> pastInputs = selected.getInputs();
        if (pastInputs == null) {
            pastInputs = Collections.emptyList();
        }

        // Build data for the new TableView
        ObservableList<Pair<String, Long>> rows = javafx.collections.FXCollections.observableArrayList();
        int n = Math.min(inputNames.size(), pastInputs.size());
        for (int i = 0; i < n; i++) {
            rows.add(new Pair<>(inputNames.get(i), pastInputs.get(i)));
        }
        rows.add(new Pair<>("y(Result)", selected.getOutputValue()));

        // Create the TableView
        TableView<Pair<String, Long>> table = new TableView<>(rows);
        TableColumn<Pair<String, Long>, String> varCol = new TableColumn<>("Variable");
        varCol.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getKey()));

        TableColumn<Pair<String, Long>, Long> valCol = new TableColumn<>("Value");
        valCol.setCellValueFactory(cd -> new ReadOnlyObjectWrapper<>(cd.getValue().getValue()));
        varCol.setStyle("-fx-alignment: CENTER;");
        valCol.setStyle("-fx-alignment: CENTER;");


        table.getColumns().addAll(varCol, valCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setMaxWidth(250);

        // Put the table in a layout (VBox)
        VBox root = new VBox(table);
        root.setAlignment(Pos.CENTER);
        root.setSpacing(10);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f9f9f9;");

        // Create and show the new stage
        Stage stage = new Stage();
        stage.setTitle("Inputs & Result");
        stage.initOwner(inputTable.getScene().getWindow());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root, 360, 420));

        // auto-center on screen
        stage.centerOnScreen();
        stage.show();
    }

    // Helper: get input variable names sorted by index (x1, x2, x10, ...)
    private List<String> getSortedInputNames() {
        return inputValues.keySet()
                .stream()
                .sorted(Comparator.comparing(s -> s.substring(1))) // assumes names like x1, x2 ...
                .collect(Collectors.toList());
    }


    // Debugger
    @FXML
    void startDebugListener(ActionEvent event) {
        String selectedArch = architectureSelection.getValue();
        if (selectedArch == null) {
            Alerts.architectureNotSelected();
            return;
        }
        int archNum = getArchitectureNumber(selectedArch);
        List<Integer> counts = mainExecutionController.getArchitecturesCount();
        for (int i = archNum + 1; i < counts.size(); i++) {
            Integer cnt = counts.get(i);
            if (cnt != null && cnt != 0) {
                Alerts.architectureDependencyAlert(selectedArch, i + 1);
                return;
            }
        }


        long cost = getAvgCost(mainExecutionController.getProgramName());
        if (cost > mainExecutionController.getCredits()) {
            Alerts.notEnoughCreditsAlert();
            return;
        }
        debugModeActive = true;
        resumeDebugButton.setDisable(false);
        stepOverDebugButton.setDisable(false);
        stopDebugButton.setDisable(false);

        Request runDebugRequest = requests.StartDebugRequest.build(
                new dto.server.request.StartDebugRequest(
                        mainExecutionController.getProgramName(),
                        mainExecutionController.getSelectedDgree(),
                        sortKeysBySubstring(inputValues),
                        mainExecutionController.getBreakpoints()
                )
        );

        HttpClientUtil.runAsync(runDebugRequest, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                requests.StartDebugRequest.onFailure(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                dto.server.response.DebugStateInfo debugStateInfo = requests.StartDebugRequest.onResponse(response);
                if (debugStateInfo.cycles() == null) {
                    Platform.runLater(() -> {
                                Alerts.noCreditsAlert();
                            });
                    endDebug(false);
                    return;
                }

                uptateCredits();

                // Update UI on the JavaFX Application Thread
                Platform.runLater(() -> {
                    mainExecutionController.getInstructionsController().updateHighlightedInstructions(List.of());


                    // Update variable-values map for variableTable
                    Map<String, Long> outMap = debugStateInfo.getVariableMap();
                    variableValues.clear();
                    variableValues.putAll(outMap);
                    variableTable.refresh();

                    if (debugStateInfo.getFinished()) {
                        // program finished without hitting a breakpoint
                        endDebug(true);
                    } else {
                        // stopped on a breakpoint
                        mainExecutionController.getInstructionsController().highlightLine(debugStateInfo.getStoppedOnLine());
                    }
                    // Update Cycles Counter
                    String valueOf = String.valueOf(debugStateInfo.getCycles());
                    CyclesCounter.setText(valueOf);

                    if (debugStateInfo.getNoCredits()) {
                        Alerts.noCreditsAlert();
                        endDebug(false);
                        return;
                    }
                });
            }
        });
    }

    private void endDebug(boolean toPrint) {
        debugModeActive = false;
        resumeDebugButton.setDisable(true);
        stepOverDebugButton.setDisable(true);
        stopDebugButton.setDisable(true);
        // clear highlighted lines
        mainExecutionController.getInstructionsController().updateHighlightedInstructions(List.of());

        // Update history table
        // requet for history table
        Request userHistoryRequest = requests.UserHistoryRequest.build(mainExecutionController.getProgramName(), mainExecutionController.userName);
        HttpClientUtil.runAsync(userHistoryRequest, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                requests.UserHistoryRequest.onFailure(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                requests.UserHistoryRequest.onResponse(response, executionController.this);
            }
        });
        if (toPrint) {
            Alerts.endOfDebug();
        }
    }

    @FXML
    void stepOverDebugListener(ActionEvent event) {
        Request stepOverDebugRequest = requests.StepOverDebugRequest.build();

        HttpClientUtil.runAsync(stepOverDebugRequest, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                requests.StepOverDebugRequest.onFailure(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                DebugStepPeek debugStepPeek = requests.StepOverDebugRequest.onResponse(response);
                if (debugStepPeek == null) {
                    return;
                }

                uptateCredits();

                // Update UI on the JavaFX Application Thread
                Platform.runLater(() -> {

                    mainExecutionController.getInstructionsController().updateHighlightedInstructions(List.of());
                    // Highlight next line
                    mainExecutionController.getInstructionsController().updateHighlightedInstructions(List.of(debugStepPeek.nextLine()));

                    // Update variable-value map for variableTable // only one variable changed
                    if (debugStepPeek.variable().isPresent()) {
                        String varName = debugStepPeek.variable().get();
                        Long newValue = debugStepPeek.newValue();

                        variableValues.put(varName, newValue);
                        variableTable.refresh();


                    }
                    if (debugStepPeek.isFailed()) {
                        // program finished
                        Alerts.noCreditsAlert();
                        endDebug(false);
                        return;
                    }
                });
            }
        });


    }

    @FXML
    void resumeDebugListener(ActionEvent event) {
        Request resumeDebugRequest = requests.ResumeDebugRequest.build();

        HttpClientUtil.runAsync(resumeDebugRequest, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                requests.ResumeDebugRequest.onFailure(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                dto.server.response.DebugStateInfo debugStateInfo = requests.ResumeDebugRequest.onResponse(response);
                if (debugStateInfo == null) {
                    return;
                }

                uptateCredits();
                // Update UI on the JavaFX Application Thread
                Platform.runLater(() -> {
                    // Update variable-values map for variableTable
                    Map<String, Long> outMap = debugStateInfo.getVariableMap();
                    variableValues.clear();
                    variableValues.putAll(outMap);
                    variableTable.refresh();

                    if (debugStateInfo.getFinished()) {
                        // program finished without hitting a breakpoint
                        endDebug(true);
                    } else {
                        // stopped on a breakpoint
                        mainExecutionController.getInstructionsController().updateHighlightedInstructions(List.of());
                        mainExecutionController.getInstructionsController().highlightLine(debugStateInfo.getStoppedOnLine());
                    }
                    // Update Cycles Counter
                    String valueOf = String.valueOf(debugStateInfo.getCycles());
                    CyclesCounter.setText(valueOf);
                    if(debugStateInfo.getNoCredits()) {
                        Alerts.noCreditsAlert();
                        endDebug(false);
                    }
                });
            }
        });
    }

    @FXML
    void stopDebugListener(ActionEvent event) {
        Request stopDebugRequest = requests.StopDebugRequest.build();

        HttpClientUtil.runAsync(stopDebugRequest, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                requests.StopDebugRequest.onFailure(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                DebugEndResult debugEndResult = requests.StopDebugRequest.onResponse(response);
                if (debugEndResult == null) {
                    return;
                }

                uptateCredits();


                // Update UI on the JavaFX Application Thread
                Platform.runLater(() -> {
                    // Update variable-values map for variableTable
                    Map<String, Long> outMap = debugEndResult.getVariableMap();
                    outMap.put("y", debugEndResult.getOutput());
                    variableValues.clear();
                    variableValues.putAll(outMap);
                    variableTable.refresh();

                    // Update Cycles Counter
                    CyclesCounter.setText(valueOf(debugEndResult.getCycles()));
                    endDebug(true);
                });
            }
        });
    }

    private void uptateCredits() {
        Request creditsRequest = requests.GetCreditsRequest.build();

        HttpClientUtil.runAsync(creditsRequest, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                requests.GetCreditsRequest.onFailure(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                long credits = requests.GetCreditsRequest.onResponse(response);
                Platform.runLater(() -> {
                    mainExecutionController.setCredits(credits);
                });
            }
        });
    }

    private int getArchitectureNumber(String architecture) {
        switch (architecture) {
            case "I":
                return 0;
            case "II":
                return 1;
            case "III":
                return 2;
            case "IV":
                return 3;
            default:
                return -1; // or throw an exception
        }
    }


    // Run
    @FXML
    void startListener(ActionEvent event) {
        if (debugModeActive) {
            Alerts.debugModeActiveAlert();
            return;
        }
        String selectedArch = architectureSelection.getValue();
        if (selectedArch == null) {
            Platform.runLater(() -> {
                Alerts.architectureNotSelected();
            });
            return;
        }
        int archNum = getArchitectureNumber(selectedArch);

        List<Integer> counts = mainExecutionController.getArchitecturesCount();
        for (int i = archNum + 1; i < counts.size(); i++) {
            Integer cnt = counts.get(i);
            if (cnt != null && cnt != 0) {
                int finalI = i;
                Platform.runLater(() -> {
                    Alerts.architectureDependencyAlert(selectedArch, finalI + 1);
                });
                return;
            }
        }

        long cost = getAvgCost(mainExecutionController.getProgramName());
        if (cost > mainExecutionController.getCredits()) {
            Alerts.notEnoughCreditsAlert();
            return;
        }






        List<Long> inputs = sortKeysBySubstring(inputValues);
        int extensionDegree = mainExecutionController.getSelectedDgree();
        String programName = mainExecutionController.getProgramName();

        Request runRequest = requests.RunRequest.build(new RunRequest(programName, extensionDegree, inputs));

        HttpClientUtil.runAsync(runRequest, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
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
                    if(res.isEndedEarly()){
                        Alerts.noCreditsAlert();
                    } else {
                        // Update the variable-values map for the variableTable
                        Map<String, Long> outMap = res.getVariableMap();// or res.variableMap() if you use record accessors
                        long result = res.getOutputValue();// or res.outputValue() if you use record accessors
                        outMap.put("y", result); // add the output value with key "y"
                        variableValues.clear();
                        variableValues.putAll(outMap);
                        variableTable.refresh();

                        // Update the Cycles Counter
                        CyclesCounter.setText(valueOf(res.getCycles()));

                        uptateCredits();

                        // Update history table
                        // requet for history table
                        Request userHistoryRequest = requests.UserHistoryRequest.build(mainExecutionController.getProgramName(), mainExecutionController.userName);
                        HttpClientUtil.runAsync(userHistoryRequest, new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                requests.UserHistoryRequest.onFailure(e);
                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) {
                                requests.UserHistoryRequest.onResponse(response, executionController.this);
                            }
                        });
                    }

                });
            }
        });
    }

    private long getAvgCost(String programName) {
        final long[] cost = new long[1];
        final ProgramData[] moreData = new ProgramData[1];
        Request programDataRequest = requests.ProgramInfoRequest.build(programName);
        try {
            Response response = HttpClientUtil.runSync(programDataRequest);
            String responseBody;
            try {
                responseBody = response.body().string();
                if (response.code() != 200) {
                    Platform.runLater(() -> {
                        Alerts.loadField(responseBody);
                    });
                } else {
                    moreData[0] = Constants.GSON_INSTANCE.fromJson(responseBody, ProgramData.class);
                }
            } catch (IOException e) {
                Platform.runLater(() -> {
                    Alerts.badBody(e.getMessage());
                });
            }
        } catch (IOException e) {
        }
        if (moreData[0] == null) {
            return 0;
        }
        cost[0] = moreData[0].getAvgCreditCost();
        return cost[0];
    }

    public void updateHistoryTable(List<ProgramExecutionResult> results) {
        // Bind columns to ProgramExecutionResult fields
        // Index column - dynamic numbering without using model field
        Index.setCellFactory(col -> new TableCell<ProgramExecutionResult, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });
        Index.setSortable(false);
        cycles.setCellValueFactory(param ->
                new ReadOnlyObjectWrapper<>(param.getValue().getCycles())
        );
        inputs.setCellValueFactory(param ->
                new ReadOnlyObjectWrapper<>(param.getValue().getInputs().toString())
        );
        level.setCellValueFactory(param ->
                new ReadOnlyObjectWrapper<>(param.getValue().getExpansionDegree())
        );
        output.setCellValueFactory(param ->
                new ReadOnlyObjectWrapper<>(param.getValue().getOutputValue())
        );
        // Clear existing rows
        historyTable.getItems().clear();

        // Add new rows
        historyTable.getItems().addAll(results);
    }

    public boolean debudgModeActive() {
        return debugModeActive;
    }


    @FXML
    void architectureSelectionListener(ActionEvent event) {
        String selectedArchitecture = architectureSelection.getValue();
        mainExecutionController.setSelectedArchitecture(selectedArchitecture);
    }

}