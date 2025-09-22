package gui.component.execution.event;

import javafx.event.ActionEvent;

public class DebugStopOnLine extends ActionEvent {
    public DebugStopOnLine(Integer lineNumber) {
        super(lineNumber, null);
    }

    @Override
    public Integer getSource() {
        return (Integer) super.getSource();
    }
}
