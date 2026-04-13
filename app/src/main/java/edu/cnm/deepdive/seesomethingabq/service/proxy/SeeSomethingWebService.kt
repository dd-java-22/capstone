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

  /**
   * Retrieves all available issue types.
   *
   * Endpoint: **GET /issue-types**
   *
   * @param bearerToken Authorization header value in the form `"Bearer <token>"`.
   * @return A list of all [IssueType] objects available in the system.
   */
  @GET("issue-types")
  suspend fun getIssueTypes(
    @Header("Authorization") bearerToken: String
  ): List<IssueType>

  /**
   * Retrieves all issue reports created by the current authenticated user.
   *
   * Endpoint: **GET /issue-reports/mine**
   *
   * @param bearerToken Authorization header value in the form `"Bearer <token>"`.
   * @return A list of [IssueReportSummary] objects created by the user.
   */
  @GET("issue-reports/mine")
  suspend fun getMyReports(
    @Header("Authorization") bearerToken: String
  ): List<IssueReportSummary>

  /**
   * Retrieves a paginated list of issue reports created by the current authenticated user.
   *
   * Endpoint: **GET /issue-reports/mine**
   *
   * @param bearerToken Authorization header value in the form `"Bearer <token>"`.
   * @param page The page number to retrieve (0-indexed). Defaults to 0.
   * @param size The number of items per page. Defaults to 10.
   * @return A [PaginatedResponse] containing [IssueReportSummary] objects.
   */
  @GET("issue-reports/mine")
  suspend fun getMyIssueReportsPage(
    @Header("Authorization") bearerToken: String,
    @Query("pageNumber") page: Int = 0,
    @Query("pageSize") size: Int = 10,
  ): PaginatedResponse<IssueReportSummary>

  /**
   * Retrieves a paginated list of all issue reports in the system.
   *
   * Endpoint: **GET /manager/issue-reports**
   *
   * This is a manager-only endpoint.
   *
   * @param bearerToken Authorization header value in the form `"Bearer <token>"`.
   * @param page The page number to retrieve (0-indexed). Defaults to 0.
   * @param size The number of items per page. Defaults to 10.
   * @return A [PaginatedResponse] containing [IssueReportSummary] objects.
   */
  @GET("manager/issue-reports")
  suspend fun getAllIssueReportsPage(
    @Header("Authorization") bearerToken: String,
    @Query("pageNumber") page: Int = 0,
    @Query("pageSize") size: Int = 10
  ): PaginatedResponse<IssueReportSummary>

  /**
   * Retrieves a paginated list of all user profiles in the system.
   *
   * Endpoint: **GET /manager/users**
   *
   * This is a manager-only endpoint.
   *
   * @param bearerToken Authorization header value in the form `"Bearer <token>"`.
   * @param page The page number to retrieve (0-indexed). Defaults to 0.
   * @param size The number of items per page. Defaults to 10.
   * @return A [PaginatedResponse] containing [UserProfileSummary] objects.
   */
  @GET("manager/users")
  suspend fun getManagerUsersPage(
    @Header("Authorization") bearerToken: String,
    @Query("pageNumber") page: Int = 0,
    @Query("pageSize") size: Int = 10
  ): PaginatedResponse<UserProfileSummary>

  /**
   * Retrieves a specific user profile by their external ID.
   *
   * Endpoint: **GET /manager/users/{externalId}**
   *
   * This is a manager-only endpoint.
   *
   * @param bearerToken Authorization header value in the form `"Bearer <token>"`.
   * @param externalId The unique external identifier of the user.
   * @return The [UserProfileSummary] for the specified user.
   */
  @GET("manager/users/{externalId}")
  suspend fun getManagerUser(
    @Header("Authorization") bearerToken: String,
    @Path("externalId") externalId: UUID
  ): UserProfileSummary

  /**
   * Retrieves all available accepted states for issue reports.
   *
   * Endpoint: **GET /manager/accepted-states**
   *
   * This is a manager-only endpoint.
   *
   * @param bearerToken Authorization header value in the form `"Bearer <token>"`.
   * @return A list of all [AcceptedState] objects available in the system.
   */
  @GET("manager/accepted-states")
  suspend fun getAcceptedStates(
    @Header("Authorization") bearerToken: String
  ): List<AcceptedState>

  /**
   * Updates the status of a specific issue report.
   *
   * Endpoint: **PUT /manager/issue-reports/{externalId}/status**
   *
   * This is a manager-only endpoint.
   *
   * @param bearerToken Authorization header value in the form `"Bearer <token>"`.
   * @param externalId The unique external identifier of the issue report.
   * @param request Request body containing the updated status information.
   * @return A [ResponseBody] indicating success or failure.
   */
  @PUT("manager/issue-reports/{externalId}/status")
  suspend fun updateManagerIssueReportStatus(
    @Header("Authorization") bearerToken: String,
    @Path("externalId") externalId: String,
    @Body request: IssueReportStatusUpdateRequest
  ): ResponseBody

  /**
   * Replaces the issue types for a specific issue report.
   *
   * Endpoint: **PUT /manager/issue-reports/{externalId}/issue-types**
   *
   * This is a manager-only endpoint.
   *
   * @param bearerToken Authorization header value in the form `"Bearer <token>"`.
   * @param externalId The unique external identifier of the issue report.
   * @param request Request body containing the new list of issue types.
   * @return A [ResponseBody] indicating success or failure.
   */
  @PUT("manager/issue-reports/{externalId}/issue-types")
  suspend fun replaceManagerIssueReportIssueTypes(
    @Header("Authorization") bearerToken: String,
    @Path("externalId") externalId: String,
    @Body request: IssueReportTypesUpdateRequest
  ): ResponseBody

  /**
   * Updates the manager status of a specific user.
   *
   * Endpoint: **PATCH /manager/users/{externalId}/manager-status**
   *
   * This is a manager-only endpoint.
   *
   * @param bearerToken Authorization header value in the form `"Bearer <token>"`.
   * @param externalId The unique external identifier of the user.
   * @param request Request body containing the new manager status.
   * @return The updated [UserProfileSummary] for the user.
   */
  @PATCH("manager/users/{externalId}/manager-status")
  suspend fun setManagerStatus(
    @Header("Authorization") bearerToken: String,
    @Path("externalId") externalId: UUID,
    @Body request: ManagerStatusUpdateRequest
  ): UserProfileSummary

  /**
   * Updates the enabled status of a specific user.
   *
   * Endpoint: **PATCH /manager/users/{externalId}/enabled**
   *
   * This is a manager-only endpoint.
   *
   * @param bearerToken Authorization header value in the form `"Bearer <token>"`.
   * @param externalId The unique external identifier of the user.
   * @param request Request body containing the new enabled status.
   * @return The updated [UserProfileSummary] for the user.
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
   * Endpoint: **POST /issue-reports**
   *
   * @param bearerToken Authorization header value in the form `"Bearer <token>"`.
   * @param request Request body containing the issue report details.
   * @return The newly created [IssueReport].
   */
  @POST("issue-reports")
  suspend fun submitIssueReport(
    @Header("Authorization") bearerToken: String,
    @Body request: IssueReportRequest
  ): IssueReport

  /**
   * Uploads an image to a specific issue report.
   *
   * Endpoint: **POST /issue-reports/{reportId}/images**
   *
   * @param bearerToken Authorization header value in the form `"Bearer <token>"`.
   * @param reportId The unique identifier of the issue report.
   * @param file Multipart file part containing the image.
   * @return The [ReportImageDto] containing information about the uploaded image.
   */
  @Multipart
  @POST("issue-reports/{reportId}/images")
  suspend fun uploadImage(
    @Header("Authorization") bearerToken: String,
    @Path("reportId") reportId: String,
    @Part file: MultipartBody.Part
  ): ReportImageDto

  /**
   * Downloads an image file from a specific issue report.
   *
   * Endpoint: **GET /issue-reports/{reportId}/images/{imageId}**
   *
   * @param bearerToken Authorization header value in the form `"Bearer <token>"`.
   * @param reportId The unique identifier of the issue report.
   * @param imageId The unique identifier of the image.
   * @return A [ResponseBody] containing the image file data.
   */
  @Streaming
  @GET("issue-reports/{reportId}/images/{imageId}")
  suspend fun downloadImageFile(
    @Header("Authorization") bearerToken: String,
    @Path("reportId") reportId: String,
    @Path("imageId") imageId: String
  ): ResponseBody

  /**
   * Deletes an image from a specific issue report.
   *
   * Endpoint: **DELETE /issue-reports/{reportId}/images/{imageId}**
   *
   * @param bearerToken Authorization header value in the form `"Bearer <token>"`.
   * @param reportId The unique identifier of the issue report.
   * @param imageId The unique identifier of the image to delete.
   * @return A [ResponseBody] indicating success or failure.
   */
  @DELETE("issue-reports/{reportId}/images/{imageId}")
  suspend fun deleteImage(
    @Header("Authorization") bearerToken: String,
    @Path("reportId") reportId: String,
    @Path("imageId") imageId: String
  ): ResponseBody

  /**
   * Retrieves a specific issue report by its ID.
   *
   * Endpoint: **GET /issue-reports/{reportId}**
   *
   * @param bearerToken Authorization header value in the form `"Bearer <token>"`.
   * @param reportId The unique identifier of the issue report.
   * @return The full [IssueReport] for the specified report.
   */
  @GET("issue-reports/{reportId}")
  suspend fun getIssueReport(
    @Header("Authorization") bearerToken: String,
    @Path("reportId") reportId: String
  ): IssueReport

  /**
   * Updates an existing issue report.
   *
   * Endpoint: **PUT /issue-reports/{reportId}**
   *
   * @param bearerToken Authorization header value in the form `"Bearer <token>"`.
   * @param reportId The unique identifier of the issue report to update.
   * @param request Request body containing the updated issue report details.
   * @return The updated [IssueReport].
   */
  @PUT("issue-reports/{reportId}")
  suspend fun updateIssueReport(
    @Header("Authorization") bearerToken: String,
    @Path("reportId") reportId: String,
    @Body request: IssueReportRequest
  ): IssueReport
}
