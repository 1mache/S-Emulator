package engine.api.dto.debug;

import engine.api.dto.ProgramExecutionResult;
import engine.debugger.ProgramDebugger;

import java.util.Optional;
import java.util.function.Consumer;

// handle to be passed to the caller
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
        return new DebugStepPeek(debugger.stepOver());
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