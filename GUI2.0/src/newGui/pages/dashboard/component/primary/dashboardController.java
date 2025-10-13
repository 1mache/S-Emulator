package newGui.pages.dashboard.component.primary;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import newGui.pages.dashboard.component.availableFunctions.availableFunctionsController;
import newGui.pages.dashboard.component.availablePrograms.availableProgramsController;
import newGui.pages.dashboard.component.history.historyTableController;
import newGui.pages.dashboard.component.top.topController;
import newGui.pages.dashboard.component.usersInfo.usersTableInfoController;
import newGui.pages.primary.mainClientAppController;
import java.net.URL;
import java.util.ResourceBundle;
import static util.Constants.*;

public class dashboardController implements Initializable {

    private mainClientAppController mainClientAppController;

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



    private void onActivated() {
//        if (topController != null)                topController.refresh();
//        if (availableProgramsController != null)  availableProgramsController.loadPrograms();
//        if (availableFunctionsController != null) availableFunctionsController.loadFunctions();
//        if (userTableInfoController != null)     usersTableInfoController.reloadUsers();
//        if (historyTableController != null)       historyTableController.reloadHistory();
    }

    public void setMainClientAppController(mainClientAppController mainAppController) {
        this.mainClientAppController = mainAppController;
    }


}
