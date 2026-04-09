package edu.cnm.deepdive.seesomethingabq.service.proxy

import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportRequest
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportSummary
import edu.cnm.deepdive.seesomethingabq.model.dto.PaginatedResponse
import edu.cnm.deepdive.seesomethingabq.model.dto.UserProfileSummary
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import retrofit2.http.POST

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
