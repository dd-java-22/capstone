package edu.cnm.deepdive.seesomethingabq.service;

import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportRequest;
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportSummary;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueReport;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service providing operations for creating, retrieving, updating, and administering
 * {@link IssueReport} entities.
 */
public interface IssueReportService {

  /**
   * Returns summaries of reports owned by the currently authenticated user.
   *
   * @param sortParam sort key/direction string as provided by the controller.
   * @return list of report summaries.
   */
  List<IssueReportSummary> getReportsForCurrentUser(String sortParam);

  /**
   * Creates a new report for the currently authenticated user.
   *
   * @param request request payload describing the report.
   * @return created report.
   */
  IssueReport createReport(IssueReportRequest request);

  /**
   * Returns a report by external identifier.
   *
   * @param externalId report external ID.
   * @return report entity.
   */
  IssueReport getReportByExternalId(UUID externalId);

  /**
   * Updates an existing report by external identifier.
   *
   * @param externalId report external ID.
   * @param request request payload describing updates to apply.
   * @return updated report.
   */
  IssueReport updateReport(UUID externalId, IssueReportRequest request);

  /**
   * Deletes a report by external identifier.
   *
   * @param externalId report external ID.
   */
  void deleteReport(UUID externalId);

  /**
   * Returns a page of reports for administrative views.
   *
   * @param pageable paging and sorting information.
   * @return page of reports.
   */
  Page<IssueReport> getAll(Pageable pageable);

  /**
   * Replaces the issue types associated with a report.
   *
   * @param externalId report external ID.
   * @param issueTypeTags replacement issue type tags.
   * @return updated report.
   */
  IssueReport replaceIssueTypes(UUID externalId, Iterable<String> issueTypeTags);

  /**
   * Sets the accepted-state/status of a report.
   *
   * @param externalId report external ID.
   * @param statusTag accepted-state status tag.
   * @return updated report.
   */
  IssueReport setAcceptedState(UUID externalId, String statusTag);
}

