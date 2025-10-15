package newGui.pages.execution.component.top;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

public class topController {

    @FXML
    private Label availableCredits;

    @FXML
    private ComboBox<?> degreeSelection;

    @FXML
    private ComboBox<?> highlightSelection;

    @FXML
    private ScrollPane top;

    @FXML
    private Label userName;

    @FXML
    void selectionListener(ActionEvent event) {

    }

}
