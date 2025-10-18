package gui.task;

import engine.api.SLanguageEngine;
import engine.loader.event.LoadingListener;
import engine.loader.exception.NotXMLException;
import engine.loader.exception.UnknownFunctionException;
import engine.loader.exception.UnknownLabelException;
import javafx.concurrent.Task;

import java.io.FileNotFoundException;

public class ProgramLoadTask extends Task<String> {
    private final SLanguageEngine engine;
    private final String programPath;

    private final LoadingListener loadingListener = new LoadingListener() {
        @Override
        public void onInstructionProcessed(int instructionIndex, int totalInstructions) {
            try {
                Thread.sleep(10); // Simulate loading time for better UX
            } catch (InterruptedException ignored) {}
            updateProgress(instructionIndex, totalInstructions);
        }

        @Override
        public void onLoadingCompleted() {
            updateMessage("Program has been loaded");
        }
    };

    public ProgramLoadTask(SLanguageEngine engine, String programPath) {
        this.engine = engine;
        this.programPath = programPath;
        updateMessage("Loading program...");
    }

    @Override
    protected String call()
            throws NotXMLException, FileNotFoundException, UnknownLabelException, UnknownFunctionException {

        return engine.loadProgramFromFile(programPath, loadingListener).getFirst();
    }
}
