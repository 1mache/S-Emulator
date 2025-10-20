package web.resource.info;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.utils.ServletUtils;

import java.io.IOException;

@WebServlet(urlPatterns = "/info/program-info")
public class ProgramInfoServlet extends HttpServlet {
    private static final String PROGRAM_NAME_PARAM = "programName";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String programName = req.getParameter(PROGRAM_NAME_PARAM);
        var appContext = ServletUtils.getAppContext(getServletContext());
        if(programName == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Missing parameter: " + PROGRAM_NAME_PARAM);
            return;
        }

        if (appContext.getEngine().programNotLoaded(programName)){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Program " +  programName + " has not been loaded");
            return;
        }

        resp.setContentType("application/json");
        ServletUtils.GsonInstance.toJson(
                ServletUtils.buildProgramDataObject(appContext.getEngine().getProgramIdentifier(programName), appContext),
                resp.getWriter()
        );
    }
}
