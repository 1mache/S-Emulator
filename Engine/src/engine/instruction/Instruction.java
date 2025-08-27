package engine.instruction;

import engine.argument.Argument;
import engine.execution.context.VariableContext;
import engine.label.Label;
import engine.program.InstructionReference;
import engine.program.Program;
import engine.program.generator.LabelVariableGenerator;
import engine.variable.Variable;

import java.util.List;
import java.util.Optional;

public interface Instruction {
    // TODO: Javadoc
    Label execute(VariableContext context);
    String getName();
    String stringRepresentation();
    boolean isSynthetic();
    int cycles();
    Variable getVariable();
    Label getLabel();
    List<Argument> getArguments();
    Optional<Program> getExpansion(int lineNumber, LabelVariableGenerator generator);
    Optional<InstructionReference> getExpanding();
}
