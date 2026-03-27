package edu.cnm.deepdive.seesomethingabq.controller;

import edu.cnm.deepdive.seesomethingabq.model.dto.AcceptedStateDescriptionUpdateRequest;
import edu.cnm.deepdive.seesomethingabq.model.entity.AcceptedState;
import edu.cnm.deepdive.seesomethingabq.service.AcceptedStateService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/manager/accepted-states")
public class ManagerAcceptedStateController {

  private final AcceptedStateService service;

  @Autowired
  public ManagerAcceptedStateController(AcceptedStateService service) {
    this.service = service;
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public List<AcceptedState> getAll() {
    return service.getAll();
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public AcceptedState createAcceptedState(@RequestBody AcceptedState newAcceptedState) {
    try {
      return service.createNewAcceptedState(newAcceptedState);
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Status tag already exists", e);
    }
  }

  @PatchMapping(path = "/{statusTag}", consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public AcceptedState updateAcceptedStateDescription(
      @PathVariable String statusTag,
      @RequestBody AcceptedStateDescriptionUpdateRequest request
  ) {
    try {
      return service.updateAcceptedStateDescription(statusTag, request.getStatusTagDescription());
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Status tag not found", e);
    }
  }

  @DeleteMapping(path = "/{statusTag}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteUnusedAcceptedState(@PathVariable String statusTag) {
    try {
      service.deleteUnusedAcceptedState(statusTag);
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Status tag not found", e);
    } catch (IllegalStateException e) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Status tag in use", e);
    }
  }

}
