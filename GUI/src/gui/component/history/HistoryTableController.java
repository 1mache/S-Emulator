package gui.component.history;

import dto.ProgramExecutionResult;
import gui.component.variable.table.VariableTableController;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

public class HistoryTableController implements Initializable {

    @FXML
    private VariableTableController variableTableController;

    @FXML
    private TableView<ProgramExecutionResult> historyTable;

    @FXML
    private TableColumn<ProgramExecutionResult, Integer> numberColumn;

    @FXML
    private TableColumn<ProgramExecutionResult, Integer> expansionColumn;

    @FXML
    private TableColumn<ProgramExecutionResult, String> inputsColumn;

    @FXML
    private TableColumn<ProgramExecutionResult, Long> resultColumn;

    @FXML
    private TableColumn<ProgramExecutionResult, Long> cyclesColumn;

    @FXML
    private Button showButton;

    @FXML
    private Button reRunButton;

    private final ObservableList<ProgramExecutionResult> executionResults = FXCollections.observableArrayList();

    private ProgramExecutionResult selectedLine;

    private final Set<EventHandler<ActionEvent>> reRunButtonListeners = new HashSet<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        historyTable.setPlaceholder(new Label("No history to display"));

        historyTable.setRowFactory(
                tv -> {
                    TableRow<ProgramExecutionResult> row = new TableRow<>();
                    // clicking on a row writes it as selected
                    row.setOnMouseClicked(event -> {
                        if (!row.isEmpty()) {
                            selectedLine = row.getItem();
                        }
                        onLineSelected();
                    });
                    return row;
                }
        );

        initHistoryTableColumns();

        variableTableController.clear();

    }

    @FXML
    public void reRunAction(ActionEvent event) {
        fireReRunButtonPressed(event);
    }

    @FXML
    public void showVariablesAction(ActionEvent event) {
        if(selectedLine == null) return;
        variableTableController.setVariableEntries(selectedLine.variableMap());
    }

    public ProgramExecutionResult getSelectedLine() {
        return selectedLine;
    }

    public void setItems(List<ProgramExecutionResult> resultList){
        executionResults.addAll(resultList);
        historyTable.setItems(executionResults);
    }

    public void addReRunButtonListener(EventHandler<ActionEvent> handler) {
        reRunButtonListeners.add(handler);
    }

    // =============== private: ================
    private void initHistoryTableColumns() {
        numberColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(
                        historyTable.getItems().indexOf(cellData.getValue()) + 1
                )
        );

        expansionColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(
                        cellData.getValue().expansionDegree()
                )
        );

        inputsColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(
                            cellData.getValue().inputs().stream()
                                    .map(String::valueOf)
                                    .collect(Collectors.joining(","))
                )
        );

        resultColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(
                        cellData.getValue().outputValue()
                )
        );

        cyclesColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(
                        cellData.getValue().cycles()
                )
        );
    }

    private void onLineSelected() {
        if(selectedLine != null) {
            showButton.setDisable(false);
            reRunButton.setDisable(false);
        }
        else{
            showButton.setDisable(true);
            reRunButton.setDisable(true);
        }
    }

    private void fireReRunButtonPressed(ActionEvent event){
        for (var listener: reRunButtonListeners)
            listener.handle(event);
    }
}