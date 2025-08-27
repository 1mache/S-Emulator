package engine.api.dto;


public record InstructionPeek(
        String stringRepresentation,
        String label,
        boolean isSynthetic,
        int cycles,
        InstructionPeek expandedFrom, // can be null
        int lineId
){}