package edu.cnm.deepdive.seesomethingabq.exception;

/**
 * Exception thrown when an {@code IssueType} resource cannot be located.
 */
public class IssueTypeNotFoundException extends ResourceNotFoundException {

  /**
   * Creates an instance with a detail message.
   *
   * @param message detail message.
   */
  public IssueTypeNotFoundException(String message) {
    super(message);
  }
}
