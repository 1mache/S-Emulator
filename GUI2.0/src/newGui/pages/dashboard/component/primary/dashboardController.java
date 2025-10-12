package newGui.pages.dashboard.component.primary;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import newGui.pages.dashboard.component.availableFunctions.availableFunctionsController;
import newGui.pages.dashboard.component.availablePrograms.availableProgramsController;
import newGui.pages.dashboard.component.history.historyTableController;
import newGui.pages.dashboard.component.top.topController;
import newGui.pages.dashboard.component.usersInfo.usersTableInfoController;

import java.net.URL;
import java.util.ResourceBundle;

public class dashboardController implements Initializable {

    // top controller
    @FXML private topController topController;

    // right controllers
    @FXML private availableProgramsController availableProgramsController; // top right
    @FXML private availableFunctionsController availableFunctionsController; // bottom right

    // left controllers
    @FXML private usersTableInfoController userTableInfoController; // top left
    @FXML private historyTableController historyTableController; // bottom left



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


    }
}
