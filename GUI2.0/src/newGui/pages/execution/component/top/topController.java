package newGui.pages.execution.component.top;

import dto.ProgramPeek;
import dto.server.response.ProgramData;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import newGui.pages.execution.component.primary.mainExecutionController;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import requests.ProgramInfoForRun;
import requests.ProgramInfoRequest;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class topController {

    private mainExecutionController mainExecutionController;

    @FXML private Label userName;
    @FXML private TextField availableCredits;
    @FXML private ComboBox<Integer> degreeSelection;
    @FXML private ComboBox<String> highlightSelection;

    public void setMainExecutionController(mainExecutionController mainExecutionController) {
        this.mainExecutionController = mainExecutionController;
    }

    @FXML
    void degreeSelectionListener(ActionEvent event) {
//        final ProgramPeek[] programPeek = new ProgramPeek[1];
//        final ProgramData[] moreData = new ProgramData[1];
//
//        String programName = mainExecutionController.getProgramName();
//        Request programRequest = ProgramInfoForRun.build(programName, degreeSelection.getValue());
//        HttpClientUtil.runAsync(programRequest, new Callback() {
//
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                ProgramInfoForRun.onFailure(e);
//            }
//
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                programPeek[0] = ProgramInfoForRun.onResponse(response);
//                Request moreInfoRequest = ProgramInfoRequest.build(programPeek[0].name());
//                HttpClientUtil.runAsync(moreInfoRequest, new Callback() {
//
//                    @Override
//                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
//                        ProgramInfoRequest.onFailure(e);
//                    }
//
//                    @Override
//                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                        moreData[0] = ProgramInfoRequest.onResponse(response);
//                        Platform.runLater(() -> {
//                            mainExecutionController.setProgramPeek(programPeek[0], moreData[0]);
//
//                        });
//                    }
//                });
//            }
//        });

    }

    @FXML
    void highlightSelectionListener(ActionEvent event) {

    }


    public void init(StringProperty userNameProperty, long credits) {
        userName.textProperty().bind(Bindings.concat("Hello ", userNameProperty));
        availableCredits.setText(String.valueOf(credits));
    }

    private void setHighlight(ProgramPeek programPeek) {

        List<String> highlightOptions = new ArrayList<>();
        highlightOptions.addAll(programPeek.inputVariables());
        highlightOptions.addAll(programPeek.workVariables());
        highlightOptions.addAll(programPeek.labelsUsed());

        highlightSelection.setPromptText("Choose Highlight");
        highlightSelection.getItems().addAll(highlightOptions);
    }

    public void set(ProgramPeek programPeek, ProgramData moreData) {
        setHighlight(programPeek);
        setDegree(moreData.getMaxExpansionDegree());

    }

    private void setDegree(int maxDegree) {
        int capped = Math.max(0, maxDegree);

        degreeSelection.setPromptText("Choose expansion degree");
        degreeSelection.getItems().clear();

        // Add integer values directly
        for (int i = 0; i <= capped; i++) {
            degreeSelection.getItems().add(i);
        }

        // Optional: set default selection (for example 0)
        degreeSelection.getSelectionModel().select(0);
    }



    public long getCredits() {
        String credits = availableCredits.getText();
        return Long.parseLong(credits);
    }

    public int getDegreeComboBoxValue() {
        return degreeSelection.getValue();
    }
}
