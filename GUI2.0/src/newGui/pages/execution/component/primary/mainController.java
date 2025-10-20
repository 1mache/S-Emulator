package newGui.pages.execution.component.primary;


import newGui.pages.execution.component.execution.executionController;
import newGui.pages.execution.component.instructions.instructionsController;
import newGui.pages.execution.component.top.topController;

public class mainController {

    private executionController executionController;
    private instructionsController instructionsController;
    private topController topController;


    public executionController getExecutionController() {
        return executionController;
    }
    public instructionsController getInstructionsController() {
        return instructionsController;
    }
    public topController getTopController() {
        return topController;
    }
}
