package web.resource.debug;

import dto.server.response.DebugStateInfo;
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

@WebServlet(urlPatterns = "/debug/resume")
public class ResumeDebugServlet extends AuthorizingServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user;
        try {
            user = authorize(req, resp);
        } catch (BadAuthorizationException e) {
            return;
        }

        AppContext appContext = ServletUtils.getAppContext(getServletContext());
        String username = user.getName();

        try {
            DebugHandle debugHandle = appContext.getDebugHandle(username);
            if(debugHandle.resume()) {
                appContext.removeDebugHandle(username);
                var result = debugHandle.getResult();
                resp.setContentType("application/json");
                ServletUtils.GsonInstance.toJson(
                        new DebugStateInfo(true, false ,result.variableMap(), result.cycles(), -1),
                        resp.getWriter()
                );
            }
            else{ // execution was paused on breakpoint
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
        } catch (NotInDebugException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("User" + username + " is not in Debug session");
        } catch(DebugStateException e){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Invalid debug state. " + e.getMessage());
        }
    }
}
