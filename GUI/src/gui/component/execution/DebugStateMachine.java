package gui.component.execution;

import gui.component.execution.event.DebugStateChange;
import javafx.event.EventHandler;

import java.util.HashSet;
import java.util.Set;

public class DebugStateMachine {
    private DebugState currentState = DebugState.NOT_IN_DEBUG;
    private final Set<EventHandler<DebugStateChange>> debugStateListeners = new HashSet<>();

    public DebugState getCurrentState() {
        return currentState;
    }

    // throws IllegalStateException if the requested transition is illegal
    public void transitionTo(DebugState newState) {
        if(!transitionIsValid(currentState, newState))
            throw new IllegalStateException("Transition from " + currentState + " to " + newState + " is illegal");
        currentState = newState;
        fireDebugStateChange();
    }

    public void addListener(EventHandler<DebugStateChange> listener){
        debugStateListeners.add(listener);
    }

    // --------- private: -----------
    private boolean transitionIsValid(DebugState oldState, DebugState newState) {
        if(oldState == newState) return true;
        return switch (oldState){
            case NOT_IN_DEBUG -> newState == DebugState.RUNNING;
            case RUNNING -> newState == DebugState.ON_INSTRUCTION || newState == DebugState.END;
            case ON_INSTRUCTION -> newState == DebugState.RUNNING || newState == DebugState.END;
            case END ->  newState == DebugState.NOT_IN_DEBUG || newState == DebugState.RUNNING;
        };
    }

    private void fireDebugStateChange() {
        debugStateListeners.forEach(
                listener ->
                        listener.handle(new DebugStateChange(currentState))
        );
    }

}
