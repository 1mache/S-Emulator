package engine.label;

import engine.argument.Argument;

public enum FixedLabel implements Label, Argument {
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

    @Override
    public Argument toArgument() {
        return this;
    }


}
