package engine.api.dto.debug;

import engine.debugger.ProgramDebugger;

import java.util.Optional;

// handle to be passed to the caller
public class DebugHandle{
    private final ProgramDebugger debugger;

    public DebugHandle(ProgramDebugger debugger){
            this.debugger = debugger;
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
        return debugger.resume();
    }

    public void addBreakpoint(int lineNumber){
        debugger.addBreakpoint(lineNumber);
    }
    public void removeBreakpoint(int lineNumber) {debugger.removeBreakpoint(lineNumber);}

    public DebugEndResult getResult() {
        return new DebugEndResult(
                debugger.getAllVariableValues(),
                debugger.getCycles()
        );
    }

    public long getCurrentCycles() {
        return debugger.getCycles();
    }
}