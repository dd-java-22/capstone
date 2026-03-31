package edu.cnm.deepdive.seesomethingabq.controller;

import edu.cnm.deepdive.seesomethingabq.exception.AccessDeniedException;
import edu.cnm.deepdive.seesomethingabq.exception.BadRequestException;
import edu.cnm.deepdive.seesomethingabq.exception.ConflictException;
import edu.cnm.deepdive.seesomethingabq.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.web.servlet.HandlerMapping;

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
   * Handles path/query parameter conversion failures (e.g., invalid UUID, invalid int).
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
    String name = ex.getName();
    Object value = ex.getValue();
    String message = (value != null)
        ? String.format("Invalid value '%s' for parameter '%s'.", value, name)
        : String.format("Invalid value for parameter '%s'.", name);
    return new ErrorResponse(message, Instant.now());
  }

  /**
   * Handles low-level conversion failures that may not surface as a {@link MethodArgumentTypeMismatchException}.
   */
  @ExceptionHandler(ConversionFailedException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleConversionFailed(ConversionFailedException ex, HttpServletRequest request) {
    Object value = ex.getValue();
    Optional<String> name = findParameterName(request, value);
    String message;
    if (name.isPresent()) {
      message = (value != null)
          ? String.format("Invalid value '%s' for parameter '%s'.", value, name.get())
          : String.format("Invalid value for parameter '%s'.", name.get());
    } else {
      message = (value != null)
          ? String.format("Invalid value '%s'.", value)
          : "Invalid value.";
    }
    return new ErrorResponse(message, Instant.now());
  }

  /**
   * Attempts to find the parameter name associated with a conversion failure by uniquely matching the
   * failed value against query parameters and path variables. If matching is ambiguous or impossible,
   * returns empty rather than guessing.
   */
  private Optional<String> findParameterName(HttpServletRequest request, Object value) {
    if (request == null || value == null) {
      return Optional.empty();
    }
    String raw = Objects.toString(value, null);
    if (raw == null) {
      return Optional.empty();
    }
    List<String> matches = new ArrayList<>();

    request.getParameterMap().forEach((name, values) -> {
      if (values != null) {
        for (String v : values) {
          if (raw.equals(v)) {
            matches.add(name);
            return;
          }
        }
      }
    });

    Object attr = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
    if (attr instanceof Map<?, ?> vars) {
      vars.forEach((k, v) -> {
        if (k != null && v != null && raw.equals(v.toString())) {
          matches.add(k.toString());
        }
      });
    }

    return (matches.size() == 1) ? Optional.of(matches.getFirst()) : Optional.empty();
  }

  /**
   * Handles malformed/invalid request bodies (e.g., JSON parse errors) as a client error, not a 500.
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
    return new ErrorResponse("Malformed request body.", Instant.now());
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
