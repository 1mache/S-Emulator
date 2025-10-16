package newGui.pages.dashboard.component.availablePrograms;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

public class availableProgramsController {

    // Functions Table
    @FXML private TableView<?> programsTable;
    @FXML private TableColumn<?, ?> averageCreditCost;
    @FXML private TableColumn<?, ?> maxLevel;
    @FXML private TableColumn<?, ?> name;
    @FXML private TableColumn<?, ?> numberOfInstructions;
    @FXML private TableColumn<?, ?> runs;
    @FXML private TableColumn<?, ?> uploadBy;

    // Buttons
    @FXML private Button executeProgram;


    @FXML
    void executeProgramListener(ActionEvent event) {

    }

}
