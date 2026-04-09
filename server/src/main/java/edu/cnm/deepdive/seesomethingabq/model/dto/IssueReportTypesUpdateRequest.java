package edu.cnm.deepdive.seesomethingabq.model.dto;

import java.util.List;

/**
 * Request body DTO for replacing the issue types associated with an issue report.
 */
public class IssueReportTypesUpdateRequest {

  private List<String> issueTypeTags;

  /**
   * Returns the replacement set of issue type tags.
   *
   * @return issue type tags.
   */
  public List<String> getIssueTypeTags() {
    return issueTypeTags;
  }

  /**
   * Sets the replacement set of issue type tags.
   *
   * @param issueTypeTags issue type tags.
   */
  public void setIssueTypeTags(List<String> issueTypeTags) {
    this.issueTypeTags = issueTypeTags;
  }

}

