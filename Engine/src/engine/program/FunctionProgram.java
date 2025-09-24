package engine.program;

import engine.instruction.Instruction;
import engine.instruction.argument.InstructionArgument;
import engine.instruction.argument.InstructionArgumentType;

import java.util.List;

public class FunctionProgram extends StandardProgram implements InstructionArgument {
    public FunctionProgram(String name, List<Instruction> instructions) {
        super(name, instructions);
    }

    @Override
    public InstructionArgumentType getArgumentType() {
        return InstructionArgumentType.FUNCTION;
    }
}
