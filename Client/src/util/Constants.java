package util;

import com.google.gson.Gson;
import okhttp3.MediaType;

public class Constants {

    // global constants
    public final static String LINE_SEPARATOR = System.getProperty("line.separator");
    public final static String START_NAME = "<Anonymous>";
    public final static int REFRESH_RATE = 2;
    public final static String CHAT_LINE_FORMATTING = "%tH:%tM:%tS | %.10s: %s%n";

    // fxml locations
    public final static String MAIN_PAGE_FXML_RESOURCE_LOCATION = "/newGui/pages/primary/mainClientApp.fxml";
    public final static String MAIN_PAGE_STYLE_RESOURCE_LOCATION = "/newGui/pages/primary/mainClientApp.css";

    public final static String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/newGui/pages/login/component/login/login.fxml";
    public final static String LOGIN_PAGE_STYLE_RESOURCE_LOCATION = "/newGui/pages/login/component/login/login.css";

    public final static String DASHBOARD_PAGE_FXML_RESOURCE_LOCATION = "/newGui/pages/dashboard/component/primary/dashboard.fxml";
    public final static String DASHBOARD_PAGE_STYLE_RESOURCE_LOCATION = "/newGui/pages/dashboard/component/primary/dashboard_styling.css";

    public final static String EXECUTION_PAGE_FXML_RESOURCE_LOCATION = "/newGui/pages/execution/component/primary/mainExecution.fxml";
    public final static String EXECUTION_PAGE_STYLE_RESOURCE_LOCATION = "/newGui/pages/execution/component/primary/main_styling.css";


    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/webServer_Web_exploded";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    // Login
    public final static String LOGIN = FULL_SERVER_PATH + "/login";
    public final static String PROGRAM_LIST = FULL_SERVER_PATH + "/dashboard/program-list";
    public final static String USERS_LIST = FULL_SERVER_PATH + "/dashboard/user-list";


    // Dashboard
    public final static String UPLOAD = FULL_SERVER_PATH + "/upload";
    public final static String ADD_CREDIT = FULL_SERVER_PATH + "/add-credit";
    public final static String USER_HISTORY = FULL_SERVER_PATH + "/user-history";
    public final static String PROGRAM_VIEW = FULL_SERVER_PATH + "/execution/view-program"; // Go to execution page
    public final static String MORE_PROGRAM_INFO = FULL_SERVER_PATH + "/info/program-info"; // Go to execution page


    // Execution

    public final static String RUN = FULL_SERVER_PATH +"/execution/run";
    public final static String HIGHLIGHT = FULL_SERVER_PATH +"/highlight";




    // infp type in the request body
    public static final MediaType MEDIA_TYPE_XML = MediaType.get("application/xml");
    public static final MediaType MEDIA_TYPE_JSON = MediaType.get("application/json; charset=utf-8");


    // GSON instance
    public final static Gson GSON_INSTANCE = new Gson();
}
