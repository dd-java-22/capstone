package edu.cnm.deepdive.seesomethingabq.service

import android.app.Activity
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportRequest
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportSummary
import edu.cnm.deepdive.seesomethingabq.model.dto.PaginatedResponse
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
class IssueReportServiceImpl @Inject constructor(
  private val authRepository: GoogleAuthRepository,
  private val webService: SeeSomethingWebService,
) : IssueReportService {

  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

  override fun getIssueReportsPage(
    activity: Activity,
    page: Int,
    size: Int
  ): CompletableFuture<PaginatedResponse<IssueReportSummary>> =
    scope.future {
      val credential = getCredential(activity)
      webService.getIssueReportsPage("Bearer ${credential.idToken}", page, size)
    }

  override fun submit(activity: Activity, request: IssueReportRequest): CompletableFuture<Void?> =
      scope.future {
          val credential = getCredential(activity)
          webService.submitIssueReport("Bearer ${credential.idToken}", request)
          null
      }

  private suspend fun getCredential(activity: Activity): GoogleIdTokenCredential =
    authRepository.getValidCredential(activity).await()

}
