package requests;

import dto.server.response.ProgramData;
import javafx.application.Platform;
import Alerts.Alerts;
import newGui.pages.dashboard.component.primary.dashboardController;
import okhttp3.*;
import util.Constants;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProgramListRequest {

    public static Request build() {

        HttpUrl url = HttpUrl.parse(Constants.PROGRAM_LIST)
                .newBuilder()
                .build();


        return new Request.Builder()
                .url(url)
                .get()
                .build();
    }

    public static void onResponse(Response response, dashboardController dashboardController) {
        String responseBody;
        try {
            responseBody = response.body().string();
        } catch (Exception e) {
            Platform.runLater(() ->
                    Alerts.badBody(e.getMessage())
            );
            return;
        }

        if (response.code() != 200) {
            Platform.runLater(() -> {
                Alerts.serverBadAnswer(responseBody);
            });
        } else {
            ProgramData[] programDataArray = Constants.GSON_INSTANCE.fromJson(responseBody, ProgramData[].class);
            List<ProgramData> programDataList = new ArrayList<>(List.of(programDataArray));
            if (!programDataList.isEmpty() && programDataList != null) {
                // dashboardController.updateProgramList(programDataList);
                 dashboardController.updateAllProgramData(programDataList);

            }
        }
    }

    public static void onFailure(IOException e) {
        Platform.runLater(() -> {
            Alerts.serverProblamResponse(e.getMessage());
        });
    }

}



//package requests;
//
//import com.google.gson.reflect.TypeToken;
//import dto.server.response.ProgramData;
//import newGui.pages.dashboard.component.primary.dashboardController;
//import okhttp3.HttpUrl;
//import okhttp3.Request;
//import okhttp3.Response;
//import util.Constants;
//
//import java.io.IOException;
//import java.lang.reflect.Type;
//import java.util.Collections;
//import java.util.List;
//import java.util.Objects;
//
///** Single request that returns all programs & functions together. No UI here. */
//public class ProgramListRequest {
//
//    /** Build a GET request to fetch all items (programs+functions). */
//    public static Request build() {
//        HttpUrl url = Objects.requireNonNull(HttpUrl.parse(Constants.PROGRAM_LIST))
//                .newBuilder()
//                .build();
//
//        return new Request.Builder()
//                .url(url)
//                .get()
//                .build();
//    }
//
//    /** Handle async response: parse JSON and ask the dashboard to update BOTH tables. */
//    public static void onResponse(Response response, dashboardController dashboardController) {
//        try (response) {
//            ensureSuccess(response);
//            String json = (response.body() != null) ? response.body().string() : "[]";
//            List<ProgramData> list = parse(json);
//            // תמיד מעדכנים (גם אם ריק) כדי לרוקן UI כשאין נתונים
//            dashboardController.updateAllProgramData(list);
//        } catch (IOException ex) {
//            // שקט: לוג בלבד
//            System.err.println("Program list fetch failed: " + ex.getMessage());
//        }
//    }
//
//    /** Handle async failure (network, DNS, timeout, etc.). No UI here. */
//    public static void onFailure(IOException e) {
//        System.err.println("Program list request failed: " + e.getMessage());
//    }
//
//    // ---------------- helpers ----------------
//
//    private static void ensureSuccess(Response res) throws IOException {
//        if (res == null) throw new IOException("Null response");
//        if (!res.isSuccessful()) {
//            String body = (res.body() != null) ? res.body().string() : "";
//            throw new IOException("HTTP " + res.code() + (body.isEmpty() ? "" : (" - " + body)));
//        }
//    }
//
//    private static List<ProgramData> parse(String json) throws IOException {
//        try {
//            Type t = new TypeToken<List<ProgramData>>() {}.getType();
//            List<ProgramData> list = Constants.GSON_INSTANCE.fromJson(json, t);
//            return (list != null) ? list : Collections.emptyList();
//        } catch (Exception e) {
//            throw new IOException("Failed to parse ProgramData list: " + e.getMessage(), e);
//        }
//    }
//}

