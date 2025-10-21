package web.resource.history;

import engine.api.RunHistory;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.utils.ServletUtils;

import java.io.IOException;

@WebServlet(urlPatterns = "/user-history")
public class HistoryServlet extends HttpServlet {
    private static final String USERNAME_PARAM = "username";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var appContext = ServletUtils.getAppContext(req.getServletContext());
        var userManager = appContext.getUserManager();
        String username = req.getParameter(USERNAME_PARAM);
        if(username == null) {
            username = ServletUtils.getUsernameFromRequest(req);
        }

        if(username == null || !userManager.userExists(username)) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        RunHistory userRunHistory = userManager.getUser(username).getRunHistory();

        resp.setContentType("application/json");
        ServletUtils.GsonInstance.toJson(userRunHistory.getAllExecutions(), resp.getWriter());
    }
}
