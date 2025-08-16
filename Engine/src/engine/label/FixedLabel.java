package engine.label;

public enum FixedLabel implements Label {
    EMPTY{
        @Override
        public String stringRepresentation() {return "";}
    },
    EXIT{
        @Override
        public String stringRepresentation() {return "EXIT";}
    }
    ;

    @Override
    public abstract String stringRepresentation();
}
