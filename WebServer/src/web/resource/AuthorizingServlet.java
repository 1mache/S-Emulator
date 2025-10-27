package web.resource;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import web.exception.BadAuthorizationException;
import web.exception.UserNameException;
import web.user.User;
import web.utils.ServletUtils;

import java.io.IOException;

public abstract class AuthorizingServlet extends HttpServlet {
    protected static final String USERNAME_PARAM = "username";

    public User authorize(HttpServletRequest request, HttpServletResponse response) throws IOException, BadAuthorizationException {
        return authorize(request, response, null);
    }

    public User authorize(HttpServletRequest request, HttpServletResponse response, String username) throws IOException, BadAuthorizationException {
        User user;
        var userManager = ServletUtils.getAppContext(getServletContext()).getUserManager();
        try {
            if(username == null) username = getUsernameFromRequest(request); // if no username provided get ut from session
        } catch (UserNameException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(e.getMessage());
            throw new BadAuthorizationException(e.getMessage());
        }

        if(!userManager.userExists(username)){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("User " + username + " unauthorized");
            throw new BadAuthorizationException("Unauthorized");
        }
        else
            user = userManager.getUser(username);

        return user;
    }

    public String getUsernameFromRequest(HttpServletRequest request) throws UserNameException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        String username = (String) session.getAttribute(ServletUtils.USERNAME_ATR_NAME);
        if(username == null)
            throw new UserNameException("Username is null");

        username = username.strip();
        if(username.isEmpty())
            throw new UserNameException("Username is empty");

        return username;
    }
}
