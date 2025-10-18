package requests;

import okhttp3.HttpUrl;
import okhttp3.Request;
import util.Constants;


public class LoadCreditRequest {
    public static Request build(int creditAmount) {

        HttpUrl url = HttpUrl.parse(Constants.ADD_CREDIT)
                .newBuilder()
                .addQueryParameter("creditamount", String.valueOf(creditAmount))
                .addQueryParameter("age", "25")
                .build();


        return new Request.Builder()
                .url(url)
                .get()
                .build();
    }
}
