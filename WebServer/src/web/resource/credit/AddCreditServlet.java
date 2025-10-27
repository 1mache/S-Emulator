package web.resource.credit;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.exception.BadAuthorizationException;
import web.resource.AuthorizingServlet;
import web.user.User;

import java.io.IOException;

@WebServlet(urlPatterns = {"/add-credit"})
public class AddCreditServlet extends AuthorizingServlet {
    private static final String CREDIT_PARAM_NAME = "creditamount";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String creditParam = req.getParameter(CREDIT_PARAM_NAME);
        if(creditParam == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Missing " + CREDIT_PARAM_NAME + " parameter");
            return;
        }

        int credits;

        try {
            credits = Integer.parseInt(creditParam);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Not a number sent via " + CREDIT_PARAM_NAME + " parameter");
            return;
        }

        User user;
        try {
            user = authorize(req, resp);
        } catch (BadAuthorizationException e) {
            return;
        }

        user.addCredits(credits);
    }
}
