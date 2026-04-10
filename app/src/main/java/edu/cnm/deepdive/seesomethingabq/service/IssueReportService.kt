package edu.cnm.deepdive.seesomethingabq.service

import android.app.Activity
import android.net.Uri
import androidx.paging.Pager
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReport
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportRequest
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportStatusUpdateRequest
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportSummary
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportTypesUpdateRequest
import edu.cnm.deepdive.seesomethingabq.model.dto.PaginatedResponse
import java.util.concurrent.CompletableFuture
import okhttp3.ResponseBody

/**
 * Defines high-level operations for working with issue reports on the client side.
 *
 * This service coordinates authentication, Retrofit calls, and paging.
 */
interface IssueReportService {

  /**
   * Submits an issue report for the currently authenticated user.
   *
   * @param activity activity used for authentication flows.
   * @param request request payload describing the report.
   * @return future that completes when submission is done.
   */
    fun submit(activity: Activity, request: IssueReportRequest): CompletableFuture<IssueReport>

    /**
     * Uploads all provided image URIs for the given report ID.
     *
     * The returned future completes when all uploads have finished.
     */
    fun uploadImages(
        activity: Activity,
        reportId: String,
        uris: List<Uri>
    ): CompletableFuture<Void?>

    /**
     * Downloads the raw image file for the given report and image IDs.
     *
     * The caller is responsible for consuming and closing the [ResponseBody].
     */
    fun downloadImageFile(
        activity: Activity,
        reportId: String,
        imageId: String
    ): CompletableFuture<ResponseBody>

  /**
   * Retrieves a single paginated page of issue report summaries.
   *
   * @param activity activity used for authentication flows.
   * @param page zero-based page number.
   * @param size page size.
   * @return future completing with a paginated response.
   */
  fun getAllIssueReportsPage(
        activity: Activity,
        page: Int = 0,
        size: Int = 10
    ): CompletableFuture<PaginatedResponse<IssueReportSummary>>

    /**
     * Retrieves a single paginated page of issue report summaries
     * submitted by the currently logged in user.
     *
     * @param activity activity used for authentication flows.
     * @param page zero-based page number.
     * @param size page size.
     * @return future completing with a paginated response.
     */
    fun getMyIssueReportsPage(
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
  fun getAllIssueReportsPager(activity: Activity): Pager<Int, IssueReportSummary>

    /**
     * Creates a paging [Pager] for issue report summaries only for the currently logged in user.
     *
     * @param activity activity used for authentication flows.
     * @return pager producing [IssueReportSummary] items.
     */
  fun getMyIssueReportsPager(activity: Activity): Pager<Int, IssueReportSummary>


  fun getReport(
    activity: Activity,
    reportId: String
  ): CompletableFuture<IssueReport>

  fun updateReport(
    activity: Activity,
    reportId: String,
    request: IssueReportRequest
  ): CompletableFuture<IssueReport>

  /**
   * Updates accepted-state/status for a report (manager-only).
   */
  fun updateManagerReportStatus(
    activity: Activity,
    reportId: String,
    request: IssueReportStatusUpdateRequest
  ): CompletableFuture<Void?>

  /**
   * Replaces issue types for a report (manager-only).
   */
  fun replaceManagerReportIssueTypes(
    activity: Activity,
    reportId: String,
    request: IssueReportTypesUpdateRequest
  ): CompletableFuture<Void?>

}
