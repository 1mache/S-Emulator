package requests;

import Alerts.Alerts;
import dto.ProgramExecutionResult;
import javafx.application.Platform;
import newGui.pages.dashboard.component.primary.dashboardController;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import util.Constants;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class GeneralHistoryForUser {

    public static Request build(String userName) {

        HttpUrl url = HttpUrl.parse(Constants.USER_HISTORY)
                .newBuilder()
                .addQueryParameter("username",userName)
                .build();


        return new Request.Builder()
                .url(url)
                .get()
                .build();
    }

    public static void onResponse(Response response, dashboardController dashboardController) {
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

            ProgramExecutionResult[] programExecutionResult = Constants.GSON_INSTANCE.fromJson(responseBody, ProgramExecutionResult[].class);
            List<ProgramExecutionResult> HistoryUsersDataList = new ArrayList<>(List.of(programExecutionResult));
            Platform.runLater(() -> {
               dashboardController.updateHistoryTable(HistoryUsersDataList);
            });

        }
    }

    public static void onFailure(IOException e) {
        Platform.runLater(() -> {
            Alerts.serverProblamResponse(e.getMessage());
        });
    }
}