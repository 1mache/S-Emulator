package engine.execution;

import engine.execution.context.VariableContext;

public interface Runner {
    // TODO: javadoc
    void initInputVariables(Long... initInputs);
    /**
     * executes the program given initial values of input variables
     */
    void run(int expansionLevel);

    /**
     * @return the result variable 'y'
     */
    Long getResult();
    Long getCycles();

    VariableContext getVariableContext();
}
