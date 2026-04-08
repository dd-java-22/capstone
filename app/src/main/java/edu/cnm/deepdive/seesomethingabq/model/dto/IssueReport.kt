package edu.cnm.deepdive.seesomethingabq.model.dto

/**
 * Represents a full issue report as returned by the backend.
 *
 * This includes the report's external ID, location, description, associated issue types,
 * and any attached images.
 */
data class IssueReport(
    val externalId: String,
    val description: String?,
    val latitude: Double,
    val longitude: Double,
    val locationDescription: String?,
    val issueTypes: List<String>,
    val reportImages: List<ReportImageDto>?
)
