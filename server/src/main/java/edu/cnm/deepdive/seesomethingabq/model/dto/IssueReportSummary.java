package edu.cnm.deepdive.seesomethingabq.model.dto;

import edu.cnm.deepdive.seesomethingabq.model.entity.IssueReport;
import java.time.Instant;
import java.util.UUID;

/**
 * Summary DTO exposing a subset of {@link IssueReport} fields for list views.
 */
public class IssueReportSummary {

  private UUID externalId;
  private String description;
  private String acceptedState;
  private Instant timeFirstReported;
  private Instant timeLastModified;

  /**
   * Creates a summary DTO from an {@link IssueReport} entity.
   *
   * @param issueReport source entity.
   * @return populated summary DTO.
   */
  public static IssueReportSummary fromIssueReport(IssueReport issueReport) {
    IssueReportSummary summary = new IssueReportSummary();
    summary.setExternalId(issueReport.getExternalId());
    summary.setDescription(issueReport.getTextDescription());
    summary.setAcceptedState(issueReport.getAcceptedState().getStatusTag());
    summary.setTimeFirstReported(issueReport.getTimeFirstReported());
    summary.setTimeLastModified(issueReport.getTimeLastModified());
    return summary;
  }

  /**
   * Returns the external identifier of the issue report.
   *
   * @return external ID.
   */
  public UUID getExternalId() {
    return externalId;
  }

  /**
   * Sets the external identifier of the issue report.
   *
   * @param externalId external ID.
   */
  public void setExternalId(UUID externalId) {
    this.externalId = externalId;
  }

  /**
   * Returns the issue report description.
   *
   * @return description.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the issue report description.
   *
   * @param description description.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Returns the accepted-state status tag.
   *
   * @return status tag.
   */
  public String getAcceptedState() {
    return acceptedState;
  }

  /**
   * Sets the accepted-state status tag.
   *
   * @param acceptedState status tag.
   */
  public void setAcceptedState(String acceptedState) {
    this.acceptedState = acceptedState;
  }

  /**
   * Returns the time the report was first created.
   *
   * @return first-reported timestamp.
   */
  public Instant getTimeFirstReported() {
    return timeFirstReported;
  }

  /**
   * Sets the time the report was first created.
   *
   * @param timeFirstReported first-reported timestamp.
   */
  public void setTimeFirstReported(Instant timeFirstReported) {
    this.timeFirstReported = timeFirstReported;
  }

  /**
   * Returns the time the report was last modified.
   *
   * @return last-modified timestamp.
   */
  public Instant getTimeLastModified() {
    return timeLastModified;
  }

  /**
   * Sets the time the report was last modified.
   *
   * @param timeLastModified last-modified timestamp.
   */
  public void setTimeLastModified(Instant timeLastModified) {
    this.timeLastModified = timeLastModified;
  }
}
