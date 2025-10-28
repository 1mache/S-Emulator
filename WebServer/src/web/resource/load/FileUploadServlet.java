package web.resource.load;

import engine.loader.exception.UnknownFunctionException;
import engine.loader.exception.UnknownLabelException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import web.exception.BadAuthorizationException;
import web.resource.AuthorizingServlet;
import web.user.User;
import web.utils.ServletUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@WebServlet(urlPatterns = {"/upload"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024, // 1 MB
        maxFileSize = 1024 * 1024 * 50,      // 50 MB
        maxRequestSize = 1024 * 1024 * 100 // 100 MB
)
public class FileUploadServlet extends AuthorizingServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");

        Collection<Part> parts = request.getParts();

        var appContext = ServletUtils.getAppContext(getServletContext());
        User user;
        try {
            user = authorize(request, response);
        } catch (BadAuthorizationException e) {return;}

        // get all InputStreams from uploaded parts
        List<InputStream> inputStreams = new ArrayList<>();
        for (Part part : parts) {
            if (part.getSubmittedFileName() != null) { // ignore form fields
                inputStreams.add(part.getInputStream());
            }
        }
        // combine all InputStreams into one
        InputStream fileContent = new SequenceInputStream(Collections.enumeration(inputStreams));

        var engine = appContext.getEngine();
        try {
            synchronized (getServletContext()) {
                List<String> addedFunctions = engine.loadProgramIncremental(fileContent, null);
                appContext.addProgramsFromUser(user.getName(), addedFunctions);
            }
        } catch (UnknownFunctionException | UnknownLabelException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(e.getMessage());
        }
    }
}
