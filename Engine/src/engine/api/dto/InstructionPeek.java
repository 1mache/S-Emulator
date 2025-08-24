package engine.api.dto;

public record InstructionPeek(
        String stringRepresentation,
        String label,
        boolean isSynthetic,
        int cycles,
        long number
){}