package edu.cnm.deepdive.seesomethingabq.service;

import edu.cnm.deepdive.seesomethingabq.model.entity.IssueReport;
import java.util.List;
import java.util.UUID;

public interface IssueReportService {

  //GET /issue-reports/mine?sort={sort}
  List<IssueReport> getReportsForCurrentUser(String sortParam);

  //POST /issue-reports
  IssueReport createReport(IssueReport report);

  //GET /issue-reports/{externalKey}
  IssueReport getReportByExternalKey(UUID externalKey);

  //PUT /issue-reports/{externalKey}
  IssueReport updateReport(UUID externalKey,IssueReport report);

  //DELETE /issue-reports/{externalKey}
  void deleteReport(UUID externalKey);
}




