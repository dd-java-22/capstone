package edu.cnm.deepdive.seesomethingabq.service.proxy

import edu.cnm.deepdive.seesomethingabq.model.dto.*
import edu.cnm.deepdive.seesomethingabq.model.entity.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*
import java.util.UUID

/**
 * Defines the Retrofit HTTP API for interacting with the SeeSomethingABQ backend service.
 *
 * All methods require a valid Google ID token passed as a Bearer token in the Authorization header.
 * This interface includes:
 * - User profile operations (get, update, avatar upload)
 * - Issue report CRUD operations
 * - Manager‑only administrative endpoints
 * - Image upload/download for reports
 *
 * All suspend functions are intended to be called from a coroutine context.
 */
interface SeeSomethingWebService {

  // ---------------------------------------------------------------------------
  //  USER PROFILE ENDPOINTS
  // ---------------------------------------------------------------------------

  /**
   * Retrieves the current authenticated user's profile.
   *
   * Endpoint: **GET /users/me**
   *
   * @param bearerToken Authorization header value in the form `"Bearer <token>"`.
   * @return The full [UserProfile] for the authenticated user.
   */
  @GET("users/me")
  suspend fun getMe(
    @Header("Authorization") bearerToken: String
  ): UserProfile

  /**
   * Updates the current user's profile fields (display name and/or email).
   *
   * Endpoint: **PATCH /users/me**
   *
   * Only the fields provided in [UpdateUserRequest] will be updated.
   *
   * @param bearerToken Authorization header value.
   * @param request Request body containing updated user fields.
   * @return The updated [UserProfile].
   */
  @PATCH("users/me")
  suspend fun updateUserProfile(
    @Header("Authorization") bearerToken: String,
    @Body request: UpdateUserRequest
  ): UserProfile

  /**
   * Uploads a new avatar image for the current user.
   *
   * Endpoint: **POST /users/me/avatar**
   *
   * The backend expects a multipart form field named `"avatar"`.
   *
   * @param bearerToken Authorization header value.
   * @param avatar Multipart file part containing the avatar image.
   * @return The updated [UserProfile] including the new avatar URL.
   */
  @Multipart
  @POST("users/me/avatar")
  suspend fun uploadUserAvatar(
    @Header("Authorization") bearerToken: String,
    @Part("avatar") avatar: MultipartBody.Part
  ): UserProfile

  // ---------------------------------------------------------------------------
  //  ISSUE TYPE & REPORT ENDPOINTS
  // ---------------------------------------------------------------------------

  @GET("issue-types")
  suspend fun getIssueTypes(
    @Header("Authorization") bearerToken: String
  ): List<IssueType>

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

  @GET("manager/issue-reports")
  suspend fun getAllIssueReportsPage(
    @Header("Authorization") bearerToken: String,
    @Query("pageNumber") page: Int = 0,
    @Query("pageSize") size: Int = 10
  ): PaginatedResponse<IssueReportSummary>

  @GET("manager/users")
  suspend fun getManagerUsersPage(
    @Header("Authorization") bearerToken: String,
    @Query("pageNumber") page: Int = 0,
    @Query("pageSize") size: Int = 10
  ): PaginatedResponse<UserProfileSummary>

  @GET("manager/users/{externalId}")
  suspend fun getManagerUser(
    @Header("Authorization") bearerToken: String,
    @Path("externalId") externalId: UUID
  ): UserProfileSummary

  @GET("manager/accepted-states")
  suspend fun getAcceptedStates(
    @Header("Authorization") bearerToken: String
  ): List<AcceptedState>

  @PUT("manager/issue-reports/{externalId}/status")
  suspend fun updateManagerIssueReportStatus(
    @Header("Authorization") bearerToken: String,
    @Path("externalId") externalId: String,
    @Body request: IssueReportStatusUpdateRequest
  ): ResponseBody

  @PUT("manager/issue-reports/{externalId}/issue-types")
  suspend fun replaceManagerIssueReportIssueTypes(
    @Header("Authorization") bearerToken: String,
    @Path("externalId") externalId: String,
    @Body request: IssueReportTypesUpdateRequest
  ): ResponseBody

  @PATCH("manager/users/{externalId}/manager-status")
  suspend fun setManagerStatus(
    @Header("Authorization") bearerToken: String,
    @Path("externalId") externalId: UUID,
    @Body request: ManagerStatusUpdateRequest
  ): UserProfileSummary

  @PATCH("manager/users/{externalId}/enabled")
  suspend fun setEnabledStatus(
    @Header("Authorization") bearerToken: String,
    @Path("externalId") externalId: UUID,
    @Body request: UserEnabledUpdateRequest
  ): UserProfileSummary

  @POST("issue-reports")
  suspend fun submitIssueReport(
    @Header("Authorization") bearerToken: String,
    @Body request: IssueReportRequest
  ): IssueReport

  @Multipart
  @POST("issue-reports/{reportId}/images")
  suspend fun uploadImage(
    @Header("Authorization") bearerToken: String,
    @Path("reportId") reportId: String,
    @Part file: MultipartBody.Part
  ): ReportImageDto

  @Streaming
  @GET("issue-reports/{reportId}/images/{imageId}")
  suspend fun downloadImageFile(
    @Header("Authorization") bearerToken: String,
    @Path("reportId") reportId: String,
    @Path("imageId") imageId: String
  ): ResponseBody

  @DELETE("issue-reports/{reportId}/images/{imageId}")
  suspend fun deleteImage(
    @Header("Authorization") bearerToken: String,
    @Path("reportId") reportId: String,
    @Path("imageId") imageId: String
  ): ResponseBody

  @GET("issue-reports/{reportId}")
  suspend fun getIssueReport(
    @Header("Authorization") bearerToken: String,
    @Path("reportId") reportId: String
  ): IssueReport

  @PUT("issue-reports/{reportId}")
  suspend fun updateIssueReport(
    @Header("Authorization") bearerToken: String,
    @Path("reportId") reportId: String,
    @Body request: IssueReportRequest
  ): IssueReport
}
