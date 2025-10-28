package web.resource.execution;

import dto.ProgramExecutionResult;
import dto.server.request.RunRequest;
import engine.api.SLanguageEngine;
import engine.instruction.Architecture;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.exception.BadAuthorizationException;
import web.resource.AuthorizingServlet;
import web.user.CreditExecutionLimiter;
import web.user.User;
import web.utils.ServletUtils;

import java.io.IOException;

@WebServlet(urlPatterns = {"/execution/run"})
public class RunProgramServlet extends AuthorizingServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var applicationContext = ServletUtils.getAppContext(req.getServletContext());
        User user;
        try {
            user = authorize(req, resp);
        } catch (BadAuthorizationException e) {return;}

        SLanguageEngine engine = applicationContext.getEngine();
        RunRequest runRequest = ServletUtils.GsonInstance.fromJson(req.getReader(), RunRequest.class);

        Architecture arch = engine.getArchitectureOf(runRequest.programName());
        if(!ServletUtils.chargeArchitectureCost(resp, user, arch))
            return;

        ProgramExecutionResult result;
        CreditExecutionLimiter creditLimiter = new CreditExecutionLimiter(user);
        synchronized (getServletContext()){
            result = engine.runProgram(
                    runRequest.programName(),
                    runRequest.expansionDegree(),
                    runRequest.inputs(),
                    true,
                    user.getRunHistory(),
                    creditLimiter
            );
        }

        resp.setContentType("application/json");
        ServletUtils.GsonInstance.toJson(result, resp.getWriter());
    }
}
