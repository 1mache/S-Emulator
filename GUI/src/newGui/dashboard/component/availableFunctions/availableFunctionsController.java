package newGui.dashboard.component.availableFunctions;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

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

    @Override public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}

