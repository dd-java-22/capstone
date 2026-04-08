package edu.cnm.deepdive.seesomethingabq.service

import android.app.Activity
import android.net.Uri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportRequest
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportSummary
import edu.cnm.deepdive.seesomethingabq.model.dto.PaginatedResponse
import edu.cnm.deepdive.seesomethingabq.service.paging.IssueReportPagingSource
import edu.cnm.deepdive.seesomethingabq.service.proxy.SeeSomethingWebService
import edu.cnm.deepdive.seesomethingabq.service.repository.GoogleAuthRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.future.await
import kotlinx.coroutines.future.future
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
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

  override fun getIssueReportsPager(activity: Activity): Pager<Int, IssueReportSummary> {
    return Pager(
      config = PagingConfig(
        pageSize = 10,
        enablePlaceholders = false
      ),
      pagingSourceFactory = { IssueReportPagingSource(activity, this) }
    )
  }

  override fun uploadImage(
    activity: Activity,
    reportId: String,
    uri: Uri
  ): CompletableFuture<Void?> =
    scope.future {
      val credential = getCredential(activity)

      // Convert URI → bytes
      val inputStream = activity.contentResolver.openInputStream(uri)
        ?: throw IllegalArgumentException("Unable to open URI: $uri")

      val bytes = inputStream.readBytes()
      val requestBody = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
      val part = MultipartBody.Part.createFormData(
        "file",
        "upload.jpg",
        requestBody
      )

      // Call Retrofit
      webService.uploadImage(
        "Bearer ${credential.idToken}",
        reportId,
        part
      )

      null
    }



  private suspend fun getCredential(activity: Activity): GoogleIdTokenCredential =
    authRepository.getValidCredential(activity).await()

}
