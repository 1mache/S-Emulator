package gui.component.instruction.table.event;

import javafx.event.ActionEvent;

public class BreakpointChangeAction extends ActionEvent {
    private final boolean breakpointSet; // true if set, false if removed

    public BreakpointChangeAction(Integer lineId, boolean breakpointSet) {
        super(lineId, null);
        this.breakpointSet = breakpointSet;
    }

    @Override
    public Integer getSource() {
        return (Integer) super.getSource();
    }

    public boolean isBreakpointSet() {
        return breakpointSet;
    }
}