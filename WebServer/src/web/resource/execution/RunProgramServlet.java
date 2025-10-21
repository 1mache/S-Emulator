package web.resource.execution;

import dto.ProgramExecutionResult;
import dto.server.request.RunRequest;
import engine.api.SLanguageEngine;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.user.User;
import web.utils.ServletUtils;

import java.io.IOException;

@WebServlet(urlPatterns = {"/execution/run"})
public class RunProgramServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var applicationContext = ServletUtils.getAppContext(req.getServletContext());
        var userManager = applicationContext.getUserManager();
        String username = ServletUtils.getUsernameFromRequest(req);
        if(!userManager.userExists(username)){
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        User user = userManager.getUser(username);

        SLanguageEngine engine = applicationContext.getEngine();
        RunRequest runRequest = ServletUtils.GsonInstance.fromJson(req.getReader(), RunRequest.class);

        ProgramExecutionResult result;
        synchronized (getServletContext()){
            result = engine.runProgram(
                    runRequest.programName(),
                    runRequest.expansionDegree(),
                    runRequest.inputs(),
                    true,
                    user.getRunHistory()
            );
        }

        resp.setContentType("application/json");
        ServletUtils.GsonInstance.toJson(result, resp.getWriter());
    }
}
