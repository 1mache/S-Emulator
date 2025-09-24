package engine.instruction.concrete;

import engine.execution.ProgramRunner;
import engine.function.FunctionReference;
import engine.function.parameter.FunctionParamList;
import engine.instruction.argument.InstructionArgument;
import engine.execution.context.RunContext;
import engine.instruction.AbstractInstruction;
import engine.instruction.InstructionData;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.function.Function;
import engine.program.Program;
import engine.variable.Variable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

    public class QuoteInstruction extends AbstractInstruction {
        private final FunctionReference quotedFuncReference;
        private final FunctionParamList functionParams;

        private long lastExecutionCycles = 0;

        public QuoteInstruction(Variable variable,
                                Label label,
                                FunctionReference quotedFuncReference,
                                FunctionParamList functionParams) {
            super(InstructionData.QUOTE, variable, label);
            this.quotedFuncReference = quotedFuncReference;
            this.functionParams = functionParams;
        }

        @Override
        public long cycles() {
            // TODO: maybe return it from execute??
            return super.cycles() + lastExecutionCycles /* however many cycles the quoted function took*/;
        }

        @Override
        public Label execute(RunContext context) {
            Function quotedFunc = quotedFuncReference.getFunction();
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
            Function quotedFunc = quotedFuncReference.getFunction();
            StringBuilder sb = new StringBuilder();
            sb.append(getVariable().stringRepresentation());
            sb.append(" <- ");

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

            return sb.toString();
        }

        @Override
        public List<InstructionArgument> getArguments() {
            return List.of(quotedFuncReference, functionParams);
        }

        @Override
        protected Program getSyntheticExpansion() {
            return super.getSyntheticExpansion();
        }
    }
