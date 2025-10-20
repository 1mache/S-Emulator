package requests;

import Alerts.Alerts;
import javafx.application.Platform;
import newGui.pages.dashboard.component.primary.dashboardController;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import util.Constants;
import java.io.IOException;


public class UserInfoRequest {

    public static Request build(String userName) {

        HttpUrl url = HttpUrl.parse(Constants.USER_INFO)
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

//            UserData[] programDataArray = Constants.GSON_INSTANCE.fromJson(responseBody, UserData[].class);
//            List<UserData> UsersDataList = new ArrayList<>(List.of(programDataArray));

            // need to see what dto it get into
        //    dashboardController.updateUserHistoryTable();

        }
    }

    public static void onFailure(IOException e) {
        Platform.runLater(() -> {
            Alerts.serverProblamResponse(e.getMessage());
        });
    }
}
