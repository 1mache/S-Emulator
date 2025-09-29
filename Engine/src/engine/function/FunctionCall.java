package engine.function;

import engine.function.parameter.FunctionParamList;
import engine.instruction.argument.InstructionArgument;
import engine.instruction.argument.InstructionArgumentType;


/**
 * class used to have a temporary object at loading process that allows to delay the
 * instantiation of the Function itself and hold a reference to it until then by name
 */
public class FunctionCall implements InstructionArgument {
    private Function function;
    private final String referralName;
    private FunctionParamList paramList;

    public FunctionCall(String referralName) {
        this.referralName = referralName;
    }

    public FunctionCall(String referralName, FunctionParamList paramList) {
        this.referralName = referralName;
        this.paramList = paramList;
    }

    @Override
    public InstructionArgumentType getArgumentType() {
        return InstructionArgumentType.FUNCTION_REF;
    }

    public String getReferralName() {
        return referralName;
    }

    public Function getFunction() {
        return function;
    }

    // when we found and processed the function we call this to resolve the reference
    public void resolveFunction(Function function) {
        this.function = function;
    }

    public FunctionParamList getParamList() {
        return paramList;
    }

    public void setParamList(FunctionParamList paramList) {
        this.paramList = paramList;
    }
}
