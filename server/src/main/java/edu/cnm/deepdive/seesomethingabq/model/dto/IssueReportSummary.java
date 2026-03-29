package edu.cnm.deepdive.seesomethingabq.model.dto;

import java.time.Instant;
import java.util.UUID;

public class IssueReportSummary {

  private UUID externalId;
  private String description;
  private String acceptedState;
  private Instant timeFirstReported;
  private Instant timeLastModified;

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