package edu.cnm.deepdive.seesomethingabq.exception;

/**
 * Exception thrown when an accepted-state/status transition request is invalid.
 */
public class InvalidStateException extends BadRequestException {

  /**
   * Creates an instance with a detail message.
   *
   * @param message detail message.
   */
  public InvalidStateException(String message) {
    super(message);
  }
}
