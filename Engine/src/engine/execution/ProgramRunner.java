package engine.execution;

import engine.execution.context.RunContext;
import engine.execution.context.RunContextImpl;
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
    protected RunContext runContext;

    // instruction pointer
    private int pc = 0;
    private long cycles = 0;

    public ProgramRunner(Program program) {
        this.program = program;
        runContext = new RunContextImpl();
    }

    public void reset(){
        runContext = new RunContextImpl();
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
                currInstruction = jumpToInstructionByLabel(jumpLabel);

                if(currInstruction.isPresent())
                    jumpLabel = executeInstruction(currInstruction.get());

            }
        }
        while (currInstruction.isPresent());

        return true;
    }

    public Long getRunOutput(){
        return runContext.getVariableValue(Variable.RESULT);
    }

    public Map<String, Long> getAllVariableValues() {
        return runContext.getOrganizedVariableValues();
    }

    public long getCycles() {
        return cycles;
    }

    public void initInputVariables(List<Long> initInput) {
        int counter = 1;
        for(Long input : initInput) {
            runContext.setVariableValue(Variable.createInputVariable(counter), input);
            counter++;
        }

        runContext.setVariableValue(Variable.RESULT, 0L);
    }

    public void initInputVariablesSpecific(List<Long> initInput) {
        if(initInput.size() > program.getInputVariables().size())
            throw new IllegalArgumentException("Too many input values provided. Expected at most " + program.getInputVariables().size() + " but got " + initInput.size());

        var inputVars = program.getInputVariables();
        int minSize = Math.min(initInput.size(), inputVars.size());
        int i;
        for(i = 0; i < minSize; i++) {
            runContext.setVariableValue(inputVars.get(i), initInput.get(i));
        }
        for(; i < inputVars.size(); i++) {
            runContext.setVariableValue(inputVars.get(i), 0L);
        }

        runContext.setVariableValue(Variable.RESULT, 0L);
    }

    // ------------ internal: ------------

    protected int getPc(){
        return pc;
    }

    // called before each instruction. override to implement debug modes
    protected boolean breakCheck(int pc) {
        return false; // this is normal execution
    }

    // Note: returns empty if label is EXIT
    protected Optional<Instruction> jumpToInstructionByLabel(Label jumpLabel) {
        Optional<Instruction> jumpedTo;
        // jump needs to happen
        jumpedTo = program.getInstructionByLabel(jumpLabel);
        return jumpedTo;
    }

    protected Label executeInstruction(Instruction instruction) {
        Optional<Instruction> optionalInstruction = Optional.ofNullable(instruction);

        var jumpLabel = optionalInstruction
                .map(ins -> ins.execute(runContext))
                .orElse(FixedLabel.EMPTY);

        optionalInstruction.ifPresent(i -> cycles += i.cycles());

        if(jumpLabel == FixedLabel.EXIT) {
            // the end, will not crash if we call getInstructionByIndex with this pc.
            pc = program.getInstructions().size() + 1;
        } else if(jumpLabel == FixedLabel.EMPTY)
            pc++;
        else {
            program.getLineNumberOfLabel(jumpLabel)
                    .ifPresent(lineId -> pc = lineId);
        }

        return jumpLabel;
    }
}