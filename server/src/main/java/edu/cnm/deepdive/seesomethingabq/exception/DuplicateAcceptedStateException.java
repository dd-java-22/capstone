package edu.cnm.deepdive.seesomethingabq.exception;

/**
 * Exception thrown when attempting to create an {@code AcceptedState} with a duplicate status tag.
 */
public class DuplicateAcceptedStateException extends ConflictException {

  /**
   * Creates an instance with a detail message.
   *
   * @param message detail message.
   */
  public DuplicateAcceptedStateException(String message) {
    super(message);
  }
}
