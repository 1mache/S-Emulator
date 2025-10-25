package engine.instruction;

import java.util.Comparator;

public enum Architecture {
    I(1),
    II(2),
    III(3),
    IV(4)
    ;
    private final int ordinal;

    Architecture(int ordinal){
        this.ordinal = ordinal;
    }

    public static final Comparator<Architecture> ARCHITECTURE_COMPARATOR = Comparator.comparingInt(Enum::ordinal);

    public int getOrdinal(){
        return ordinal;
    }
}
