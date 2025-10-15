package engine.api;

import dto.FunctionIdentifier;
import dto.ProgramExecutionResult;
import dto.ProgramPeek;
import engine.api.debug.DebugHandle;
import engine.debugger.ProgramDebugger;
import engine.execution.ProgramRunner;
import engine.execution.exception.SProgramNotLoadedException;
import engine.expansion.ProgramExpander;
import engine.function.Function;
import engine.label.FixedLabel;
import engine.label.Label;
import engine.label.NumericLabel;
import engine.loader.FromXMLProgramLoader;
import engine.loader.event.LoadingListener;
import engine.loader.exception.NotXMLException;
import engine.loader.exception.UnknownFunctionException;
import engine.loader.exception.UnknownLabelException;
import engine.peeker.ProgramViewer;
import engine.program.Program;
import engine.variable.Variable;
import engine.variable.VariableType;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

public class SLanguageEngine {
    private final Map<String,Program> avaliablePrograms = new HashMap<>();

    private SLanguageEngine(){}
    // Singleton
    private static final SLanguageEngine instance = new SLanguageEngine();

    public static SLanguageEngine getInstance(){
        return instance;
    }

    public String loadProgram(String path, LoadingListener listener)
            throws NotXMLException, FileNotFoundException, UnknownLabelException, UnknownFunctionException {
        FromXMLProgramLoader loader = new FromXMLProgramLoader();
        loader.loadXML(path, listener);
        loader.validateProgram();
        var mainProgram = loader.getProgram();

        avaliablePrograms.clear();
        avaliablePrograms.put(mainProgram.getName(), mainProgram);
        avaliablePrograms.putAll(
                loader.getFunctions().stream()
                        .collect(Collectors.toMap(
                                Program::getName, program -> program
                        ))
        );

        return mainProgram.getName();
    }

    public boolean programNotLoaded(String programName) {
        return !avaliablePrograms.containsKey(programName);
    }

    public int getMaxExpansionDegree(String programName) {
        if(programNotLoaded(programName))
            throw new SProgramNotLoadedException("Program " +  programName + " has not been loaded");

        var program = getProgramByName(programName);

        return program.getMaxExpansionDegree();
    }

    public ProgramPeek getProgramPeek(String programName, int expansionDegree) {
        if(programNotLoaded(programName))
            throw new SProgramNotLoadedException("Program " +  programName + " has not been loaded");

        var program = getProgramByName(programName);
        if(expansionDegree > getMaxExpansionDegree(programName)) {
            throw new IllegalArgumentException("Expansion degree exceeds maximum allowed. Which is " + program.getMaxExpansionDegree());
        }

        return new ProgramViewer(program).getProgramPeek(expansionDegree);
    }

    public ProgramExecutionResult runProgram(String programName,
                                             int expansionDegree,
                                             List<Long> inputs,
                                             boolean specificInputs,
                                             RunHistory history) {
        validateInputs(inputs);

        Program expandedProgram = createExpandedProgram(programName, expansionDegree);
        var runner = new ProgramRunner(expandedProgram);

        initializeInputs(runner, inputs, specificInputs);

        runner.run();

        var executionResult = new ProgramExecutionResult(
                runner.getRunOutput(),
                runner.getAllVariableValues(),
                inputs,
                expansionDegree,
                runner.getCycles()
        );

        history.addExecution(programName, executionResult);

        return executionResult;
    }

    // ========================== Debug ===========================
    public DebugHandle startDebugSession(String programName,
                                         int expansionDegree,
                                         List<Long> inputs,
                                         boolean specificInputs,
                                         RunHistory history) {
        validateInputs(inputs);

        Program expandedProgram = createExpandedProgram(programName, expansionDegree);
        var debugger = new ProgramDebugger(expandedProgram);

        initializeInputs(debugger, inputs, specificInputs);

        return new DebugHandle(
                debugger,
                debugResult -> history.addExecution(
                        programName,
                        new ProgramExecutionResult(
                                debugResult.output(),
                                debugResult.variableMap(),
                                inputs,
                                expansionDegree,
                                debugger.getCycles()
                        )
                )
        );
    }
    // =============================================================

