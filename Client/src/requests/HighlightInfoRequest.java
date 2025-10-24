package requests;

import Alerts.Alerts;
import dto.server.request.HighlightRequest;
import javafx.application.Platform;
import newGui.pages.execution.component.primary.mainExecutionController;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import util.Constants;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import static util.Constants.GSON_INSTANCE;

public class HighlightInfoRequest {
    public static Request build(HighlightRequest info ) {
        String json = GSON_INSTANCE.toJson(info);
        RequestBody body = RequestBody.create(json, Constants.MEDIA_TYPE_JSON);

        HttpUrl url = HttpUrl.parse(Constants.HIGHLIGHT)
                .newBuilder()
                .build();

        return new Request.Builder()
                .url(url)
                .post(body)
                .build();
    }

    public static void onResponse(Response response, mainExecutionController mainExecutionController) {
        String responseBody;
        try {
            responseBody = response.body().string();
        } catch (Exception e) {
            Platform.runLater(() ->
                    Alerts.badBody(e.getMessage())
            );
            return;
        }

        if (response.code() != 200) {
            Platform.runLater(() -> {
                Alerts.serverBadAnswer(responseBody);
            });
        } else {

            Integer[] numbers = Constants.GSON_INSTANCE.fromJson(responseBody, Integer[].class);
            List<Integer> numbersList = Arrays.asList(numbers);
            Platform.runLater(() -> {
                mainExecutionController.updateHighlightedInstructions(numbersList);
            });

        }
    }

    public static void onFailure(IOException e) {
        Platform.runLater(() -> {
            Alerts.serverProblamResponse(e.getMessage());
        });
    }
}
