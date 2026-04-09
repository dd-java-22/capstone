package edu.cnm.deepdive.seesomethingabq.controller;

import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType;
import edu.cnm.deepdive.seesomethingabq.service.IssueTypeService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing read-only access to issue types.
 */
@RestController
@RequestMapping("/issue-types")
public class IssueTypeController {

  private final IssueTypeService service;

  /**
   * Creates a controller exposing read-only issue type operations.
   *
   * @param service issue type service.
   */
  @Autowired
  public IssueTypeController(IssueTypeService service) {
    this.service = service;
  }

  /**
   * Returns all issue types.
   *
   * @return list of issue types.
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public List<IssueType> getAllIssueTypes() {
    return service.getAll();
  }
}
