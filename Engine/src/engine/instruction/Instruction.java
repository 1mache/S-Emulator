package engine.instruction;

import engine.execution.context.VariableContext;
import engine.label.Label;
import engine.variable.Variable;

import java.util.Map;

public interface Instruction {
    // TODO: Javadoc
    Label execute(VariableContext context);
    String getName();
    String stringRepresentation();
    int cycles();
    Variable getVariable();
    Label getLabel();
}
