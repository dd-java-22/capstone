package edu.cnm.deepdive.seesomethingabq.controller;

import edu.cnm.deepdive.seesomethingabq.exception.AccessDeniedException;
import edu.cnm.deepdive.seesomethingabq.exception.BadRequestException;
import edu.cnm.deepdive.seesomethingabq.exception.ConflictException;
import edu.cnm.deepdive.seesomethingabq.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
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

  // Simple DTO for consistent error responses
  public record ErrorResponse(String message, Instant timestamp) {}

  /**
   * Handles resource not found exceptions.
   */
  @ExceptionHandler({
      ResourceNotFoundException.class,
      NoSuchElementException.class,
      EntityNotFoundException.class
  })
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleNotFound(Exception ex) {
    return new ErrorResponse(ex.getMessage(), Instant.now());
  }

  /**
   * Handles access denied exceptions.
   */
  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ErrorResponse handleAccessDenied(AccessDeniedException ex) {
    return new ErrorResponse(ex.getMessage(), Instant.now());
  }

  /**
   * Handles bad request exceptions.
   */
  @ExceptionHandler({
      IllegalArgumentException.class,
      BadRequestException.class
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleBadRequest(Exception ex) {
    return new ErrorResponse(ex.getMessage(), Instant.now());
  }

  /**
   * Handles Bean Validation failures (e.g., invalid ReportLocation).
   */
  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleConstraintViolation(ConstraintViolationException ex) {
    String message = ex.getConstraintViolations().stream()
        .map(v -> v.getMessage())
        .distinct()
        .reduce((a, b) -> a + "; " + b)
        .orElse(ex.getMessage());
    return new ErrorResponse(message, Instant.now());
  }

  /**
   * Handles conflict exceptions (duplicate types, states, deleting in-use resources, etc.).
   */
  @ExceptionHandler({
      ConflictException.class,
      IllegalStateException.class
  })
  @ResponseStatus(HttpStatus.CONFLICT)
  public ErrorResponse handleConflict(Exception ex) {
    return new ErrorResponse(ex.getMessage(), Instant.now());
  }

  /**
   * Handles all other unhandled exceptions.
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse handleInternalError(Exception ex) {
    return new ErrorResponse("An unexpected error occurred: " + ex.getMessage(), Instant.now());
  }
}
