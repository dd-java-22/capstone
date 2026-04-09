package edu.cnm.deepdive.seesomethingabq.service

import android.app.Activity
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import edu.cnm.deepdive.seesomethingabq.model.dto.PaginatedResponse
import edu.cnm.deepdive.seesomethingabq.model.dto.UserProfileSummary
import edu.cnm.deepdive.seesomethingabq.service.paging.ManagerUserPagingSource
import edu.cnm.deepdive.seesomethingabq.service.proxy.SeeSomethingWebService
import edu.cnm.deepdive.seesomethingabq.service.repository.GoogleAuthRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.future.await
import kotlinx.coroutines.future.future
import java.util.concurrent.CompletableFuture

@Singleton
/**
 * Default [ManagerUserService] implementation backed by [SeeSomethingWebService].
 */
class ManagerUserServiceImpl @Inject constructor(
  private val authRepository: GoogleAuthRepository,
  private val webService: SeeSomethingWebService,
) : ManagerUserService {

  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

  override fun getManagerUsersPage(
    activity: Activity,
    page: Int,
    size: Int
  ): CompletableFuture<PaginatedResponse<UserProfileSummary>> =
    scope.future {
      val credential = getCredential(activity)
      webService.getManagerUsersPage("Bearer ${credential.idToken}", page, size)
    }

  override fun getManagerUsersPager(activity: Activity): Pager<Int, UserProfileSummary> {
    return Pager(
      config = PagingConfig(
        pageSize = 10,
        enablePlaceholders = false
      ),
      pagingSourceFactory = { ManagerUserPagingSource(activity, this) }
    )
  }

  private suspend fun getCredential(activity: Activity): GoogleIdTokenCredential =
    authRepository.getValidCredential(activity).await()

}

