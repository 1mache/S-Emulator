package dto;


public record InstructionPeek(
        String stringRepresentation,
        String label,
        boolean isSynthetic,
        long cycles,
        // which instruction was this expanded from
        InstructionPeek expandedFrom, // can be null
        int lineId,
        String architecture
){}