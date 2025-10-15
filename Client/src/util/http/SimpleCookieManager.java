package util.http;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SimpleCookieManager implements CookieJar {

    private final Map<String, Map<String, Cookie>> cookies = new HashMap<>();

    @NotNull
    @Override
    public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
        String host = httpUrl.host();
        synchronized (this) {
            if (cookies.containsKey(host)) {
                return new ArrayList<>(cookies.get(host).values());
            }
        }
        return Collections.emptyList();
    }

    @Override
    public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> responseCookies) {
        String host = httpUrl.host();
        synchronized (this) {
            Map<String, Cookie> cookiesMap = cookies.computeIfAbsent(host, key -> new HashMap<>());
            for (Cookie cookie : responseCookies) {
                cookiesMap.put(cookie.name(), cookie); // replace always
            }
        }
    }

    public void removeCookiesOf(String domain) {
        synchronized (this) {
            cookies.remove(domain);
        }
    }
}
