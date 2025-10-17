package web.utils;

import com.google.gson.Gson;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import web.context.AppContext;

public class ServletUtils {
    private static final String APP_CONTEXT_ATR_NAME = "appContext";
    public static final String USERNAME_ATR_NAME = "username";

    private static final Object APP_CONTEXT_LOCK = new Object();

    public static final Gson GsonInstance = new Gson();

    public static AppContext getAppContext(ServletContext servletContext) {
        AppContext appContext;
        synchronized (APP_CONTEXT_LOCK) {
            appContext = (AppContext) servletContext.getAttribute(APP_CONTEXT_ATR_NAME);
            if(appContext == null) {
                servletContext.setAttribute(APP_CONTEXT_ATR_NAME, new AppContext());
                appContext = (AppContext) servletContext.getAttribute(APP_CONTEXT_ATR_NAME);
            }
        }

        return appContext;
    }

    public static String getUsernameFromRequest(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (String) session.getAttribute(ServletUtils.USERNAME_ATR_NAME);
    }
}
