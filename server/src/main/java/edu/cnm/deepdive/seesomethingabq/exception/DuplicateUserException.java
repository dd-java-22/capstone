package edu.cnm.deepdive.seesomethingabq.exception;

/**
 * Exception thrown when attempting to create a user that already exists.
 */
public class DuplicateUserException extends ConflictException {

  public DuplicateUserException(String message) {
    super(message);
  }
}
