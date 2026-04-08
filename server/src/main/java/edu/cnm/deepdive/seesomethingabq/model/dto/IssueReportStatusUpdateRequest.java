package edu.cnm.deepdive.seesomethingabq.model.dto;

/**
 * Request body DTO for updating the accepted-state of an issue report.
 */
public class IssueReportStatusUpdateRequest {

  private String statusTag;

  /**
   * Returns the new accepted-state status tag.
   *
   * @return status tag.
   */
  public String getStatusTag() {
    return statusTag;
  }

  /**
   * Sets the new accepted-state status tag.
   *
   * @param statusTag status tag.
   */
  public void setStatusTag(String statusTag) {
    this.statusTag = statusTag;
  }

}

