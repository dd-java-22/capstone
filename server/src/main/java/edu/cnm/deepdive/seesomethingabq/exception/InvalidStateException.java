package edu.cnm.deepdive.seesomethingabq.exception;

public class InvalidStateException extends BadRequestException {

  public InvalidStateException(String message) {
    super(message);
  }
}
