package web.resource.dashboard;

import dto.server.response.ProgramData;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.context.AppContext;
import web.exception.BadAuthorizationException;
import web.resource.AuthorizingServlet;
import web.utils.ServletUtils;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/dashboard/program-list"})
public class ProgramListServlet extends AuthorizingServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        AppContext context = ServletUtils.getAppContext(getServletContext());

        try {
            authorize(req, resp);
        } catch (BadAuthorizationException e) {
            return;
        }

        List<ProgramData> programsData;
        synchronized (getServletContext()) {
            programsData = context.getEngine().getAvaliablePrograms().stream()
                    .map(functionIdentifier ->
                            ServletUtils.buildProgramDataObject(functionIdentifier, 0,context)
                    )
                    .toList();
        }

        resp.setContentType("application/json");
        ServletUtils.GsonInstance.toJson(programsData, resp.getWriter());
    }
}
