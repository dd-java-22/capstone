package edu.cnm.deepdive.seesomethingabq.exception;

public class InvalidIssueTypeException extends BadRequestException {

  public InvalidIssueTypeException(String message) {
    super(message);
  }
}
