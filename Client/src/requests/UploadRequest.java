package requests;

import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import util.Constants;
import java.io.File;

public class UploadRequest {

    public static Request build( File xmlFile) {

        // Create file body (content type: application/xml)
        RequestBody fileBody = RequestBody.create(xmlFile, Constants.MEDIA_TYPE_XML);

        // Build multipart body
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", xmlFile.getName(), fileBody)
                .build();

        // Build request to /upload endpoint
        return new Request.Builder()
                .url(Constants.UPLOAD)
                .post(requestBody)
                .build();
    }
}

