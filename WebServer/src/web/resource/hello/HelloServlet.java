package web.resource.hello;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.utils.ServletUtils;

@WebServlet(name = "HelloServlet", urlPatterns = {"/hello"})
public class HelloServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws java.io.IOException {

        String username = ServletUtils.getUsernameFromRequest(request);

        response.setContentType("text/plain");
        if(username == null){
            response.getWriter().println("Hello, please log in at /login");
        } else {
            response.getWriter().println("Hello, " + username + "!");
        }
    }
}
