package dto.server.request;

public record HighlightRequest(String programName, int expansionDegree, String symbolToHighlight) {
}
