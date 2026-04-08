package edu.cnm.deepdive.seesomethingabq.exception;

/**
 * Exception thrown when attempting to create an {@code IssueType} with a duplicate tag.
 */
public class DuplicateIssueTypeException extends ConflictException {

  /**
   * Creates an instance with a detail message.
   *
   * @param message detail message.
   */
  public DuplicateIssueTypeException(String message) {
    super(message);
  }
}
