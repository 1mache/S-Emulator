package newGui.pages.execution.component.top;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

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


}
