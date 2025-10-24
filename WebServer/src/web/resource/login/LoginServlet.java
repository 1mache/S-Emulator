package web.resource.login;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import web.utils.ServletUtils;

import java.io.IOException;

@WebServlet(urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    public static String USERNAME_PARAM = "username";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var appContext = ServletUtils.getAppContext(getServletContext());
        var userManager = appContext.getUserManager();
        String username = request.getParameter(USERNAME_PARAM);
        if(username == null){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("No username provided");
            return;
        }

        if(userManager.addUser(username)){
            HttpSession session = request.getSession(true);
            session.setAttribute(ServletUtils.USERNAME_ATR_NAME, username);
            response.setStatus(HttpServletResponse.SC_OK);
        }
        else{
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            response.getWriter().println("User with this name already exists");
        }

    }
}
