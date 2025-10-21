package newGui.pages.primary;

import javafx.application.Platform;
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
import requests.ProgramListRequest;
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
    private Stage primaryStage;

    public mainClientAppController() {
        currentUserName = new SimpleStringProperty(START_NAME);
    }

    @FXML
    public void initialize() {
        userGreetingLabel.textProperty().bind(Bindings.concat("Hello ", currentUserName));

        // prepare components
        loadLoginPage();

        // Store the primary stage reference
        Platform.runLater(() -> {
            if (mainPanel.getScene() != null) {
                primaryStage = (Stage) mainPanel.getScene().getWindow();
            }
        });
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

        Request functionsRequest = ProgramListRequest.build();
        HttpClientUtil.runAsync(functionsRequest, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                ProgramListRequest.onFailure(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ProgramListRequest.onResponse(response, dashboardController);
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

            // Save the primary stage reference if not already saved
            if (primaryStage == null) {
                primaryStage = (Stage) mainPanel.getScene().getWindow();
            }

            // Set minimum size and title
            primaryStage.setMinHeight(800);
            primaryStage.setMinWidth(1000);
            primaryStage.setTitle("S-Emulator – Dashboard");

            // Change the root of the Scene
            Scene scene = primaryStage.getScene();
            scene.setRoot(dashboradComponent);

            // Ensure the component resizes with the window
            dashboradComponent.prefWidthProperty().bind(scene.widthProperty());
            dashboradComponent.prefHeightProperty().bind(scene.heightProperty());

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
        loadExecutionPage(programName);
    }

    private void loadExecutionPage(String programName) {
        URL executionPageUrl = getClass().getResource(EXECUTION_PAGE_FXML_RESOURCE_LOCATION);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(executionPageUrl);
            executionComponent = fxmlLoader.load();
            loadExecutionStyles(executionComponent);
            executionController = fxmlLoader.getController();
            executionController.setMainClientAppController(this);
            executionController.activate(dashboardController.getCredits());

            // Call set with program name
            executionController.set(programName);

            // Use the existing primary stage reference
            primaryStage.setMinHeight(800);
            primaryStage.setMinWidth(1000);
            primaryStage.setTitle("S-Emulator – Execution");

            // Change the root of the Scene
            Scene scene = primaryStage.getScene();
            scene.setRoot(executionComponent);

            // Ensure the component resizes with the window
            executionComponent.prefWidthProperty().bind(scene.widthProperty());
            executionComponent.prefHeightProperty().bind(scene.heightProperty());

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



    public void returnToDashboard(int credits) {
        // Update credits in dashboard controller
        /// need to reload the dashboard data with the name and credits
//        // טען מחדש את ה-Dashboard
//        loadDashboardPage();
//
//        // בצע בקשות לעדכון מידע
//        Request functionsRequest = ProgramListRequest.build();
//        HttpClientUtil.runAsync(functionsRequest, new Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                ProgramListRequest.onFailure(e);
//            }
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                ProgramListRequest.onResponse(response, dashboardController);
//            }
//        });
//
//        Request usersRequest = UsersInfoListRequest.build();
//        HttpClientUtil.runAsync(usersRequest, new Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                UsersInfoListRequest.onFailure(e);
//            }
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                UsersInfoListRequest.onResponse(response, dashboardController);
//            }
//        });
    }
}