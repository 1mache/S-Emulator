package requests;

import Alerts.Alerts;
import dto.ProgramPeek;
import javafx.application.Platform;
import okhttp3.*;
import util.Constants;

import java.io.IOException;

import static java.lang.String.valueOf;

public class ProgramInfoForRun {

    public static Request build(String programName, int expansionDegree) {

        HttpUrl url = HttpUrl.parse(Constants.PROGRAM_VIEW)
                .newBuilder()
                .addQueryParameter("programName",programName)
                .addQueryParameter("expansionDegree",valueOf(expansionDegree))
                .build();


        return new Request.Builder()
                .url(url)
                .get()
                .build();

    }

    public static ProgramPeek onResponse(Response response) {
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
            ProgramPeek programPeek = Constants.GSON_INSTANCE.fromJson(responseBody, ProgramPeek.class);
            return programPeek;
        }
    }

    public static void onFailure(IOException e) {
        Platform.runLater(() -> {
            Alerts.serverProblamResponse(e.getMessage());
        });
    }
}
