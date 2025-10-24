package requests;

import Alerts.Alerts;
import dto.ProgramExecutionResult;
import dto.debug.DebugStepPeek;
import javafx.application.Platform;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import util.Constants;

import java.io.IOException;

import static util.Constants.GSON_INSTANCE;

public class StepOverDebugRequest {

    public static Request build() {

        HttpUrl url = HttpUrl.parse(Constants.STEP_OVER_DEBUG)
                .newBuilder()
                .build();

        return new Request.Builder()
                .url(url)
                .post(RequestBody.create(new byte[0], null))
                .build();
    }

    public static DebugStepPeek onResponse(Response response) {
        String responseBody;
        try {
            responseBody = response.body().string();
        } catch (Exception e) {
            Platform.runLater(() ->
                    Alerts.badBody(e.getMessage())
            );
            return null;
        }

        if (response.code() != 200) {
            Platform.runLater(() -> {
                Alerts.serverBadAnswer(responseBody);

            });
            return null;
        } else {
            return Constants.GSON_INSTANCE.fromJson(responseBody, DebugStepPeek.class);
        }
    }

    public static void onFailure(IOException e) {
        Platform.runLater(() -> {
            Alerts.serverProblamResponse(e.getMessage());
        });
    }
}
