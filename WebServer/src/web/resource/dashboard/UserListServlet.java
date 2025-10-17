package web.resource.dashboard;

import dto.server.response.UserData;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.utils.ServletUtils;

import java.io.IOException;

@WebServlet(urlPatterns = {"/dashboard/user-list"})
public class UserListServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var appContext = ServletUtils.getAppContext(getServletContext());
        var userManager = appContext.getUserManager();
        var userData = userManager.getUsers().stream()
                .map(user -> {
                    String username = user.getUsername();
                    return new UserData(
                            username,
                            user.getTotalCredits(),
                            user.getUsedCredits(),
                            appContext.getUserPrograms(username).size(),
                            appContext.getUserFunctions(username).size()
                    );
                }
                ).toList();

        String json = ServletUtils.GsonInstance.toJson(userData);
        resp.setContentType("application/json");
        resp.getWriter().write(json);
    }
}
