package requests;

import Alerts.Alerts;
import dto.ProgramPeek;
import dto.server.request.ProgramViewRequest;
import dto.server.response.ProgramData;
import javafx.application.Platform;
import newGui.pages.execution.component.primary.mainExecutionController;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import util.Constants;

import java.io.IOException;

public class ProgramInfoRequest {

    public static Request build(String programName) {

        HttpUrl url = HttpUrl.parse(Constants.MORE_PROGRAM_INFO)
                .newBuilder()
                .addQueryParameter("programName",programName)
                .build();


        return new Request.Builder()
                .url(url)
                .get()
                .build();
    }

    public static ProgramData onResponse(Response response) {
        String responseBody;
        try {
            responseBody = response.body().string();

        } catch (IOException e) {
            Platform.runLater(() -> {
                Alerts.badBody(e.getMessage());
            });
            return null;
        }

        if (response.code() != 200) {
            Platform.runLater(() -> {
                Alerts.loadField(responseBody);
            });
            return null;
        } else {
            ProgramData moreData = Constants.GSON_INSTANCE.fromJson(responseBody, ProgramData.class);
            return moreData;
        }
    }

    public static void onFailure(IOException e) {
        Platform.runLater(() -> {
            Alerts.serverProblamResponse(e.getMessage());
        });
    }


}
