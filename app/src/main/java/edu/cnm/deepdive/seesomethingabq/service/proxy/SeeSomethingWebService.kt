package edu.cnm.deepdive.seesomethingabq.service.proxy

import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportSummary
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile
import retrofit2.http.GET
import retrofit2.http.Header

interface SeeSomethingWebService {

  @GET("users/me")
  suspend fun getMe(@Header("Authorization") bearerToken: String): UserProfile

  @GET("issue-types")
  suspend fun getIssueTypes(@Header("Authorization") bearerToken: String): List<IssueType>

  @GET("issue-reports/mine")
  suspend fun getMyReports(@Header("Authorization") bearerToken: String): List<IssueReportSummary>

  @GET("manager/issue-reports")
  suspend fun getAllReportsPaged(@Header("Authorization") bearerToken: String): List<IssueReportSummary>

}
