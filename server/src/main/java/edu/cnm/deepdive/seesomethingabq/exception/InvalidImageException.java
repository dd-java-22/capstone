package edu.cnm.deepdive.seesomethingabq.exception;

/**
 * Exception thrown when an uploaded image is invalid or cannot be processed.
 */
public class InvalidImageException extends BadRequestException {

  /**
   * Creates an instance with a detail message.
   *
   * @param message detail message.
   */
  public InvalidImageException(String message) {
    super(message);
  }
}
