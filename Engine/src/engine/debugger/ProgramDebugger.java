package engine.debugger;

import engine.execution.ProgramRunner;
import engine.instruction.Instruction;
import engine.program.Program;
import engine.variable.Variable;

import java.util.HashSet;
import java.util.Set;

public class ProgramDebugger extends ProgramRunner {
    private enum DebuggerState{
        WAIT_FOR_START, ON_BREAKPOINT, END
    }

    private DebuggerState state = DebuggerState.WAIT_FOR_START;
    private Instruction pausedOn = null;

    private final Set<Integer> breakpoints = new HashSet<>();;

    public ProgramDebugger(Program program) {
        super(program);
    }

    @Override
    public boolean run() {
        if(state != DebuggerState.WAIT_FOR_START){
            reset(); // if we're in the middle of the debug, stop the previous one
        }

        boolean reachedEnd = super.run();
        if(reachedEnd)
            state = DebuggerState.END;

        return reachedEnd;
    }

    // aka stop debug
    @Override
    public void reset(){
        if(state == DebuggerState.WAIT_FOR_START)
            throw new IllegalStateException("Debugger not started");

        pausedOn = null;
        state = DebuggerState.WAIT_FOR_START;
        super.reset();
    }

    public VariableChange stepOver() {
        validateBreakpointState();

        Variable variable = pausedOn.getVariable();
        Long oldValue = variableContext.getVariableValue(variable);
        executeInstruction(pausedOn);
        Long newValue = variableContext.getVariableValue(variable);

        if(oldValue.equals(newValue))
            variable = Variable.NO_VAR; // the instruction did not change its variable, nothing to report

        return new VariableChange(variable, oldValue, newValue);
    }

    public VariableChange stepBack(){
        validateBreakpointState();
        return new VariableChange(Variable.NO_VAR, 0L,0L); // NOT IMPLEMENTED
    }

    public boolean resume(){
        validateBreakpointState();

        var jumpTo = executeInstruction(pausedOn);
        return run(jumpTo);
    }

    public void addBreakpoint(int lineNumber){
        breakpoints.add(lineNumber);
    }

    public void removeBreakpoint(int lineNumber){
        breakpoints.remove(lineNumber);
    }

    @Override
    protected boolean breakCheck(int pc) {
        boolean hitBreakPoint = breakpoints.contains(pc);
        if(hitBreakPoint){
            pausedOn = program.getInstructionByIndex(pc).orElse(null); // (should never actually be null)
            state = DebuggerState.ON_BREAKPOINT;
        }

        return hitBreakPoint;
    }

    private void validateBreakpointState() {
        if(state != DebuggerState.ON_BREAKPOINT){
            throw new IllegalStateException("Debugger state is " + state);
        }
    }
}