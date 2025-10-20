package newGui.pages.dashboard.component.usersInfo;


import dto.server.response.UserData;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import newGui.pages.dashboard.component.primary.dashboardController;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import requests.LoadCreditRequest;
import requests.UserInfoRequest;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.List;

public class usersTableInfoController {

    private dashboardController dashboardController;


    // Won't Use
    @FXML private Label usersInformation;

    // Buttons
    @FXML private Button unselectButton;

    // Users Table
    @FXML private TableView<UserData> usersTable;
    @FXML private TableColumn<UserData, String> name;
    @FXML private TableColumn<UserData, Integer> numberOfNewFunctions;
    @FXML private TableColumn<UserData, Integer> numberOfNewPrograms;
    @FXML private TableColumn<UserData, Integer> runs;
    @FXML private TableColumn<UserData, Long> usedCredit;
    @FXML private TableColumn<UserData, Long> currentCredit;

    public void setDashboardController(dashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @FXML void UnselectButtonListener(ActionEvent event) {
        dashboardController.clearUserInfo();
    }


    public void updateUsersList(List<UserData> usersDataList) {
        // Create an observable list for the TableView
        ObservableList<UserData> observableList = FXCollections.observableArrayList(usersDataList);

        // Set up the column bindings (only once)
        name.setCellValueFactory(new PropertyValueFactory<>("username"));
        numberOfNewFunctions.setCellValueFactory(new PropertyValueFactory<>("functionsUploaded"));
        numberOfNewPrograms.setCellValueFactory(new PropertyValueFactory<>("programsUploaded"));
        runs.setCellValueFactory(new PropertyValueFactory<>("runCount"));
        usedCredit.setCellValueFactory(new PropertyValueFactory<>("usedCredits"));
        currentCredit.setCellValueFactory(new PropertyValueFactory<>("totalCredits"));

        // Attach the data to the table
        usersTable.setItems(observableList);
    }

    @FXML
    void onRowClicked(MouseEvent event) {
        UserData selected = usersTable.getSelectionModel().getSelectedItem();

        Request userInfoRequest = UserInfoRequest.build(selected.getUsername());
        HttpClientUtil.runAsync(userInfoRequest, new Callback()  {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                UserInfoRequest.onFailure(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                UserInfoRequest.onResponse(response,dashboardController);
            }
        });

    }
}

