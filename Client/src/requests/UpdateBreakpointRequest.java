package requests;

import Alerts.Alerts;
import dto.server.request.BreakpointRequest;
import javafx.application.Platform;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import util.Constants;

import java.io.IOException;

import static util.Constants.GSON_INSTANCE;

public class UpdateBreakpointRequest {
    public static Request build(BreakpointRequest info) {

        String json = GSON_INSTANCE.toJson(info);
        RequestBody body = RequestBody.create(json, Constants.MEDIA_TYPE_JSON);


        HttpUrl url = HttpUrl.parse(Constants.UPDATE_BREAKPOINTS)
                .newBuilder()
                .build();

        return new Request.Builder()
                .url(url)
                .post(body)
                .build();
    }

    public static void onResponse(Response response) {
        if (response.code() != 200) {
            try {
                String responseBody = response.body().string();
                Platform.runLater(() -> {
                    Alerts.loadField(responseBody);
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    Alerts.badBody(e.getMessage());
                });
            }
        }
    }

    public static void onFailure(IOException e) {
        Platform.runLater(() -> {
            Alerts.serverProblamResponse(e.getMessage());
        });
    }
}
