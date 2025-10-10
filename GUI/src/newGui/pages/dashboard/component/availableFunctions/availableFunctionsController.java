package newGui.pages.dashboard.component.availableFunctions;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;


public class availableFunctionsController implements Initializable {


    @FXML private Label availbaleFunctions;
    @FXML private TableColumn<?, ?> averageCreditCost;
    @FXML private TableView<?> historyTable;
    @FXML private ScrollPane mainAvailbaleFunctions;
    @FXML private TableColumn<?, ?> maxLevel;
    @FXML private TableColumn<?, ?> name;
    @FXML private TableColumn<?, ?> numberOfInstructions;
    @FXML private TableColumn<?, ?> runs;
    @FXML private TableColumn<?, ?> uploadBy;
    @FXML private Button execute;

    @Override public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}

