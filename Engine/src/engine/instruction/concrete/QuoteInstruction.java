package engine.instruction.concrete;

import engine.execution.ProgramRunner;
import engine.function.parameter.FunctionParam;
import engine.function.parameter.FunctionParamList;
import engine.instruction.argument.InstructionArgument;
import engine.execution.context.VariableContext;
import engine.instruction.AbstractInstruction;
import engine.instruction.InstructionData;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.program.FunctionProgram;
import engine.program.Program;
import engine.variable.Variable;

import java.util.List;

public class QuoteInstruction extends AbstractInstruction {
    private final FunctionProgram quotedFunc;
    private final FunctionParamList functionParams;
    private long lastExecutionCycles = 0;

    public QuoteInstruction(Variable variable,
                            Label label,
                            FunctionProgram quotedFunc,
                            FunctionParamList functionParams) {
        super(InstructionData.QUOTE, variable, label);
        this.quotedFunc = quotedFunc;
        this.functionParams = functionParams;
    }

    @Override
    public long cycles() {
        // TODO: maybe return it from execute??
        return super.cycles() + lastExecutionCycles /* however many cycles the quoted function took*/;
    }

    @Override
    public Label execute(VariableContext context) {
        // TODO: maybe store the function programs in the context and access them by string name
        var runner = new ProgramRunner(quotedFunc);
        runner.initInputVariablesSpecific(
                functionParams.params().stream().
                        map(param -> param.eval(context))
                        .toList()
        );
        context.setVariableValue(getVariable(), runner.getRunOutput());
        lastExecutionCycles = runner.getCycles();
        return FixedLabel.EMPTY;
    }

    @Override
    public String stringRepresentation() {
        return "";
    }

    @Override
    public List<InstructionArgument> getArguments() {
        return List.of(quotedFunc, functionParams);
    }

    @Override
    protected Program getSyntheticExpansion() {
        return super.getSyntheticExpansion();
    }
}
