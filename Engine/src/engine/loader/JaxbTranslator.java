package engine.loader;

import engine.function.FunctionCall;
import engine.function.parameter.FunctionParam;
import engine.function.parameter.FunctionParamList;
import engine.instruction.argument.InstructionArgument;
import engine.instruction.argument.InstructionArgumentType;
import engine.numeric.constant.NumericConstant;
import engine.instruction.*;
import engine.jaxb.generated.*;
import engine.loader.event.LoadingListener;
import engine.loader.exception.SProgramXMLException;
import engine.label.*;
import engine.function.Function;
import engine.program.Program;
import engine.program.StandardProgram;

import engine.variable.Variable;
import engine.variable.VariableType;

import java.util.*;
import java.util.function.BiConsumer;

public class JaxbTranslator {
    private static final Map<String, InstructionArgumentType> argumentTypes = Map.ofEntries(
            // JUMP_NOT_ZERO
            Map.entry("JNZLabel", InstructionArgumentType.LABEL),

            // GOTO_LABEL
            Map.entry("gotoLabel", InstructionArgumentType.LABEL),

            // ASSIGNMENT
            Map.entry("assignedVariable", InstructionArgumentType.VARIABLE),

            // CONSTANT_ASSIGNMENT
            Map.entry("constantValue", InstructionArgumentType.CONSTANT),

            // JUMP_ZERO
            Map.entry("JZLabel", InstructionArgumentType.LABEL),

            // JUMP_EQUAL_CONSTANT
            Map.entry("JEConstantLabel", InstructionArgumentType.LABEL),
            // * there is also a constant value here like in const assignment. but it's the same

            // JUMP_EQUAL_VARIABLE
            Map.entry("JEVariableLabel", InstructionArgumentType.LABEL),
            Map.entry("variableName", InstructionArgumentType.VARIABLE),

            //QUOTE + JUMP_EQUAL_FUNCTION
            Map.entry("functionName", InstructionArgumentType.FUNCTION_REF),
            Map.entry("functionArguments", InstructionArgumentType.FUNC_PARAM_LIST),

            // JUMP_EQUAL_FUNCTION
            Map.entry("JEFunctionLabel", InstructionArgumentType.LABEL)
    );

    private final static BiConsumer<Integer, Integer> DO_NOTHING = (a,b) -> {};
    // what functions we have in the file
    private final Map<String,Function> name2Function = new HashMap<>();

    // keeps references for functions by name until we processed them
    private final Set<FunctionCall> toBeResolved = new HashSet<>();

    public Program getProgram(SProgram sProgram, LoadingListener listener) {
        List<SInstruction> sInstructions = sProgram.getSInstructions().getSInstruction();


        var sFunctions = sProgram.getSFunctions();
        if(sFunctions != null){
            for (SFunction sFunction: sFunctions.getSFunction()){
                Function function = translateFunction(sFunction);
                name2Function.put(function.getName(),function);
            }
        }

        List<Instruction> instructions = translateInstructions(
                sInstructions,
                (currentInstruction, totalInstructions) -> {
                    if(listener != null)
                        listener.onInstructionProcessed(currentInstruction, totalInstructions);
                }
        );

        // resolve all the function references, because now we processed all of them
        toBeResolved.forEach(
                functionCall -> functionCall.resolveFunction(
                        name2Function.get(functionCall.getReferralName())
                )
        );
        // Note: some of them may be null here, this will be checked in the validation process

        if(listener != null) {
            listener.onLoadingCompleted();
        }

        return new StandardProgram(sProgram.getName(), instructions);
    }

    public Set<FunctionCall> getFunctionReferences() {
        return toBeResolved;
    }

