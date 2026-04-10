package edu.cnm.deepdive.seesomethingabq.model.dto

import com.google.gson.annotations.SerializedName

data class IssueReportTypesUpdateRequest(
  @SerializedName("issueTypeTags")
  val issueTypeTags: List<String>
)

