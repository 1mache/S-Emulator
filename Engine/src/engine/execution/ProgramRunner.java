package engine.execution;

import engine.execution.context.VariableContext;
import engine.execution.context.VariableTable;
import engine.instruction.Instruction;
import engine.label.Label;
import engine.label.FixedLabel;
import engine.program.Program;
import engine.program.generator.LabelVariableGenerator;
import engine.variable.Variable;

import java.util.Optional;

public class ProgramRunner implements Runner {
    private final Program program;
    private final VariableContext variableContext;
    private final LabelVariableGenerator labelVariableGenerator;

    // instruction pointer
    private int pc = 0;
    private long cycles = 0;

    public ProgramRunner(Program program) {
        this(program, new VariableTable(), new  LabelVariableGenerator(program));
    }

    // private ctor for internal logic use
    private ProgramRunner(Program program,
                         VariableContext variableContext,
                         LabelVariableGenerator labelVariableGenerator) {
        this.program = program;
        this.variableContext = variableContext;
        this.labelVariableGenerator = labelVariableGenerator;
    }

    @Override
    public Label run(int expansionLevel) {
        Optional<Instruction> currInstruction;
        Label jumpLabel = FixedLabel.EMPTY;

        do {
            if (jumpLabel == FixedLabel.EMPTY) {
                currInstruction = program.getInstructionByIndex(pc);
                jumpLabel = executeInstruction(expansionLevel, currInstruction.orElse(null));
            }
            else {
                // jump needs to happen
                currInstruction = program.getInstruction(jumpLabel);
                // set the pc to the relevant line
                program.getLabelLineId(jumpLabel)
                        .ifPresent(lineId -> pc = lineId);

                if(currInstruction.isPresent())
                    jumpLabel = executeInstruction(expansionLevel, currInstruction.get());

            }
            if(jumpLabel == FixedLabel.EXIT) {break;} // check for exit
        }
        while (currInstruction.isPresent());

        // return the last jump label
        return jumpLabel;
    }

    @Override
    public Long getResult() {
        return variableContext.getVariableValue(Variable.RESULT);
    }

    @Override
    public Long getCycles() {
        return cycles;
    }

    @Override
    public VariableContext getVariableContext() {
        return variableContext;
    }

    public void initInputVariables(Long... initInput) {
        int counter = 1;
        for(Long input : initInput) {
            variableContext.setVariableValue(Variable.createInputVariable(counter), input);
            counter++;
        }
    }

    // expands and executes
    private Label executeInstruction(int expansionLevel, Instruction instruction) {
        if (instruction == null) {
            pc++;
            return FixedLabel.EMPTY;
        }

        Optional<Program> expansion = instruction.getExpansion(pc, labelVariableGenerator);

        // if expansion is empty the instruction is synthetic
        if (expansionLevel == 0 || expansion.isEmpty()) {
            return executeInstruction(instruction);
        }

        pc++;
        // run with the same variable context and labelVariableGenerator
        ProgramRunner runner = new ProgramRunner(expansion.get(), variableContext, labelVariableGenerator);
        Label result = runner.run(expansionLevel-1);
        cycles += runner.getCycles();
        return result;
    }

    // executes instruction the normal
    private Label executeInstruction(Instruction instruction) {
        pc++;
        Optional<Instruction> optionalInstruction = Optional.ofNullable(instruction);
        optionalInstruction.ifPresent(i -> cycles += i.cycles());

        return optionalInstruction
                .map(ins -> ins.execute(variableContext))
                .orElse(FixedLabel.EMPTY);
    }
}