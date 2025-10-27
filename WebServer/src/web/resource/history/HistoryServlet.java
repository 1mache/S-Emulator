package web.resource.history;

import engine.api.RunHistory;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.exception.BadAuthorizationException;
import web.resource.AuthorizingServlet;
import web.user.User;
import web.utils.ServletUtils;

import java.io.IOException;

@WebServlet(urlPatterns = "/user-history")
public class HistoryServlet extends AuthorizingServlet {
    private static final String PROGRAM_NAME_PARAM = "programName";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = req.getParameter(USERNAME_PARAM);
        User user;
        try {
            user = authorize(req, resp, username);
        } catch (BadAuthorizationException e) {return;}

        RunHistory userRunHistory = user.getRunHistory();
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
