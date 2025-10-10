package newGui.pages.dashboard.component.top;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class topController implements Initializable {

    @FXML private Label availableCredits;
    @FXML private Button chargeCreditsButton;
    @FXML private TextField creditsAmount;
    @FXML private TextField currentlyLoadedFilePath;
    @FXML private Button loadFileButton;
    @FXML private ProgressBar progressBar;
    @FXML private Label statusLabel;
    @FXML private ScrollPane top;

    @FXML
    void ChargeListener(ActionEvent event) {

    }

    @FXML
    void LoadListener(ActionEvent event) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

}
