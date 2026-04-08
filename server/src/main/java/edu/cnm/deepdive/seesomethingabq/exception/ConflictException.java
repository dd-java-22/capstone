package edu.cnm.deepdive.seesomethingabq.exception;

/**
 * Exception thrown when an operation conflicts with existing data (e.g., duplicates, deleting in-use resources).
 */
public class ConflictException extends RuntimeException {

  /**
   * Creates an instance with no detail message.
   */
  public ConflictException() {
    super();
  }

  /**
   * Creates an instance with a detail message.
   *
   * @param message detail message.
   */
  public ConflictException(String message) {
    super(message);
  }

  /**
   * Creates an instance with a detail message and cause.
   *
   * @param message detail message.
   * @param cause underlying cause.
   */
  public ConflictException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Creates an instance with a cause.
   *
   * @param cause underlying cause.
   */
  public ConflictException(Throwable cause) {
    super(cause);
  }
}
