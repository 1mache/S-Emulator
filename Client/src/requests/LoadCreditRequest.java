package requests;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import util.Constants;


public class LoadCreditRequest {
    public static Request build(int creditAmount) {

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
}
