package edu.cnm.deepdive.seesomethingabq.service.proxy

import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReport
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportRequest
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportSummary
import edu.cnm.deepdive.seesomethingabq.model.dto.PaginatedResponse
import edu.cnm.deepdive.seesomethingabq.model.dto.ReportImageDto
import edu.cnm.deepdive.seesomethingabq.model.dto.UserProfileSummary
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

import java.util.UUID

/**
 * Defines the Retrofit HTTP API for interacting with the SeeSomethingABQ backend service.
 *
 * All methods require a valid Google ID token passed as a Bearer token in the Authorization header.
 */
interface SeeSomethingWebService {

  /**
   * Retrieves the current user's profile from the server.
   *
   * @param bearerToken Authorization header value.
   * @return user profile.
   */
  @GET("users/me")
  suspend fun getMe(
    @Header("Authorization") bearerToken: String
  ): UserProfile

  /**
   * Retrieves all issue types from the server.
   *
   * @param bearerToken Authorization header value.
   * @return list of issue types.
   */
  @GET("issue-types")
  suspend fun getIssueTypes(
    @Header("Authorization") bearerToken: String
  ): List<IssueType>

  /**
   * Retrieves the current user's issue report summaries.
   *
   * @param bearerToken Authorization header value.
   * @return list of report summaries.
   */
  @GET("issue-reports/mine")
  suspend fun getMyReports(
    @Header("Authorization") bearerToken: String
  ): List<IssueReportSummary>

  @GET("issue-reports/mine")
  suspend fun getMyIssueReportsPage(
    @Header("Authorization") bearerToken: String,
    @Query("pageNumber") page: Int = 0,
    @Query("pageSize") size: Int = 10,
  ): PaginatedResponse<IssueReportSummary>

  /**
   * Retrieves a page of issue report summaries for manager views.
   *
   * @param bearerToken Authorization header value.
   * @param page zero-based page number.
   * @param size page size.
   * @return paginated response of report summaries.
   */
  @GET("manager/issue-reports")
  suspend fun getAllIssueReportsPage(
    @Header("Authorization") bearerToken: String,
    @Query("pageNumber") page: Int = 0,
    @Query("pageSize") size: Int = 10
  ): PaginatedResponse<IssueReportSummary>

  /**
   * Retrieves a page of user profiles for manager views.
   *
   * @param bearerToken Authorization header value.
   * @param page zero-based page number.
   * @param size page size.
   * @return paginated response of user profile summaries.
   */
  @GET("manager/users")
  suspend fun getManagerUsersPage(
    @Header("Authorization") bearerToken: String,
    @Query("pageNumber") page: Int = 0,
    @Query("pageSize") size: Int = 10
  ): PaginatedResponse<UserProfileSummary>

  /**
   * Retrieves a single manager-visible user profile by external ID.
   *
   * @param bearerToken Authorization header value.
   * @param externalId user external ID (UUID).
   * @return user profile summary.
   */
  @GET("manager/users/{externalId}")
  suspend fun getManagerUser(
    @Header("Authorization") bearerToken: String,
    @Path("externalId") externalId: UUID
  ): UserProfileSummary

  /**
   * Submits a new issue report.
   *
   * @param bearerToken Authorization header value.
   * @param request report request payload.
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
  @POST("issue-reports/{reportId}/images")
  suspend fun uploadImage(
    @Header("Authorization") bearerToken: String,
    @Path("reportId") reportId: String,
    @Part file: MultipartBody.Part
  ): ReportImageDto

  /**
   * Retrieves metadata for a specific image belonging to an issue report.
   */
  @GET("issue-reports/{reportId}/images/{imageId}") // ⭐ FIXED
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
  @GET("issue-reports/{reportId}/images/{imageId}/file") // ⭐ FIXED
  suspend fun downloadImageFile(
    @Header("Authorization") bearerToken: String,
    @Path("reportId") reportId: String,
    @Path("imageId") imageId: String
  ): ResponseBody

  /**
   * Retrieves a full issue report including metadata and image list.
   */
  @GET("issue-reports/{reportId}")
  suspend fun getIssueReport(
    @Header("Authorization") bearerToken: String,
    @Path("reportId") reportId: String
  ): IssueReport
}
