package edu.cnm.deepdive.seesomethingabq.exception;

/**
 * Exception thrown when a requested user is not found.
 */
public class UserNotFoundException extends ResourceNotFoundException {

  /**
   * Creates an instance with a detail message.
   *
   * @param message detail message.
   */
  public UserNotFoundException(String message) {
    super(message);
  }
}
