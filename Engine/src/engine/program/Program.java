package engine.program;

import engine.instruction.Instruction;
import engine.label.Label;
import engine.variable.Variable;

import java.util.List;
import java.util.Optional;

public interface Program {
    /**
     * @return program name
     */
    String getName();
    /**
     * @return list of all the input variables used by the Program
     */
    List<Variable> getInputVariables();
    /**
     * @param label the instruction label
     * @return lineId of that label, empty if label is EMPTY
     */
    Optional<Integer> getLabelLineId(Label label);
    /**
     * @param label the instruction label
     * @return instruction by Label, empty if label is EMPTY
     */
    Optional<Instruction> getInstruction(Label label);
    /**
     * @return next instruction by index (line number) in the Program.
     *         empty if index out of bounds
     */
    Optional<Instruction> getInstructionByIndex(int index);
}