package edu.cnm.deepdive.seesomethingabq.model.dto

/**
 * Represents metadata for an image attached to an issue report.
 *
 * This corresponds to the backend ReportImage entity and is used for
 * displaying image information and constructing download URLs.
 */
data class ReportImageDto(
    val externalId: String,
    val filename: String,
    val mimeType: String,
    val albumOrder: Int
)
