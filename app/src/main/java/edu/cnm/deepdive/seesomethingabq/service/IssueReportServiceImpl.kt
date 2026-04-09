package edu.cnm.deepdive.seesomethingabq.service

import android.app.Activity
import android.net.Uri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReport
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
import okhttp3.ResponseBody
import java.util.concurrent.CompletableFuture

/**
 * Concrete implementation of [IssueReportService] that uses Retrofit and Google authentication.
 *
 * All network calls are executed on a background coroutine scope and exposed as [CompletableFuture]s
 * for convenient use from Java-based ViewModels.
 */
@Singleton
/**
 * Default [IssueReportService] implementation backed by [SeeSomethingWebService].
 */
class IssueReportServiceImpl @Inject constructor(
    private val authRepository: GoogleAuthRepository,
    private val webService: SeeSomethingWebService,
) : IssueReportService {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun getAllIssueReportsPage(
        activity: Activity,
        page: Int,
        size: Int
    ): CompletableFuture<PaginatedResponse<IssueReportSummary>> =
        scope.future {
            val credential = getCredential(activity)
            webService.getAllIssueReportsPage("Bearer ${credential.idToken}", page, size)
        }

    override fun getMyIssueReportsPage(
        activity: Activity,
        page: Int,
        size: Int
    ): CompletableFuture<PaginatedResponse<IssueReportSummary>> =
        scope.future {
            val credential = getCredential(activity)
            webService.getMyIssueReportsPage("Bearer ${credential.idToken}", page, size)
        }

  override fun submit(
    activity: Activity,
    request: IssueReportRequest
  ): CompletableFuture<IssueReport> =
    scope.future {
      val credential = getCredential(activity)
      val report = webService.submitIssueReport("Bearer ${credential.idToken}", request)
      report
    }



  override fun uploadImages(
    activity: Activity,
    reportId: String,
    uris: List<Uri>
  ): CompletableFuture<Void?> =
    scope.future {
      if (uris.isEmpty()) {
        return@future null
      }

      val credential = getCredential(activity)
      val bearer = "Bearer ${credential.idToken}"

      for (uri in uris) {
        val inputStream = activity.contentResolver.openInputStream(uri)
          ?: continue

        val bytes = inputStream.readBytes()
        inputStream.close()

        val requestBody = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData(
          "file",              // MUST match @RequestPart("file")
          "upload.jpg",        // filename (backend ignores it)
          requestBody
        )

        // This calls POST /issue-reports/{reportId}/images
        webService.uploadImage(bearer, reportId, part)
      }

      null
    }

    override fun downloadImageFile(
    activity: Activity,
    reportId: String,
    imageId: String
  ): CompletableFuture<ResponseBody> =
    scope.future {
      val credential = getCredential(activity)
      webService.downloadImageFile(
        "Bearer ${credential.idToken}",
        reportId,
        imageId
      )
    }

  override fun getAllIssueReportsPager(activity: Activity): Pager<Int, IssueReportSummary> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                IssueReportPagingSource { pageNum, pageSize ->
                    getAllIssueReportsPage(activity, pageNum, pageSize)
                }
            }
        )
    }

    override fun getMyIssueReportsPager(activity: Activity): Pager<Int, IssueReportSummary> =
         Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                IssueReportPagingSource { pageNum, pageSize ->
                    getMyIssueReportsPage(activity, pageNum, pageSize)
                }
            }
        )
  override fun getReport(
    activity: Activity,
    reportId: String
  ): CompletableFuture<IssueReport> =
    scope.future {
      val credential = getCredential(activity)
      webService.getIssueReport(
        "Bearer ${credential.idToken}",
        reportId
      )
    }

  override fun updateReport(
    activity: Activity,
    reportId: String,
    request: IssueReportRequest
  ): CompletableFuture<IssueReport> =
    scope.future {
      val credential = getCredential(activity)
      webService.updateIssueReport(
        "Bearer ${credential.idToken}",
        reportId,
        request
      )
    }

    private suspend fun getCredential(activity: Activity): GoogleIdTokenCredential =
        authRepository.getValidCredential(activity).await()

}
