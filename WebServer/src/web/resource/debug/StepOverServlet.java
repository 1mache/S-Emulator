package web.resource.debug;

import dto.debug.DebugStepPeek;
import engine.api.debug.DebugHandle;
import engine.debugger.exception.DebugStateException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.context.AppContext;
import web.exception.BadAuthorizationException;
import web.exception.NotInDebugException;
import web.resource.AuthorizingServlet;
import web.user.User;
import web.utils.ServletUtils;

import java.io.IOException;

@WebServlet(urlPatterns = "/debug/step-over")
public class StepOverServlet extends AuthorizingServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user;
        try {
            user = authorize(req, resp);
        } catch (BadAuthorizationException e) {
            return;
        }
        String username = user.getName();
        AppContext appContext = ServletUtils.getAppContext(getServletContext());

        try {
            DebugHandle debugHandle = appContext.getDebugHandle(username);
            DebugStepPeek stepInfo = debugHandle.stepOver();
            resp.setContentType("application/json");
            ServletUtils.GsonInstance.toJson(stepInfo, resp.getWriter());
        } catch (NotInDebugException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("User" + username + " is not in Debug session");
        } catch(DebugStateException e){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Invalid debug state. " + e.getMessage());
        }
    }
}
