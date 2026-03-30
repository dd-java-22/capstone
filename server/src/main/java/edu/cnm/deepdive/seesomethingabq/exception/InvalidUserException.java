package edu.cnm.deepdive.seesomethingabq.exception;

/**
 * Exception thrown when user data is invalid.
 */
public class InvalidUserException extends BadRequestException {

  public InvalidUserException(String message) {
    super(message);
  }
}
