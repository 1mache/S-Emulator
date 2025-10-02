package gui.component.history.event;

import engine.api.dto.ProgramExecutionResult;
import javafx.event.ActionEvent;

public class HistoryRowAction extends ActionEvent {
    public HistoryRowAction(ProgramExecutionResult selectedRow) {
        super(selectedRow, null);
    }

    public ProgramExecutionResult getSelectedRow(){
        return (ProgramExecutionResult)getSource();
    }
}
