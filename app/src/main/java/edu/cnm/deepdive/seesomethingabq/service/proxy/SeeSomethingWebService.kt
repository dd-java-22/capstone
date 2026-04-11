package edu.cnm.deepdive.seesomethingabq.service.proxy

import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReport
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportStatusUpdateRequest
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportTypesUpdateRequest
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportRequest
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportSummary
import edu.cnm.deepdive.seesomethingabq.model.dto.ManagerStatusUpdateRequest
import edu.cnm.deepdive.seesomethingabq.model.dto.PaginatedResponse
import edu.cnm.deepdive.seesomethingabq.model.dto.UserEnabledUpdateRequest
import edu.cnm.deepdive.seesomethingabq.model.dto.ReportImageDto
import edu.cnm.deepdive.seesomethingabq.model.dto.UserProfileSummary
import edu.cnm.deepdive.seesomethingabq.model.entity.AcceptedState
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PUT
import retrofit2.http.Streaming

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
   * Updates the current user's profile (display name and/or email).
   *
   * @param bearerToken Authorization header value.
   * @param request request body containing updated user fields.
   * @return updated user profile.
   */
  @PATCH("users/me")
  suspend fun updateUserProfile(
    @Header("Authorization") bearerToken: String,
    @Body request: edu.cnm.deepdive.seesomethingabq.model.dto.UpdateUserRequest
  ): UserProfile

  /**
   * Uploads a new avatar image for the current user.
   *
   * @param bearerToken Authorization header value.
   * @param file Multipart file part containing the avatar image.
   * @return updated user profile with new avatar URL.
   */
  @Multipart
  @POST("users/me/avatar")
  suspend fun uploadUserAvatar(
    @Header("Authorization") bearerToken: String,
    @Part file: MultipartBody.Part
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
   * Retrieves accepted states for manager workflows.
   *
   * Endpoint: GET /manager/accepted-states
   */
  @GET("manager/accepted-states")
  suspend fun getAcceptedStates(
    @Header("Authorization") bearerToken: String
  ): List<AcceptedState>

  /**
   * Updates the accepted-state/status of an issue report (manager-only).
   *
   * Endpoint: PUT /manager/issue-reports/{externalId}/status
   *
   * Response body is an IssueReport entity; we intentionally treat it as raw bytes and
   * then reload via the normal full-report endpoint to normalize the UI.
   */
  @PUT("manager/issue-reports/{externalId}/status")
  suspend fun updateManagerIssueReportStatus(
    @Header("Authorization") bearerToken: String,
    @Path("externalId") externalId: String,
    @Body request: IssueReportStatusUpdateRequest
  ): ResponseBody

  /**
   * Replaces issue types on an issue report (manager-only).
   *
   * Endpoint: PUT /manager/issue-reports/{externalId}/issue-types
   *
   * Response body is an IssueReport entity; we intentionally treat it as raw bytes and
   * then reload via the normal full-report endpoint to normalize the UI.
   */
  @PUT("manager/issue-reports/{externalId}/issue-types")
  suspend fun replaceManagerIssueReportIssueTypes(
    @Header("Authorization") bearerToken: String,
    @Path("externalId") externalId: String,
    @Body request: IssueReportTypesUpdateRequest
  ): ResponseBody

  /**
   * Sets manager authorization status for a user.
   *
   * Endpoint: PATCH /manager/users/{externalId}/manager-status
   *
   * @param bearerToken Authorization header value.
   * @param externalId user external ID (UUID).
   * @param request request body containing the desired manager status.
   * @return updated user profile summary.
   */
  @PATCH("manager/users/{externalId}/manager-status")
  suspend fun setManagerStatus(
    @Header("Authorization") bearerToken: String,
    @Path("externalId") externalId: UUID,
    @Body request: ManagerStatusUpdateRequest
  ): UserProfileSummary

  /**
   * Sets enabled/active status for a user.
   *
   * Endpoint: PATCH /manager/users/{externalId}/enabled
   *
   * @param bearerToken Authorization header value.
   * @param externalId user external ID (UUID).
   * @param request request body containing the desired enabled status.
   * @return updated user profile summary.
   */
  @PATCH("manager/users/{externalId}/enabled")
  suspend fun setEnabledStatus(
    @Header("Authorization") bearerToken: String,
    @Path("externalId") externalId: UUID,
    @Body request: UserEnabledUpdateRequest
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
   * Downloads the raw image bytes for a specific image.
   *
   * @return The image file as a streaming response body.
   */
  @Streaming
  @GET("issue-reports/{reportId}/images/{imageId}")
  suspend fun downloadImageFile(
    @Header("Authorization") bearerToken: String,
    @Path("reportId") reportId: String,
    @Path("imageId") imageId: String
  ): ResponseBody

  /**
   * Deletes an attached image for a specific issue report.
   */
  @DELETE("issue-reports/{reportId}/images/{imageId}")
  suspend fun deleteImage(
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

  /**
   * Updates an existing issue report.
   *
   * @param bearerToken Authorization header value.
   * @param reportId External ID of the issue report (UUID string).
   * @param request report request payload containing updated fields.
   * @return updated issue report DTO.
   */
  @PUT("issue-reports/{reportId}")
  suspend fun updateIssueReport(
    @Header("Authorization") bearerToken: String,
    @Path("reportId") reportId: String,
    @Body request: IssueReportRequest
  ): IssueReport
}
