package edu.cnm.deepdive.seesomethingabq.model.dto;

import edu.cnm.deepdive.seesomethingabq.model.entity.IssueReport;
import java.time.Instant;
import java.util.UUID;

public class IssueReportSummary {

  private UUID externalId;
  private String description;
  private String acceptedState;
  private Instant timeFirstReported;
  private Instant timeLastModified;


  public static IssueReportSummary fromIssueReport(IssueReport issueReport) {
    IssueReportSummary summary = new IssueReportSummary();
    summary.setExternalId(issueReport.getExternalId());
    summary.setDescription(issueReport.getTextDescription());
    summary.setAcceptedState(issueReport.getAcceptedState().getStatusTag());
    summary.setTimeFirstReported(issueReport.getTimeFirstReported());
    summary.setTimeLastModified(issueReport.getTimeLastModified());
    return summary;
  }

  public UUID getExternalId() {
    return externalId;
  }

  public void setExternalId(UUID externalId) {
    this.externalId = externalId;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getAcceptedState() {
    return acceptedState;
  }

  public void setAcceptedState(String acceptedState) {
    this.acceptedState = acceptedState;
  }

  public Instant getTimeFirstReported() {
    return timeFirstReported;
  }

  public void setTimeFirstReported(Instant timeFirstReported) {
    this.timeFirstReported = timeFirstReported;
  }

  public Instant getTimeLastModified() {
    return timeLastModified;
  }

  public void setTimeLastModified(Instant timeLastModified) {
    this.timeLastModified = timeLastModified;
  }
}