package engine.api;

import engine.api.dto.FunctionIdentifier;
import engine.api.dto.debug.DebugHandle;
import engine.api.dto.ProgramExecutionResult;
import engine.api.dto.ProgramPeek;
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
    private String mainProgramName;
    private Program currentProgram;
    private final Map<String,Program> avaliablePrograms = new HashMap<>();
    private ProgramExpander programExpander;

    // history
    private Map<String, List<ProgramExecutionResult>> previousExecutions;

    private SLanguageEngine(){}
    // Singleton
    private static final SLanguageEngine instance = new SLanguageEngine();

    public static SLanguageEngine getInstance(){
        return instance;
    }

    public void loadProgram(String path, LoadingListener listener)
            throws NotXMLException, FileNotFoundException, UnknownLabelException, UnknownFunctionException {
        FromXMLProgramLoader loader = new FromXMLProgramLoader();
        loader.loadXML(path, listener);
        loader.validateProgram();
        currentProgram = loader.getProgram();

        mainProgramName = currentProgram.getName();
        avaliablePrograms.clear();
        avaliablePrograms.put(currentProgram.getName(), currentProgram);
        avaliablePrograms.putAll(
                loader.getFunctions().stream()
                        .collect(Collectors.toMap(
                                Program::getName, program -> program
                        ))
        );

        programExpander = new ProgramExpander(currentProgram);

        previousExecutions = new LinkedHashMap<>();
    }

    public boolean programNotLoaded(){
        return currentProgram == null;
    }

    public int getMaxExpansionDegree() {
        if(programNotLoaded())
            throw new SProgramNotLoadedException("Program has not been loaded");
        return currentProgram.getMaxExpansionDegree();
    }

    public ProgramPeek getProgramPeek() {
        return getExpandedProgramPeek(0) ;
    }

    public ProgramPeek getExpandedProgramPeek(int expansionDegree) {
        if(expansionDegree > getMaxExpansionDegree())
            throw new IllegalArgumentException("Expansion degree exceeds maximum allowed. Which is " + currentProgram.getMaxExpansionDegree());
        if(programNotLoaded()) {
            throw new SProgramNotLoadedException("Program is not loaded");
        }

        return new ProgramViewer(currentProgram).getProgramPeek(expansionDegree, programExpander);
    }

    public ProgramExecutionResult runProgram(List<Long> inputs, int expansionDegree, boolean specificInputs) {
        validateInputs(inputs);

        var expandedProgram = createExpandedProgram(expansionDegree);
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

        addExecutionToHistory(executionResult);

        return executionResult;
    }

    public DebugHandle debugProgram(List<Long> inputs, int expansionDegree, boolean specificInputs) {
       validateInputs(inputs);

        var expandedProgram = createExpandedProgram(expansionDegree);
        var debugger = new ProgramDebugger(expandedProgram);

        initializeInputs(debugger, inputs, specificInputs);

        return new DebugHandle(
                debugger,
                debugResult -> {
                    addExecutionToHistory(
                            new ProgramExecutionResult(
                                    debugResult.output(),
                                    debugResult.variableMap(),
                                    inputs,
                                    expansionDegree,
                                    debugger.getCycles()
                            )
                    );
                }
        );
    }


    // returns all the functions names that the program uses including the main program. the main is first in list
    public List<FunctionIdentifier> getAvaliablePrograms() {
        var functionStringsList = new ArrayList<>(avaliablePrograms.values().stream()
                .filter(program -> !program.getName().equals(mainProgramName))
                // everyone except main is a function (if not something's wrong)
                .map(program -> {
                    var function = (Function) program;
                    return new FunctionIdentifier(function.getName(), function.getUserString());
                })
                .toList());
        // main program comes first, then functions
        functionStringsList.addFirst(new FunctionIdentifier(mainProgramName, mainProgramName));

        return functionStringsList;
    }

    public void setCurrentProgram(FunctionIdentifier programName) {
        Program requested = avaliablePrograms.get(programName.name());
        if(requested == null)
            throw new IllegalArgumentException("File does not contain program: " + programName);

        currentProgram = requested;
        programExpander = new ProgramExpander(currentProgram);
    }

    public List<ProgramExecutionResult> getExecutionHistoryOfCurrent(){
        List<ProgramExecutionResult> results = previousExecutions.get(currentProgram.getName());

        // if no history for this function, return empty list
        return Objects.requireNonNullElseGet(results, List::of);
    }

    public List<Integer> getInstructionsIdsThatUse(String symbolStr, int expansionDegree){
        Variable maybeVariable = str2Variable(symbolStr);
        if(maybeVariable != null)
            return ProgramViewer.idsOfInstructionsThatUse(
                    programExpander.expand(expansionDegree),
                    maybeVariable
            );
        Label maybeLabel = str2Label(symbolStr);
        if(maybeLabel != null)
            return ProgramViewer.idsOfInstructionsThatUse(
                    programExpander.expand(expansionDegree),
                    maybeLabel
            );

        throw new IllegalArgumentException("Illegal symbol: " + symbolStr);
    }

    // =============== private ===============

    private void validateInputs(List<Long> inputs) {
        for (var input : inputs) {
            if (input < 0)
                throw new IllegalArgumentException("Input values must be non-negative");
        }
        if (programNotLoaded())
            throw new SProgramNotLoadedException("Program is not loaded");
    }

    private Program createExpandedProgram(int expansionDegree) {
        if (expansionDegree > currentProgram.getMaxExpansionDegree())
            throw new IllegalArgumentException("Expansion degree exceeds maximum allowed. Which is " + currentProgram.getMaxExpansionDegree());
        return programExpander.expand(expansionDegree);
    }

    private void initializeInputs(ProgramRunner runner, List<Long> inputs, boolean specificInputs) {
        if (specificInputs)
            runner.initInputVariablesSpecific(inputs);
        else
            runner.initInputVariables(inputs);
    }

    private void addExecutionToHistory(ProgramExecutionResult executionResult) {
        List<ProgramExecutionResult> executionResults = previousExecutions.get(currentProgram.getName());
        if (executionResults == null) {
            // key does not exist, create a new list with the item
            executionResults = new ArrayList<>();
            executionResults.add(executionResult);
            previousExecutions.put(currentProgram.getName(), executionResults);
        } else {
            // key exists, add the item to the existing list
            executionResults.add(executionResult);
        }
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
