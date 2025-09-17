package engine.instruction;

import engine.argument.Argument;
import engine.execution.context.VariableContext;
import engine.label.Label;
import engine.program.Program;
import engine.variable.Variable;

import java.util.List;
import java.util.Optional;

public interface Instruction {

    /**
     * Executes this instruction in the given variable context.
     *
     * @param context the current {@link VariableContext} holding variable states
     * @return the {@link Label} to which execution should jump next,
     *         or empty label if there shouldn't be a jump
     */
    Label execute(VariableContext context);

    /**
     * Returns the name of this instruction.
     *
     * @return a human-readable name for the instruction
     */
    String getName();

    /**
     * Returns a string representation of this instruction
     * as it would appear in source code.
     *
     * @return a textual representation of the instruction
     */
    String stringRepresentation();

    /**
     * Indicates whether this instruction is synthetic.
     *
     * @return {@code true} if the instruction is synthetic; {@code false} otherwise
     */
    boolean isSynthetic();

    /**
     * Returns the number of cycles this instruction requires for execution.
     *
     * @return the cycle cost of executing this instruction
     */
    int cycles();

    /**
     * Returns the main variable associated with this instruction,
     * if applicable.
     *
     * @return the associated {@link Variable}
     */
    Variable getVariable();

    /**
     * Returns the label associated with this instruction,
     * if any.
     *
     * @return the associated {@link Label}, or empty label if none
     */
    Label getLabel();

    InstructionData getData();

    /**
     * Returns the list of arguments for this instruction.
     *
     * @return a list of {@link Argument} objects, possibly empty
     */
    List<Argument> getArguments();

    /**
     * Returns the program expansion for this instruction, if it exists.
     * @return an {@link Optional} containing the expansion {@link Program},
     */
    Optional<Program> getExpansion();
}
