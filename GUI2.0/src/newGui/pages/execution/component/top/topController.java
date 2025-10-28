package newGui.pages.execution.component.top;

import dto.ProgramPeek;
import dto.server.request.HighlightRequest;
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
import requests.HighlightInfoRequest;
import requests.ProgramInfoForRun;
import requests.ProgramInfoRequest;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class topController {

    private mainExecutionController mainExecutionController;

    @FXML private Label userName;
    @FXML private TextField availableCredits;

    @FXML private ComboBox<Integer> degreeSelection;
    private boolean populatingDegrees = false;
    private Integer lastUserSelectedDegree = null;
    private Integer currentMaxDegree = null;

    @FXML private ComboBox<String> highlightSelection;
    private boolean highlightReady = false;     // guards against firing during initial fill
    private String lastSentHighlight = null;    // avoids duplicate requests for same value

    public void setMainExecutionController(mainExecutionController mainExecutionController) {
        this.mainExecutionController = mainExecutionController;
    }

    @FXML
    void degreeSelectionListener(ActionEvent event) {

        // ignore events while we populate items programmatically
        if (populatingDegrees) return;

        Integer selected = degreeSelection.getValue();
        if (selected == null) {
            return; // no selection yet
        }

        lastUserSelectedDegree = selected;

        final ProgramPeek[] programPeek = new ProgramPeek[1];
        final ProgramData[] moreData = new ProgramData[1];

        String programName = mainExecutionController.getProgramName();
        Request programRequest = ProgramInfoForRun.build(programName, degreeSelection.getValue());
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
                            mainExecutionController.setProgramPeek(programPeek[0], moreData[0]);

                        });
                    }
                });
            }
        });

    }

    @FXML
    void highlightSelectionListener(ActionEvent event) {
        // Ignore if still initializing / populating
        if (!highlightReady) return;

        // Read current selection
        String selected = highlightSelection.getValue();
        if (selected == null || selected.isBlank()) return;

        // Avoid re-sending the exact same value
        if (selected.equals(lastSentHighlight)) return;

        // send highlight Request and update lastSentHighlight
        Request highlightRequest = HighlightInfoRequest.build( new HighlightRequest(
                mainExecutionController.getProgramName(),
                getDegreeComboBoxValue(),
                selected)
        );
        HttpClientUtil.runAsync(highlightRequest, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                HighlightInfoRequest.onFailure(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                HighlightInfoRequest.onResponse(response, mainExecutionController);
            }
        });
        lastSentHighlight = selected;
    }


    public void init(StringProperty userNameProperty, long credits) {
        userName.textProperty().bind(Bindings.concat("Hello ", userNameProperty));
        availableCredits.setText(String.valueOf(credits));
    }

    private void setHighlight(ProgramPeek programPeek) {

        highlightReady = false;

        List<String> highlightOptions = new ArrayList<>();
        highlightOptions.addAll(programPeek.inputVariables());
        highlightOptions.addAll(programPeek.workVariables());
        highlightOptions.addAll(programPeek.labelsUsed());

        highlightSelection.setPromptText("Choose Highlight");
        highlightSelection.getItems().setAll(highlightOptions);
        highlightReady = true;


    }

    public void set(ProgramPeek programPeek, ProgramData moreData) {

        Integer newMax = moreData.getMaxExpansionDegree();
        if (!Objects.equals(currentMaxDegree, newMax)) {
            setDegree(newMax);
        }

        setHighlight(programPeek);
        setDegree(moreData.getMaxExpansionDegree());
    }

    private void setDegree(int maxDegree) {
        int capped = Math.max(0, maxDegree);
        currentMaxDegree = capped;

        Integer prev = (lastUserSelectedDegree != null)
                ? lastUserSelectedDegree
                : degreeSelection.getValue();

        populatingDegrees = true;
        try {
            degreeSelection.setPromptText("Choose expansion degree");
            degreeSelection.getItems().clear();

            for (int i = 0; i <= capped; i++) {
                degreeSelection.getItems().add(i);
            }

            Integer toSelect = null;
            if (prev != null) {
                int cappedPrev = Math.min(prev, capped);
                if (degreeSelection.getItems().contains(cappedPrev)) {
                    toSelect = cappedPrev;
                }
            }

            if (toSelect != null) {
                degreeSelection.getSelectionModel().select(toSelect);
            } else if (degreeSelection.getSelectionModel().getSelectedItem() == null
                    && !degreeSelection.getItems().isEmpty()) {
                degreeSelection.getSelectionModel().select(0);
                lastUserSelectedDegree = 0;
            }
        } finally {
            populatingDegrees = false;
        }
    }

    public long getCredits() {
        String credits = availableCredits.getText();
        return Long.parseLong(credits);
    }

    public int getDegreeComboBoxValue() {
        Integer v = degreeSelection.getValue();
        return (v != null) ? v : 0;
    }

    public void setCredits(long credits) {
        availableCredits.setText(String.valueOf(credits));
    }
}