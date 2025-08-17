package engine.program;

import engine.instruction.Instruction;
import engine.label.Label;
import engine.variable.Variable;

import java.util.Optional;

public interface Program {
    /**
     * @return program name
     */
    String getName();
    /**
     * @param label the instruction label
     * @return boolean whether the label is in use
     */
    boolean hasLabel(Label label);
    /**
     * @param label the instruction label
     * @return instruction by Label, empty if label doesn't exist
     */
    Optional<Instruction> getInstruction(Label label);
    /**
     * @return next instruction by pc (instruction pointer).
     *         empty if pc reached the end of the program
     */
    Optional<Instruction> getNextInstruction();
    /**
     * @return 0 if the variable wasn't used yet, and its value right now otherwise
     */
    int getVariableState(Variable variable);

    /**
     * @param variable the variable we want to change
     * @param value the value we want to give it
     */
    void setVariableState(Variable variable, int value);
}
