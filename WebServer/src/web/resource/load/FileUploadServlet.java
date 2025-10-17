package web.resource.load;

import engine.loader.exception.UnknownFunctionException;
import engine.loader.exception.UnknownLabelException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import web.exception.InvalidUserException;
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
public class FileUploadServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");

        Collection<Part> parts = request.getParts();

        String username = ServletUtils.getUsernameFromRequest(request);

        // get all InputStreams from uploaded parts
        List<InputStream> inputStreams = new ArrayList<>();
        for (Part part : parts) {
            if (part.getSubmittedFileName() != null) { // ignore form fields
                inputStreams.add(part.getInputStream());
            }
        }

        // combine all InputStreams into one
        InputStream fileContent = new SequenceInputStream(Collections.enumeration(inputStreams));

        var appContext = ServletUtils.getAppContext(getServletContext());
        try {
            appContext.loadProgram(username, fileContent);
        } catch (UnknownFunctionException | UnknownLabelException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(e.getMessage());
        } catch (InvalidUserException e){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(e.getMessage());
        }
    }
}
