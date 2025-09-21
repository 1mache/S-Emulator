package engine.api.dto.debug;

import engine.debugger.ProgramDebugger;

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

    public VariableChangePeek stepOver() {
        return new VariableChangePeek(debugger.stepOver());
    }

    public VariableChangePeek stepBack(){
        return new VariableChangePeek(debugger.stepBack());
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
}