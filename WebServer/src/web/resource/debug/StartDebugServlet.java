package web.resource.debug;

import dto.server.request.StartDebugRequest;
import dto.server.response.DebugStateInfo;
import engine.api.debug.DebugHandle;
import engine.debugger.exception.DebugStateException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.context.AppContext;
import web.utils.ServletUtils;

import java.io.IOException;

@WebServlet(name = "StartDebugServlet", urlPatterns = {"/debug/start"})
public class StartDebugServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var appContext = (AppContext) req.getServletContext().getAttribute("appContext");
        var userManager = appContext.getUserManager();

        String username = ServletUtils.getUsernameFromRequest(req);

        if(username == null || !appContext.getUserManager().userExists(username)) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        var engine = appContext.getEngine();
        StartDebugRequest debugRequest = ServletUtils.GsonInstance.fromJson(req.getReader(), StartDebugRequest.class);
        DebugHandle debugHandle;
        synchronized (getServletContext()) {
            debugHandle = engine.startDebugSession(
                    debugRequest.programName(),
                    debugRequest.expansionDegree(),
                    debugRequest.inputs(),
                    userManager.getUser(username).getRunHistory(),
                    debugRequest.breakpoints()
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
                    new DebugStateInfo(true, result.variableMap(), result.cycles(), -1),
                    resp.getWriter()
            );
        }
        else // execution was paused on breakpoint
        {
            var result = debugHandle.getResult();
            resp.setContentType("application/json");
            ServletUtils.GsonInstance.toJson(
                    new DebugStateInfo(false,
                            result.variableMap(),
                            result.cycles(),
                            debugHandle.whichLine().orElseThrow()
                    ),
                    resp.getWriter()
            );
        }
    }
}
