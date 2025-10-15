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
            .cookieJar(simpleCookieManager)
             .followRedirects(false)
            .followSslRedirects(false).
    build();



    // A Callback is an interface from OkHttp that handles asynchronous responses.
    // It contains two methods:
    //
    // 1. onFailure(Call call, IOException e)
    //    -> Called when the request fails due to network issues (timeout, no internet, DNS error, etc.)
    //       Parameters:
    //         - call: the HTTP call object that failed
    //         - e: the IOException describing what went wrong
    //
    // 2. onResponse(Call call, Response response)
    //    -> Called when the server successfully returns a response (any HTTP status code, e.g. 200 or 404)
    //       Parameters:
    //         - call: the HTTP call object representing this request
    //         - response: the Response object returned by the server
    //
    // The Response object contains:
    //   - int code()              → the HTTP status code (e.g. 200, 404, 500)
    //   - String message()        → the status message (e.g. "OK", "Not Found")
    //   - Headers headers()       → all the HTTP headers returned by the server
    //   - ResponseBody body()     → the actual content of the response (text, JSON, HTML, etc.)
    //       * To get the body as a string: response.body().string()
    //         (Note: can be called only once; it closes the stream)
    //   - boolean isSuccessful()  → true if the code is between 200–299
    //   - Request request()       → the original request that generated this response
    //   - Protocol protocol()     → the protocol used (HTTP/1.1, HTTP/2)
    //   - Handshake handshake()   → TLS/SSL info (null if not HTTPS)
    //   - long sentRequestAtMillis() / receivedResponseAtMillis() → timing info


    // Perform an async GET request to the given URL, using the provided callback to handle the response
    public static void runAsync(String finalUrl, Callback callback) {
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        Call call = HttpClientUtil.HTTP_CLIENT.newCall(request);
        call.enqueue(callback);


    }

    public static void runAsync(Request request, Callback callback) {
        Call call = HttpClientUtil.HTTP_CLIENT.newCall(request);
        call.enqueue(callback);


    }

    // Shutdown the HTTP client, releasing its resources
    public static void shutdown() {
        HTTP_CLIENT.dispatcher().executorService().shutdown();
        HTTP_CLIENT.connectionPool().evictAll();
    }
}


// Set a logging facility for the cookie manager (Where to print its logs)
//
//    // Sets where the cookie manager will write its log messages.
//    // logConsumer → a function that receives each log line (e.g. System.out::println).
//    // Example: HttpClientUtil.setCookieManagerLoggingFacility(System.out::println);
//    public static void setCookieManagerLoggingFacility(Consumer<String> logConsumer) {
//        simpleCookieManager.setLogData(logConsumer);
//    }
//
//    // Remove all cookies for a specific domain
//    public static void removeCookiesOf(String domain) {
//        simpleCookieManager.removeCookiesOf(domain);
//    }