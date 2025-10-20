package requests;


import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import newGui.pages.primary.mainClientAppController;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import util.Constants;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import static util.Constants.GSON_INSTANCE;

public class LoginRequest {

    public static Request build(String userName) {

        Map<String, Object> jsonMap = new LinkedHashMap<>();
        jsonMap.put("username", userName);
        jsonMap.put("startCreditAmount", Long.valueOf(1000));
        String json = GSON_INSTANCE.toJson(jsonMap);

        RequestBody body = RequestBody.create(json, Constants.MEDIA_TYPE_JSON);

        return new Request.Builder()
                .url(Constants.LOGIN)
                .post(body)
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
