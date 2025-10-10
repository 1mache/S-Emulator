package newGui.pages.dashboard.component.usersInfo;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class usersTableInfoController implements Initializable {

    @FXML private TableColumn<?, ?> currentCredit;
    @FXML private TableColumn<?, ?> name;
    @FXML private TableColumn<?, ?> numberOfNewFunctions;
    @FXML private TableColumn<?, ?> numberOfNewPrograms;
    @FXML private TableColumn<?, ?> runs;
    @FXML private Button unselectButton;
    @FXML private TableColumn<?, ?> usedCredit;
    @FXML private ScrollPane users;
    @FXML private Label usersInformation;
    @FXML private TableView<?> usersTable;


    @FXML void UnselectButtonListener(ActionEvent event) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

}

