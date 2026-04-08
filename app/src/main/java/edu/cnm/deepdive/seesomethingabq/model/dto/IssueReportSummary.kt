package edu.cnm.deepdive.seesomethingabq.model.dto

import com.google.gson.annotations.Expose
import java.time.Instant
import java.util.UUID

/**
 * Summary DTO returned by the server API for issue report list views.
 *
 * @property externalId report external identifier.
 * @property description report description text.
 * @property acceptedState accepted-state status tag.
 * @property timeFirstReported timestamp when the report was created.
 * @property timeLastModified timestamp when the report was last updated.
 */
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
