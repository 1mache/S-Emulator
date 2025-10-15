package util.http;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.util.function.Consumer;

public class HttpClientUtil {

    // A simple cookie manager to handle cookies
    private final static SimpleCookieManager simpleCookieManager = new SimpleCookieManager();

    // An OkHttpClient instance configured with the cookie manager and set to not follow redirects automatically
    private final static OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
            .cookieJar(simpleCookieManager).followRedirects(false).build();




    // Set a logging facility for the cookie manager (Where to print its logs)
    public static void setCookieManagerLoggingFacility(Consumer<String> logConsumer) {
        simpleCookieManager.setLogData(logConsumer);
    }

    // Remove all cookies for a specific domain
    public static void removeCookiesOf(String domain) {
        simpleCookieManager.removeCookiesOf(domain);
    }

    // Perform an async GET request to the given URL, using the provided callback to handle the response
    public static void runAsync(String finalUrl, Callback callback) {
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        Call call = HttpClientUtil.HTTP_CLIENT.newCall(request);

        call.enqueue(callback);
    }

    // Shutdown the HTTP client, releasing its resources
    public static void shutdown() {
        System.out.println("Shutting down HTTP CLIENT");
        HTTP_CLIENT.dispatcher().executorService().shutdown();
        HTTP_CLIENT.connectionPool().evictAll();
    }
}
