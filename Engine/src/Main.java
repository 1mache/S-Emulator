import engine.instruction.AbstractInstruction;
import engine.instruction.Instruction;
import engine.instruction.InstructionData;
import engine.label.NumericLabel;
import engine.variable.Variable;
import engine.variable.VariableType;

public class Main {
    public static void main(String[] args) {
        Instruction instruction = AbstractInstruction.createInstruction(
                InstructionData.INCREASE,
                Variable.createWorkVariable(1),
                new NumericLabel(23)
        );

        instruction.execute();
        System.out.println(instruction.getName());
        System.out.println(instruction.cycles());
        System.out.println(instruction.getLabel().stringRepresentation());
        System.out.println(instruction.getVariable().stringRepresentation());
    }
}