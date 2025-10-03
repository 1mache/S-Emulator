package engine.loader.event;

public interface LoadingListener {
    void onInstructionProcessed(int instructionIndex, int totalInstructions);
    void onLoadingCompleted();
}
