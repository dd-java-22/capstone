package edu.cnm.deepdive.seesomethingabq.service.proxy

import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportRequest
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportSummary
import edu.cnm.deepdive.seesomethingabq.model.dto.ManagerStatusUpdateRequest
import edu.cnm.deepdive.seesomethingabq.model.dto.PaginatedResponse
import edu.cnm.deepdive.seesomethingabq.model.dto.UserEnabledUpdateRequest
import edu.cnm.deepdive.seesomethingabq.model.dto.UserProfileSummary
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.POST
import java.util.UUID

/**
 * Retrofit interface for the SeeSomethingABQ server API.
 */
interface SeeSomethingWebService {

  /**
   * Retrieves the current user's profile from the server.
   *
   * @param bearerToken Authorization header value.
   * @return user profile.
   */
  @GET("users/me")
  suspend fun getMe(@Header("Authorization") bearerToken: String): UserProfile

  /**
   * Retrieves all issue types from the server.
   *
   * @param bearerToken Authorization header value.
   * @return list of issue types.
   */
  @GET("issue-types")
  suspend fun getIssueTypes(@Header("Authorization") bearerToken: String): List<IssueType>

  /**
   * Retrieves the current user's issue report summaries.
   *
   * @param bearerToken Authorization header value.
   * @return list of report summaries.
   */
  @GET("issue-reports/mine")
  suspend fun getMyReports(@Header("Authorization") bearerToken: String): List<IssueReportSummary>

  /**
   * Retrieves a page of issue report summaries for manager views.
   *
   * @param bearerToken Authorization header value.
   * @param page zero-based page number.
   * @param size page size.
   * @return paginated response of report summaries.
   */
  @GET("manager/issue-reports")
  suspend fun getIssueReportsPage(
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
  )

}
