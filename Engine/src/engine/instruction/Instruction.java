package engine.instruction;

import engine.argument.Argument;
import engine.execution.context.VariableContext;
import engine.label.Label;
import engine.program.Program;
import engine.program.generator.LabelVariableGenerator;
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

    /**
     * Returns the list of arguments for this instruction.
     *
     * @return a list of {@link Argument} objects, possibly empty
     */
    List<Argument> getArguments();

    /**
     * Returns an optional expanded version of this instruction.
     * <p>
     * Some high-level instructions may expand into a sequence
     * of lower-level instructions or an entire {@link Program}.
     * </p>
     *
     * @param generator the label-variable generator to use for creating fresh labels and variables
     * @return an {@link Optional} containing the expanded program,
     *         or empty if this instruction does not expand.
     *         Note: this caches the expansion! Because otherwise it would generate new
     *               variables and labels every time we expand
     */
    Optional<Program> getExpansionInProgram(LabelVariableGenerator generator);

    /**
     * same but because no generator is provided, this expansion is not Program context-dependent,
     * it will use any labels and variables, and it is uncached
     */
    Optional<Program> getExpansionStandalone();
}
