package edu.cnm.deepdive.seesomethingabq.exception;

/**
 * Exception thrown when an issue report request is invalid.
 */
public class InvalidIssueReportException extends BadRequestException {

  /**
   * Creates an instance with a detail message.
   *
   * @param message detail message.
   */
  public InvalidIssueReportException(String message) {
    super(message);
  }
}
