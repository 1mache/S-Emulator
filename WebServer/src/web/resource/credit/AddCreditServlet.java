package web.resource.credit;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.utils.ServletUtils;

import java.io.IOException;

@WebServlet(urlPatterns = {"/add-credit"})
public class AddCreditServlet extends HttpServlet {
    private static final String CREDIT_PARAM_NAME = "creditamount";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String creditParam = req.getParameter(CREDIT_PARAM_NAME);
        if(creditParam == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Missing " + CREDIT_PARAM_NAME + " parameter");
            return;
        }

        int credits;

        try {
            credits = Integer.parseInt(creditParam);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Not a number sent via " + CREDIT_PARAM_NAME + " parameter");
            return;
        }

        String username = ServletUtils.getUsernameFromRequest(req);
        var appContext = ServletUtils.getAppContext(getServletContext());
        var userManager = appContext.getUserManager();
        if(!userManager.userExists(username)){
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        var user = userManager.getUser(username);
        user.addCredits(credits);
    }
}
