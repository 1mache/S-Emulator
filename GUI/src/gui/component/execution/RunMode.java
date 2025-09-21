package gui.component.execution;

public enum RunMode {
    EXECUTION("Execution"),
    DEBUG("Debug")
    ;

    private final String name;

    RunMode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
