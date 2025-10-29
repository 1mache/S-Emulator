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
        int rowCount = historyTable.getItems().size();
        if (architectures.size() != rowCount || isMain.size() != rowCount) {
            System.err.println("Mismatch between table size and provided lists!");
            return;
        }

        // לא רוצים שמיון של העמודות האלו יבלגן התאמה לפי אינדקס
        architecture.setSortable(false);
        FunctionOrProgram.setSortable(false);

        // עמודת ארכיטקטורה – מציגים לפי האינדקס של השורה בתצוגה
        architecture.setCellFactory(col -> new TableCell<ProgramExecutionResult, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    return;
                }
                int idx = getIndex();
                if (idx >= 0 && idx < architectures.size()) {
                    setText(architectures.get(idx));
                } else {
                    setText("");
                }
            }
        });

        // עמודת פונקציה/תכנית – מציגים "true"/"false" לפי isMain
        FunctionOrProgram.setCellFactory(col -> new TableCell<ProgramExecutionResult, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    return;
                }
                int idx = getIndex();
                if (idx >= 0 && idx < isMain.size()) {
                    setText(String.valueOf(isMain.get(idx))); // "true" or "false"
                } else {
                    setText("");
                }
            }
        });

        // רענון התצוגה
        historyTable.refresh();
    }

}
