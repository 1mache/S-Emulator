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
        if(state != DebuggerState.WAIT_FOR_START) // should be impossible to reach here
            throw new IllegalStateException("Previous debug run in progress");

        boolean reachedEnd = super.run();
        if(reachedEnd)
            transitionToEnd();

        return reachedEnd;
    }

    // aka stop debug
    @Override
    public void reset() {
        transitionToWaitForStart();
        super.reset();
    }

    // on breakpoint this will return the line we stopped at
    public Optional<Integer> whichLine(){
        if(state == DebuggerState.ON_INSTRUCTION && getPc() < program.getInstructions().size())
            return Optional.of(getPc());

        return Optional.empty();
    }

    public DebugStep stepOver() {
        enforceOnInstructionState();

        int pcBeforeExecution = getPc();
        Variable variable = pausedOn.getVariable();
        Long oldValue = variableContext.getVariableValue(variable);

        Label jumpLabel = executeInstruction(pausedOn);

        Long newValue = variableContext.getVariableValue(variable);

        if(oldValue.equals(newValue))
            variable = Variable.NO_VAR; // the instruction did not change its variable, nothing to report

        if(jumpLabel != FixedLabel.EMPTY){
            pausedOn = jumpToInstructionByLabel(jumpLabel).orElse(null);
        } else {
            // set the pc to the next line
            pausedOn = program.getInstructionByIndex(getPc()).orElse(null);
        }

        if(pausedOn == null) // executed last instruction
            transitionToEnd();

        return new DebugStep(variable, oldValue, newValue, pcBeforeExecution);
    }

    public DebugStep stepBack(){
        int pcBeforeExecution = getPc();

        enforceOnInstructionState();
        return new DebugStep(Variable.NO_VAR, 0L,0L, pcBeforeExecution); // NOT IMPLEMENTED
    }

    public boolean resume(){
        enforceOnInstructionState();

        var jumpToLabel = executeInstruction(pausedOn);
        return super.run(jumpToLabel);
    }

    public void addBreakpoint(int lineNumber){
        breakpoints.add(lineNumber);
    }

    public void removeBreakpoint(int lineNumber){
        breakpoints.remove(lineNumber);
    }


    // -------- internal: -------
    @Override
    protected boolean breakCheck(int pc) {
        boolean hitBreakPoint = breakpoints.contains(pc);
        if(hitBreakPoint)
            // (should never actually be null here))
            transitionToOnInstruction(program.getInstructionByIndex(pc).orElse(null));

        return hitBreakPoint;
    }

    private void enforceOnInstructionState() {
        if(state != DebuggerState.ON_INSTRUCTION){
            throw new IllegalStateException("Debugger state is " + state);
        }
    }

    private void transitionToOnInstruction(Instruction instruction) {
        pausedOn = instruction;
        state = DebuggerState.ON_INSTRUCTION;
    }

    private void transitionToEnd() {
        pausedOn = null;
        state = DebuggerState.END;
    }

    private void transitionToWaitForStart() {
        pausedOn = null;
        state = DebuggerState.WAIT_FOR_START;
    }

}