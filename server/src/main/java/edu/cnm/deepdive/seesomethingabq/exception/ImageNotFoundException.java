package edu.cnm.deepdive.seesomethingabq.exception;

/**
 * Exception thrown when a requested report image cannot be located.
 */
public class ImageNotFoundException extends ResourceNotFoundException {

  /**
   * Creates an instance with a detail message.
   *
   * @param message detail message.
   */
  public ImageNotFoundException(String message) {
    super(message);
  }
}
