package web.resource.debug;

import dto.server.request.BreakpointRequest;
import engine.api.debug.DebugHandle;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.exception.BadAuthorizationException;
import web.exception.NotInDebugException;
import web.resource.AuthorizingServlet;
import web.user.User;
import web.utils.ServletUtils;

import java.io.IOException;

@WebServlet(urlPatterns = "/debug/breakpoint")
public class BreakpointServlet extends AuthorizingServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User user;
        try {
            user = authorize(req, resp);
        } catch (BadAuthorizationException e) {
            return;
        }

        var appContext = ServletUtils.getAppContext(getServletContext());

        try {
            DebugHandle debugHandle = appContext.getDebugHandle(user.getName());
            BreakpointRequest breakpointRequest = ServletUtils.GsonInstance.fromJson(req.getReader(), BreakpointRequest.class);
            if(breakpointRequest.remove())
                debugHandle.removeBreakpoint(breakpointRequest.line());
            else
                debugHandle.addBreakpoint(breakpointRequest.line());

        } catch (NotInDebugException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Debugger couldn't step over. " + e.getMessage());
        }
    }
}
