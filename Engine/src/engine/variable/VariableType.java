package engine.variable;

public enum VariableType {
    INPUT{
        @Override
        public String stringRepresentation() {return "x";}
    },
    WORK{
        @Override
        public String stringRepresentation() {return "z";}
    },
    RESULT{
        @Override
        public String stringRepresentation() {return "y";}
    }
    ;

    public abstract String stringRepresentation();
}
