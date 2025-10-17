package dto.server.response;

public record UserData(
        String username,
        long totalCredits,
        long usedCredits,
        int programsUploaded,
        int functionsUploaded)
{}
