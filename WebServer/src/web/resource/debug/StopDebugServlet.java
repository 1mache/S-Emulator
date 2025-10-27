package web.resource.debug;

import engine.api.debug.DebugHandle;
import engine.debugger.exception.DebugStateException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.exception.BadAuthorizationException;
import web.exception.NotInDebugException;
import web.resource.AuthorizingServlet;
import web.user.User;
import web.utils.ServletUtils;

import java.io.IOException;

@WebServlet(urlPatterns = "/debug/stop")
public class StopDebugServlet extends AuthorizingServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user;
        try {
            user = authorize(req, resp);
        } catch (BadAuthorizationException e) {
            return;
        }

        var appContext = ServletUtils.getAppContext(getServletContext());
        String username = user.getName();

        try {
            DebugHandle debugHandle = appContext.getDebugHandle(username);
            debugHandle.stopDebug();
            appContext.removeDebugHandle(username); // debug session over

            resp.setContentType("application/json");
            ServletUtils.GsonInstance.toJson(debugHandle.getResult(),  resp.getWriter());
        } catch (NotInDebugException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("User" + username + " is not in Debug session");
        } catch(DebugStateException e){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Invalid debug state. " + e.getMessage());
        }
    }
}
