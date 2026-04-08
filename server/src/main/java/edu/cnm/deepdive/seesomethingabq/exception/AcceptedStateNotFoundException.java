package edu.cnm.deepdive.seesomethingabq.exception;

/**
 * Exception thrown when an {@code AcceptedState} resource cannot be located.
 */
public class AcceptedStateNotFoundException extends ResourceNotFoundException {

  /**
   * Creates an instance with a detail message.
   *
   * @param message detail message.
   */
  public AcceptedStateNotFoundException(String message) {
    super(message);
  }
}
