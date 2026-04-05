package edu.cnm.deepdive.seesomethingabq.model.dto

import java.time.Instant
import java.util.UUID

data class IssueReportSummary (
    val externalId: UUID,
    val description: String = "",
    val acceptedState: String = "",
    val timeFirstReported: Instant = Instant.EPOCH,
    val timeLastModified: Instant = Instant.EPOCH,
)