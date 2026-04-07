package edu.cnm.deepdive.seesomethingabq.service;

import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportRequest;
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportSummary;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueReport;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IssueReportService {

  //GET /issue-reports/mine?sort={sort}
  List<IssueReportSummary> getReportsForCurrentUser(String sortParam);

  //POST /issue-reports
  IssueReport createReport(IssueReportRequest request);

  //GET /issue-reports/{externalId}
  IssueReport getReportByExternalId(UUID externalId);

  //PUT /issue-reports/{externalId}
  IssueReport updateReport(UUID externalId, IssueReportRequest request);

  //DELETE /issue-reports/{externalId}
  void deleteReport(UUID externalId);

  // GET /managers/issue-reports/
  Page<IssueReport> getAll(Pageable pageable);

  // PUT /manager/issue-reports/{externalId}/status/
  IssueReport replaceIssueTypes(UUID externalId, Iterable<String> issueTypeTags);

  // PUT /manager/issue-reports/{externalId}/issue-types/
  IssueReport setAcceptedState(UUID externalId, String statusTag);
}

