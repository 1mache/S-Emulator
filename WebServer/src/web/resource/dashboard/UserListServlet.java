package web.resource.dashboard;

import dto.server.response.UserData;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.utils.ServletUtils;

import java.io.IOException;

@WebServlet(urlPatterns = {"/dashboard/user-list"})
public class UserListServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var appContext = ServletUtils.getAppContext(getServletContext());
        var userManager = appContext.getUserManager();
        var userData = userManager.getUsers().stream()
                .map(user -> {
                    String username = user.getName();
                    return new UserData(
                            username,
                            user.getTotalCredits(),
                            user.getUsedCredits(),
                            appContext.getUserPrograms(username).size(),
                            appContext.getUserFunctions(username).size(),
                            user.getRunCount()
                    );
                }
                ).toList();

        resp.setContentType("application/json");
        ServletUtils.GsonInstance.toJson(userData, resp.getWriter());
    }
}
