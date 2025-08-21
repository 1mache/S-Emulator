package engine.execution;

public interface Runner {
    /**
     * executes the program given initial values of input variables
     */
    void run(Long... initInput);
    /**
     * @return the result variable 'y'
     */
    Long getResult();
}
