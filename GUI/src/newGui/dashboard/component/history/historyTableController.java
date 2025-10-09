package newGui.dashboard.component.history;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

public class historyTableController {

    @FXML
    private TableColumn<?, ?> Architecture;

    @FXML
    private TableColumn<?, ?> Cycels;

    @FXML
    private TableColumn<?, ?> FunctionOrProgram;

    @FXML
    private Label History;

    @FXML
    private TableColumn<?, ?> Level;

    @FXML
    private TableColumn<?, ?> Name;

    @FXML
    private TableColumn<?, ?> Number;

    @FXML
    private TableColumn<?, ?> Result;

    @FXML
    private TableView<?> historyTable;

    @FXML
    private VBox rght;

}
