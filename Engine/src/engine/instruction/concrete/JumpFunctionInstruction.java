package engine.instruction.concrete;

import engine.execution.context.RunContext;
import engine.function.Function;
import engine.function.FunctionCall;
import engine.function.parameter.FunctionParamList;
import engine.instruction.AbstractJumpInstruction;
import engine.instruction.InstructionData;
import engine.instruction.argument.InstructionArgument;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.loader.exception.SProgramXMLException;
import engine.program.Program;
import engine.program.StandardProgram;
import engine.variable.Variable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JumpFunctionInstruction extends AbstractJumpInstruction {
    private final FunctionCall quotedFunctionCall;

    public JumpFunctionInstruction(Variable variable,
                                   Label label,
                                   Label tagetLabel,
                                   FunctionCall quotedFunctionCall,
                                   FunctionParamList paramList) {
        super(InstructionData.JUMP_EQUAL_FUNCTION, variable, label, tagetLabel);
        if(paramList == null)
            throw new SProgramXMLException("No param list provided for " + quotedFunctionCall.getReferralName());
        this.quotedFunctionCall = quotedFunctionCall;
        quotedFunctionCall.setParamList(paramList);
    }

    @Override
    protected IsJumpResult isJump(RunContext context) {
        var evalResult = quotedFunctionCall.eval(context);

        return new IsJumpResult(
                context.getVariableValue(getVariable()).equals(evalResult.value()),
                evalResult.calculationCyclesCost() + staticCycles()
        );
    }

    @Override
    public String stringRepresentation() {
        var quotedFunc = quotedFunctionCall.getFunction();
        StringBuilder sb = new StringBuilder();
        var functionParams = quotedFunctionCall.getParamList();

        sb.append("IF ").append(getVariable().stringRepresentation()).append(" = ");

        String funcUserString;
        if(quotedFunc instanceof Function f)
            funcUserString = f.getUserString();
        else
            funcUserString = quotedFunctionCall.getReferralName();

        if(functionParams.params().isEmpty()){
            sb.append(String.format("(%s)", funcUserString));
        }
        else {
            sb.append(
                    Stream.of(funcUserString, functionParams.stringRepresentation())
                            .collect(Collectors.joining(", ", "(", ")"))
            );
        }
        sb.append(" GOTO ").append(getTargetLabel());

        return sb.toString();
    }

    @Override
    public List<InstructionArgument> getArguments() {
        return List.of(getTargetLabel(), quotedFunctionCall);
    }

    @Override
    protected Program getSyntheticExpansion() {
        int avaliableWorkVarNumber = getAvaliableWorkVarNumber();
        Variable z1 = Variable.createWorkVariable(avaliableWorkVarNumber);

        return new StandardProgram(
                getName() + "_EXP",
                List.of(
                        new QuoteInstruction(z1, getLabel(), quotedFunctionCall, quotedFunctionCall.getParamList()),
                        new JumpVariableInstruction(getVariable(), FixedLabel.EMPTY, getTargetLabel(), z1)
                )
        );
    }
}
