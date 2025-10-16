package engine.api.debug;

public class DebugSessionNotFound extends RuntimeException {
    public DebugSessionNotFound(String message) {
        super(message);
    }
}
