package web.resource.dashboard;

import engine.api.SLanguageEngine;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.context.AppContext;
import web.utils.ServletUtils;

import java.io.IOException;

@WebServlet(urlPatterns = {"/dashboard/program-list"})
public class ProgramListServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AppContext context = ServletUtils.getAppContext(getServletContext());
        String username = ServletUtils.getUsernameFromRequest(req);
        if(username == null || !context.getUserManager().userExists(username)){
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        SLanguageEngine engine = context.getEngine();
        
        var programsData = context.getEngine().getAvaliablePrograms().stream()
                .map(functionIdentifier ->
                        ServletUtils.buildProgramDataObject(functionIdentifier, context)
                )
                .toList();

        resp.setContentType("application/json");
        ServletUtils.GsonInstance.toJson(programsData, resp.getWriter());
    }
}
