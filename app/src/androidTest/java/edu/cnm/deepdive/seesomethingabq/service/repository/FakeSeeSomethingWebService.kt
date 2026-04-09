package edu.cnm.deepdive.seesomethingabq.service.repository

import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportRequest
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportSummary
import edu.cnm.deepdive.seesomethingabq.model.dto.ManagerStatusUpdateRequest
import edu.cnm.deepdive.seesomethingabq.model.dto.PaginatedResponse
import edu.cnm.deepdive.seesomethingabq.model.dto.UserEnabledUpdateRequest
import edu.cnm.deepdive.seesomethingabq.model.dto.UserProfileSummary
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile
import edu.cnm.deepdive.seesomethingabq.service.proxy.SeeSomethingWebService
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.time.Instant
import java.util.UUID

@Singleton
class FakeSeeSomethingWebService @Inject constructor() : SeeSomethingWebService {

  var failGetMe: Boolean = false

  var fakeEmail: String = "test@example.com"
  var fakeDisplayName: String = "Test User"
  var fakeManager: Boolean = false
  var fakeUserEnabled: Boolean = true

  override suspend fun getMe(bearerToken: String): UserProfile {
    if (failGetMe) {
      throw IllegalStateException("Simulated getMe failure")
    }

    // Deterministic ID derived from token, so tests can assert consistently.
    val externalId = UUID.nameUUIDFromBytes(bearerToken.toByteArray())

    return UserProfile(
      externalId = externalId,
      displayName = fakeDisplayName,
      oauthKey = "",
      email = fakeEmail,
      avatar = null,
      manager = fakeManager,
      timeCreated = Instant.EPOCH,
      userEnabled = fakeUserEnabled,
    )
  }

  override suspend fun getIssueTypes(bearerToken: String): List<IssueType> {
    return emptyList()
  }

  override suspend fun getMyReports(bearerToken: String): List<IssueReportSummary> {
    return emptyList()
  }

  override suspend fun getIssueReportsPage(
    bearerToken: String,
    page: Int,
    size: Int
  ): PaginatedResponse<IssueReportSummary> {
    return PaginatedResponse(
      totalElements = 0,
      totalPages = 0,
      content = emptyList(),
      size = 0,
      number = 0,
      last = true,
      first = true,
    )
  }

  override suspend fun getManagerUsersPage(
    bearerToken: String,
    page: Int,
    size: Int
  ): PaginatedResponse<UserProfileSummary> {
    return PaginatedResponse(
      totalElements = 0,
      totalPages = 0,
      content = emptyList(),
      size = size,
      number = page,
      last = true,
      first = page == 0,
    )
  }

  override suspend fun getManagerUser(bearerToken: String, externalId: UUID): UserProfileSummary {
    return UserProfileSummary(
      externalId = externalId,
      displayName = fakeDisplayName,
      email = fakeEmail,
      avatar = null,
      manager = fakeManager,
      timeCreated = Instant.EPOCH,
      userEnabled = fakeUserEnabled,
    )
  }

  override suspend fun setManagerStatus(
    bearerToken: String,
    externalId: UUID,
    request: ManagerStatusUpdateRequest
  ): UserProfileSummary {
    fakeManager = request.manager
    return getManagerUser(bearerToken, externalId)
  }

  override suspend fun setEnabledStatus(
    bearerToken: String,
    externalId: UUID,
    request: UserEnabledUpdateRequest
  ): UserProfileSummary {
    fakeUserEnabled = request.enabled
    return getManagerUser(bearerToken, externalId)
  }

  override suspend fun submitIssueReport(bearerToken: String, request: IssueReportRequest) {
  }
}

