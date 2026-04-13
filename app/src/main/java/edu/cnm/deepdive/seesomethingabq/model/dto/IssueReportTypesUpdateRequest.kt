package edu.cnm.deepdive.seesomethingabq.model.dto

import com.google.gson.annotations.SerializedName

/**
 * Request DTO for replacing the issue-type tags of an issue report (manager-only).
 *
 * @property issueTypeTags replacement list of issue-type tags.
 */
data class IssueReportTypesUpdateRequest(
  @SerializedName("issueTypeTags")
  val issueTypeTags: List<String>
)

