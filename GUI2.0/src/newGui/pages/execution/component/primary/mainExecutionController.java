package newGui.pages.execution.component.primary;


import dto.ProgramPeek;
import newGui.pages.execution.component.execution.executionController;
import newGui.pages.execution.component.instructions.instructionsController;
import newGui.pages.execution.component.top.topController;
import newGui.pages.primary.mainClientAppController;

public class mainExecutionController {

    private mainClientAppController mainClientAppController;

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



    public void setMainClientAppController(mainClientAppController mainAppController) {
        this.mainClientAppController = mainAppController;
    }

    public void activate(long credits) {
        topController.init(mainClientAppController.getUserNameProperty(), credits);
    }

    public void setProgramPeek(ProgramPeek programPeek) {

        topController.set(programPeek);
        executionController.setProgramPeek(programPeek);
        instructionsController.setProgramPeek(programPeek.instructions());



    }
}
