package edu.cnm.deepdive.seesomethingabq.service.paging

import android.app.Activity
import androidx.paging.PagingSource
import androidx.paging.PagingState
import edu.cnm.deepdive.seesomethingabq.model.dto.UserProfileSummary
import edu.cnm.deepdive.seesomethingabq.service.ManagerUserService
import kotlinx.coroutines.future.await

/**
 * Paging 3 source that loads pages of [UserProfileSummary] from [ManagerUserService].
 *
 * @property activity activity used for authentication flows.
 * @property managerUserService service used to fetch pages.
 */
class ManagerUserPagingSource(
  private val activity: Activity,
  private val managerUserService: ManagerUserService
) : PagingSource<Int, UserProfileSummary>() {

  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserProfileSummary> {
    return try {
      val page = params.key ?: 0
      val pageSize = params.loadSize

      val response = managerUserService
        .getManagerUsersPage(activity, page, pageSize)
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

  override fun getRefreshKey(state: PagingState<Int, UserProfileSummary>): Int? {
    return state.anchorPosition?.let { anchorPosition ->
      val anchorPage = state.closestPageToPosition(anchorPosition)
      anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
    }
  }
}

