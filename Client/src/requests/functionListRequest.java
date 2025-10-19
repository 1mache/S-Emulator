package requests;

import dto.server.response.ProgramData;
import javafx.application.Platform;
import Alerts.Alerts;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import util.Constants;

import java.io.IOException;
import java.util.List;

public class functionListRequest {

    public static Request build() {

        HttpUrl url = HttpUrl.parse(Constants.FUNCTION_LIST)
                .newBuilder()
                .build();


        return new Request.Builder()
                .url(url)
                .get()
                .build();


    }

    public List<ProgramData> onResponse(Response response) {
        String responseBody;
        try {
            responseBody = response.body().string();
        } catch (Exception e) {
            Platform.runLater(() ->
                    Alerts.dtoTranslate(e.getMessage())
            );
            return null;
        }

        if (response.code() != 200) {
            Platform.runLater(() -> {
                Alerts.loadField(responseBody);
            });
            return null;
        } else {

        ProgramData[] programDataArray = Constants.GSON_INSTANCE.fromJson(responseBody, ProgramData[].class);
        return List.of(programDataArray);
        }
    }

    public void onFailure(IOException e) {
        Platform.runLater(() -> {
            Alerts.serverProblamResponse(e.getMessage());
        });
    }
}
