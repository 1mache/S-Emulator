package engine.debugger;

import engine.execution.ProgramRunner;
import engine.instruction.Instruction;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.program.Program;
import engine.variable.Variable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ProgramDebugger extends ProgramRunner {
    private enum DebuggerState{
        WAIT_FOR_START, ON_INSTRUCTION, END
    }

    private DebuggerState state = DebuggerState.WAIT_FOR_START;
    private Instruction pausedOn = null;

    private final Set<Integer> breakpoints = new HashSet<>();

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

    // on breakpoint this will return the line we stopped at
    public Optional<Integer> whichLine(){
        if(state == DebuggerState.ON_INSTRUCTION && pc < program.getInstructions().size())
            return Optional.of(pc);

        return Optional.empty();
    }

    public DebugStep stepOver() {
        enforceOnInstructionState();

        int pcBeforeExecution = pc;
        Variable variable = pausedOn.getVariable();
        Long oldValue = variableContext.getVariableValue(variable);

        Label jumpLabel = executeInstruction(pausedOn);

        Long newValue = variableContext.getVariableValue(variable);

        if(oldValue.equals(newValue))
            variable = Variable.NO_VAR; // the instruction did not change its variable, nothing to report

        if(jumpLabel != FixedLabel.EMPTY){
            pausedOn = jumpToLabel(jumpLabel).orElse(null);
        } else {
            // set the pc to the next line
            pausedOn = program.getInstructionByIndex(pc).orElse(null);
        }

        if(pausedOn == null) // executed last instruction
            state = DebuggerState.END;

        return new DebugStep(variable, oldValue, newValue, pcBeforeExecution);
    }

    public DebugStep stepBack(){
        int pcBeforeExecution = pc;

        enforceOnInstructionState();
        return new DebugStep(Variable.NO_VAR, 0L,0L, pcBeforeExecution); // NOT IMPLEMENTED
    }

    public boolean resume(){
        enforceOnInstructionState();

        var jumpToLabel = executeInstruction(pausedOn);
        return run(jumpToLabel);
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
            pausedOn = program.getInstructionByIndex(pc).orElse(null); // (should never actually be null here)
            state = DebuggerState.ON_INSTRUCTION;
        }

        return hitBreakPoint;
    }

    private void enforceOnInstructionState() {
        if(state != DebuggerState.ON_INSTRUCTION){
            throw new IllegalStateException("Debugger state is " + state);
        }
    }
}