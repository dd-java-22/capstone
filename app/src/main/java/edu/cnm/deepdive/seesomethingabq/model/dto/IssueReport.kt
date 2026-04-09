package edu.cnm.deepdive.seesomethingabq.model.dto

import com.google.gson.annotations.SerializedName

data class IssueReport(
    @SerializedName("externalId") val externalId: String,
    @SerializedName("textDescription") val description: String?,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("streetCoordinate") val streetCoordinate: String?,
    @SerializedName("locationDescription") val locationDescription: String?,
    @SerializedName("issueTypes") val issueTypes: List<String>,
    @SerializedName("reportImages") val reportImages: List<ReportImageDto>?
)
