package newGui.pages.dashboard.component.availableFunctions;

import dto.server.response.ProgramData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import newGui.pages.dashboard.component.primary.dashboardController;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class availableFunctionsController {

    private dashboardController dashboardController;

    // Functions Table
    @FXML private TableView<ProgramData> programsTable;
    @FXML private TableColumn<ProgramData, String> uploadBy;
    @FXML private TableColumn<ProgramData, String> name;
    @FXML private TableColumn<ProgramData, Integer> maxLevel;
    @FXML private TableColumn<ProgramData, Integer> numberOfInstructions;
    @FXML private TableColumn<ProgramData, Integer> runs;
    @FXML private TableColumn<ProgramData, Long> averageCreditCost;

    // Buttons
    @FXML private Button executeProgram;

    public void setDashboardController(dashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @FXML
    void executeProgramListener(ActionEvent event) {
        ProgramData selectedProgram = programsTable.getSelectionModel().getSelectedItem();
        if (selectedProgram != null) {
            dashboardController.loadExecutionPage(selectedProgram.getName());
        }
    }

    public void updateFunctionList(List<ProgramData> funcList) {
        List<ProgramData> filtered = funcList.stream()
                .filter(p -> !p.isMain())
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

}

