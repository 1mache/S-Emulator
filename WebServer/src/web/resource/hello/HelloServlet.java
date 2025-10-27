package web.resource.hello;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.exception.UserNameException;
import web.resource.AuthorizingServlet;
import web.utils.ServletUtils;

@WebServlet(name = "HelloServlet", urlPatterns = {"/hello"})
public class HelloServlet extends AuthorizingServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws java.io.IOException {

        String username;
        try {
            username = getUsernameFromRequest(request);
        } catch (UserNameException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(e.getMessage());
            return;
        }

        response.setContentType("text/plain");
        if(ServletUtils.getAppContext(getServletContext()).getUserManager().userExists(username)) {
            response.getWriter().println("Hello, " + username + "!");
        } else {
            response.getWriter().println("Hello, please log in at /login");
        }
    }
}
