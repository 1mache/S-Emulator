package web.resource.execution;

import dto.server.request.ProgramViewRequest;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.utils.ServletUtils;

import java.io.IOException;

@WebServlet(urlPatterns = {"/execution/view-program"})
public class ViewProgramServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = ServletUtils.getUsernameFromRequest(req);
        var appContext = ServletUtils.getAppContext(getServletContext());
        if(username == null || !appContext.getUserManager().userExists(username)){
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        var viewRequest = ServletUtils.GsonInstance.fromJson(req.getReader(), ProgramViewRequest.class);

        ServletUtils.GsonInstance.toJson(
                appContext.getEngine().getProgramPeek(viewRequest.programName(), viewRequest.expansionDegree()),
                resp.getWriter()
        );
    }
}
