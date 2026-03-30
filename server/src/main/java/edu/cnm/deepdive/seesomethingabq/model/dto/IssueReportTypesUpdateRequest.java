package edu.cnm.deepdive.seesomethingabq.model.dto;

import java.util.List;

/**
 * Request body DTO for replacing the issue types associated with an issue report.
 */
public class IssueReportTypesUpdateRequest {

  private List<String> issueTypeTags;

  public List<String> getIssueTypeTags() {
    return issueTypeTags;
  }

  public void setIssueTypeTags(List<String> issueTypeTags) {
    this.issueTypeTags = issueTypeTags;
  }

}

