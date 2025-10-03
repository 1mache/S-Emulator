package engine.function;

import engine.instruction.Instruction;
import engine.program.StandardProgram;

import java.util.List;

public class Function extends StandardProgram {
    private final String userString;

    public Function(String name, String userString, List<Instruction> instructions) {
        super(name, instructions);
        this.userString = userString;
    }

    public String getUserString() {
        return userString;
    }
}
