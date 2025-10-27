package web.exception;

public class BadAuthorizationException extends Exception {
    public BadAuthorizationException(String message) {
        super(message);
    }
}
