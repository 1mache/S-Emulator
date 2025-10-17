package requests;

import okhttp3.Request;
import okhttp3.RequestBody;
import util.Constants;
import java.util.LinkedHashMap;
import java.util.Map;

import static util.Constants.GSON_INSTANCE;

public class LoginRequest {

    public static Request build(String userName) {

        // Create JSON body { "username": "<userName>" }
        Map<String, Object> jsonMap = new LinkedHashMap<>();
        jsonMap.put("username", userName);
        jsonMap.put("startCreditAmount", Long.valueOf(1000));
        String json = GSON_INSTANCE.toJson(jsonMap);

        RequestBody body = RequestBody.create(json, Constants.MEDIA_TYPE_JSON);

        return new Request.Builder()
                .url(Constants.LOGIN_PAGE)
                .post(body)
                .build();
    }
}
