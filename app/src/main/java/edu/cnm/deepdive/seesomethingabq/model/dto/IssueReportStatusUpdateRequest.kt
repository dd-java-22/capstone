package edu.cnm.deepdive.seesomethingabq.model.dto

import com.google.gson.annotations.SerializedName

data class IssueReportStatusUpdateRequest(
  @SerializedName("statusTag")
  val statusTag: String
)

