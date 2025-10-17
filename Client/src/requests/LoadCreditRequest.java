package requests;

import okhttp3.Request;
import okhttp3.RequestBody;
import util.Constants;

import java.util.LinkedHashMap;
import java.util.Map;

import static util.Constants.GSON_INSTANCE;

public class LoadCreditRequest {
    public static Request build(int creditAmount) {

        Map<String, Object> jsonMap = new LinkedHashMap<>();
        jsonMap.put("creditamount", creditAmount);
        String json = GSON_INSTANCE.toJson(jsonMap);

        RequestBody body = RequestBody.create(json, Constants.MEDIA_TYPE_JSON);

        return new Request.Builder()
                .url(Constants.ADD_CREDIT)
                .post(body)
                .build();
    }
}
