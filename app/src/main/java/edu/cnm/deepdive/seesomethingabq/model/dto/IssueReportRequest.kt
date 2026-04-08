package edu.cnm.deepdive.seesomethingabq.model.dto

import com.google.gson.annotations.Expose

/**
 * Request DTO for submitting an issue report to the server API.
 *
 * @property textDescription user-supplied description of the issue.
 * @property latitude location latitude.
 * @property longitude location longitude.
 * @property streetCoordinate street address/coordinate text, if available.
 * @property locationDescription free-form location description, if available.
 * @property issueTypes issue type tags to associate with the report.
 */
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
