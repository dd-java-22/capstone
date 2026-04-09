package edu.cnm.deepdive.seesomethingabq.service.paging

import android.app.Activity
import androidx.paging.PagingSource
import androidx.paging.PagingState
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportSummary
import edu.cnm.deepdive.seesomethingabq.model.dto.PaginatedResponse
import edu.cnm.deepdive.seesomethingabq.service.IssueReportService
import kotlinx.coroutines.future.await
import java.util.concurrent.CompletableFuture

/**
 * Paging 3 source that loads pages of [IssueReportSummary] from [IssueReportService].
 *
 * @property activity activity used for authentication flows.
 * @property issueReportService service used to fetch pages.
 */
class IssueReportPagingSource(
    private val reportSummaryPageProvider: (Int, Int) -> CompletableFuture<PaginatedResponse<IssueReportSummary>>
) : PagingSource<Int, IssueReportSummary>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, IssueReportSummary> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize

            val response = reportSummaryPageProvider(page, pageSize)
                .await()

            LoadResult.Page(
                data = response.content,
                prevKey = if (response.first) null else page - 1,
                nextKey = if (response.last) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, IssueReportSummary>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
