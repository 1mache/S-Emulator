package web.resource.info;

import dto.server.response.UserData;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.exception.BadAuthorizationException;
import web.resource.AuthorizingServlet;
import web.user.User;
import web.utils.ServletUtils;

import java.io.IOException;

@WebServlet(urlPatterns = "/info/user")
public class UserInfoServlet extends AuthorizingServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var appContext = ServletUtils.getAppContext(getServletContext());
        User user;
        try {
            user = authorize(req, resp);
        } catch (BadAuthorizationException e) {
            return;
        }
        String username = user.getName();

        UserData userData = new UserData(
                username,
                user.getTotalCredits(),
                user.getUsedCredits(),
                appContext.getUserPrograms(username).size(),
                appContext.getUserFunctions(username).size(),
                user.getRunCount()
        );

        resp.setContentType("application/json");
        ServletUtils.GsonInstance.toJson(userData, resp.getWriter());
    }
}
