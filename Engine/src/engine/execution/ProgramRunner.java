package engine.execution;

import engine.execution.context.VariableContext;
import engine.execution.context.VariableTable;
import engine.instruction.Instruction;
import engine.label.Label;
import engine.label.FixedLabel;
import engine.program.Program;
import engine.variable.Variable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ProgramRunner {
    protected final Program program;
    protected VariableContext variableContext;

    // instruction pointer
    protected int pc = 0;
    private long cycles = 0;

    public ProgramRunner(Program program) {
        this.program = program;
        variableContext = new VariableTable();
    }

    public void reset(){
        variableContext = new VariableTable();
        pc = 0;
        cycles = 0;
    }


    public boolean run(){
        return run(FixedLabel.EMPTY);
    }
    /*
     an execution that starts at pc (or at init jump label)
     returns whether the run reached the end of the program
    */
    protected boolean run(Label initJumpLabel) {
        Optional<Instruction> currInstruction;
        Label jumpLabel = initJumpLabel;

        do {
            if(breakCheck(pc))
                return false; // early stop

            if (jumpLabel == FixedLabel.EMPTY) {
                currInstruction = program.getInstructionByIndex(pc);
                jumpLabel = executeInstruction(currInstruction.orElse(null));
            }
            else {
                currInstruction = jumpToLabel(jumpLabel);

                if(currInstruction.isPresent())
                    jumpLabel = executeInstruction(currInstruction.get());

            }
        }
        while (currInstruction.isPresent());

        return true;
    }

    public Long getRunOutput(){
        return variableContext.getVariableValue(Variable.RESULT);
    }

    public Map<String, Long> getAllVariableValues() {
        return variableContext.getOrganizedVariableValues();
    }

    public Long getCycles() {
        return cycles;
    }

    public void initInputVariables(List<Long> initInput) {
        int counter = 1;
        for(Long input : initInput) {
            variableContext.setVariableValue(Variable.createInputVariable(counter), input);
            counter++;
        }

        variableContext.setVariableValue(Variable.RESULT, 0L);
    }

    public void initInputVariablesSpecific(List<Long> initInput) {
        if(initInput.size() > program.getInputVariables().size())
            throw new IllegalArgumentException("Too many input values provided. Expected at most " + program.getInputVariables().size() + " but got " + initInput.size());

        var inputVars = program.getInputVariables();
        int minSize = Math.min(initInput.size(), inputVars.size());
        int i;
        for(i = 0; i < minSize; i++) {
            variableContext.setVariableValue(inputVars.get(i), initInput.get(i));
        }
        for(; i < inputVars.size(); i++) {
            variableContext.setVariableValue(inputVars.get(i), 0L);
        }

        variableContext.setVariableValue(Variable.RESULT, 0L);
    }

    // ------------ internal: ------------
    protected boolean breakCheck(int pc) {
        return false; // this is normal execution
    }

    // Note: returns empty if label is EXIT
    protected Optional<Instruction> jumpToLabel(Label jumpLabel) {
        Optional<Instruction> jumpedTo;
        // jump needs to happen
        jumpedTo = program.getInstructionByLabel(jumpLabel);
        // set the pc to the relevant line
        program.getLineNumberOfLabel(jumpLabel)
                .ifPresent(lineId -> pc = lineId);
        return jumpedTo;
    }

    protected Label executeInstruction(Instruction instruction) {
        pc++;
        Optional<Instruction> optionalInstruction = Optional.ofNullable(instruction);
        optionalInstruction.ifPresent(i -> cycles += i.cycles());

        return optionalInstruction
                .map(ins -> ins.execute(variableContext))
                .orElse(FixedLabel.EMPTY);
    }
}