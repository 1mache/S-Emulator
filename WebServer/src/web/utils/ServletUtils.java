package web.utils;

import jakarta.servlet.ServletContext;
import web.resource.context.AppContext;

public class ServletUtils {
    private static final String APP_CONTEXT_ATR_NAME = "appContext";

    private static final Object APP_CONTEXT_LOCK = new Object();

    public static AppContext getAppContext(ServletContext servletContext) {
        AppContext appContext;
        synchronized (APP_CONTEXT_LOCK) {
            appContext = (AppContext) servletContext.getAttribute(APP_CONTEXT_ATR_NAME);
            if(appContext == null) {
                servletContext.setAttribute(APP_CONTEXT_ATR_NAME, new AppContext());
            }
        }

        return appContext;
    }
}
