package web.resource.login;

import dto.server.request.LoginForm;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import web.utils.ServletUtils;

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet(urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        var appContext = ServletUtils.getAppContext(getServletContext());
        var userManager = appContext.getUserManager();

        try (BufferedReader reader = request.getReader()) {
            LoginForm loginForm = ServletUtils.GsonInstance.fromJson(reader, LoginForm.class);
            if(userManager.addUser(loginForm.username()))
                response.setStatus(HttpServletResponse.SC_OK);
            else
                response.setStatus(HttpServletResponse.SC_CONFLICT);
        }

    }
}
