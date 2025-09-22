package gui.component.instruction.table;

import engine.api.dto.InstructionPeek;
import gui.component.instruction.table.event.BreakpointChangeAction;
import gui.component.instruction.table.event.RowClickAction;
import gui.utility.CssClasses;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

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

    private boolean breakpointsDisabled = true;
    private final Set<Integer> breakpoints = new HashSet<>();
    private final Set<EventHandler<BreakpointChangeAction>> breakPointChangeListeners = new HashSet<>();

    private final int NO_LINE = -1;
    private int debugHighlightedLine = NO_LINE;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setPlaceholderMessage("No instructions to display");

        defineColumnValues();

        setInstructionRowBehaviour();

        defineBreakpointSettingInLineColumn();
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

    public void addBreakpointChangeListener(EventHandler<BreakpointChangeAction> listener){
        breakPointChangeListeners.add(listener);
    }

    public void clear(){
        instructionTable.setItems(null);
        breakpoints.clear();
        resetDebugHighlight();
    }

    public void disableBreakpoints(boolean disable){
        breakpointsDisabled = disable;
    }

    public void setDebugHighlight(int lineNumber) {
        debugHighlightedLine = lineNumber;
        instructionTable.refresh(); // force re-render so CSS updates
    }

    public void resetDebugHighlight(){
        debugHighlightedLine = NO_LINE;
        instructionTable.refresh(); // force re-render so CSS updates
    }

    // private:
    private void defineColumnValues() {
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
    }

    private void setInstructionRowBehaviour() {
        instructionTable.setRowFactory(tv -> {
            // highlight logic
            TableRow<InstructionPeek> row = new TableRow<>() {
                @Override
                protected void updateItem(InstructionPeek item, boolean empty) {
                    super.updateItem(item, empty);

                    // always reset styles first
                    getStyleClass().remove(CssClasses.DEBUG_HIGHLIGHTED);

                    if (!empty && item != null) {
                        if (item.lineId() == debugHighlightedLine) {
                            getStyleClass().add(CssClasses.DEBUG_HIGHLIGHTED);
                        }
                    }
                }
            };

            // clicking on a row triggers an event
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    InstructionPeek rowData = row.getItem();
                    fireRowClick(rowData);
                }
            });

            return row;
        });
    }

    private void defineBreakpointSettingInLineColumn() {
        // clicking the line column will set a breakpoint
        lineNumberColumn.setCellFactory(col -> new TableCell<>() {
            private final Circle circle = new Circle(5, Color.RED);
            {
                setOnMouseClicked(event -> {
                    if(breakpointsDisabled) return;
                    if (!isEmpty()) {
                        Integer lineId = getItem() - 1; // - 1 because we start at 1

                        if (breakpoints.contains(lineId)) {
                            breakpoints.remove(lineId);
                            setGraphic(null);
                        } else {
                            breakpoints.add(lineId);
                            setGraphic(circle);
                        }

                        fireBreakpointChange(lineId, breakpoints.contains(lineId));
                    }
                });
            }

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    int lineId = item -1;
                    setText(item.toString());
                    setGraphic(breakpoints.contains(lineId) ? circle : null);
                }
            }
        });
    }

    private void fireRowClick(InstructionPeek rowData) {
        rowClickListeners.forEach(
                listener -> listener.handle(new RowClickAction(rowData))
        );
    }

    private void fireBreakpointChange(int lineId, boolean isBreakpointSet){
        breakPointChangeListeners.forEach(
            listener -> listener.handle(new BreakpointChangeAction(lineId, isBreakpointSet))
        );
    }
}
