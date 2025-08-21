package engine.jaxb.loader;

import engine.argument.Argument;
import engine.instruction.AbstractInstruction;
import engine.instruction.Instruction;
import engine.instruction.InstructionBuilder;
import engine.instruction.InstructionData;
import engine.jaxb.generated.SInstruction;
import engine.jaxb.generated.SInstructionArgument;
import engine.jaxb.generated.SProgram;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.label.NumericLabel;
import engine.program.Program;

import engine.program.ProgramImpl;
import engine.variable.Variable;
import engine.variable.VariableType;

import java.util.ArrayList;
import java.util.List;

public class JaxbTranslator {
    private static final String JNZ_ARG_NAME = "JNZLabel";
    private static final String VARIABLE_ARG_NAME = "assignedVariable";

    public static Program getProgram(SProgram sProgram) {
        List<Instruction> instructions = new ArrayList<>();
        List<SInstruction> sInstructions = sProgram.getSInstructions().getSInstruction();
        for (SInstruction sInstruction : sInstructions) {
            try{
                InstructionData instructionData = InstructionData.valueOf(sInstruction.getName());
                Variable variable = str2Variable(sInstruction.getSVariable());
                Label label = str2Label(sInstruction.getSLabel());
                List<Argument>  arguments = getArguments(sInstruction);

                InstructionBuilder builder = new InstructionBuilder(instructionData, variable);
                builder.setArguments(arguments);
                builder.setLabel(label);
                Instruction instruction = builder.build();

                instructions.add(instruction);

            }catch(IllegalArgumentException e){
                System.out.println("Unknown instruction: " + sInstruction.getName());
            }
        }

        return new ProgramImpl(sProgram.getName(), instructions);
    }

    private static Variable str2Variable(String str) {
        if(str.length() == 2) {
            return switch (str.charAt(0)) {
                case VariableType.INPUT_VARIABLE_CHAR -> Variable.createInputVariable(
                        Character.getNumericValue(str.charAt(1))
                );
                case VariableType.WORK_VARIABLE_CHAR -> Variable.createWorkVariable(
                        Character.getNumericValue(str.charAt(1))
                );
                default -> throw new IllegalArgumentException("Unknown instruction variable: " + str);
            };
        }

        // different length, must be "y", or it is invalid
        if(str.equals(Character.toString(VariableType.RESULT_VARIABLE_CHAR))) {
            return Variable.RESULT;
        }

        throw new IllegalArgumentException("Unknown instruction variable: " + str);
    }

    private static Label str2Label(String str) {
        if(str == null || str.isEmpty())
           return FixedLabel.EMPTY; // empty label
        if(str.equals(FixedLabel.EXIT.stringRepresentation()))
            return FixedLabel.EXIT; // exit label

        return new NumericLabel(Character.getNumericValue(str.charAt(1)));
    }

    private static List<Argument> getArguments(SInstruction sInstruction) {
        List<Argument> res = new ArrayList<>();

        var sArgsList = sInstruction.getSInstructionArguments();
        if(sArgsList == null) {return res;} // empty list

        for(SInstructionArgument argument: sArgsList.getSInstructionArgument()){
            switch (argument.getName()) {
                case JNZ_ARG_NAME:
                    res.add(str2Label(argument.getValue()).toArgument());
                    break;
                case VARIABLE_ARG_NAME:
                    res.add(str2Variable(argument.getValue()));
                    break;
                default:
                    throw new IllegalArgumentException("Unknown instruction argument: " + argument.getName());
            }
        }

        return res;
    }
}
