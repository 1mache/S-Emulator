package engine.instruction.concrete;

import engine.execution.ProgramRunner;
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
    private final FunctionCall quotedFuncReference;

    private long lastExecutionCycles;

    public JumpFunctionInstruction(Variable variable,
                                   Label label,
                                   Label tagetLabel,
                                   FunctionCall quotedFuncReference,
                                   FunctionParamList paramList) {
        super(InstructionData.JUMP_EQUAL_FUNCTION, variable, label, tagetLabel);
        if(paramList == null)
            throw new SProgramXMLException("No param list provided for " + quotedFuncReference.getReferralName());
        this.quotedFuncReference = quotedFuncReference;
        quotedFuncReference.setParamList(paramList);
    }

    @Override
    protected boolean isJump(RunContext context) {
        Function quotedFunc = quotedFuncReference.getFunction();
        var runner = new ProgramRunner(quotedFunc);
        runner.initInputVariablesSpecific(
                quotedFuncReference.getParamList().params().stream().
                        map(param -> param.eval(context))
                        .toList()
        );
        runner.run();
        lastExecutionCycles = runner.getCycles();
        return context.getVariableValue(getVariable()).equals(runner.getRunOutput());
    }

    @Override
    public long cycles() {
        return super.cycles() + lastExecutionCycles;
    }

    @Override
    public String stringRepresentation() {
        Function quotedFunc = quotedFuncReference.getFunction();
        StringBuilder sb = new StringBuilder();
        var functionParams = quotedFuncReference.getParamList();

        sb.append("IF ").append(getVariable().stringRepresentation()).append(" = ");

        var funcUserString = quotedFunc.getUserString();
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
        return List.of(getTargetLabel(), quotedFuncReference);
    }

    @Override
    protected Program getSyntheticExpansion() {
        int avaliableWorkVarNumber = getAvaliableWorkVarNumber();
        Variable z1 = Variable.createWorkVariable(avaliableWorkVarNumber);

        return new StandardProgram(
                getName() + "_EXP",
                List.of(
                        new QuoteInstruction(z1, getLabel(), quotedFuncReference, quotedFuncReference.getParamList()),
                        new JumpVariableInstruction(getVariable(), FixedLabel.EMPTY, getTargetLabel(), z1)
                )
        );
    }
}
