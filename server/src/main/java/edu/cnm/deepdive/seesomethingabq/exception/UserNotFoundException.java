package edu.cnm.deepdive.seesomethingabq.exception;

/**
 * Exception thrown when a requested user is not found.
 */
public class UserNotFoundException extends ResourceNotFoundException {

  public UserNotFoundException(String message) {
    super(message);
  }
}
