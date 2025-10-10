package newGui.pages.dashboard.component.availablePrograms;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class availableProgramsController implements Initializable {

    @FXML private ScrollPane mainAvailbaleProgram;
    @FXML private Label availbalePrograms;
    @FXML private TableColumn<?, ?> averageCreditCost;
    @FXML private TableColumn<?, ?> maxLevel;
    @FXML private TableColumn<?, ?> name;
    @FXML private TableColumn<?, ?> numberOfInstructions;
    @FXML private TableView<?> programsTable;
    @FXML private TableColumn<?, ?> runs;
    @FXML private TableColumn<?, ?> uploadBy;
    @FXML private Button execute;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
