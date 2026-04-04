package edu.cnm.deepdive.seesomethingabq.model.dto

import java.time.Instant
import java.util.UUID

data class IssueReportSummary (
    private val externalId: UUID,
    private val description: String = "",
    private val acceptedState: String = "",
    private val timeFirstReported: Instant = Instant.EPOCH,
    private val timeLastModified: Instant = Instant.EPOCH,
)