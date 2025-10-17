package web.resource.dashboard;

import dto.server.response.ProgramData;
import engine.api.SLanguageEngine;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.context.AppContext;
import web.utils.ServletUtils;

import java.io.IOException;

@WebServlet(urlPatterns = {"/dashboard/function-list"})
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
                .map(functionIdentifier -> {
                            String name = functionIdentifier.name();
                            return new ProgramData(
                                    name,
                                    functionIdentifier.userString(),
                                    functionIdentifier.isMain(),
                                    context.getFunctionOwner(name),
                                    engine.instructionCountOf(name),
                                    engine.getMaxExpansionDegree(name),
                                    engine.getRunCountOf(name),
                                    engine.getAverageCostOf(name)
                            );
                        }
                )
                .toList();

        resp.setContentType("application/json");
        ServletUtils.GsonInstance.toJson(programsData, resp.getWriter());
    }
}
