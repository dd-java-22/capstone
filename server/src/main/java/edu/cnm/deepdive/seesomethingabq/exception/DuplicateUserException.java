package edu.cnm.deepdive.seesomethingabq.exception;

/**
 * Exception thrown when attempting to create a user that already exists.
 */
public class DuplicateUserException extends ConflictException {

  /**
   * Creates an instance with a detail message.
   *
   * @param message detail message.
   */
  public DuplicateUserException(String message) {
    super(message);
  }
}
