package web.resource.debug;

import dto.server.request.BreakpointRequest;
import engine.api.debug.DebugHandle;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.exception.NotInDebugException;
import web.utils.ServletUtils;

import java.io.IOException;

@WebServlet(urlPatterns = "/debug/breakpoint")
public class BreakpointServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = ServletUtils.getUsernameFromRequest(req);
        var appContext = ServletUtils.getAppContext(getServletContext());
        if(username == null || !appContext.getUserManager().userExists(username)) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            DebugHandle debugHandle = appContext.getDebugHandle(username);
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
