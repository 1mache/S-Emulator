package engine.variable;

public enum VariableType {
    INPUT{
        @Override
        public String stringRepresentation() {return Character.toString(INPUT_VARIABLE_CHAR);}
    },
    WORK{
        @Override
        public String stringRepresentation() {return Character.toString(WORK_VARIABLE_CHAR);}
    },
    RESULT{
        @Override
        public String stringRepresentation() {return Character.toString(RESULT_VARIABLE_CHAR);}
    }
    ;

    public abstract String stringRepresentation();

    public static final char INPUT_VARIABLE_CHAR = 'x';
    public static final char WORK_VARIABLE_CHAR = 'z';
    public static final char RESULT_VARIABLE_CHAR = 'y';
}
