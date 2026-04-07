package edu.cnm.deepdive.seesomethingabq.model.dto

import com.google.gson.annotations.Expose
import java.time.Instant
import java.util.UUID

data class IssueReportSummary (
    @Expose
    val externalId: UUID,
    @Expose
    val description: String = "",
    @Expose
    val acceptedState: String = "",
    @Expose
    val timeFirstReported: Instant = Instant.EPOCH,
    @Expose
    val timeLastModified: Instant = Instant.EPOCH,
)