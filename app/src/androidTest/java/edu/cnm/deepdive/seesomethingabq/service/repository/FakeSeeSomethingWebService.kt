package edu.cnm.deepdive.seesomethingabq.service.repository

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
}

