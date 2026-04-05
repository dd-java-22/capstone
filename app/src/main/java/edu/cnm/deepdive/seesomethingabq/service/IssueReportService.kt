package edu.cnm.deepdive.seesomethingabq.service

import android.app.Activity
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportSummary
import edu.cnm.deepdive.seesomethingabq.model.dto.PaginatedResponse
import java.util.concurrent.CompletableFuture

interface IssueReportService {

  fun getIssueReportsPage(
    activity: Activity,
    page: Int = 0,
    size: Int = 10
  ): CompletableFuture<PaginatedResponse<IssueReportSummary>>

}
