package newGui.pages.dashboard.component.primary;


import Refreshers.Dashboard.ProgramsRefresher;
import Refreshers.Dashboard.UsersRefresher;
import dto.ProgramExecutionResult;
import dto.server.response.ProgramData;
import dto.server.response.UserData;
import javafx.application.Platform;
import javafx.fxml.FXML;
import newGui.pages.dashboard.component.availableFunctions.availableFunctionsController;
import newGui.pages.dashboard.component.availablePrograms.availableProgramsController;
import newGui.pages.dashboard.component.history.historyTableController;
import newGui.pages.dashboard.component.top.topController;
import newGui.pages.dashboard.component.usersInfo.usersTableInfoController;
import newGui.pages.primary.mainClientAppController;
import okhttp3.Request;
import requests.ProgramInfoRequest;
import util.Constants;
import util.http.HttpClientUtil;

import java.util.ArrayList;
import java.util.List;

public class dashboardController {

    private mainClientAppController mainClientAppController;

    // top controller
    @FXML private topController topController;

    // right controllers
    @FXML private availableProgramsController availableProgramsController; // top right
    @FXML private availableFunctionsController availableFunctionsController; // bottom right

    private ProgramsRefresher programsRefresher;
    private UsersRefresher usersRefresher;


    // left controllers
    @FXML private usersTableInfoController userTableInfoController; // top left
    @FXML private historyTableController historyTableController; // bottom left

    @FXML
    public void initialize() {
        topController.setDashboardController(this);
        availableProgramsController.setDashboardController(this);
        availableFunctionsController.setDashboardController(this);
        userTableInfoController.setDashboardController(this);
        historyTableController.setDashboardController(this);
    }

    public void setMainClientAppController(mainClientAppController mainAppController) {
        this.mainClientAppController = mainAppController;
    }

    public void activate() {
        topController.init(mainClientAppController.getUserNameProperty());
        setActive();
    }

    public void setActive() {
        if (programsRefresher == null) {
            programsRefresher = new ProgramsRefresher(
                    availableProgramsController,
                    availableFunctionsController,
                    this,
                    Constants.REFRESH_RATE
            );
        }
        programsRefresher.start();

        if (usersRefresher == null) {
            usersRefresher = new UsersRefresher(
                    userTableInfoController,
                    this,
                    Constants.REFRESH_RATE
            );
        }
        usersRefresher.start();
    }


    public void setInActive() {
        if (programsRefresher != null) {
            programsRefresher.stop();
        }
        if (usersRefresher != null) {
            usersRefresher.stop();
        }
    }


    public void updateAllProgramData(List<ProgramData> list) {
        Platform.runLater(() -> {
            if (availableFunctionsController != null) {
                availableFunctionsController.setFunctions(list);
            }
            if (availableProgramsController != null) {
                availableProgramsController.setPrograms(list);
            }
        });
    }

    public void updateUsersList(List<UserData> usersDataList) {
        Platform.runLater(() -> {
            userTableInfoController.updateUsersList(usersDataList);
        });

    }

    public void clearUserInfo() {
        historyTableController.clearHistoryTable();
    }

    public void loadExecutionPage(String name, String currentUserName) {
        setInActive();
        mainClientAppController.switchToExecution(name, currentUserName);
    }

    public long getCredits() {
        return topController.getCredits();
    }

    public void updateCredits(long credits) {
        Platform.runLater(() -> {
            topController.updateCredits(credits);
        });
    }

    public String getCurrentUserName() {
        return mainClientAppController.getUserNameProperty().get();
    }

    public void updateHistoryTable(List<ProgramExecutionResult> historyUsersDataList) {
        List<String> arcitectures = new ArrayList<>();
        List<Boolean> isMain = new ArrayList<>();
        for (ProgramExecutionResult result : historyUsersDataList) {
            String arch = result.getProgramName();
            Request programDataRequest = ProgramInfoRequest.build(arch);
            HttpClientUtil.runAsync(programDataRequest, new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, java.io.IOException e) {
                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws java.io.IOException {
                    ProgramData programData = ProgramInfoRequest.onResponse(response);
                    arcitectures.add(programData.getArchitecture());
                    isMain.add(programData.getIsMain());
                }
            });

        }
        historyTableController.updateHistoryTable(historyUsersDataList);
        historyTableController.updateHistoryTableArciAndMain(arcitectures, isMain);
    }
}