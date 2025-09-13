package engine.loader.exception;

public class UnknownLabelException extends Exception {
    public UnknownLabelException(String message) {
        super(message);
    }

    public UnknownLabelException(String message, Throwable cause) {
        super(message, cause);
    }
}
