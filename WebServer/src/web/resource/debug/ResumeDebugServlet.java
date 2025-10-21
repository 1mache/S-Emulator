package web.resource.debug;

import dto.server.response.DebugStateInfo;
import engine.api.debug.DebugHandle;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.exception.NotInDebugException;
import web.utils.ServletUtils;

import java.io.IOException;

@WebServlet(urlPatterns = "debug/resume")
public class ResumeDebugServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = ServletUtils.getUsernameFromRequest(req);
        var appContext = ServletUtils.getAppContext(getServletContext());
        if(username == null || !appContext.getUserManager().userExists(username)) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            DebugHandle debugHandle = appContext.getDebugHandle(username);
            if(debugHandle.resume()) {
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
        } catch (NotInDebugException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("User" + username + " is not in Debug session");
        }
    }
}
