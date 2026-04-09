package edu.cnm.deepdive.seesomethingabq.service

import android.app.Activity
import androidx.paging.Pager
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportRequest
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportSummary
import edu.cnm.deepdive.seesomethingabq.model.dto.PaginatedResponse
import java.util.concurrent.CompletableFuture

interface IssueReportService {

    fun submit(activity: Activity, request: IssueReportRequest): CompletableFuture<Void?>

    fun getAllIssueReportsPage(
        activity: Activity,
        page: Int = 0,
        size: Int = 10
    ): CompletableFuture<PaginatedResponse<IssueReportSummary>>

    fun getMyIssueReportsPage(
        activity: Activity,
        page: Int = 0,
        size: Int = 10
    ): CompletableFuture<PaginatedResponse<IssueReportSummary>>


    fun getAllIssueReportsPager(activity: Activity): Pager<Int, IssueReportSummary>

    fun getMyIssueReportsPager(activity: Activity): Pager<Int, IssueReportSummary>

}
