package newGui.pages.execution.component.top;

import dto.ProgramPeek;
import dto.server.response.ProgramData;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

import java.util.ArrayList;
import java.util.List;

public class topController {

    // Won't Use
    @FXML private ScrollPane top;

    @FXML private Label userName;
    @FXML private Label availableCredits;

    @FXML private ComboBox<String> degreeSelection;
    @FXML private ComboBox<String> highlightSelection;

    @FXML
    void degreeSelectionListener(ActionEvent event) {

    }

    @FXML
    void highlightSelectionListener(ActionEvent event) {

    }


    public void init(StringProperty userNameProperty, long credits) {
        userName.textProperty().bind(userNameProperty);
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
        for (int i = 0; i <= capped; i++) {
            degreeSelection.getItems().add(String.valueOf(i));
        }
    }
}
