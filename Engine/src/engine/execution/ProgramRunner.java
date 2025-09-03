package engine.execution;

import engine.execution.context.VariableContext;
import engine.execution.context.VariableTable;
import engine.instruction.Instruction;
import engine.label.Label;
import engine.label.FixedLabel;
import engine.program.Program;
import engine.program.generator.LabelVariableGenerator;
import engine.variable.Variable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ProgramRunner {
    private final Program program;
    private VariableContext variableContext;
    private final LabelVariableGenerator labelVariableGenerator;

    // instruction pointer
    private int pc = 0;
    private long cycles = 0;

    public ProgramRunner(Program program) {
        this(program, new VariableTable(), new LabelVariableGenerator(program));
    }

    // private ctor for internal logic use
    private ProgramRunner(Program program,
                         VariableContext variableContext,
                         LabelVariableGenerator labelVariableGenerator) {
        this.program = program;
        this.variableContext = variableContext;
        this.labelVariableGenerator = labelVariableGenerator;
    }

    public void reset(){
        labelVariableGenerator.reset();
        variableContext = new VariableTable();
        pc = 0;
        cycles = 0;
    }

    public Label run(int expansionDegree) {
        Optional<Instruction> currInstruction;
        Label jumpLabel = FixedLabel.EMPTY;

        do {
            if (jumpLabel == FixedLabel.EMPTY) {
                currInstruction = program.getInstructionByIndex(pc);
                jumpLabel = executeInstruction(expansionDegree, currInstruction.orElse(null));
            }
            else {
                // jump needs to happen
                currInstruction = program.getInstructionByLabel(jumpLabel);
                // set the pc to the relevant line
                program.getLineNumberOfLabel(jumpLabel)
                        .ifPresent(lineId -> pc = lineId);

                if(currInstruction.isPresent())
                    jumpLabel = executeInstruction(expansionDegree, currInstruction.get());

            }
            if(jumpLabel == FixedLabel.EXIT) break; // check for exit
        }
        while (currInstruction.isPresent());

        // return the last jump label
        return jumpLabel;
    }

    public Long getRunOutput(){
        return variableContext.getVariableValue(Variable.RESULT);
    }

    public Map<String, Long> getVariableValues() {
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
    }

    // ------------ private: -------------

    // expands and executes
    private Label executeInstruction(int expansionLevel, Instruction instruction) {
        if (instruction == null) {
            return FixedLabel.EMPTY;
        }

        Optional<Program> expansion = Optional.empty();
        if(expansionLevel != 0)
            expansion = instruction.getExpansionInProgram(labelVariableGenerator);

        // if expansion is empty, the instruction is synthetic
        if (expansion.isEmpty()) {
            return executeInstruction(instruction);
        }

        pc++;
        // run with the same variable context and labelVariableGenerator
        ProgramRunner runner = new ProgramRunner(expansion.get(), variableContext, labelVariableGenerator);
        Label result = runner.run(expansionLevel-1);
        cycles += runner.getCycles();
        return result;
    }

    // executes instruction the normal way
    private Label executeInstruction(Instruction instruction) {
        pc++;
        Optional<Instruction> optionalInstruction = Optional.ofNullable(instruction);
        optionalInstruction.ifPresent(i -> cycles += i.cycles());

        return optionalInstruction
                .map(ins -> ins.execute(variableContext))
                .orElse(FixedLabel.EMPTY);
    }
}