package engine.instruction.concrete;

import engine.execution.ProgramRunner;
import engine.expansion.SymbolRegistry;
import engine.function.FunctionCall;
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
import engine.loader.exception.SProgramXMLException;
import engine.numeric.constant.NumericConstant;
import engine.program.Program;
import engine.program.StandardProgram;
import engine.resolver.SymbolResolver;
import engine.variable.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

    public class QuoteInstruction extends AbstractInstruction {
        private final FunctionCall quotedFuncReference;

        private long lastExecutionCycles = 0;

        public QuoteInstruction(Variable variable,
                                Label label,
                                FunctionCall quotedFuncReference,
                                FunctionParamList paramList) {
            super(InstructionData.QUOTE, variable, label);
            if(paramList == null)
                throw new SProgramXMLException("No param list provided for " + quotedFuncReference.getReferralName());

            this.quotedFuncReference = quotedFuncReference;
            quotedFuncReference.setParamList(paramList);
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
                    quotedFuncReference.getParamList().params().stream().
                            map(param -> param.eval(context))
                            .toList()
            );
            runner.run();
            // set the variable to the result of the run
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
            if(quotedFuncReference.getParamList().params().isEmpty()){
                sb.append(String.format("(%s)", funcUserString));
            }
            else {
                sb.append(
                        Stream.of(funcUserString, quotedFuncReference.getParamList().stringRepresentation())
                        .collect(Collectors.joining(",", "(", ")"))
                );
            }

            return sb.toString();
        }

        @Override
        public List<InstructionArgument> getArguments() {
            return List.of(quotedFuncReference, quotedFuncReference.getParamList());
        }

        @Override
        protected Program getSyntheticExpansion() {
            var quotedFunc = quotedFuncReference.getFunction();
            int avaliableWorkVarNumber = getAvaliableWorkVarNumber();
            int avaliableLabelNumber = getAvaliableLabelNumber();
            final Label emptyLabel = FixedLabel.EMPTY; // just to not type it :)

            SymbolRegistry usedSymbols = new SymbolRegistry();
            // add all the used labels and variables to the usedSymbols registry
            Instructions.extractWorkVariables(List.of(this)).forEach(usedSymbols::registerVariable);
            usedSymbols.registerLabel(this.getLabel());

            List<Instruction> instructions = new ArrayList<>();
            // first instruction is a NOOP with a label of this instruction
            instructions.add(new NeutralInstruction(Variable.RESULT, getLabel()));

            // work variables to substitute input variables IN ORDER
            List<Variable> inputSubstitutions = new ArrayList<>();
            // z_i <- x_i for all inputs x_i of the quoted function
            for (int i = 0; i < quotedFuncReference.getParamList().params().size(); i++) {
                // param that "x_i" of the function gets
                var paramXi = quotedFuncReference.getParamList().params().get(i);

                Variable zi = Variable.createWorkVariable(avaliableWorkVarNumber++);

                instructions.add(getAssignmentInstructionForParam(zi, emptyLabel ,paramXi));

                inputSubstitutions.add(zi);
            }
            // result variable substitution
            Variable resultSubstitution = Variable.createWorkVariable(avaliableWorkVarNumber++);
            // EXIT label substitution
            Label exitSubstitution = new NumericLabel(avaliableLabelNumber++);

            instructions.addAll(
                    SymbolResolver.forFunctionExpansion(
                            quotedFunc,
                            usedSymbols,
                            inputSubstitutions,
                            resultSubstitution,
                            exitSubstitution
                    ).resolveSymbolsCollisions(quotedFunc.getInstructions())
            );

            // z_y <- result of the quoted function
            instructions.add(new AssignmentInstruction(getVariable(), exitSubstitution, resultSubstitution));

            return new StandardProgram(getName() + "_EXP",instructions);
        }

        private Instruction getAssignmentInstructionForParam(Variable into, Label label, FunctionParam param) {
            if(param instanceof Variable v)
                return new AssignmentInstruction(into, label, v);
            else if (param instanceof NumericConstant c)
                return new ConstantAssignmentInstruction(into, label, c);

            // TODO: Quote for composition

            throw new IllegalArgumentException("Unknown param type");
        }
    }
