package edu.cnm.deepdive.seesomethingabq.exception;

/**
 * Exception thrown when a request contains invalid data or parameters.
 */
public class BadRequestException extends RuntimeException {

  /**
   * Creates an instance with no detail message.
   */
  public BadRequestException() {
    super();
  }

  /**
   * Creates an instance with a detail message.
   *
   * @param message detail message.
   */
  public BadRequestException(String message) {
    super(message);
  }

  /**
   * Creates an instance with a detail message and cause.
   *
   * @param message detail message.
   * @param cause underlying cause.
   */
  public BadRequestException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Creates an instance with a cause.
   *
   * @param cause underlying cause.
   */
  public BadRequestException(Throwable cause) {
    super(cause);
  }
}
