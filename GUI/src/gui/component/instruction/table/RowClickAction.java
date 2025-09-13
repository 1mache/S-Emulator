package gui.component.instruction.table;

import engine.api.dto.InstructionPeek;
import javafx.event.ActionEvent;

public class RowClickAction extends ActionEvent {
    public RowClickAction(InstructionPeek rowData) {
        super(rowData, null);
    }

    public InstructionPeek getRowData() {
        return (InstructionPeek) getSource();
    }
}
