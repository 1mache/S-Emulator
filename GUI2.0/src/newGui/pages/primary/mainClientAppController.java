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
import newGui.pages.dashboard.component.primary.dashboardController;
import newGui.pages.login.component.login.loginController;

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

    private void loadLoginPage() {
        URL loginPageUrl = getClass().getResource(LOGIN_PAGE_FXML_RESOURCE_LOCATION);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(loginPageUrl);
            loginComponent = fxmlLoader.load();
            logicController = fxmlLoader.getController();
            logicController.setMainClientAppController(this);
            loadLoginStyles(loginComponent.getScene());
            setMainPanelTo(loginComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadLoginStyles(Scene scene){

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
            dashboardController = fxmlLoader.getController();
            dashboardController.setMainClientAppController(this);
            loadDashboardStyles(dashboradComponent.getScene());
            setMainPanelTo(dashboradComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadDashboardStyles(Scene scene){

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
    }







}
