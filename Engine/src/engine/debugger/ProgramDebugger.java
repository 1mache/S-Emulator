package engine.debugger;

import engine.api.dto.VariableChange;
import engine.execution.ProgramRunner;
import engine.instruction.Instruction;
import engine.program.Program;
import engine.variable.Variable;

import java.util.HashSet;
import java.util.Set;

public class ProgramDebugger extends ProgramRunner {
    private final Set<Integer> breakpoints = new HashSet<>();
    private Instruction pausedOn = null;

    public ProgramDebugger(Program program) {
        super(program);
    }

    public void stopDebug(){
        breakpoints.clear();
        pausedOn = null;
        reset();
    }

    public VariableChange stepOver() {
        Variable variable = pausedOn.getVariable();
        Long oldValue = variableContext.getVariableValue(variable);
        executeInstruction(pausedOn);
        Long newValue = variableContext.getVariableValue(variable);

        if(oldValue.equals(newValue))
            variable = Variable.NO_VAR; // the instruction did not change its variable, nothing to report

        return new VariableChange(variable, oldValue, newValue);
    }

    public VariableChange stepBack(){
        return new VariableChange(Variable.NO_VAR, 0L,0L); // NOT IMPLEMENTED
    }

    public void resume(){
        executeInstruction(pausedOn);
        run();
    }

    public void setBreakpoint(int lineNumber){
        breakpoints.add(lineNumber);
    }

    @Override
    protected boolean breakCheck(int pc) {
        return breakpoints.contains(pc);
    }
}