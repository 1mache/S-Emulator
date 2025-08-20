package engine.execution;

import engine.execution.context.VariableContext;
import engine.execution.context.VariableTable;
import engine.instruction.Instruction;
import engine.label.Label;
import engine.label.FixedLabel;
import engine.program.Program;
import engine.variable.Variable;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

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

        Optional<Instruction> currInstruction = Optional.empty();
        AtomicReference<Label> jumpLabel = new AtomicReference<>(FixedLabel.EMPTY);

        do {
            if(jumpLabel.get() == FixedLabel.EMPTY) {
                currInstruction = program.getInstructionByIndex(pc);

                currInstruction
                        .map(instruction -> instruction.execute(variableContext))
                        .ifPresent(jumpLabel::set);
                pc++;
            }
            else // jump needs to happen
                currInstruction = program.getInstruction(jumpLabel.get());
        }while(currInstruction.isPresent());
    }

    @Override
    public Long getResult() {
        return variableContext.getVariableValue(Variable.RESULT);
    }

    private void initInputVariables(Long... initInput) {
        var variableList = program.getInputVariables();
        int minLength = Math.min(variableList.size(), initInput.length);
        // initialize what we can with the values we got (rest is 0)
        for(int i = 0; i < minLength; i++)
            variableContext.setVariableValue(variableList.get(i), initInput[i]);
    }
}
