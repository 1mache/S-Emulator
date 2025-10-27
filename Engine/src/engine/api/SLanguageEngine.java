package engine.api;

import dto.ProgramIdentifier;
import dto.ProgramExecutionResult;
import dto.ProgramPeek;
import engine.api.debug.DebugHandle;
import engine.debugger.ProgramDebugger;
import engine.execution.ExecutionLimiter;
import engine.execution.ProgramRunner;
import engine.execution.exception.SProgramNotLoadedException;
import engine.expansion.ProgramExpander;
import engine.function.Function;
import engine.instruction.Architecture;
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
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class SLanguageEngine {
    private final Map<String,Program> avaliablePrograms = new HashMap<>();
    private final Map<String, Integer> programRunCounts = new HashMap<>();

    private SLanguageEngine(){}
    // Singleton
    private static final SLanguageEngine instance = new SLanguageEngine();

    public static SLanguageEngine getInstance(){
        return instance;
    }

    // returns the names of the loaded programs and functions, main program is first in list
    public List<String> loadProgramFromFile(String path, LoadingListener listener)
            throws NotXMLException, FileNotFoundException, UnknownLabelException, UnknownFunctionException {
        FromXMLProgramLoader loader = new FromXMLProgramLoader();
        loader.loadXML(path, listener);
        return loadProgram(loader, true);
    }

    // returns the names of the loaded programs and functions, main program is first in list
    public List<String> loadProgramIncremental(InputStream inputStream, LoadingListener listener)
            throws UnknownLabelException, UnknownFunctionException {
        FromXMLProgramLoader loader = new FromXMLProgramLoader();
        loader.loadXML(inputStream, listener, avaliablePrograms);
        return loadProgram(loader, false);
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

    public int instructionCountOf(String programName) {
        if(programNotLoaded(programName))
            throw new SProgramNotLoadedException("Program " +  programName + " has not been loaded");

        var program = getProgramByName(programName);
        return program.getInstructions().size();
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

    // no limiter version
    public ProgramExecutionResult runProgram(String programName,
                                             int expansionDegree,
                                             List<Long> inputs,
                                             boolean specificInputs,
                                             RunHistory history) {
        return runProgram(programName, expansionDegree, inputs, specificInputs, history, null);
    }

    // no annoying boolean parameter
    public ProgramExecutionResult runProgram(String programName,
                                             int expansionDegree,
                                             List<Long> inputs,
                                             RunHistory history,
                                             ExecutionLimiter executionLimiter) {
        return runProgram(programName, expansionDegree, inputs, true, history, executionLimiter);
    }

    public ProgramExecutionResult runProgram(String programName,
                                             int expansionDegree,
                                             List<Long> inputs,
                                             boolean specificInputs, // kept for legacy, in all advanced versions this is true
                                             RunHistory history,
                                             ExecutionLimiter executionLimiter) {
        validateInputs(inputs);

        Program expandedProgram = createExpandedProgram(programName, expansionDegree);
        var runner = new ProgramRunner(expandedProgram, executionLimiter);

        initializeInputs(runner, inputs, specificInputs);

        runner.run();

        var executionResult = new ProgramExecutionResult(
                programName,
                runner.getRunOutput(),
                runner.getAllVariableValues(),
                inputs,
                expansionDegree,
                runner.getCycles(),
                executionLimiter != null && executionLimiter.isStopped()
        );

        history.addExecution(programName, executionResult);
        incrementRunCount(programName);

        return executionResult;
    }

    public int getRunCountOf(String programName) {
        if(programNotLoaded(programName))
            throw new SProgramNotLoadedException("Program " +  programName + " has not been loaded");

        return programRunCounts.getOrDefault(programName, 0);
    }

    // ========================== Debug ===========================
    // no limiter version
    public DebugHandle startDebugSession(String programName,
                                         int expansionDegree,
                                         List<Long> inputs,
                                         RunHistory history,
                                         List<Integer> breakpoints) {
        return startDebugSession(programName, expansionDegree, inputs, history, breakpoints, null);
    }

    public DebugHandle startDebugSession(String programName,
                                         int expansionDegree,
                                         List<Long> inputs,
                                         RunHistory history,
                                         List<Integer> breakpoints,
                                         ExecutionLimiter executionLimiter) {
        validateInputs(inputs);

        Program expandedProgram = createExpandedProgram(programName, expansionDegree);
        var debugger = new ProgramDebugger(expandedProgram, executionLimiter);
        breakpoints.forEach(debugger::addBreakpoint); // add breakpoints

        initializeInputs(debugger, inputs, true);

        incrementRunCount(programName);

        return new DebugHandle(
                debugger,
                debugResult -> history.addExecution(
                        programName,
                        new ProgramExecutionResult(
                                programName,
                                debugResult.output(),
                                debugResult.variableMap(),
                                inputs,
                                expansionDegree,
                                debugger.getCycles(),
                                executionLimiter != null && executionLimiter.isStopped()
                        )
                )
        );
    }
    // =============================================================

    public long getAverageCostOf(String programName) {
        if (programNotLoaded(programName))
            throw new SProgramNotLoadedException("Program " +  programName + " has not been loaded");

        Program program = getProgramByName(programName);
        return 0; // TODO: proper logic
    }

    public Architecture getArchitectureOf(String programName){
        if (programNotLoaded(programName))
            throw new SProgramNotLoadedException("Program " +  programName + " has not been loaded");

        Program program = getProgramByName(programName);
        return program.getArchitecture();
    }

    // returns all the functions names that the program uses including the main programs. the programs are first in list
    public List<ProgramIdentifier> getAvaliablePrograms() {
        return getFunctionIdentifiers(avaliablePrograms.values());
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

    public ProgramIdentifier getProgramIdentifier(String programName) {
        if (programNotLoaded(programName))
            throw new SProgramNotLoadedException("Program " +  programName + " has not been loaded");

        var program = getProgramByName(programName);
        if (program instanceof Function function) {
            return new ProgramIdentifier(function.getName(), function.getUserString(), false);
        } else {
            return new ProgramIdentifier(program.getName(), program.getName(), true);
        }
    }

    // =============== private ===============

    private Program getProgramByName(String programName) {
        Program program;
        program = avaliablePrograms.get(programName);

        if(program == null)
            throw new IllegalArgumentException("File does not contain program: " + programName);
        return program;
    }

    // helper for internal use
    private List<String> loadProgram(FromXMLProgramLoader loader, boolean clearExisting)
            throws UnknownLabelException, UnknownFunctionException {

        loader.validateProgram();
        if(clearExisting) {
            avaliablePrograms.clear();
        }

        var mainProgram = loader.getProgram();
        Set<Program> newPrograms = new HashSet<>();

        // only add program if not already present
        if(!avaliablePrograms.containsKey(mainProgram.getName())){
            newPrograms.add(mainProgram);
        }
        newPrograms.addAll(
                loader.getFunctions().stream()
                        // only add functions that are not already present
                        .filter(func -> !avaliablePrograms.containsKey(func.getName()))
                        .collect(Collectors.toSet())
        );

        avaliablePrograms.putAll(
                newPrograms.stream()
                        .collect(Collectors.toMap(
                                Program::getName, program -> program
                        ))
        );

        return getFunctionIdentifiers(newPrograms).stream()
                .map(ProgramIdentifier::name)
                .toList();
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

    private void incrementRunCount(String programName) {
        programRunCounts.put(
                programName,
                programRunCounts.getOrDefault(programName, 0) + 1
        );
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

    private List<ProgramIdentifier> getFunctionIdentifiers(Collection<Program> functions) {
        var functionStringsList = new ArrayList<>(functions.stream()
                .filter(program -> program instanceof Function)
                .map(program -> {
                    var function = (Function) program;
                    return new ProgramIdentifier(function.getName(), function.getUserString(), false);
                })
                .toList());

        // programs are first in the list
        functions.stream()
                .filter(program -> !(program instanceof Function))
                .map(program -> new ProgramIdentifier(program.getName(), program.getName(), true))
                .forEach(functionStringsList::addFirst);

        return functionStringsList;
    }
}
