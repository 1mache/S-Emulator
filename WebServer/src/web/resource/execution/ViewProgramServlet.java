package web.resource.execution;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.context.AppContext;
import web.exception.BadAuthorizationException;
import web.resource.AuthorizingServlet;
import web.utils.ServletUtils;

import java.io.IOException;

@WebServlet(urlPatterns = {"/execution/view-program"})
public class ViewProgramServlet extends AuthorizingServlet {
    private static final String PROGRAM_NAME_PARAM = "programName";
    private static final String EXP_DEGREE_PARAM = "expansionDegree";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            // just an authorization check
            authorize(req, resp);
        } catch (BadAuthorizationException e) {
            return;
        }

        String programName = req.getParameter(PROGRAM_NAME_PARAM);
        if(programName == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Please provide a program name at " + PROGRAM_NAME_PARAM);
            return;
        }

        int expansionDegree;
        try {
            expansionDegree = Integer.parseInt(req.getParameter(EXP_DEGREE_PARAM));
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Please provide a number at " +  EXP_DEGREE_PARAM);
            return;
        }

        resp.setContentType("application/json");
        AppContext appContext = ServletUtils.getAppContext(getServletContext());
        synchronized (getServletContext()) {
            ServletUtils.GsonInstance.toJson(
                    appContext.getEngine().getProgramPeek(programName, expansionDegree),
                    resp.getWriter()
            );
        }
    }
}
