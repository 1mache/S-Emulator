package newGui.pages.dashboard.component.top;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import requests.LoadFileRequest;
import util.http.HttpClientUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;

public class topController implements Initializable {


    // Won't Use
    @FXML private ScrollPane top;

    // Buttons
    @FXML private Button chargeCreditsButton;
    @FXML private Button loadFileButton;

    // Text Fields and Labels that will change dynamically
    @FXML private Label statusLabel;
    @FXML private Label userName;
    @FXML private Label availableCredits;
    @FXML private TextField creditsAmount;
    @FXML private TextField currentlyLoadedFilePath;

    // Effect
    @FXML private ProgressBar progressBar;


    // Last directory visited
    private File lastDir = new File(System.getProperty("user.home"));
    private PauseTransition clearStatusLater;


    public void bindUserName(StringProperty name) {
        userName.textProperty().bind(Bindings.concat("Hello ", name));

    }

    @FXML
    void ChargeListener(ActionEvent event) {

    }


    private void loafField(Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Load Error");
        alert.setHeaderText(null);
        alert.setContentText("Load Error: " + ex.getMessage());
        alert.showAndWait();
    }

    @FXML
    void LoadListener(ActionEvent event) {

        FileChooser fc = new FileChooser();
        fc.setTitle("Open Resource File");
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml"));
        if (lastDir != null && lastDir.isDirectory()) {
            fc.setInitialDirectory(lastDir);
        }
        Stage stage = (Stage) ((Node) loadFileButton).getScene().getWindow();
        File file = fc.showOpenDialog(stage);
        if (file != null) {
            lastDir = file.getParentFile();
        } else {
            return;
        }

        currentlyLoadedFilePath.setText(file.getAbsolutePath());

        // Safely reset the status label before (re)binding it in startLoadFileProgress
        if (clearStatusLater != null) { // cancel any previous clear-timer
            clearStatusLater.stop();
            clearStatusLater = null;
        }
        statusLabel.textProperty().unbind();  // IMPORTANT: unbind before setText
        statusLabel.setText("");



        // Read a path from text field and build request
        final Request request;
        try {
            request = LoadFileRequest.build(file.toPath());
        } catch (IllegalArgumentException ex) {
            loafField(ex);
            return;
        }

        setBusy(true);

        // Send async and update UI by response
        HttpClientUtil.runAsync(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Platform.runLater(() -> {
                    setBusy(false);
                    setStatus("Something went wrong: " + e.getMessage(),  false);
                });
            }

            @Override
            public void onResponse(Call call, Response response) {
                try (response) {
                    final int code = response.code();
                    final String body = response.body() != null ? response.body().string() : "";

                    Platform.runLater(() -> {
                        setBusy(false);
                        if (response.isSuccessful()) {
                            // Success path: show some info from server
                            // If your server returns JSON, you can parse it here instead of showing raw body.
                            setStatus("Load Successfully" + code + ")", true);
                            // Example: you can also reflect details from body:
                            // detailsLabel.setText(body);  // if you add a label for details
                        } else {
                            setStatus("Problem with the server " + code + "): " + body, false);
                        }
                    });
                } catch (IOException io) {
                    Platform.runLater(() -> {
                        setBusy(false);
                        setStatus("Error in read server response" + io.getMessage(), false);
                    });
                }
            }
        });
    }

    private void setBusy(boolean busy) {
        if (progressBar != null) {
            progressBar.setVisible(busy);
            progressBar.setProgress(busy ? ProgressBar.INDETERMINATE_PROGRESS : 0);
        }
//        if (loadButton != null) {
//            loadButton.setDisable(busy);
//        }
    }

    private void setStatus(String msg, boolean ok) {
        if (statusLabel != null) {
            statusLabel.setText(msg);
            statusLabel.getStyleClass().removeAll("status-ok", "status-error");
            statusLabel.getStyleClass().add(ok ? "status-ok" : "status-error");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setBusy(false);
        setStatus("", true);
    }
}


