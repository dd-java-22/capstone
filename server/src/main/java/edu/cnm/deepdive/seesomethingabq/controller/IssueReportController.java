package edu.cnm.deepdive.seesomethingabq.controller;

import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportRequest;
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportSummary;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueReport;
import edu.cnm.deepdive.seesomethingabq.service.IssueReportService;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/issue-reports")
public class IssueReportController {

  private final IssueReportService issueReportService;

  public IssueReportController(IssueReportService issueReportService) {
    this.issueReportService = issueReportService;
  }

  @GetMapping(
      value = "/mine",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public List<IssueReportSummary> getMyReports(
      @RequestParam(defaultValue = "last_modified") String sort) {
    return issueReportService.getReportsForCurrentUser(sort);
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public ResponseEntity<IssueReport> createReport(@RequestBody IssueReportRequest request) {
    IssueReport created = issueReportService.createReport(request);
    URI location = linkTo(methodOn(IssueReportController.class)
        .getReport(created.getExternalId()))
        .toUri();
    return ResponseEntity.created(location).body(created);
  }

  @GetMapping("/{externalId}")
  public IssueReport getReport(@PathVariable UUID externalId) {
    return issueReportService.getReportByExternalId(externalId);
  }

  @PutMapping("/{externalId}")
  public IssueReport updateReport(@PathVariable UUID externalId, @RequestBody IssueReportRequest request) {
    return issueReportService.updateReport(externalId, request);
  }

  @DeleteMapping("/{externalId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteReport(@PathVariable UUID externalId) {
    issueReportService.deleteReport(externalId);
  }


}
