package web.resource.debug;

import dto.server.request.StartDebugRequest;
import dto.server.response.DebugStateInfo;
import engine.api.debug.DebugHandle;
import engine.debugger.exception.DebugStateException;
import engine.instruction.Architecture;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.context.AppContext;
import web.exception.BadAuthorizationException;
import web.resource.AuthorizingServlet;
import web.user.CreditExecutionLimiter;
import web.user.User;
import web.utils.ServletUtils;

import java.io.IOException;

@WebServlet(name = "StartDebugServlet", urlPatterns = {"/debug/start"})
public class StartDebugServlet extends AuthorizingServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user;
        try {
            user = authorize(req, resp);
        } catch (BadAuthorizationException e) {
            return;
        }
        String username = user.getName();
        AppContext appContext = ServletUtils.getAppContext(getServletContext());

        var engine = appContext.getEngine();
        StartDebugRequest debugRequest = ServletUtils.GsonInstance.fromJson(req.getReader(), StartDebugRequest.class);

        Architecture arch = engine.getArchitectureOf(debugRequest.programName(), debugRequest.expansionDegree());
        if(!ServletUtils.chargeArchitectureCost(user, arch)){
            ServletUtils.GsonInstance.toJson(constructFailedStartResponse(), resp.getWriter());
            return;
        }

        CreditExecutionLimiter creditLimiter = new CreditExecutionLimiter(user);
        DebugHandle debugHandle;
        synchronized (getServletContext()) {
            debugHandle = engine.startDebugSession(
                    debugRequest.programName(),
                    debugRequest.expansionDegree(),
                    debugRequest.inputs(),
                    user.getRunHistory(),
                    debugRequest.breakpoints(),
                    creditLimiter
            );
        }
        // store the debug handle for the user
        appContext.setDebugHandle(username, debugHandle);

        boolean isRunFinished;
        try {
            isRunFinished = debugHandle.startDebug();
        }
        catch (DebugStateException e){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println(e.getMessage());
            return;
        }

        if(isRunFinished) {
            appContext.removeDebugHandle(username);
            var result = debugHandle.getResult();
            resp.setContentType("application/json");
            ServletUtils.GsonInstance.toJson(
                    new DebugStateInfo(true, false, result.variableMap(), result.cycles(), -1),
                    resp.getWriter()
            );
        }
        else // execution was paused on breakpoint
        {
            var result = debugHandle.getResult();
            resp.setContentType("application/json");
            ServletUtils.GsonInstance.toJson(
                    new DebugStateInfo(false,
                            result.isStoppedEarly(),
                            result.variableMap(),
                            result.cycles(),
                            debugHandle.whichLine().orElse(-1)
                    ),
                    resp.getWriter()
            );
        }
    }

    private DebugStateInfo constructFailedStartResponse() {
        return new DebugStateInfo(
                false, true, null, null, null
        );
    }
}
