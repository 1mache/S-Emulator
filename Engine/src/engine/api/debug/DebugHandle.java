package engine.api.debug;

import dto.debug.DebugEndResult;
import dto.debug.DebugStepPeek;
import engine.debugger.DebugStep;
import engine.debugger.ProgramDebugger;
import engine.variable.Variable;

import java.util.Optional;
import java.util.function.Consumer;

// handle to be allocated for a debugging session
public class DebugHandle {
    private final Consumer<DebugEndResult> onRunEnded;
    private final ProgramDebugger debugger;

    public DebugHandle(ProgramDebugger debugger, Consumer<DebugEndResult> onRunEnded){
        this.debugger = debugger;
        this.onRunEnded = onRunEnded;
    }

    public boolean startDebug() {return debugger.run();}

    public void stopDebug(){
        debugger.reset();
    }

    // on breakpoint this will return the line we stopped at
    public Optional<Integer> whichLine(){
        return debugger.whichLine();
    }

    public DebugStepPeek stepOver() {
        DebugStep debugStep = debugger.stepOver();
        String variableChanged = null;
        if(debugStep.variableChanged() != Variable.NO_VAR)
            variableChanged = debugStep.variableChanged().stringRepresentation();

        return new DebugStepPeek(
                variableChanged,
                debugStep.newValue()
        );
    }

    public boolean resume(){
        if (debugger.resume()) {
            onRunEnded.accept(getResult());
            return true;
        }

        return false;
    }

    public void addBreakpoint(int lineNumber){
        debugger.addBreakpoint(lineNumber);
    }
    public void removeBreakpoint(int lineNumber) {debugger.removeBreakpoint(lineNumber);}

    public DebugEndResult getResult() {
        return new DebugEndResult(
                debugger.getRunOutput(),
                debugger.getAllVariableValues(),
                debugger.getCycles()
        );
    }

    public long getCurrentCycles() {
        return debugger.getCycles();
    }
}