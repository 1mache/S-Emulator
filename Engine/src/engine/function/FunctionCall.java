package engine.function;

import engine.execution.ProgramRunner;
import engine.execution.context.RunContext;
import engine.function.parameter.FunctionParam;
import engine.function.parameter.FunctionParamList;
import engine.instruction.argument.InstructionArgument;
import engine.instruction.argument.InstructionArgumentType;
import engine.program.Program;

import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * class used to have a temporary object at loading process that allows to delay the
 * instantiation of the Function itself and hold a reference to it until then by name
 */
public class FunctionCall implements InstructionArgument, FunctionParam {
    private Program function;
    private final String referralName;
    private FunctionParamList paramList;

    public FunctionCall(String referralName) {
        this.referralName = referralName;
    }

    public FunctionCall(String referralName, FunctionParamList paramList) {
        this.referralName = referralName;
        this.paramList = paramList;
    }

    public FunctionCall(Program function, String referralName, FunctionParamList paramList) {
        this.function = function;
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

    public Program getFunction() {
        return function;
    }

    // when we found and processed the function we call this to resolve the reference
    public void resolveFunction(Program function) {
        this.function = function;
    }

    public FunctionParamList getParamList() {
        return paramList;
    }

    public void setParamList(FunctionParamList paramList) {
        this.paramList = paramList;
    }

    @Override
    public EvaluationResult eval(RunContext context) {
        var runner = new ProgramRunner(function);
        final long[] cycles = {0};
        runner.initInputVariablesSpecific(
                paramList.params().stream()
                        .map(param -> {
                            EvaluationResult result = param.eval(context);
                            cycles[0] += result.calculationCyclesCost();
                            return result.value();
                        })
                        .toList()
        );
        runner.run();
        cycles[0] += runner.getCycles();

        return new EvaluationResult(runner.getRunOutput(), cycles[0]);
    }

    @Override
    public String stringRepresentation() {
        StringBuilder sb = new StringBuilder();
        String funcUserString;
        if(function instanceof Function func)
            funcUserString = func.getUserString();
        else
            funcUserString = referralName;

        if(paramList.params().isEmpty()){
            sb.append(String.format("(%s)", funcUserString));
        }
        else {
            sb.append(
                    Stream.of(funcUserString, paramList.stringRepresentation())
                            .collect(Collectors.joining(",", "(", ")"))
            );
        }

        return sb.toString();
    }
}
