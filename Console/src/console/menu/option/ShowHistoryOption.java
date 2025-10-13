package console.menu.option;

import console.menu.option.helper.ProgramName;
import engine.api.EngineRequest;
import engine.api.SLanguageEngine;
import engine.api.dto.ProgramExecutionResult;
import engine.execution.exception.SProgramNotLoadedException;

import java.util.List;

public class ShowHistoryOption extends MenuPage {
    public ShowHistoryOption() {
        super("Show Execution History", "");
    }

    @Override
    public void execute(SLanguageEngine engine, ProgramName programName) {
        try {
            List<ProgramExecutionResult> history = engine.getExecutionHistory(
                    new EngineRequest(USERNAME, programName.get())
            );
            if(history.isEmpty()){
                System.out.println("No executions found for " + programName.get() + " yet.");
            }
            int i = 0;
            for (var execution : history) {
                System.out.printf(
                        "Execution #%d: On Inputs: %s with Expansion Degree: %d, took %d Cycles. Result: %d%n",
                        i + 1,
                        execution.inputs(),
                        execution.expansionDegree(),
                        execution.cycles(),
                        execution.outputValue()
                );

                i++;
            }

        } catch (SProgramNotLoadedException e) {
            System.out.println("Program is not loaded. Load it first (option 1).");
        }
    }
}
