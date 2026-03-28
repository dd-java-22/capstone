package edu.cnm.deepdive.seesomethingabq.controller;

import edu.cnm.deepdive.seesomethingabq.exception.AccessDeniedException;
import edu.cnm.deepdive.seesomethingabq.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for REST controllers. Maps exceptions to appropriate HTTP status codes.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handles resource not found exceptions.
   *
   * @param ex The exception that was thrown.
   * @return Error message.
   */
  @ExceptionHandler({ResourceNotFoundException.class, NoSuchElementException.class,
      EntityNotFoundException.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public String handleNotFound(Exception ex) {
    return ex.getMessage();
  }

  /**
   * Handles access denied exceptions.
   *
   * @param ex The exception that was thrown.
   * @return Error message.
   */
  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public String handleAccessDenied(AccessDeniedException ex) {
    return ex.getMessage();
  }

  /**
   * Handles illegal argument exceptions.
   *
   * @param ex The exception that was thrown.
   * @return Error message.
   */
  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public String handleBadRequest(IllegalArgumentException ex) {
    return ex.getMessage();
  }

  /**
   * Handles all other unhandled exceptions.
   *
   * @param ex The exception that was thrown.
   * @return Generic error message.
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public String handleInternalError(Exception ex) {
    return "An unexpected error occurred: " + ex.getMessage();
  }
}
