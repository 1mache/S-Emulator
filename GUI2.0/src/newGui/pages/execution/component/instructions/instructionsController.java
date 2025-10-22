package newGui.pages.execution.component.instructions;

import dto.InstructionPeek;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import newGui.pages.execution.component.primary.mainExecutionController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class instructionsController {

    mainExecutionController mainExecutionController;

    // Won't Use
    @FXML private VBox left;
    @FXML private Label SelectedInstructionHistoryChain;


    // Instructions Table
    @FXML private TableView<InstructionPeek> instructionsTable;
    @FXML private TableColumn<InstructionPeek, String> colBS;
    @FXML private TableColumn<InstructionPeek, Long> colCycles;
    @FXML private TableColumn<InstructionPeek, String> colArchitecture;
    @FXML private TableColumn<InstructionPeek, String> colInstruction;
    @FXML private TableColumn<InstructionPeek, String> colLabel;
    @FXML private TableColumn<InstructionPeek, Long> colNumber;

    private final Set<Integer> highlighted = new HashSet<>();



    // History Chain Table
    @FXML private TableView<InstructionPeek> instructionsHistoryTable;
    @FXML private TableColumn<InstructionPeek, ?> colHistoryLabel;
    @FXML private TableColumn<InstructionPeek, ?> colHistoryNumber;
    @FXML private TableColumn<InstructionPeek, ?> colHistoryBS;
    @FXML private TableColumn<InstructionPeek, ?> colHistoryCycles;
    @FXML private TableColumn<InstructionPeek, ?> colHistoryInstruction;
    @FXML private TableColumn<InstructionPeek, ?> colHistoryArchitecture;

    @FXML private TextField SummaryLine;

    public void setMainExecutionController(mainExecutionController mainExecutionController) {
        this.mainExecutionController = mainExecutionController;
    }

    @FXML
    void showHistoryChain(MouseEvent event) {

    }

    public void setProgramPeek(List<InstructionPeek> instructions) {
        instructionsTable.getItems().clear();
        instructionsTable.getItems().addAll(instructions);

        colInstruction.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().stringRepresentation()));

        colLabel.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().label()));

        colCycles.setCellValueFactory(data ->
                new SimpleLongProperty(data.getValue().cycles()).asObject());

        colNumber.setCellValueFactory(data ->
                new SimpleLongProperty(data.getValue().lineId()).asObject());

        colBS.setCellValueFactory(data -> {
            boolean isSynthetic = data.getValue().isSynthetic();
            String text = isSynthetic ? "S" : "B";
            return new SimpleStringProperty(text);
        });

        colArchitecture.setCellValueFactory(data -> new SimpleStringProperty(""));
        installRowHighlighter();

    }


    private void installRowHighlighter() {
        if (instructionsTable.getRowFactory() != null) return;

        instructionsTable.setRowFactory(tv -> new TableRow<InstructionPeek>() {
            @Override
            protected void updateItem(InstructionPeek item, boolean empty) {
                super.updateItem(item, empty);

                // Apply yellow background only for highlighted row indices
                if (!empty && highlighted.contains(getIndex())) {
                    setStyle("-fx-background-color: #fff59d;"); // light yellow
                } else {
                    setStyle(""); // reset
                }
            }
        });
    }


    public void updateHighlightedInstructions(List<Integer> indices) {
        highlighted.clear();
        if (indices != null) {
            highlighted.addAll(indices);
        }
        instructionsTable.refresh(); // re-render rows to apply styles
    }
}