package gui.component.execution;

public enum RunMode {
    RUN("Execution"),
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
