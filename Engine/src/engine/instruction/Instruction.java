package engine.instruction;

import engine.argument.ArgumentType;
import engine.execution.context.VariableContext;
import engine.label.Label;
import engine.variable.Variable;

import java.util.List;

public interface Instruction {
    // TODO: Javadoc
    Label execute(VariableContext context);
    String getName();
    String stringRepresentation();
    boolean isSynthetic();
    int cycles();
    Variable getVariable();
    Label getLabel();
    List<ArgumentType> getArgumentTypes();
}
