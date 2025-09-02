package engine.api.dto;


public record InstructionPeek(
        String stringRepresentation,
        String label,
        boolean isSynthetic,
        int cycles,
        // which instruction was this expanded from
        InstructionPeek expandedFrom, // can be null
        int lineId
){}