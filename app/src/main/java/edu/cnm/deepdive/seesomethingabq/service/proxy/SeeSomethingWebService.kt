package edu.cnm.deepdive.seesomethingabq.service.proxy

import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportRequest
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportSummary
import edu.cnm.deepdive.seesomethingabq.model.dto.PaginatedResponse
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import retrofit2.http.POST

interface SeeSomethingWebService {

  @GET("users/me")
  suspend fun getMe(@Header("Authorization") bearerToken: String): UserProfile

  @GET("issue-types")
  suspend fun getIssueTypes(@Header("Authorization") bearerToken: String): List<IssueType>

  @GET("issue-reports/mine")
  suspend fun getMyReports(@Header("Authorization") bearerToken: String): List<IssueReportSummary>

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

  @POST("issue-reports")
  suspend fun submitIssueReport(
    @Header("Authorization") bearerToken: String,
    @Body request: IssueReportRequest
  )

}
