package gui.components.instruction.table;

import engine.api.dto.InstructionPeek;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

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

    private ObservableList<InstructionPeek> instructions;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        instructions = FXCollections.observableArrayList();
        instructionTable.setPlaceholder(new Label("No instructions to display"));

        lineNumberColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().lineId())
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
    }

    public void setInstructions(List<InstructionPeek> instructions){
        this.instructions.addAll(instructions);
        instructionTable.setItems(this.instructions);
    }
}
