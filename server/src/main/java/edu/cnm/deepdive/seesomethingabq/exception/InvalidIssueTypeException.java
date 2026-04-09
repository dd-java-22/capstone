package edu.cnm.deepdive.seesomethingabq.exception;

/**
 * Exception thrown when an issue type request is invalid.
 */
public class InvalidIssueTypeException extends BadRequestException {

  /**
   * Creates an instance with a detail message.
   *
   * @param message detail message.
   */
  public InvalidIssueTypeException(String message) {
    super(message);
  }
}
