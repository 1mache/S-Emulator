package newGui.pages.execution.component.instructions;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class instructionsController {

    @FXML
    private Label SelectedInstructionHistoryChain;

    @FXML
    private TextField SummaryLine;

    @FXML
    private TableColumn<?, ?> colBS;

    @FXML
    private TableColumn<?, ?> colCycles;

    @FXML
    private TableColumn<?, ?> colHistoryBS;

    @FXML
    private TableColumn<?, ?> colHistoryCycles;

    @FXML
    private TableColumn<?, ?> colHistoryInstruction;

    @FXML
    private TableColumn<?, ?> colHistoryLabel;

    @FXML
    private TableColumn<?, ?> colHistoryNumber;

    @FXML
    private TableColumn<?, ?> colInstruction;

    @FXML
    private TableColumn<?, ?> colLabel;

    @FXML
    private TableColumn<?, ?> colNumber;

    @FXML
    private TableView<?> instructionsHistoryTable;

    @FXML
    private TableView<?> instructionsTable;

    @FXML
    private VBox left;

    @FXML
    void showHistoryChain(MouseEvent event) {

    }

}
