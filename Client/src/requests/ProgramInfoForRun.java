package requests;

import Alerts.Alerts;
import dto.ProgramPeek;
import dto.server.request.ProgramViewRequest;
import dto.server.response.UserData;
import javafx.application.Platform;
import newGui.pages.execution.component.primary.mainExecutionController;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import util.Constants;

import java.io.IOException;

public class ProgramInfoForRun {

    public static Request build(String programName, int expansionDegree) {

        // Build JSON body
        String json = Constants.GSON_INSTANCE.toJson(
                new ProgramViewRequest(programName, expansionDegree)
        );

        // Create request body with proper media type
        RequestBody body = RequestBody.create(json, Constants.MEDIA_TYPE_JSON);

        return new Request.Builder()
                .url(Constants.PROGRAM_VIEW)
                .method("GET", body)
                .build();
    }

    public static void onResponse(Response response, mainExecutionController executionController) {
        String responseBody;
        try {
            responseBody = response.body().string();

        } catch (IOException e) {
            Platform.runLater(() -> {
                Alerts.badBody(e.getMessage());
            });
            return;
        }

        if (response.code() != 200) {
            Platform.runLater(() -> {
                Alerts.loadField(responseBody);
            });
        } else {
            ProgramPeek programPeek = Constants.GSON_INSTANCE.fromJson(responseBody, ProgramPeek.class);

        }
    }

    public static void onFailure(IOException e) {
        Platform.runLater(() -> {
            Alerts.serverProblamResponse(e.getMessage());
        });
    }
}
