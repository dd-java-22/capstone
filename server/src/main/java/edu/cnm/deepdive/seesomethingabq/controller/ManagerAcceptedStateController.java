package edu.cnm.deepdive.seesomethingabq.controller;

import edu.cnm.deepdive.seesomethingabq.model.dto.AcceptedStateDescriptionUpdateRequest;
import edu.cnm.deepdive.seesomethingabq.model.entity.AcceptedState;
import edu.cnm.deepdive.seesomethingabq.service.AcceptedStateService;
import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * REST controller exposing manager-only accepted-state administration endpoints.
 */
@RestController
@RequestMapping("/manager/accepted-states")
public class ManagerAcceptedStateController {

  private final AcceptedStateService service;

  /**
   * Creates a controller exposing manager-only accepted-state administration operations.
   *
   * @param service accepted state service.
   */
  @Autowired
  public ManagerAcceptedStateController(AcceptedStateService service) {
    this.service = service;
  }

  /**
   * Returns all accepted states.
   *
   * @return list of accepted states.
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public List<AcceptedState> getAll() {
    return service.getAll();
  }

  /**
   * Returns a single accepted state by status tag.
   *
   * @param statusTag accepted-state status tag.
   * @return accepted state entity.
   */
  @GetMapping(path = "/{statusTag}", produces = MediaType.APPLICATION_JSON_VALUE)
  public AcceptedState getAcceptedState(@PathVariable String statusTag) {
    return service.getByStatusTag(statusTag);
  }

  /**
   * Creates a new accepted state.
   *
   * @param newAcceptedState accepted state to create.
   * @return response containing the created entity and a {@code Location} header.
   */
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AcceptedState> createAcceptedState(@RequestBody AcceptedState newAcceptedState) {
    AcceptedState created = service.createNewAcceptedState(newAcceptedState);
    URI location = linkTo(methodOn(ManagerAcceptedStateController.class)
        .getAcceptedState(created.getStatusTag()))
        .toUri();
    return ResponseEntity.created(location).body(created);
  }

  /**
   * Updates the description of an accepted state.
   *
   * @param statusTag accepted-state status tag.
   * @param request request payload containing the new description.
   * @return updated accepted state.
   */
  @PatchMapping(path = "/{statusTag}", consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public AcceptedState updateAcceptedStateDescription(
      @PathVariable String statusTag,
      @RequestBody AcceptedStateDescriptionUpdateRequest request
  ) {
    return service.updateAcceptedStateDescription(statusTag, request.getStatusTagDescription());
  }

  /**
   * Deletes an accepted state if it is not currently referenced by any reports.
   *
   * @param statusTag accepted-state status tag.
   */
  @DeleteMapping(path = "/{statusTag}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteUnusedAcceptedState(@PathVariable String statusTag) {
    service.deleteUnusedAcceptedState(statusTag);
  }

}
