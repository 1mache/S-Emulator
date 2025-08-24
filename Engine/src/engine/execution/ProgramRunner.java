package engine.execution;

import engine.execution.context.VariableContext;
import engine.execution.context.VariableTable;
import engine.instruction.Instruction;
import engine.label.Label;
import engine.label.FixedLabel;
import engine.program.Program;
import engine.variable.Variable;

import java.util.Optional;

public class ProgramRunner implements Runner {
    private final Program program;
    private final VariableContext variableContext;

    // instruction pointer
    private int pc = 0;

    public ProgramRunner(Program program) {
        this.program = program;
        variableContext = new VariableTable();
    }

    @Override
    public void run(Long... initInput) {
        initInputVariables(initInput);

        Optional<Instruction> currInstruction;
        Label jumpLabel = FixedLabel.EMPTY;

        do {
            if (jumpLabel == FixedLabel.EMPTY) {
                currInstruction = program.getInstructionByIndex(pc);
            }
            else {
                // jump needs to happen
                currInstruction = program.getInstruction(jumpLabel);
                // set the pc to the relevant line
                program.getLabelLineId(jumpLabel)
                        .ifPresent(lineId -> pc = lineId);

            }
            jumpLabel = executeInstruction(currInstruction.orElse(null));
            if(jumpLabel == FixedLabel.EXIT) {break;} // check for exit
        }
        while (currInstruction.isPresent());
    }


    @Override
    public Long getResult() {
        return variableContext.getVariableValue(Variable.RESULT);
    }

    @Override
    public VariableContext getVariableContext() {
        return variableContext;
    }

    private void initInputVariables(Long... initInput) {
        int counter = 1;
        for(Long input : initInput) {
            variableContext.setVariableValue(Variable.createInputVariable(counter), input);
            counter++;
        }
    }

    private Label executeInstruction(Instruction instruction) {
        pc++;
        Optional<Instruction> optionalInstruction = Optional.ofNullable(instruction);
        return optionalInstruction
                .map(ins -> ins.execute(variableContext))
                .orElse(FixedLabel.EMPTY);
    }
}