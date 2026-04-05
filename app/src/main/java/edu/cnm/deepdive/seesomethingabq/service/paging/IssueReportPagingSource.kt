package edu.cnm.deepdive.seesomethingabq.service.paging

import android.app.Activity
import androidx.paging.PagingSource
import androidx.paging.PagingState
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportSummary
import edu.cnm.deepdive.seesomethingabq.service.IssueReportService
import kotlinx.coroutines.future.await

class IssueReportPagingSource(
  private val activity: Activity,
  private val issueReportService: IssueReportService
) : PagingSource<Int, IssueReportSummary>() {

  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, IssueReportSummary> {
    return try {
      val page = params.key ?: 0
      val pageSize = params.loadSize

      val response = issueReportService
        .getIssueReportsPage(activity, page, pageSize)
        .await()

      LoadResult.Page(
        data = response.content,
        prevKey = if (page == 0) null else page - 1,
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
