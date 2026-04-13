package edu.cnm.deepdive.seesomethingabq.model.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO representing a full issue report returned by the server API.
 *
 * @property externalId report external identifier (string form of server UUID).
 * @property description user-supplied description text, if present.
 * @property acceptedState current accepted-state/status tag, if present.
 * @property latitude latitude of the report location.
 * @property longitude longitude of the report location.
 * @property streetCoordinate street address/coordinate text, if present.
 * @property locationDescription free-form location description, if present.
 * @property issueTypes issue type tags associated with the report.
 * @property reportImages images associated with the report, if included in the response.
 */
data class IssueReport(
    @SerializedName("externalId") val externalId: String,
    @SerializedName("textDescription") val description: String?,
    @SerializedName("acceptedState") val acceptedState: String?,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("streetCoordinate") val streetCoordinate: String?,
    @SerializedName("locationDescription") val locationDescription: String?,
    @SerializedName("issueTypes") val issueTypes: List<String>,
    @SerializedName("reportImages") val reportImages: List<ReportImageDto>?
)
