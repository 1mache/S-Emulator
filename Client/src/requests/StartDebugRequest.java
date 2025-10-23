package requests;

import Alerts.Alerts;
import dto.ProgramExecutionResult;
import dto.server.response.DebugStateInfo;
import javafx.application.Platform;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import util.Constants;

import java.io.IOException;

import static util.Constants.GSON_INSTANCE;

public class StartDebugRequest {

    public static Request build(dto.server.request.StartDebugRequest info ) {
        String json = GSON_INSTANCE.toJson(info);
        RequestBody body = RequestBody.create(json, Constants.MEDIA_TYPE_JSON);

        HttpUrl url = HttpUrl.parse(Constants.START_DEBUG)
                .newBuilder()
                .build();

        return new Request.Builder()
                .url(url)
                .post(body)
                .build();
    }

    public static DebugStateInfo onResponse(Response response) {
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
            return Constants.GSON_INSTANCE.fromJson(responseBody, DebugStateInfo.class);
        }
    }

    public static void onFailure(IOException e) {
        Platform.runLater(() -> {
            Alerts.serverProblamResponse(e.getMessage());
        });
    }
}
