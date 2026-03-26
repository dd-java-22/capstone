package edu.cnm.deepdive.seesomethingabq.controller;

import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType;
import edu.cnm.deepdive.seesomethingabq.service.IssueTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manager/issue-types")
public class ManagerIssueTypeController {

  private final IssueTypeService service;

  @Autowired
  public ManagerIssueTypeController(IssueTypeService service) {
    this.service = service;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public boolean createIssueType(@RequestBody IssueType newIssueType) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public boolean updateIssueTypeDescription(@RequestBody IssueType issueTypeWithNewDescription) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @DeleteMapping(path = "/{issueTypeTag}")
  public boolean deleteIssueType(@PathVariable String issueTypeTag) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

}