    private List<Instruction> translateInstructions(List<SInstruction> sInstructions,
                                                    BiConsumer<Integer, Integer> onInstructionProcessed) {
        List<Instruction> instructions = new ArrayList<>();

        int totalInstructions = sInstructions.size();
        int currentInstruction = 0;

        for (SInstruction sInstruction : sInstructions) {
            InstructionData instructionData = InstructionData.valueOf(sInstruction.getName());
            Variable variable = str2Variable(sInstruction.getSVariable());
            Label label = str2Label(sInstruction.getSLabel());

            List<InstructionArgument> arguments = getArguments(sInstruction);

            Instruction instruction = InstructionFactory.
                    createInstruction(instructionData, variable, label, arguments);

            instructions.add(instruction);
            currentInstruction++;

            onInstructionProcessed.accept(currentInstruction, totalInstructions);
        }
        return instructions;
    }

    private Function translateFunction(SFunction sFunction){
        return new Function(
                sFunction.getName(),
                sFunction.getUserString(),
                translateInstructions(sFunction.getSInstructions().getSInstruction(), DO_NOTHING)
        );
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

        // all labels should be L[some number] so this should never throw
        if(!str.matches("l\\d++"))
            throw new SProgramXMLException("Unknown label format: " + str);
        int numberPart = Integer.parseInt(str.replaceAll("\\D", ""));

        if(numberPart == 0) // negatives won't match at prev check anyway
            throw new SProgramXMLException("Invalid label number: " + str);

        // take only the lineId part and construct a numeric label
        return new NumericLabel(numberPart);
    }

    private FunctionParamList parseParamsString(String paramsString) {
        if (paramsString == null || paramsString.isEmpty()) {
            return new FunctionParamList(List.of());
        }

        List<String> parts = splitTopLevelParams(paramsString);

        List<FunctionParam> params = new ArrayList<>();

        for (String part : parts) {
            if (part.isEmpty()) continue;

            try {
                // try parsing as number
                Long number = Long.parseLong(part);
                params.add(new NumericConstant(number));
            } catch (NumberFormatException e) {
                if(part.startsWith("("))
                    params.add(parseCompositionCall(part));
                else
                    params.add(str2Variable(part));
            }
        }

        return new FunctionParamList(params);
    }

    private List<String> splitTopLevelParams(String input) {
        List<String> params = new ArrayList<>();
        int depth = 0, last = 0;
        for(int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if(c == '(') depth++;
            else if(c == ')') depth--;
            else if(c == ',' && depth == 0) {
                params.add(input.substring(last, i).trim());  // Only split at depth 0
                last = i + 1;
            }
        }
        params.add(input.substring(last).trim());
        return params;
    }

    private FunctionCall parseCompositionCall(String callString) {
        if (callString == null || !callString.startsWith("(") || !callString.endsWith(")")) {
            throw new IllegalArgumentException("Invalid call string: " + callString + " while parsing composition call");
        }

        // remove parentheses
        String innerString = callString.substring(1, callString.length() - 1);

        // split on first comma if any
        int firstComma = innerString.indexOf(',');
        String referralName;
        FunctionParamList paramList;

        if (firstComma == -1) {
            // only referralName, no params
            referralName = innerString;
            paramList = new FunctionParamList(List.of());
        } else {
            referralName = innerString.substring(0, firstComma);
            String paramsString = innerString.substring(firstComma + 1);
            paramList = parseParamsString(paramsString);
        }

        var functionCall = new FunctionCall(referralName, paramList);
        toBeResolved.add(functionCall);
        return functionCall;
    }


    private List<InstructionArgument> getArguments(SInstruction sInstruction) {
        List<InstructionArgument> res = new ArrayList<>();

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
                    NumericConstant constant = new NumericConstant(
                            Long.valueOf(argument.getValue())
                    );
                    res.add(constant);
                    break;

                case FUNCTION_REF:
                    FunctionCall functionCall = new FunctionCall(argument.getValue());
                    toBeResolved.add(functionCall);
                    res.add(functionCall);
                    break;

                case FUNC_PARAM_LIST:
                    FunctionParamList paramList = parseParamsString(argument.getValue());
                    res.add(paramList);
                    break;
            }
        }

        return res;
    }
}
