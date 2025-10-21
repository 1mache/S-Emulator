package web.resource.highlight;

import dto.server.request.HighlightRequest;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.utils.ServletUtils;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = "/highlight")
public class HighlightServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var appContext = ServletUtils.getAppContext(getServletContext());
        var engine = appContext.getEngine();

        HighlightRequest request = ServletUtils.GsonInstance.fromJson(req.getReader(), HighlightRequest.class);

        List<Integer> highlightedLines;
        synchronized (getServletContext()) {
            try {
                highlightedLines = engine.getInstructionsIdsThatUse(request.programName(),
                        request.expansionDegree(),
                        request.symbolToHighlight());
            } catch (IllegalArgumentException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println(e.getMessage());
                return;
            }
        }

        resp.setContentType("application/json");
        ServletUtils.GsonInstance.toJson(highlightedLines, resp.getWriter());
    }
}
