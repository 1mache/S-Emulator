package newGui.pages.execution.component.primary;

import dto.ProgramPeek;
import dto.server.response.ProgramData;
import javafx.application.Platform;
import javafx.fxml.FXML;
import newGui.pages.execution.component.execution.executionController;
import newGui.pages.execution.component.instructions.instructionsController;
import newGui.pages.execution.component.top.topController;
import newGui.pages.primary.mainClientAppController;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import requests.ProgramInfoForRun;
import requests.ProgramInfoRequest;
import util.http.HttpClientUtil;
import java.io.IOException;

public class mainExecutionController {

    private mainClientAppController mainClientAppController;


    @FXML private topController topController;
    @FXML private instructionsController instructionsController;
    @FXML private executionController executionController;

    String programName;

    @FXML
    public void initialize() {
        topController.setMainExecutionController(this);
        instructionsController.setMainExecutionController(this);
        executionController.setMainExecutionController(this);
    }

    public void setMainClientAppController(mainClientAppController mainAppController) {
        this.mainClientAppController = mainAppController;
    }

    public void activate(long credits) {
        topController.init(mainClientAppController.getUserNameProperty(), credits);
    }


    public void setProgramPeek(ProgramPeek programPeek,  ProgramData moreData) {

        topController.set(programPeek, moreData);
        executionController.setProgramPeek(programPeek);
        instructionsController.setProgramPeek(programPeek.instructions());
        programName = programPeek.name();
    }

    public String getProgramName() {
        return programName;
    }

    public void set(String programName) {
        final ProgramPeek[] programPeek = new ProgramPeek[1];
        final ProgramData[] moreData = new ProgramData[1];

        Request programRequest = ProgramInfoForRun.build(programName,0);
        HttpClientUtil.runAsync(programRequest, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                ProgramInfoForRun.onFailure(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                programPeek[0] = ProgramInfoForRun.onResponse(response);
                Request moreInfoRequest = ProgramInfoRequest.build(programPeek[0].name());
                HttpClientUtil.runAsync(moreInfoRequest, new Callback() {

                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        ProgramInfoRequest.onFailure(e);
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        moreData[0] = ProgramInfoRequest.onResponse(response);
                        Platform.runLater(() -> {
                            setProgramPeek(programPeek[0], moreData[0]);

                        });
                    }
                });
            }
        });

    }

    public void returnToDashboard() {
        mainClientAppController.returnToDashboard(topController.getCredits());
    }

    public int getSelectedDgree() {
        return topController.getDegreeComboBoxValue();
    }
}
