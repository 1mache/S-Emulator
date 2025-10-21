package newGui.pages.dashboard.component.availablePrograms;

import dto.server.response.ProgramData;
import dto.server.response.UserData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import newGui.pages.dashboard.component.primary.dashboardController;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import requests.ProgramInfoForRun;
import requests.ProgramInfoRequest;
import util.http.HttpClientUtil;

import java.io.IOException;
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

    // Buttons
    @FXML
    private Button executeProgram;

    public void setDashboardController(dashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @FXML
    void executeProgramListener(ActionEvent event) {
        ProgramData selected = programsTable.getSelectionModel().getSelectedItem();
        Request programRequest = ProgramInfoForRun.build(selected.getName(), 0);
        HttpClientUtil.runAsync(programRequest, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                ProgramInfoForRun.onFailure(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ProgramInfoForRun.onResponse(response);
            }
        });
    }




    public void updateFunctionList(List<ProgramData> funcList) {
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
}
