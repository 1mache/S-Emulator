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
     * @return sorted list of all the input variables used by the Program
     */
    List<Variable> getInputVariables();
    /**
     * @return sorted list of all the work variables used by the Program
     */
    List<Variable> getWorkVariables();
    /**
     * @return sorted list of all the labels used by the Program,
     *         Exit in the end if used
     */
    List<Label> getUsedLabels();
    /**
     * Checks if label is present in the program
     * @param label (if EMPTY returns true, but has no meaning)
     */
    boolean hasLabel(Label label);
    /**
     * @param label the instruction label
     * @return lineId of that label, empty if label is EMPTY or doesn't exist (sometimes in expansion)s
     */
    Optional<Integer> getLineNumberOfLabel(Label label);
    /**
     * @param label the instruction label
     * @return instruction by Label, empty if label is EMPTY or doesn't exist (sometimes in expansion)
     */
    Optional<Instruction> getInstructionByLabel(Label label);
    /**
     * @return next instruction by index (line lineId) in the Program.
     *         empty if index out of bounds
     */
    Optional<Instruction> getInstructionByIndex(int index);
    /**
     * @return all the instructions in the program
     */
    List<Instruction> getInstructions();

    /**
     * @return the max degree this program can be expanded to
     */
    int getMaxExpansionDegree();
}