package engine.jaxb.loader.exception;

public class NotXMLException extends Exception {
    public NotXMLException(String message) {
        super(message);
    }

  public NotXMLException(String message, Throwable cause) {
    super(message, cause);
  }
}
