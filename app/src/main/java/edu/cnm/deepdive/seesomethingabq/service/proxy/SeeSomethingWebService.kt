package edu.cnm.deepdive.seesomethingabq.service.proxy

import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReport
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportRequest
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportSummary
import edu.cnm.deepdive.seesomethingabq.model.dto.PaginatedResponse
import edu.cnm.deepdive.seesomethingabq.model.dto.ReportImageDto
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Defines the Retrofit HTTP API for interacting with the SeeSomethingABQ backend service.
 *
 * All methods require a valid Google ID token passed as a Bearer token in the Authorization header.
 */
interface SeeSomethingWebService {

  /** Retrieves the authenticated user's profile. */
  @GET("users/me")
  suspend fun getMe(
    @Header("Authorization") bearerToken: String
  ): UserProfile

  /** Retrieves the list of available issue types. */
  @GET("issue-types")
  suspend fun getIssueTypes(
    @Header("Authorization") bearerToken: String
  ): List<IssueType>

  /** Retrieves all issue reports submitted by the authenticated user. */
  @GET("issue-reports/mine")
  suspend fun getMyReports(
    @Header("Authorization") bearerToken: String
  ): List<IssueReportSummary>

  /** Retrieves a paginated list of issue reports for managers. */
  @GET("manager/issue-reports")
  suspend fun getIssueReportsPage(
    @Header("Authorization") bearerToken: String,
    @Query("pageNumber") page: Int = 0,
    @Query("pageSize") size: Int = 10
  ): PaginatedResponse<IssueReportSummary>

  /**
   * Submits a new issue report and returns the created report, including its external ID.
   */
  @POST("issue-reports")
  suspend fun submitIssueReport(
    @Header("Authorization") bearerToken: String,
    @Body request: IssueReportRequest
  ): IssueReport

  /**
   * Uploads an image file associated with an existing issue report.
   *
   * @param bearerToken Google ID token in the form `"Bearer <token>"`.
   * @param reportId External ID of the issue report.
   * @param file Multipart file part containing the image data.
   * @return The created image metadata.
   */
  @Multipart
  @POST("see-something/issue-reports/{reportId}/images/upload")
  suspend fun uploadImage(
    @Header("Authorization") bearerToken: String,
    @Path("reportId") reportId: String,
    @Part file: MultipartBody.Part
  ): ReportImageDto

  /**
   * Retrieves metadata for a specific image belonging to an issue report.
   */
  @GET("see-something/issue-reports/{reportId}/images/{imageId}")
  suspend fun getImageMetadata(
    @Header("Authorization") bearerToken: String,
    @Path("reportId") reportId: String,
    @Path("imageId") imageId: String
  ): ReportImageDto

  /**
   * Downloads the raw image file associated with a specific image.
   *
   * @return The image file as a streaming response body.
   */
  @GET("see-something/issue-reports/{reportId}/images/{imageId}/file")
  suspend fun downloadImageFile(
    @Header("Authorization") bearerToken: String,
    @Path("reportId") reportId: String,
    @Path("imageId") imageId: String
  ): ResponseBody

  @GET("issue-reports/{reportId}")
  suspend fun getIssueReport(
    @Header("Authorization") bearerToken: String,
    @Path("reportId") reportId: String
  ): IssueReport

}
