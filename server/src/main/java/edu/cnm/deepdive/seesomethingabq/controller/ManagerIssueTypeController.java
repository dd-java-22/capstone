package edu.cnm.deepdive.seesomethingabq.controller;

import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType;
import edu.cnm.deepdive.seesomethingabq.service.IssueTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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
  public IssueType createIssueType(@RequestBody IssueType newIssueType) {
    return service.createNewIssueType(newIssueType);
  }

  @PatchMapping(path = "/{issueTypeTag}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public IssueType updateIssueTypeDescription(
      @PathVariable String issueTypeTag,
      @RequestBody String newIssueTypeDescription
  ) {
    return service.updateIssueTypeDescription(issueTypeTag, newIssueTypeDescription);
  }

  @DeleteMapping(path = "/{issueTypeTag}" )
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteUnusedIssueType(@PathVariable String issueTypeTag) {
    service.deleteUnusedIssueType(issueTypeTag);
  }

}
