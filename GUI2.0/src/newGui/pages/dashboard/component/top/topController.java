package newGui.pages.dashboard.component.top;

import Alerts.Alerts;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import newGui.pages.dashboard.component.primary.dashboardController;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import requests.LoadCreditRequest;
import requests.LoginRequest;
import requests.UploadRequest;
import util.http.HttpClientUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


public class topController {

    private dashboardController dashboardController;



    // Won't Use
    @FXML private ScrollPane top;

    // Buttons
    @FXML private Button chargeCreditsButton;
    @FXML private Button loadFileButton;

    // Text Fields and Labels that will change dynamically
    @FXML private Label statusLabel;
    @FXML private Label userName;
    @FXML private TextField availableCredits;
    private LongProperty credits = new SimpleLongProperty(1000);

    @FXML private TextField creditsAmount;
    @FXML private TextField currentlyLoadedFilePath;

    // Effect
    @FXML private ProgressBar progressBar;


    // Last directory visited
    private File lastDir = new File(System.getProperty("user.home"));
    private PauseTransition clearStatusLater;

    public void init(StringProperty name) {
        setupPlaceholder(creditsAmount);
        bindUserName(name);
        bindCredits();
    }

    public void setDashboardController(dashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    public void bindUserName(StringProperty name) {
        userName.textProperty().bind(Bindings.concat("Hello ", name));
    }

    public void bindCredits() {
        availableCredits.textProperty().bind(Bindings.convert(credits));
    }


    private void setupPlaceholder(TextField textField) {
        final String placeholderText = textField.getText();
        textField.setStyle("-fx-text-fill: gray;");

        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                if (textField.getText().equals(placeholderText)) {
                    textField.clear();
                    textField.setStyle("-fx-text-fill: black;");
                }
            } else {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholderText);
                    textField.setStyle("-fx-text-fill: gray;");
                }
            }
        });
    }
    @FXML
    void ChargeListener(ActionEvent event) {
        try {
            Integer.parseInt(creditsAmount.getText());
            if (Integer.parseInt(creditsAmount.getText()) <= 0) {
                throw new NumberFormatException();
            }
            if (creditsAmount.getText().isEmpty()) {
                throw new NumberFormatException();
            }
            } catch (NumberFormatException e) {
            Alerts.invalidInput();
            return;
        }

        long amount = Long.parseLong(creditsAmount.getText());
        credits.set(credits.get() + amount);
        creditsAmount.clear();

        // new request to server to charge credits
        Request request = LoadCreditRequest.build(credits.get());
        HttpClientUtil.runAsync(request, new Callback()  {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                LoadCreditRequest.onFailure(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Platform.runLater(() -> {
                    setBusy(false);
                });
                LoadCreditRequest.onResponse(response);
            }
        });

    }


    @FXML
    void LoadListener(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose S-Program XML");
        fc.getExtensionFilters().setAll(new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml"));

        if (lastDir != null && lastDir.isDirectory()) {
            fc.setInitialDirectory(lastDir);
        }

        Stage stage = (Stage) ((Node) loadFileButton).getScene().getWindow();
        File file = fc.showOpenDialog(stage);
        if (file == null) {
            return; // user canceled
        }
        lastDir = file.getParentFile();
        currentlyLoadedFilePath.setText(file.getAbsolutePath());

        if (clearStatusLater != null) {
            clearStatusLater.stop();
            clearStatusLater = null;
        }

        statusLabel.textProperty().unbind();
        setStatus("", true, statusLabel, clearStatusLater);

        try {
            validateXmlFileOrThrow(file);
        } catch (IllegalArgumentException ex) {
            currentlyLoadedFilePath.clear();
            Platform.runLater(Alerts.loadField(ex.getMessage()));
            return;
        }

        Platform.runLater(() -> {
            setBusy(true);
        });

        Request request = UploadRequest.build(file);
        HttpClientUtil.runAsync(request, new Callback()  {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    setBusy(false);
                    UploadRequest.onFailure(e, currentlyLoadedFilePath, statusLabel, clearStatusLater);
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Platform.runLater(() -> {
                    setBusy(false);
                });
                UploadRequest.onResponse( response, currentlyLoadedFilePath, statusLabel, clearStatusLater);
//                if (response.code() != 200) {
//                    assert response.body() != null;
//                    String responseBody = response.body().string();
//                    Platform.runLater(() -> {
//                        Alerts.loadField(responseBody);
//                        setStatus("", true);
//                        currentlyLoadedFilePath.setText("Currently Loaded File");
//                    });
//                }
//                else {
//                    String responseBody = response.body().string();
//                    Platform.runLater(() -> {
//                        if (responseBody != null && !responseBody.isEmpty()) {
//                           Alerts.loadField(responseBody);
//                        } else {
//                            Alerts.loadSucceeded();
//                        }
//                        setStatus("Finish", true);
//                        currentlyLoadedFilePath.setText("Currently Loaded File");
//
//                    });
//                }
            }
        });
    }

    public static void setStatus(String msg, boolean ok, Label statusLabel, PauseTransition clearStatusLater) {
        if (statusLabel != null) {
            statusLabel.setText(msg);

            if (clearStatusLater != null) {
                clearStatusLater.stop();
            }

            clearStatusLater = new PauseTransition(Duration.seconds(5));
            clearStatusLater.setOnFinished(event -> statusLabel.setText(""));
            clearStatusLater.play();
        }

    }

    private void validateXmlFileOrThrow(File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException("File does not exist.");
        }
        if (!file.isFile()) {
            throw new IllegalArgumentException("Selected item is not a regular file.");
        }
        if (!file.canRead()) {
            throw new IllegalArgumentException("File is not readable.");
        }
    }

    private void setBusy(boolean busy) {
        if (progressBar != null) {
            progressBar.setVisible(busy);
            progressBar.setProgress(busy ? ProgressBar.INDETERMINATE_PROGRESS : 0);
        }
        loadFileButton.setDisable(busy);
    }

}


