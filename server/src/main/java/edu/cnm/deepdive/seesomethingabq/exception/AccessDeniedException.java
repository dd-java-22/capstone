package edu.cnm.deepdive.seesomethingabq.exception;

/**
 * Exception thrown when a user attempts to access a resource they don't have permission for.
 */
public class AccessDeniedException extends RuntimeException {

  public AccessDeniedException() {
    super();
  }

  public AccessDeniedException(String message) {
    super(message);
  }

  public AccessDeniedException(String message, Throwable cause) {
    super(message, cause);
  }

  public AccessDeniedException(Throwable cause) {
    super(cause);
  }
}
