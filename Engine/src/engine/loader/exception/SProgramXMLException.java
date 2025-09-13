package engine.loader.exception;

/**
 * This exception is for when the formats that were defined for the input XML files aren't
 * preserved. Should never happen, that's why the exception is unchecked.
 */
public class SProgramXMLException extends RuntimeException {
    public SProgramXMLException(String message) {
        super(message);
    }

  public SProgramXMLException(String message, Throwable cause) {
    super(message, cause);
  }
}
