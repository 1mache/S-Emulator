package requests;

import util.Constants;

import java.io.File;
import java.nio.file.Path;
import okhttp3.Request;
import okhttp3.RequestBody;

public class LoadFileRequest {

        private LoadFileRequest() {}

        public static Request build(Path xmlPath) {

            String name = xmlPath.getFileName().toString().toLowerCase();
            File f = xmlPath.toFile();
            if (!f.exists() || !f.isFile()) {
                throw new IllegalArgumentException("File does not exist: " + xmlPath);
            }

            // Body: send file content only
            RequestBody body = RequestBody.create(f, Constants.MEDIA_TYPE_XML);

            // Build POST request to your server endpoint
            return new Request.Builder()
                    .url(Constants.LOAD_FILE_PAGE)
                    .post(body)
                    .build();
        }
    }

