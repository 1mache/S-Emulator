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
    private static final String PROGRAM_NAME_PARAM = "programName";

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
        String programName = req.getParameter(PROGRAM_NAME_PARAM);
        resp.setContentType("application/json");
        if(programName == null) { // no program name specified, return all executions
            ServletUtils.GsonInstance.toJson(userRunHistory.getAllExecutions(), resp.getWriter());
        }
        else { // return executions for the specified program
            ServletUtils.GsonInstance.toJson(userRunHistory.getExecutionsOf(programName), resp.getWriter());
        }

    }
}
