package newGui.pages.execution.component.primary;

import dto.InstructionPeek;
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
import java.util.List;

public class mainExecutionController {

    private mainClientAppController mainClientAppController;


    @FXML private topController topController;
    @FXML private instructionsController instructionsController;
    @FXML private executionController executionController;

    String programName;
    public String userName;
    private String architectureSelected;

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

        List<Integer> architectures = getArchitecturesFromInstructions(programPeek.instructions());
        int B = getBasicInstructionsCount(programPeek.instructions());
        int S = programPeek.instructions().size() - B;

        instructionsController.setSummaryLine(B, S, architectures);
    }

    private int getBasicInstructionsCount(List<InstructionPeek> instructions) {
        int count = 0;
        for (InstructionPeek instruction : instructions) {
            if (!instruction.isSynthetic()) {
                count++;
            }
        }
        return count;
    }

    private List<Integer> getArchitecturesFromInstructions(List<InstructionPeek> instructions) {
        // Initialize counters for each architecture
        int countI = 0;
        int countII = 0;
        int countIII = 0;
        int countIV = 0;

        // Loop through all instructions and count occurrences
        for (InstructionPeek instruction : instructions) {
            String arch = instruction.architecture();
            if (arch == null) continue; // skip if null

            switch (arch.trim()) {
                case "I" -> countI++;
                case "II" -> countII++;
                case "III" -> countIII++;
                case "IV" -> countIV++;
            }
        }

        // Return as list in order [I, II, III, IV]
        return List.of(countI, countII, countIII, countIV);
    }

    public String getProgramName() {
        return programName;
    }

    public void set(String programName, String userName) {
        final ProgramPeek[] programPeek = new ProgramPeek[1];
        final ProgramData[] moreData = new ProgramData[1];
        this.programName = programName;
        this.userName = userName;

        Request programRequest = ProgramInfoForRun.build(programName,0);
        HttpClientUtil.runAsync(programRequest, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                ProgramInfoForRun.onFailure(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                programPeek[0] = ProgramInfoForRun.onResponse(response);
                Request moreInfoRequest = ProgramInfoRequest.build(programName);
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

                        // Fill History Table
                        Request userHistoryRequest = requests.UserHistoryRequest.build(programName, userName);

                        HttpClientUtil.runAsync(userHistoryRequest, new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                requests.UserHistoryRequest.onFailure(e);
                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) {
                                requests.UserHistoryRequest.onResponse(response, executionController);
                            }
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

    public void updateHighlightedInstructions(List<Integer> numbersList) {
        instructionsController.updateHighlightedInstructions(numbersList);
    }

    public List<Integer> getBreakpoints() {
        return instructionsController.getBreakpointIndices();
    }

    public instructionsController getInstructionsController() {
        return instructionsController;
    }

    public boolean debugModeActive() {
        return executionController.debudgModeActive();
    }

    public void setSelectedArchitecture(String selectedArchitecture) {
        architectureSelected = selectedArchitecture;
    }

    public List<Integer> getArchitecturesCount() {
        return instructionsController.architectures;
    }

    public void setCredits(long credits) {
        topController.setCredits(credits);
    }
}