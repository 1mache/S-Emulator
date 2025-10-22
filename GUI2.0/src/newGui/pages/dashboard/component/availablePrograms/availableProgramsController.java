package newGui.pages.dashboard.component.availablePrograms;

import dto.server.response.ProgramData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import newGui.pages.dashboard.component.primary.dashboardController;
import java.util.List;

public class availableProgramsController {

    private dashboardController dashboardController;

    // Programs Table
    @FXML private TableView<ProgramData> programsTable;
    @FXML private TableColumn<ProgramData, String> uploadBy;
    @FXML private TableColumn<ProgramData, String> name;
    @FXML private TableColumn<ProgramData, Integer> maxLevel;
    @FXML private TableColumn<ProgramData, Integer> numberOfInstructions;
    @FXML private TableColumn<ProgramData, Integer> runs;
    @FXML private TableColumn<ProgramData, Long> averageCreditCost;

    @FXML
    private void initialize() {
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        uploadBy.setCellValueFactory(new PropertyValueFactory<>("uploadedBy"));
        numberOfInstructions.setCellValueFactory(new PropertyValueFactory<>("instructionCount"));
        maxLevel.setCellValueFactory(new PropertyValueFactory<>("maxExpansionDegree"));
        runs.setCellValueFactory(new PropertyValueFactory<>("runCount"));
        averageCreditCost.setCellValueFactory(new PropertyValueFactory<>("avgCreditCost"));
    }

    public void setDashboardController(dashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @FXML
    void executeProgramListener(ActionEvent event) {
        ProgramData selectedProgram = programsTable.getSelectionModel().getSelectedItem();
        if (selectedProgram != null) {
            dashboardController.loadExecutionPage(selectedProgram.getName(),dashboardController.getCurrentUserName());
        }
    }

    public void updateProgramList(List<ProgramData> funcList) {
        List<ProgramData> filtered = funcList.stream()
                .filter(p -> p.isMain())
                .toList();

        // Create an observable list for the TableView
        ObservableList<ProgramData> observableList = FXCollections.observableArrayList(filtered);

        // Set up the column bindings (only once)
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        uploadBy.setCellValueFactory(new PropertyValueFactory<>("uploadedBy"));
        numberOfInstructions.setCellValueFactory(new PropertyValueFactory<>("instructionCount"));
        maxLevel.setCellValueFactory(new PropertyValueFactory<>("maxExpansionDegree"));
        runs.setCellValueFactory(new PropertyValueFactory<>("runCount"));
        averageCreditCost.setCellValueFactory(new PropertyValueFactory<>("avgCreditCost"));

        // Attach the data to the table
        programsTable.setItems(observableList);
    }


    public void setPrograms(List<ProgramData> data) {
        List<ProgramData> filteredList = data.stream()
                .filter(p -> !p.isMain())
                .toList();
        programsTable.setItems(FXCollections.observableArrayList(filteredList));
    }
}