//
//
//
//    @Override
//    public void initialize(URL url, ResourceBundle resourceBundle) {
//        setBusy(false);
//        setStatus("", true);
//    }




////    @FXML
////    void LoadListener(ActionEvent event) {
////
////        FileChooser fc = new FileChooser();
////        fc.setTitle("Open Resource File");
////        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml"));
////        if (lastDir != null && lastDir.isDirectory()) {
////            fc.setInitialDirectory(lastDir);
////        }
////        Stage stage = (Stage) ((Node) loadFileButton).getScene().getWindow();
////        File file = fc.showOpenDialog(stage);
////        if (file != null) {
////            lastDir = file.getParentFile();
////        } else {
////            return;
////        }
////
////        currentlyLoadedFilePath.setText(file.getAbsolutePath());
////
////        // Safely reset the status label before (re)binding it in startLoadFileProgress
////        if (clearStatusLater != null) { // cancel any previous clear-timer
////            clearStatusLater.stop();
////            clearStatusLater = null;
////        }
////        statusLabel.textProperty().unbind();  // IMPORTANT: unbind before setText
////        statusLabel.setText("");
////
////
////
////        // Read a path from text field and build request
////        final Request request;
////        try {
////            request = LoadFileRequest.build(file.toPath());
////        } catch (IllegalArgumentException ex) {
////            loafField(ex);
////            return;
////        }
////
////        setBusy(true);
////
////        // Send async and update UI by response
////        HttpClientUtil.runAsync(request, new Callback() {
////            @Override
////            public void onFailure(Call call, IOException e) {
////                Platform.runLater(() -> {
////                    setBusy(false);
////                    setStatus("Something went wrong: " + e.getMessage(),  false);
////                });
////            }
////
////            @Override
////            public void onResponse(Call call, Response response) {
////                try (response) {
////                    final int code = response.code();
////                    final String body = response.body() != null ? response.body().string() : "";
////
////                    Platform.runLater(() -> {
////                        setBusy(false);
////                        if (response.isSuccessful()) {
////                            // Success path: show some info from server
////                            // If your server returns JSON, you can parse it here instead of showing raw body.
////                            setStatus("Load Successfully" + code + ")", true);
////                            // Example: you can also reflect details from body:
////                            // detailsLabel.setText(body);  // if you add a label for details
////                        } else {
////                            setStatus("Problem with the server " + code + "): " + body, false);
////                        }
////                    });
////                } catch (IOException io) {
////                    Platform.runLater(() -> {
////                        setBusy(false);
////                        setStatus("Error in read server response" + io.getMessage(), false);
////                    });
////                }
////            }
////        });
////    }





////    @FXML
////    void LoadListener(ActionEvent event) {
////
////        FileChooser fc = new FileChooser();
////        fc.setTitle("Open Resource File");
////        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml"));
////        if (lastDir != null && lastDir.isDirectory()) {
////            fc.setInitialDirectory(lastDir);
////        }
////        Stage stage = (Stage) ((Node) loadFileButton).getScene().getWindow();
////        File file = fc.showOpenDialog(stage);
////        if (file != null) {
////            lastDir = file.getParentFile();
////        } else {
////            return;
////        }
////
////        currentlyLoadedFilePath.setText(file.getAbsolutePath());
////
////        // Safely reset the status label before (re)binding it in startLoadFileProgress
////        if (clearStatusLater != null) { // cancel any previous clear-timer
////            clearStatusLater.stop();
////            clearStatusLater = null;
////        }
////        statusLabel.textProperty().unbind();  // IMPORTANT: unbind before setText
////        statusLabel.setText("");
////
////
////
////        // Read a path from text field and build request
////        final Request request;
////        try {
////            request = LoadFileRequest.build(file.toPath());
////        } catch (IllegalArgumentException ex) {
////            loafField(ex);
////            return;
////        }
////
////        setBusy(true);
////
////        // Send async and update UI by response
////        HttpClientUtil.runAsync(request, new Callback() {
////            @Override
////            public void onFailure(Call call, IOException e) {
////                Platform.runLater(() -> {
////                    setBusy(false);
////                    setStatus("Something went wrong: " + e.getMessage(),  false);
////                });
////            }
////
////            @Override
////            public void onResponse(Call call, Response response) {
////                try (response) {
////                    final int code = response.code();
////                    final String body = response.body() != null ? response.body().string() : "";
////
////                    Platform.runLater(() -> {
////                        setBusy(false);
////                        if (response.isSuccessful()) {
////                            // Success path: show some info from server
////                            // If your server returns JSON, you can parse it here instead of showing raw body.
////                            setStatus("Load Successfully" + code + ")", true);
////                            // Example: you can also reflect details from body:
////                            // detailsLabel.setText(body);  // if you add a label for details
////                        } else {
////                            setStatus("Problem with the server " + code + "): " + body, false);
////                        }
////                    });
////                } catch (IOException io) {
////                    Platform.runLater(() -> {
////                        setBusy(false);
////                        setStatus("Error in read server response" + io.getMessage(), false);
////                    });
////                }
////            }
////        });
////    }