    // returns all the functions names that the program uses including the main programs. the programs are first in list
    public List<FunctionIdentifier> getAvaliablePrograms() {
        var functionStringsList = new ArrayList<>(avaliablePrograms.values().stream()
                .filter(program -> program instanceof Function)
                .map(program -> {
                    var function = (Function) program;
                    return new FunctionIdentifier(function.getName(), function.getUserString());
                })
                .toList());

        // programs are first in the list
        avaliablePrograms.values().stream()
                .filter(program -> !(program instanceof Function))
                .map(program -> new FunctionIdentifier(program.getName(), program.getName()))
                .forEach(functionStringsList::addFirst);

        return functionStringsList;
    }

    public List<Integer> getInstructionsIdsThatUse(String programName, int expansionDegree, String symbolStr){
        var expandedProgram = createExpandedProgram(programName, expansionDegree);

        Variable maybeVariable = str2Variable(symbolStr);
        if(maybeVariable != null)
            return ProgramViewer.idsOfInstructionsThatUse(
                    expandedProgram,
                    maybeVariable
            );
        Label maybeLabel = str2Label(symbolStr);
        if(maybeLabel != null)
            return ProgramViewer.idsOfInstructionsThatUse(
                    expandedProgram,
                    maybeLabel
            );

        throw new IllegalArgumentException("Illegal symbol: " + symbolStr);
    }

    // =============== private ===============

    private synchronized Program getProgramByName(String programName) {
        // synchronized because avaliablePrograms map is accessed
        var program = avaliablePrograms.get(programName);
        if(program == null)
            throw new IllegalArgumentException("File does not contain program: " + programName);
        return program;
    }

    private void validateInputs(List<Long> inputs) {
        for (var input : inputs) {
            if (input < 0)
                throw new IllegalArgumentException("Input values must be non-negative");
        }
    }

    private Program createExpandedProgram(String programName, int expansionDegree) {
        if(programNotLoaded(programName))
            throw new SProgramNotLoadedException("Program " + programName + " has not been loaded");

        var program = getProgramByName(programName);

        if (expansionDegree > program.getMaxExpansionDegree())
            throw new IllegalArgumentException("Expansion degree exceeds maximum allowed. Which is " + program.getMaxExpansionDegree());

        return new ProgramExpander(program).expand(expansionDegree);
    }

    private void initializeInputs(ProgramRunner runner, List<Long> inputs, boolean specificInputs) {
        if (specificInputs)
            runner.initInputVariablesSpecific(inputs);
        else
            runner.initInputVariables(inputs);
    }

    private Variable str2Variable(String str){
        if(str.length() == 2) {
            return switch (str.charAt(0)) {
                case VariableType.INPUT_VARIABLE_CHAR -> Variable.createInputVariable(
                        Character.getNumericValue(str.charAt(1))
                );
                case VariableType.WORK_VARIABLE_CHAR -> Variable.createWorkVariable(
                        Character.getNumericValue(str.charAt(1))
                );
                default -> null;
            };
        }

        // different length, must be "y", or it is invalid
        if(str.equals(Character.toString(VariableType.RESULT_VARIABLE_CHAR))) {
            return Variable.RESULT;
        }

        return null;
    }

    private Label str2Label(String str) {
        if(str.equals(FixedLabel.EXIT.stringRepresentation()))
            return FixedLabel.EXIT; // exit label

        if(!str.matches("L\\d++"))
            return null;
        int numberPart = Integer.parseInt(str.replaceAll("\\D", ""));

        // take only the lineId part and construct a numeric label
        return new NumericLabel(numberPart);
    }
}
