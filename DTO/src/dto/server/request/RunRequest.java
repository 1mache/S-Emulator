package dto.server.request;

import java.util.List;

public record RunRequest(String programName, int expansionDegree, List<Long> inputs){
}
