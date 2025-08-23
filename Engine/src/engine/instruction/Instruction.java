package engine.instruction;

import engine.execution.context.VariableContext;
import engine.label.Label;
import engine.variable.Variable;

public interface Instruction {
    // TODO: Javadoc
    Label execute(VariableContext context);
    String getName();
    String stringRepresentation();
    boolean isSynthetic();
    int cycles();
    Variable getVariable();
    Label getLabel();
}
