package edu.cnm.deepdive.seesomethingabq.model.dto;

/**
 * Request body DTO for updating the accepted-state of an issue report.
 */
public class IssueReportStatusUpdateRequest {

  private String statusTag;

  public String getStatusTag() {
    return statusTag;
  }

  public void setStatusTag(String statusTag) {
    this.statusTag = statusTag;
  }

}

