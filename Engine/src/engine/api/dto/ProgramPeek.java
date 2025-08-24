package engine.api.dto;

import java.util.List;

public record ProgramPeek(
        String name,
        List<String> inputVariables,
        List<String> labelsUsed,
        List<InstructionPeek> instructions
){}
