package web.resource.debug;

import engine.api.debug.DebugHandle;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.exception.NotInDebugException;
import web.utils.ServletUtils;

import java.io.IOException;

@WebServlet(urlPatterns = "/debug/stop")
public class StopDebugServlet extends HttpServlet {
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
            debugHandle.stopDebug();
            appContext.removeDebugHandle(username); // debug session over
        } catch (NotInDebugException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("User" + username + " is not in Debug session");
        }
    }
}
