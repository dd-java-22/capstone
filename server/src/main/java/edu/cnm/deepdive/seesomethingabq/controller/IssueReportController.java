package edu.cnm.deepdive.seesomethingabq.controller;

import edu.cnm.deepdive.seesomethingabq.model.entity.IssueReport;
import edu.cnm.deepdive.seesomethingabq.service.IssueReportService;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
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

@RestController
@RequestMapping("/issue-reports")
public class IssueReportController {

  private final IssueReportService issueReportService;

  public IssueReportController(IssueReportService issueReportService) {
    this.issueReportService = issueReportService;
  }

  @GetMapping("/mine")
  public List<IssueReport> getMyReports(@RequestParam(defaultValue = "last_modified") String sort) {
    return issueReportService.getReportsForCurrentUser(sort);
  }

  @PostMapping
  public ResponseEntity<IssueReport> createReport(@RequestBody IssueReport report) {
    IssueReport created = issueReportService.createReport(report);
    URI location = URI.create("/issue-reports/" + created.getExternalId());
    return ResponseEntity.created(location).body(created);
  }

  @GetMapping("/{externalKey}")
  public IssueReport getReport(@PathVariable UUID externalKey) {
    return issueReportService.getReportByExternalKey(externalKey);
  }

  @PutMapping("/{externalKey}")
  public IssueReport updateReport(@PathVariable UUID externalKey, @RequestBody IssueReport report) {
    return issueReportService.updateReport(externalKey, report);
  }

  @DeleteMapping("/{externalKey}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteReport(@PathVariable UUID externalKey) {
    issueReportService.deleteReport(externalKey);
  }


}

//PUT /issue-reports/{externalKey}
//DELETE /issue-reports/{externalKey}