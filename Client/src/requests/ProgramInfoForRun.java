package requests;

import Alerts.Alerts;
import dto.ProgramPeek;
import dto.server.request.ProgramViewRequest;
import dto.server.response.ProgramData;
import dto.server.response.UserData;
import javafx.application.Platform;
import newGui.pages.execution.component.primary.mainExecutionController;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

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


//        // Build JSON body
//        String json = Constants.GSON_INSTANCE.toJson(
//                new ProgramViewRequest(programName, expansionDegree)
//        );
//
//        // Create request body with proper media type
//        RequestBody body = RequestBody.create(json, Constants.MEDIA_TYPE_JSON);
//
//        return new Request.Builder()
//                .url(Constants.PROGRAM_VIEW)
//                .method("GET", body)
//                .build();
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
