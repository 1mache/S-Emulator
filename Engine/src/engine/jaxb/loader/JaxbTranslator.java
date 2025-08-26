package engine.jaxb.loader;

import engine.argument.Argument;
import engine.argument.ArgumentType;
import engine.argument.ConstantArgument;
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
import java.util.Map;

public class JaxbTranslator {
    public static final Map<String, ArgumentType> argumentTypes = Map.ofEntries(
            // JUMP_NOT_ZERO
            Map.entry("JNZLabel", ArgumentType.LABEL),

            // GOTO_LABEL
            Map.entry("gotoLabel", ArgumentType.LABEL),

            // ASSIGNMENT
            Map.entry("assignedVariable", ArgumentType.VARIABLE),

            // CONSTANT_ASSIGNMENT
            Map.entry("constantValue", ArgumentType.CONSTANT),

            // JUMP_ZERO
            Map.entry("JZLabel", ArgumentType.LABEL),

            // JUMP_EQUAL_CONSTANT
            Map.entry("JEConstantLabel", ArgumentType.LABEL),
            // * there is also a constant value here like in const assignment. but it's the same

            // JUMP_EQUAL_VARIABLE
            Map.entry("JEVariableLabel", ArgumentType.LABEL),
            Map.entry("variableName", ArgumentType.VARIABLE)
    );


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

    private Variable str2Variable(String str) {
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
        if(str.equals(FixedLabel.EXIT.stringRepresentation().toLowerCase()))
            return FixedLabel.EXIT; // exit label

        // take only the number part and construct a numeric label
        return new NumericLabel(Integer.parseInt(str.replaceAll("\\D", "")));
    }

    private List<Argument> getArguments(SInstruction sInstruction) {
        List<Argument> res = new ArrayList<>();

        var sArgsList = sInstruction.getSInstructionArguments();
        if (sArgsList == null) return res;

        // parse arguments based on their name
        for(SInstructionArgument argument: sArgsList.getSInstructionArgument()){
            var argumentType = argumentTypes.get(argument.getName());
            if(argumentType == null)
                throw new SProgramXMLException("Unknown instruction argument: " + argument.getName());
            switch (argumentType){
                case LABEL:
                    Label label = str2Label(argument.getValue());
                    res.add(label);
                    break;
                case VARIABLE:
                    Variable variable = str2Variable(argument.getValue());
                    res.add(variable);
                    break;
                case CONSTANT:
                    ConstantArgument constant = new ConstantArgument(
                            Long.valueOf(argument.getValue())
                    );
                    res.add(constant);
                    break;
            }
        }

        return res;
    }
}
