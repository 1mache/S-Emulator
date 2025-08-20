import engine.execution.ProgramRunner;
import engine.execution.Runner;
import engine.instruction.Instruction;
import engine.instruction.concrete.IncreaseInstruction;
import engine.program.Program;
import engine.program.ProgramImpl;
import engine.variable.Variable;
import engine.variable.VariableType;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Variable x1 = Variable.createInputVariable(1);
        Variable x2 = Variable.createInputVariable(2);

        List<Instruction> instructions = new ArrayList<>();
        instructions.add(new IncreaseInstruction(x1));
        instructions.add(new IncreaseInstruction(x2));
        instructions.add(new IncreaseInstruction(Variable.RESULT));

        Program program = new ProgramImpl("test", instructions);
        Runner runner = new ProgramRunner(program);
        runner.run();
        System.out.println("The result is: " + runner.getResult());
    }
}