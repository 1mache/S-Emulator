package web.resource.dashboard;

import dto.server.response.FunctionData;
import engine.api.SLanguageEngine;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import web.context.AppContext;
import web.utils.ServletUtils;

import java.io.IOException;

@WebServlet(urlPatterns = {"/dashboard/function-list"})
public class ProgramListServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AppContext context = ServletUtils.getAppContext(getServletContext());
        HttpSession session = req.getSession(true);

        String username = (String) session.getAttribute(ServletUtils.USERNAME_ATR_NAME);
        if(username == null || !context.getUserManager().userExists(username)){
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        SLanguageEngine engine = context.getEngine();
        
        var functionsData = context.getEngine().getAvaliablePrograms().stream()
                .map(functionIdentifier -> {
                            String name = functionIdentifier.name();
                            return new FunctionData(
                                    name,
                                    functionIdentifier.userString(),
                                    functionIdentifier.isProgram(),
                                    context.getFunctionOwner(name),
                                    engine.instructionCountOf(name),
                                    engine.getMaxExpansionDegree(name),
                                    engine.getRunCountOf(name),
                                    engine.getAverageCostOf(name)
                            );
                        }
                )
                .toList();

        String json = ServletUtils.GsonInstance.toJson(functionsData);
        resp.setContentType("application/json");
        resp.getWriter().write(json);
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
