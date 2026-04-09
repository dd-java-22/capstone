package edu.cnm.deepdive.seesomethingabq.exception;

/**
 * Exception thrown when user data is invalid.
 */
public class InvalidUserException extends BadRequestException {

  /**
   * Creates an instance with a detail message.
   *
   * @param message detail message.
   */
  public InvalidUserException(String message) {
    super(message);
  }
}
