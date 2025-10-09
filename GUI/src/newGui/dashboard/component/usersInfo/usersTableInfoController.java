package newGui.dashboard.component.usersInfo;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class usersTableInfoController {

    @FXML
    private TableColumn<?, ?> CurrentCredit;

    @FXML
    private TableColumn<?, ?> Name;

    @FXML
    private TableColumn<?, ?> NumberOfNewFunctions;

    @FXML
    private TableColumn<?, ?> NumberOfNewPrograms;

    @FXML
    private TableColumn<?, ?> Runs;

    @FXML
    private Button UnselectButton;

    @FXML
    private TableColumn<?, ?> UsedCredit;

    @FXML
    private TableView<?> historyTable;

    @FXML
    private ScrollPane usersTable;

}
