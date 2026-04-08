package edu.cnm.deepdive.seesomethingabq.exception;

/**
 * Exception thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends RuntimeException {

  /**
   * Creates an instance with no detail message.
   */
  public ResourceNotFoundException() {
    super();
  }

  /**
   * Creates an instance with a detail message.
   *
   * @param message detail message.
   */
  public ResourceNotFoundException(String message) {
    super(message);
  }

  /**
   * Creates an instance with a detail message and cause.
   *
   * @param message detail message.
   * @param cause underlying cause.
   */
  public ResourceNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Creates an instance with a cause.
   *
   * @param cause underlying cause.
   */
  public ResourceNotFoundException(Throwable cause) {
    super(cause);
  }
}
