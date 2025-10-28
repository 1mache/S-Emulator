package newGui.pages.dashboard.component.history;

import dto.ProgramExecutionResult;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import newGui.pages.dashboard.component.primary.dashboardController;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class historyTableController {

    private dashboardController dashboardController;

    // History Table
    @FXML private TableView<ProgramExecutionResult> historyTable;
    @FXML private TableColumn<ProgramExecutionResult, String> architecture;
    @FXML private TableColumn<ProgramExecutionResult, String> FunctionOrProgram;
    @FXML private TableColumn<ProgramExecutionResult, Number> number;
    @FXML private TableColumn<ProgramExecutionResult, Long> cycels;
    @FXML private TableColumn<ProgramExecutionResult, Integer> level;
    @FXML private TableColumn<ProgramExecutionResult, String> name;
    @FXML private TableColumn<ProgramExecutionResult, Long> result;

    public void setDashboardController(dashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    public void clearHistoryTable() {
        historyTable.getItems().clear();
    }

    public void updateHistoryTable(List<ProgramExecutionResult> historyUsersDataList) {
        // Index column (number) - dynamic row numbering
        number.setCellFactory(col -> new TableCell<ProgramExecutionResult, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });
        number.setSortable(false);

        // Bind other columns directly from the ProgramExecutionResult record
        cycels.setCellValueFactory(param ->
                new ReadOnlyObjectWrapper<>(param.getValue().getCycles())
        );
        level.setCellValueFactory(param ->
                new ReadOnlyObjectWrapper<>(param.getValue().getExpansionDegree())
        );
        name.setCellValueFactory(param ->
                new ReadOnlyObjectWrapper<>(param.getValue().getProgramName())
        );
        result.setCellValueFactory(param ->
                new ReadOnlyObjectWrapper<>(param.getValue().getOutputValue())
        );
//        // architecture column — not provided in the record, so we leave it empty or custom:
//        architecture.setCellValueFactory(param ->
//                new ReadOnlyObjectWrapper<>("N/A") // you can change this to any relevant value
//        );

        // Refresh the table content
        historyTable.getItems().clear();
        historyTable.getItems().addAll(historyUsersDataList);
        historyTable.refresh();

    }



    public void updateHistoryTableArciAndMain(List<String> architectures, List<Boolean> isMain) {
        // Ensure lists match the table size
        int rowCount = historyTable.getItems().size();
        if (architectures.size() != rowCount || isMain.size() != rowCount) {
            System.err.println("Mismatch between table size and provided lists!");
            return;
        }

        // Architecture column (string from architectures list)
        architecture.setCellValueFactory(param -> {
            int index = historyTable.getItems().indexOf(param.getValue());
            if (index >= 0 && index < architectures.size()) {
                return new ReadOnlyObjectWrapper<>(architectures.get(index));
            }
            return new ReadOnlyObjectWrapper<>("");
        });

        // FunctionOrProgram column — "true" or "false"
        FunctionOrProgram.setCellValueFactory(param -> {
            int index = historyTable.getItems().indexOf(param.getValue());
            if (index >= 0 && index < isMain.size()) {
                return new ReadOnlyObjectWrapper<>(String.valueOf(isMain.get(index)));
            }
            return new ReadOnlyObjectWrapper<>("");
        });

        // Refresh to apply updates
        historyTable.refresh();
    }

}
