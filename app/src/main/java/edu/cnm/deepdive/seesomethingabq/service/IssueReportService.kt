package edu.cnm.deepdive.seesomethingabq.service

import android.app.Activity
import androidx.paging.Pager
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportRequest
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportSummary
import edu.cnm.deepdive.seesomethingabq.model.dto.PaginatedResponse
import java.util.concurrent.CompletableFuture

/**
 * Service for submitting and retrieving issue reports from the server API.
 */
interface IssueReportService {

  /**
   * Submits an issue report for the currently authenticated user.
   *
   * @param activity activity used for authentication flows.
   * @param request request payload describing the report.
   * @return future that completes when submission is done.
   */
  fun submit(activity: Activity, request: IssueReportRequest): CompletableFuture<Void?>

  /**
   * Retrieves a single paginated page of issue report summaries.
   *
   * @param activity activity used for authentication flows.
   * @param page zero-based page number.
   * @param size page size.
   * @return future completing with a paginated response.
   */
  fun getIssueReportsPage(
    activity: Activity,
    page: Int = 0,
    size: Int = 10
  ): CompletableFuture<PaginatedResponse<IssueReportSummary>>

  /**
   * Creates a paging [Pager] for issue report summaries.
   *
   * @param activity activity used for authentication flows.
   * @return pager producing [IssueReportSummary] items.
   */
  fun getIssueReportsPager(activity: Activity): Pager<Int, IssueReportSummary>

}
