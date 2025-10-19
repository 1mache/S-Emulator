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
import newGui.pages.login.component.login.loginController;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import requests.functionListRequest;
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

    public void switchToDashboard() {
        loadDashboardPage();

//        Request request = functionListRequest.build();
//
//        HttpClientUtil.runAsync(request, new Callback() {
//
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                Platform.runLater(() ->
//                        errorMessageProperty.set("Something went wrong: " + e.getMessage())
//                );
//            }
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                if (response.code() != 200) {
//                    String responseBody = response.body().string();
//                    Platform.runLater(() ->
//                            errorMessageProperty.set("Something went wrong " + responseBody)
//                    );
//                } else {
//                    Platform.runLater(() -> {
//                        mainClientAppController.updateUserName(userName);
//                        mainClientAppController.switchToDashboard();
//                    });
//                }
//            }
//        });
    }
}