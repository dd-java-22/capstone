package edu.cnm.deepdive.seesomethingabq.model.dto

import com.google.gson.annotations.SerializedName

/**
 * Request DTO for updating the accepted-state/status of an issue report (manager-only).
 *
 * @property statusTag desired status tag.
 */
data class IssueReportStatusUpdateRequest(
  @SerializedName("statusTag")
  val statusTag: String
)

