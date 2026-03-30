package edu.cnm.deepdive.seesomethingabq.exception;

/**
 * Exception thrown when an operation conflicts with existing data (e.g., duplicates, deleting in-use resources).
 */
public class ConflictException extends RuntimeException {

  public ConflictException() {
    super();
  }

  public ConflictException(String message) {
    super(message);
  }

  public ConflictException(String message, Throwable cause) {
    super(message, cause);
  }

  public ConflictException(Throwable cause) {
    super(cause);
  }
}
