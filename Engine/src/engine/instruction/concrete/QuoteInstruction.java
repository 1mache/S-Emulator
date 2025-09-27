package engine.instruction.concrete;

import engine.execution.ProgramRunner;
import engine.expansion.ProgramExpander;
import engine.expansion.SymbolRegistry;
import engine.function.FunctionReference;
import engine.function.parameter.FunctionParam;
import engine.function.parameter.FunctionParamList;
import engine.instruction.Instruction;
import engine.instruction.argument.InstructionArgument;
import engine.execution.context.RunContext;
import engine.instruction.AbstractInstruction;
import engine.instruction.InstructionData;
import engine.instruction.utility.Instructions;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.function.Function;
import engine.label.NumericLabel;
import engine.numeric.constant.NumericConstant;
import engine.program.Program;
import engine.program.StandardProgram;
import engine.program.generator.LabelVariableGenerator;
import engine.resolver.ResolutionContext;
import engine.resolver.SymbolResolver;
import engine.variable.Variable;
import engine.variable.VariableType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            var quotedFunc = quotedFuncReference.getFunction();
            int avaliableWorkVarNumber = getAvaliableWorkVarNumber();
            int avaliableLabelNumber = getAvaliableLabelNumber();
            final Label emptyLabel = FixedLabel.EMPTY; // just to not type it :)

            SymbolRegistry usedSymbols = new SymbolRegistry();

            List<Instruction> instructions = new ArrayList<>();
            // first instruction is a NOOP with a label of this instruction
            instructions.add(new NeutralInstruction(Variable.RESULT, getLabel()));

            List<Variable> usedWorkVariables = new ArrayList<>();
            if(getVariable().getType() == VariableType.WORK) usedWorkVariables.add(getVariable());

            // z_i <- x_i for all inputs x_i of the quoted function
            Map<Variable,Variable> inputSubstitutions = new HashMap<>();

            for (int i = 0; i < functionParams.params().size(); i++) {
                // param that "x_i" of the function gets
                var paramXi = functionParams.params().get(i);

                Variable zi = Variable.createWorkVariable(avaliableWorkVarNumber++);

                instructions.add(getAssignmentForParam(zi, emptyLabel ,paramXi));

                var xi = Variable.createInputVariable(i+1);
                inputSubstitutions.put(xi, zi);
                usedSymbols.registerVariable(zi);
                usedWorkVariables.add(zi);
            }
            // result variable substitution
            Variable zy = Variable.createWorkVariable(avaliableWorkVarNumber++);
            usedSymbols.registerVariable(zy);
            usedWorkVariables.add(zy);
            // EXIT label substitution
            Label exitSubstitution = new NumericLabel(avaliableLabelNumber++);
            usedSymbols.registerLabel(exitSubstitution);

            var usedLabels = Instructions.extractUsedLabels(this);

            SymbolRegistry ignoredSymbols = new SymbolRegistry(
                    usedLabels,
                    Instructions.extractVariables(this)
            );

            ResolutionContext resolutionContext = new ResolutionContext(
                usedSymbols,
                ignoredSymbols,
                new LabelVariableGenerator(
                        usedLabels,
                        usedWorkVariables
                )
            );

            instructions.addAll(
                    new SymbolResolver(resolutionContext).resolveFunctionSymbolsCollisions(
                            quotedFunc.getInstructions(),
                            exitSubstitution,
                            zy,
                            inputSubstitutions
                    )
            );

            // z_y <- result of the quoted function
            instructions.add(new AssignmentInstruction(getVariable(), exitSubstitution, zy));

            return new StandardProgram(getName() + "_EXP",instructions);
        }

        private Instruction getAssignmentForParam(Variable into, Label label, FunctionParam param) {
            if(param instanceof Variable v)
                return new AssignmentInstruction(into, label, v);
            else if (param instanceof NumericConstant c)
                return new ConstantAssignmentInstruction(into, label, c);

            throw new IllegalArgumentException("Unknown param type");
        }
    }
