package requests;

import Alerts.Alerts;
import javafx.application.Platform;
import newGui.pages.dashboard.component.primary.dashboardController;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import util.Constants;

import java.io.IOException;


public class LoadCreditRequest {

    public static Request build(long creditAmount) {

        HttpUrl url = HttpUrl.parse(Constants.ADD_CREDIT)
                .newBuilder()
                .addQueryParameter("creditamount", String.valueOf(creditAmount))
                .build();

        RequestBody body = RequestBody.create(new byte[0], null);

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
        } else {
            Platform.runLater(Alerts::creditLoadSucceeded);
        }
    }

    public static void onFailure(IOException e) {
        Platform.runLater(() -> {
            Alerts.serverProblamResponse(e.getMessage());
        });
    }
}
