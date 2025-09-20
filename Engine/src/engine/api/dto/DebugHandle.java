package engine.api.dto;

import engine.debugger.ProgramDebugger;

// handle to be passed to the caller
public class DebugHandle{
        private final ProgramDebugger debugger;

        public DebugHandle(ProgramDebugger debugger){
            this.debugger = debugger;
        }

        public void stopDebug(){
            debugger.stopDebug();
        }

        public VariableChange stepOver() {
            return debugger.stepOver();
        }

        public VariableChange stepBack(){
            return debugger.stepBack();
        }

        public void resume(){
            debugger.resume();
        }

        public void setBreakpoint(int lineNumber){
            debugger.setBreakpoint(lineNumber);
        }
    }