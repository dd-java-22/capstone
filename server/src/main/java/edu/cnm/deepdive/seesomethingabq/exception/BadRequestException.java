package edu.cnm.deepdive.seesomethingabq.exception;

/**
 * Exception thrown when a request contains invalid data or parameters.
 */
public class BadRequestException extends RuntimeException {

  public BadRequestException() {
    super();
  }

  public BadRequestException(String message) {
    super(message);
  }

  public BadRequestException(String message, Throwable cause) {
    super(message, cause);
  }

  public BadRequestException(Throwable cause) {
    super(cause);
  }
}
