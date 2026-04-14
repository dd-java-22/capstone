package edu.cnm.deepdive.seesomethingabq.service

import okhttp3.HttpUrl.Companion.toHttpUrl
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class UserServiceImplTest {

  @Test
  fun backendAvatarUrl_httpReturned_httpsBaseUrl_treatedAsProtected() {
    val baseUrl = "https://gilesvolmir.ddc-java.services/api/".toHttpUrl()
    val externalId = "00000000-0000-0000-0000-000000000000"
    val avatar = "http://gilesvolmir.ddc-java.services/api/users/$externalId/avatar".toHttpUrl()

    assertTrue(UserServiceImpl.isProtectedBackendAvatarUrl(baseUrl, avatar, externalId))
  }

  @Test
  fun backendAvatarUrl_defaultPortsDiffer_treatedAsProtected() {
    val baseUrl = "https://example.com/api/".toHttpUrl() // default 443
    val externalId = "11111111-1111-1111-1111-111111111111"
    val avatar = "http://example.com/api/users/$externalId/avatar".toHttpUrl() // default 80

    assertTrue(UserServiceImpl.isProtectedBackendAvatarUrl(baseUrl, avatar, externalId))
  }

  @Test
  fun externalAvatarUrl_treatedAsExternal() {
    val baseUrl = "https://example.com/api/".toHttpUrl()
    val externalId = "22222222-2222-2222-2222-222222222222"
    val avatar = "https://cdn.example.net/avatars/$externalId.png".toHttpUrl()

    assertFalse(UserServiceImpl.isProtectedBackendAvatarUrl(baseUrl, avatar, externalId))
  }

  @Test
  fun backendAvatarUrl_nonDefaultPortMismatch_notTreatedAsProtected() {
    val baseUrl = "https://example.com:8443/api/".toHttpUrl()
    val externalId = "33333333-3333-3333-3333-333333333333"
    val avatar = "https://example.com/api/users/$externalId/avatar".toHttpUrl() // default 443

    assertFalse(UserServiceImpl.isProtectedBackendAvatarUrl(baseUrl, avatar, externalId))
  }

}
