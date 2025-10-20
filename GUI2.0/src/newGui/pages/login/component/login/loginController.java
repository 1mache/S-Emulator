package newGui.pages.login.component.login;


import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import newGui.pages.primary.mainClientAppController;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import requests.LoginRequest;
import util.http.HttpClientUtil;
import javafx.scene.control.Button;
import java.io.IOException;

public class loginController {

    private mainClientAppController mainClientAppController;

    // Won't Use - Bottom
    @FXML private Button loginButton;

    @FXML private TextField userNameTextField;
    @FXML private Label errorMessageLabel;

    private final StringProperty errorMessageProperty = new SimpleStringProperty();

    @FXML
    public void initialize() {
        loginButton.setDefaultButton(true); // Enter triggers this button
        errorMessageLabel.textProperty().bind(errorMessageProperty);
    }

    public void setMainClientAppController(mainClientAppController mainAppController) {
        this.mainClientAppController = mainAppController;
    }

    @FXML
    private void loginButtonClicked(ActionEvent event) {

        String userName = userNameTextField.getText();
        if (userName.isEmpty()) {
            errorMessageProperty.set("User name is empty. You can't login with empty user name");
            return;
        }

        Request request = LoginRequest.build(userName);
        HttpClientUtil.runAsync(request, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                LoginRequest.onFailure(e, errorMessageProperty);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                LoginRequest.onResponse(response, errorMessageProperty, mainClientAppController, userName);
            }
        });
    }

    @FXML
    void quitButtonClicked(ActionEvent event) {
        Platform.exit();
    }

    // Clear error message when user types
    @FXML
    private void userNameKeyTyped(KeyEvent event) {
        errorMessageProperty.set("");
    }

}