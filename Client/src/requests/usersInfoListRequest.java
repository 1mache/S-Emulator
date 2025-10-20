package requests;

import Alerts.Alerts;
import dto.server.response.UserData;
import javafx.application.Platform;
import newGui.pages.dashboard.component.primary.dashboardController;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import util.Constants;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UsersInfoListRequest {

    public static Request build() {

    HttpUrl url = HttpUrl.parse(Constants.USERS_LIST)
            .newBuilder()
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

        UserData[] programDataArray = Constants.GSON_INSTANCE.fromJson(responseBody, UserData[].class);
        List<UserData> UsersDataList = new ArrayList<>(List.of(programDataArray));
        dashboardController.updateUsersList(UsersDataList);
    }
}

    public static void onFailure(IOException e) {
    Platform.runLater(() -> {
        Alerts.serverProblamResponse(e.getMessage());
    });
}
}
