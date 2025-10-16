package newGui.pages.dashboard.component.history;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class historyTableController {

    // Won't Use
    @FXML private ScrollPane mainHistory;

    // History Table
    @FXML private TableView<?> historyTable;
    @FXML private TableColumn<?, ?> architecture;
    @FXML private TableColumn<?, ?> cycels;
    @FXML private TableColumn<?, ?> functionOrProgram;
    @FXML private TableColumn<?, ?> level;
    @FXML private TableColumn<?, ?> name;
    @FXML private TableColumn<?, ?> number;
    @FXML private TableColumn<?, ?> result;


//    public void initialize(URL url, ResourceBundle resourceBundle) {
//        //historyTable.setPlaceholder(new Label("No history to display"));
//
//    }
}
