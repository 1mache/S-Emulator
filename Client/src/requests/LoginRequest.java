package requests;


import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import newGui.pages.primary.mainClientAppController;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import util.Constants;
import java.io.IOException;

public class LoginRequest {

    public static Request build(String userName) {

        HttpUrl url = HttpUrl.parse(Constants.LOGIN)
                .newBuilder()
                .addQueryParameter("username",userName)
                .build();

        return new Request.Builder()
                .url(url)
                .post(RequestBody.create(new byte[0], null))
                .build();
    }

    public static void onResponse(Response response, StringProperty errorMessageProperty, mainClientAppController mainClientAppController, String userName) {
        if (response.code() != 200) {
            try {
                String responseBody = response.body().string();
                Platform.runLater(() ->
                        errorMessageProperty.set("Something went wrong " + responseBody)
                );
            } catch (Exception e) {

            }
        } else {
            Platform.runLater(() -> {
                mainClientAppController.updateUserName(userName);
                mainClientAppController.switchToDashboard();
            });
        }
    }

    public static void onFailure(IOException e, StringProperty errorMessageProperty) {
        Platform.runLater(() ->
                errorMessageProperty.set("Something went wrong: " + e.getMessage())
        );
    }
}
