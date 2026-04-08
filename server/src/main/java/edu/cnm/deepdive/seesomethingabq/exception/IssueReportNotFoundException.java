package edu.cnm.deepdive.seesomethingabq.exception;

/**
 * Exception thrown when an {@code IssueReport} resource cannot be located.
 */
public class IssueReportNotFoundException extends ResourceNotFoundException {

  /**
   * Creates an instance with a detail message.
   *
   * @param message detail message.
   */
  public IssueReportNotFoundException(String message) {
    super(message);
  }
}
