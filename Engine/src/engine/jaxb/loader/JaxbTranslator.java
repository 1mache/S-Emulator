package engine.jaxb.loader;

import engine.argument.Argument;
import engine.instruction.*;
import engine.jaxb.generated.*;
import engine.jaxb.loader.exception.SProgramXMLException;
import engine.label.*;
import engine.program.Program;
import engine.program.ProgramImpl;

import engine.variable.Variable;
import engine.variable.VariableType;

import java.util.ArrayList;
import java.util.List;

public class JaxbTranslator {
    private static final String JNZ_ARG_NAME = "JNZLabel";
    private static final String VARIABLE_ARG_NAME = "assignedVariable";

    private static final List<Label> argumentLabels = new ArrayList<>();

    public Program getProgram(SProgram sProgram) {
        List<Instruction> instructions = new ArrayList<>();
        List<SInstruction> sInstructions = sProgram.getSInstructions().getSInstruction();
        for (SInstruction sInstruction : sInstructions) {
            InstructionData instructionData = InstructionData.valueOf(sInstruction.getName());
            Variable variable = str2Variable(sInstruction.getSVariable());
            Label label = str2Label(sInstruction.getSLabel());

            List<Argument> arguments = getArguments(sInstruction);

            Instruction instruction = InstructionFactory.
                    createInstruction(instructionData, variable, label, arguments);

            instructions.add(instruction);
        }

        return new ProgramImpl(sProgram.getName(), instructions);
    }

    public List<Label> getArgumentLabels() {
        return argumentLabels;
    }

    private Variable str2Variable(String str) {
        if(str == null) return Variable.NONE;
        str = str.toLowerCase();
        if(str.length() == 2) {
            return switch (str.charAt(0)) {
                case VariableType.INPUT_VARIABLE_CHAR -> Variable.createInputVariable(
                        Character.getNumericValue(str.charAt(1))
                );
                case VariableType.WORK_VARIABLE_CHAR -> Variable.createWorkVariable(
                        Character.getNumericValue(str.charAt(1))
                );
                default -> throw new SProgramXMLException("Unknown variable format: " + str);
            };
        }

        // different length, must be "y", or it is invalid
        if(str.equals(Character.toString(VariableType.RESULT_VARIABLE_CHAR))) {
            return Variable.RESULT;
        }

        throw new SProgramXMLException("Unknown variable format: " + str);
    }

    private Label str2Label(String str) {
        if(str == null) return FixedLabel.EMPTY;
        str = str.toLowerCase();
        if(str.equals(FixedLabel.EXIT.stringRepresentation()))
            return FixedLabel.EXIT; // exit label

        return new NumericLabel(Character.getNumericValue(str.charAt(1)));
    }

    private List<Argument> getArguments(SInstruction sInstruction) {
        List<Argument> res = new ArrayList<>();

        var sArgsList = sInstruction.getSInstructionArguments();
        if (sArgsList == null) return res;

        for(SInstructionArgument argument: sArgsList.getSInstructionArgument()){
            switch (argument.getName()) {
                case JNZ_ARG_NAME:
                    var label = str2Label(argument.getValue());
                    res.add(label);
                    argumentLabels.add(label);
                    break;
                case VARIABLE_ARG_NAME:
                    res.add(str2Variable(argument.getValue()));
                    break;
                default:
                    throw new SProgramXMLException("Unknown instruction argument: " + argument.getName());
            }
        }

        return res;
    }
}
