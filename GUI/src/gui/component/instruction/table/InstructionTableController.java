package gui.component.instruction.table;

import engine.api.dto.InstructionPeek;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class InstructionTableController implements Initializable {

    @FXML
    private TableView<InstructionPeek> instructionTable;

    @FXML
    private TableColumn<InstructionPeek, Integer> lineNumberColumn;

    @FXML
    private TableColumn<InstructionPeek, String> labelColumn;

    @FXML
    private TableColumn<InstructionPeek, String> baseOrSynthColumn;

    @FXML
    private TableColumn<InstructionPeek, String> instructionColumn;

    @FXML
    private TableColumn<InstructionPeek, Integer> cyclesColumn;

    private final ObservableList<InstructionPeek> instructions = FXCollections.observableArrayList();
    private final Set<EventHandler<RowClickAction>> rowClickListeners = new HashSet<>();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setPlaceholderMessage("No instructions to display");

        lineNumberColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().lineId() + 1)
        );

        labelColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().label())
        );

        baseOrSynthColumn.setCellValueFactory(cellData ->
            new ReadOnlyStringWrapper(
                    cellData.getValue().isSynthetic() ? "S" : "B"
            ) // display S for synthetic, B for base
        );

        instructionColumn.setCellValueFactory(cellData ->
                new ReadOnlyStringWrapper(cellData.getValue().stringRepresentation())
        );

        cyclesColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().cycles())
        );

        instructionTable.setRowFactory(tv -> {
            TableRow<InstructionPeek> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    InstructionPeek rowData = row.getItem();
                    onRowClick(rowData);
                }
            });
            return row;
        });
    }

    public void setPlaceholderMessage(String message){
        instructionTable.setPlaceholder(new Label(message));
    }

    public void setInstructions(List<InstructionPeek> instructions){
        this.instructions.clear();
        this.instructions.addAll(instructions);
        instructionTable.setItems(this.instructions);
    }

    public void addRowClickListener(EventHandler<RowClickAction> listener){
        rowClickListeners.add(listener);
    }

    public void clear(){
        instructionTable.setItems(null);
    }

    private void onRowClick(InstructionPeek rowData) {
        var listeners = Set.copyOf(rowClickListeners);
        listeners.forEach(
                listener -> listener.handle(new RowClickAction(rowData))
        );
    }
}
