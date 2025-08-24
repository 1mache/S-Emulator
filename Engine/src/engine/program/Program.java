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
     * @return all the instructions printed as a big string
     */
    String print();
    /**
     * @return sorted list of all the input variables used by the Program
     */
    List<Variable> getInputVariables();
    /**
     * @return sorted list of all the labels used by the Program,
     *         Exit in the end if used
     */
    List<Label> getLabels();
    /**
     * Checks if label is present in the program
     * @param label (if EMPTY returns true, but has no meaning)
     */
    boolean hasLabel(Label label);
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