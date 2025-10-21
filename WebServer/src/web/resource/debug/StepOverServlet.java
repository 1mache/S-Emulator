package web.resource.debug;

import dto.debug.DebugStepPeek;
import engine.api.debug.DebugHandle;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.exception.NotInDebugException;
import web.utils.ServletUtils;

import java.io.IOException;

@WebServlet(urlPatterns = "/debug/step-over")
public class StepOverServlet extends HttpServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = ServletUtils.getUsernameFromRequest(req);
        var appContext = ServletUtils.getAppContext(getServletContext());
        if(username == null || !appContext.getUserManager().userExists(username)) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            DebugHandle debugHandle = appContext.getDebugHandle(username);
            if(debugHandle == null){
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println("User" + username + " is not in Debug session");
                return;
            }

            DebugStepPeek stepInfo = debugHandle.stepOver();
            resp.setContentType("application/json");
            ServletUtils.GsonInstance.toJson(stepInfo, resp.getWriter());
        } catch (NotInDebugException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Debugger couldn't step over. " + e.getMessage());
        }
    }
}
