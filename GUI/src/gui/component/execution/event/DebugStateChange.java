package gui.component.execution.event;

import gui.component.execution.DebugState;
import javafx.event.ActionEvent;

public class DebugStateChange extends ActionEvent {
    public DebugStateChange(DebugState debugState) {
        super(debugState, null);
    }

    @Override
    public DebugState getSource() {
        return (DebugState)super.getSource();
    }
}
