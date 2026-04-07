package edu.cnm.deepdive.seesomethingabq.model.dto

import com.google.gson.annotations.Expose

data class IssueReportRequest(
  @Expose
  val textDescription: String,
  @Expose
  val latitude: Double,
  @Expose
  val longitude: Double,
  @Expose
  val streetCoordinate: String?,
  @Expose
  val locationDescription: String?,
  @Expose
  val issueTypes: List<String>,
)
