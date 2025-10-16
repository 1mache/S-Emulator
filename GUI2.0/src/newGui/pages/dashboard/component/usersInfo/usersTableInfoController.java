package newGui.pages.dashboard.component.usersInfo;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class usersTableInfoController {

    // Won't Use
    @FXML private ScrollPane users;
    @FXML private Label usersInformation;

    // Buttons
    @FXML private Button unselectButton;

    // Users Table
    @FXML private TableView<?> usersTable;
    @FXML private TableColumn<?, ?> currentCredit;
    @FXML private TableColumn<?, ?> name;
    @FXML private TableColumn<?, ?> numberOfNewFunctions;
    @FXML private TableColumn<?, ?> numberOfNewPrograms;
    @FXML private TableColumn<?, ?> runs;
    @FXML private TableColumn<?, ?> usedCredit;

    @FXML void UnselectButtonListener(ActionEvent event) {

    }


}

