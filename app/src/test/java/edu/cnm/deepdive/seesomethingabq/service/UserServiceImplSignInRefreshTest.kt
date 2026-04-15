package edu.cnm.deepdive.seesomethingabq.service

import androidx.lifecycle.LiveData
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile
import edu.cnm.deepdive.seesomethingabq.service.dao.UserDao
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.net.URL
import java.time.Instant
import java.util.UUID

class UserServiceImplSignInRefreshTest {

  @Test
  fun refreshCachedUserFromServer_insertsWhenMissing() = runBlocking {
    val dao = FakeUserDao()

    val oauthKey = "oauth-123"
    val serverUser = sampleUser(oauthKey = oauthKey, reportCount = 42)

    val refreshed = UserServiceImpl.refreshCachedUserFromServer(dao, oauthKey, serverUser)

    assertTrue(refreshed.id > 0)
    assertEquals(42, refreshed.reportCount)
    val cached = dao.getByOauthKey(oauthKey)
    assertNotNull(cached)
    assertEquals(refreshed, cached)
    assertEquals(1, dao.insertCalls)
    assertEquals(0, dao.updateCalls)
  }

  @Test
  fun refreshCachedUserFromServer_updatesWhenPresent_preservesRoomId() = runBlocking {
    val dao = FakeUserDao()

    val oauthKey = "oauth-456"
    val existing = sampleUser(oauthKey = oauthKey, reportCount = 1).copy(id = 7)
    dao.seed(existing)

    val serverUser = sampleUser(oauthKey = oauthKey, reportCount = 99)

    val refreshed = UserServiceImpl.refreshCachedUserFromServer(dao, oauthKey, serverUser)

    assertEquals(7, refreshed.id, "Room primary key should be preserved")
    assertEquals(99, refreshed.reportCount, "Server-backed fields should be refreshed from server")
    assertNotEquals(existing.reportCount, refreshed.reportCount)
    assertEquals(0, dao.insertCalls)
    assertEquals(1, dao.updateCalls)
    assertEquals(refreshed, dao.getByOauthKey(oauthKey))
  }

  private fun sampleUser(oauthKey: String, reportCount: Long): UserProfile =
    UserProfile(
      id = 0,
      externalId = UUID.fromString("00000000-0000-0000-0000-000000000001"),
      displayName = "Alice",
      oauthKey = oauthKey,
      email = "alice@example.com",
      avatar = URL("https://cdn.example.net/avatar.png"),
      manager = false,
      timeCreated = Instant.EPOCH,
      userEnabled = true,
      reportCount = reportCount,
    )

  private class FakeUserDao : UserDao {

    private val usersByOauthKey = linkedMapOf<String, UserProfile>()
    private var nextId = 1L

    var insertCalls: Int = 0
      private set
    var updateCalls: Int = 0
      private set

    fun seed(user: UserProfile) {
      usersByOauthKey[user.oauthKey] = user
      nextId = maxOf(nextId, user.id + 1)
    }

    override suspend fun insert(userProfile: UserProfile): Long {
      insertCalls++
      val id = nextId++
      usersByOauthKey[userProfile.oauthKey] = userProfile.copy(id = id)
      return id
    }

    override suspend fun update(userProfile: UserProfile) {
      updateCalls++
      usersByOauthKey[userProfile.oauthKey] = userProfile
    }

    override suspend fun delete(userProfile: UserProfile) {
      usersByOauthKey.remove(userProfile.oauthKey)
    }

    override fun getById(id: Long): LiveData<UserProfile> =
      throw UnsupportedOperationException("Not needed for these tests")

    override suspend fun getByOauthKey(oauthKey: String): UserProfile? =
      usersByOauthKey[oauthKey]

    override suspend fun getByExternalId(externalId: UUID): UserProfile? =
      throw UnsupportedOperationException("Not needed for these tests")

    override suspend fun getByDisplayName(displayName: String): UserProfile? =
      throw UnsupportedOperationException("Not needed for these tests")
  }

}
