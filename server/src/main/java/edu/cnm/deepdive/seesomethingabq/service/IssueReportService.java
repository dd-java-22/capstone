package edu.cnm.deepdive.seesomethingabq.service;

import edu.cnm.deepdive.seesomethingabq.model.entity.IssueReport;
import java.util.Optional;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IssueReportService {

  //GET /issue-reports/mine?sort={sort}
  List<IssueReport> getReportsForCurrentUser(String sortParam);

  Page<IssueReport> getAll(Pageable pageable);

  //POST /issue-reports
  IssueReport createReport(IssueReport report);

  //GET /issue-reports/{externalKey}
  IssueReport getReportByExternalKey(UUID externalKey);
  Optional<IssueReport> getByExternalId(UUID externalId); // FIXME: 3/27/2026 choose one way to get reports by UUID

  IssueReport setAcceptedState(UUID externalId, String statusTag);

  //PUT /issue-reports/{externalKey}
  IssueReport updateReport(UUID externalKey, IssueReport report);
  IssueReport replaceIssueTypes(UUID externalId, Iterable<String> issueTypeTags);

  //DELETE /issue-reports/{externalKey}
  void deleteReport(UUID externalKey);
}

