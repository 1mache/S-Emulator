package dto.server.response;

import dto.ProgramExecutionResult;

public record ProgramExecutionResponse(ProgramExecutionResult result, boolean isEnoughCredits){
}
