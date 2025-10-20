package newGui.pages.primary;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import newGui.pages.dashboard.component.primary.dashboardController;
import newGui.pages.execution.component.primary.mainExecutionController;
import newGui.pages.login.component.login.loginController;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import requests.FunctionListRequest;
import requests.ProgramInfoForRun;
import requests.UsersInfoListRequest;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.net.URL;

import static util.Constants.*;

public class mainClientAppController {

    @FXML private Parent httpStatusComponent;

    // Login
    private GridPane loginComponent;
    private loginController logicController;

    // Dashboard
    private ScrollPane dashboradComponent;
    private dashboardController dashboardController;

    // Execution
     private ScrollPane executionComponent;
     private mainExecutionController executionController;

    @FXML private Label userGreetingLabel;
    @FXML private AnchorPane mainPanel;

    private final StringProperty currentUserName;

    public mainClientAppController() {
        currentUserName = new SimpleStringProperty(START_NAME);
    }

    @FXML
    public void initialize() {
        userGreetingLabel.textProperty().bind(Bindings.concat("Hello ", currentUserName));

        // prepare components
        loadLoginPage();
    }

    public StringProperty getUserNameProperty() {
        return currentUserName;
    }
    public void updateUserName(String userName) {
        currentUserName.set(userName);
    }

    private void setMainPanelTo(Parent pane) {
        mainPanel.getChildren().clear();
        mainPanel.getChildren().add(pane);
        AnchorPane.setBottomAnchor(pane, 0.0);
        AnchorPane.setTopAnchor(pane, 0.0);
        AnchorPane.setLeftAnchor(pane, 0.0);
        AnchorPane.setRightAnchor(pane, 0.0);
    }


    // Login Page
    private void loadLoginPage() {
        URL loginPageUrl = getClass().getResource(LOGIN_PAGE_FXML_RESOURCE_LOCATION);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(loginPageUrl);
            loginComponent = fxmlLoader.load();
            loadLoginStyles(loginComponent);
            logicController = fxmlLoader.getController();
            logicController.setMainClientAppController(this);
            setMainPanelTo(loginComponent);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadLoginStyles(Parent scene){

        var stylesUrl = getClass().getResource(LOGIN_PAGE_STYLE_RESOURCE_LOCATION);
        // Add the stylesheet to the scene if found`
        if (stylesUrl != null) {
            scene.getStylesheets().add(stylesUrl.toExternalForm());
        } else {
            System.out.println("Unable to locate " + LOGIN_PAGE_STYLE_RESOURCE_LOCATION);
        }
    }

    // Dashboard Page
    public void switchToDashboard() {
        loadDashboardPage();

        Request functionsRequest = FunctionListRequest.build();
        HttpClientUtil.runAsync(functionsRequest, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                FunctionListRequest.onFailure(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                FunctionListRequest.onResponse(response, dashboardController);
            }
        });

        Request usersRequest = UsersInfoListRequest.build();
        HttpClientUtil.runAsync(usersRequest, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                UsersInfoListRequest.onFailure(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                UsersInfoListRequest.onResponse(response, dashboardController);
            }
        });
    }

    private void loadDashboardPage() {
        URL dashboardPageUrl = getClass().getResource(DASHBOARD_PAGE_FXML_RESOURCE_LOCATION);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(dashboardPageUrl);
            dashboradComponent = fxmlLoader.load();
            loadDashboardStyles(dashboradComponent);
            dashboardController = fxmlLoader.getController();
            dashboardController.setMainClientAppController(this);
            dashboardController.activate();

            // Get the current stage
            Stage stage = (Stage) mainPanel.getScene().getWindow();
            stage.setMinHeight(800);
            stage.setMinWidth(1000);
            stage.setTitle("S-Emulator – Dashboard");

            Scene scene = stage.getScene();
            scene.setRoot(dashboradComponent);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadDashboardStyles(Parent scene){

        var stylesUrl = getClass().getResource(DASHBOARD_PAGE_STYLE_RESOURCE_LOCATION);
        // Add the stylesheet to the scene if found`
        if (stylesUrl != null) {
            scene.getStylesheets().add(stylesUrl.toExternalForm());
        } else {
            System.out.println("Unable to locate " + DASHBOARD_PAGE_STYLE_RESOURCE_LOCATION);
        }
    }

    // Execution Page
    public void switchToExecution(String programName) {
        loadExecutionPage();

        Request programRequest = ProgramInfoForRun.build(programName,0);
        HttpClientUtil.runAsync(programRequest, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                ProgramInfoForRun.onFailure(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ProgramInfoForRun.onResponse(response,executionController);
            }
        });


    }

    private void loadExecutionPage() {
        URL executionPageUrl = getClass().getResource(EXECUTION_PAGE_FXML_RESOURCE_LOCATION);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(executionPageUrl);
            executionComponent = fxmlLoader.load();
            loadExecutionStyles(executionComponent);
            executionController = fxmlLoader.getController();
            executionController.setMainClientAppController(this);
            executionController.activate();

            // Get the current stage
            Stage stage = (Stage) mainPanel.getScene().getWindow();
            stage.setMinHeight(800);
            stage.setMinWidth(1000);
            stage.setTitle("S-Emulator – Execution");

            Scene scene = stage.getScene();
            scene.setRoot(executionComponent);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadExecutionStyles(Parent scene){

        var stylesUrl = getClass().getResource(EXECUTION_PAGE_STYLE_RESOURCE_LOCATION);
        // Add the stylesheet to the scene if found`
        if (stylesUrl != null) {
            scene.getStylesheets().add(stylesUrl.toExternalForm());
        } else {
            System.out.println("Unable to locate " + EXECUTION_PAGE_STYLE_RESOURCE_LOCATION);
        }
    }

}