package console.menu.option.helper;

// wrapper for program name to be passed by reference
public class ProgramName {
    private String programName;

    public ProgramName() {
        this(null);
    }
    public ProgramName(String programName) {
        this.programName = programName;
    }

    public String get() {
        return programName;
    }

    public void set(String programName) {
        this.programName = programName;
    }
}
