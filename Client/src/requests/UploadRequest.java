package requests;

import Alerts.Alerts;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import newGui.pages.primary.mainClientAppController;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import util.Constants;
import java.io.File;
import java.io.IOException;

import static newGui.pages.dashboard.component.top.topController.setStatus;

public class UploadRequest {

    public static Request build( File xmlFile) {

        // Create file body (content type: application/xml)
        RequestBody fileBody = RequestBody.create(xmlFile, Constants.MEDIA_TYPE_XML);

        // Build multipart body
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", xmlFile.getName(), fileBody)
                .build();

        // Build request to /upload endpoint
        return new Request.Builder()
                .url(Constants.UPLOAD)
                .post(requestBody)
                .build();
    }

    public static void onResponse(Response response,
                                  TextField currentlyLoadedFilePath,
                                  Label statusLabel,
                                  PauseTransition clearStatusLater) {

        String responseBody;
        try {
            responseBody = response.body().string();

        } catch (Exception e) {
            Alerts.badBody(e.getMessage());
            return;
        }

        if (response.code() != 200) {
            assert response.body() != null;
            Platform.runLater(() -> {
                Alerts.loadField(responseBody);
                setStatus("", true, statusLabel, clearStatusLater);
                currentlyLoadedFilePath.setText("Currently Loaded File");
            });

        } else {
            Platform.runLater(() -> {
                if (responseBody != null && !responseBody.isEmpty()) {
                    Alerts.loadField(responseBody);
                } else {
                    Alerts.loadSucceeded();
                }
                setStatus("Finish", true, statusLabel, clearStatusLater);
                currentlyLoadedFilePath.setText("Currently Loaded File");

            });
        }
    }

    public static void onFailure(IOException e,
                                 TextField currentlyLoadedFilePath,
                                 Label statusLabel,
                                 PauseTransition clearStatusLater) {
        Alerts.loadField(e.getMessage());
        currentlyLoadedFilePath.setText("Currently Loaded File");
        setStatus("", true, statusLabel, clearStatusLater);

    }
}

