package edu.cnm.deepdive.seesomethingabq.service

import android.app.Activity
import android.net.Uri
import edu.cnm.deepdive.seesomethingabq.R
import edu.cnm.deepdive.seesomethingabq.model.dto.UpdateUserRequest
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile
import edu.cnm.deepdive.seesomethingabq.service.dao.UserDao
import edu.cnm.deepdive.seesomethingabq.service.proxy.SeeSomethingWebService
import edu.cnm.deepdive.seesomethingabq.service.repository.GoogleAuthRepository
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.future.await
import kotlinx.coroutines.future.future
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.buffer
import okio.sink
import java.io.File
import java.util.concurrent.CompletableFuture

/**
 * Default implementation of [UserService], combining Google authentication,
 * backend profile operations, and local persistence through [UserDao].
 *
 * This service:
 * - Performs Google OAuth sign‑in and token retrieval.
 * - Retrieves and updates user profile data from the backend.
 * - Uploads avatar images as multipart form data.
 * - Caches user profiles locally using Room.
 * - Exposes all operations as [CompletableFuture] for ViewModel compatibility.
 *
 * All network operations run on a dedicated IO coroutine scope and are bridged
 * to Java futures using `scope.future { ... }`.
 */
@Singleton
class UserServiceImpl @Inject constructor(
  private val authRepository: GoogleAuthRepository,
  private val webService: SeeSomethingWebService,
  private val userDao: UserDao
) : UserService {

  /** Coroutine scope for IO‑bound asynchronous operations. */
  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

  /**
   * Signs the user in using Google OAuth. Attempts a silent sign‑in first,
   * falling back to interactive sign‑in if required. After authentication,
   * retrieves the user's profile from the backend and stores it locally.
   *
   * @param activity Activity used to launch Google sign‑in flows.
   * @return Future completing with the authenticated [UserProfile].
   */
  override fun signIn(activity: Activity): CompletableFuture<UserProfile> =
    scope.future {
      val credential = try {
        authRepository.signInQuickly(activity).await()
      } catch (e: GoogleAuthRepository.SignInRequiredException) {
        authRepository.signIn(activity).await()
      }

      val oauthKey = credential.id

      // Fetch user profile from backend
      val serverUser = webService
        .getMe("Bearer ${credential.idToken}")
        .copy(oauthKey = oauthKey)

      // Always refresh the local cached profile from server truth on sign-in, while preserving the
      // local Room primary key if this user has been seen before.
      refreshCachedUserFromServer(userDao, oauthKey, serverUser)
    }

  /**
   * Signs the user out of Google authentication.
   *
   * @return Future completing when sign‑out is finished.
   */
  override fun signOut(): CompletableFuture<Void?> =
    authRepository.signOut()

  /**
   * Updates the user's profile information (display name and/or email).
   * Sends the update request to the backend, updates the local cache,
   * and returns the updated profile.
   *
   * @param activity Activity used to refresh Google credentials if needed.
   * @param displayName New display name (nullable if unchanged).
   * @param email New email address (nullable if unchanged).
   * @return Future completing with the updated [UserProfile].
   */
  override fun updateProfile(
    activity: Activity,
    displayName: String?,
    email: String?
  ): CompletableFuture<UserProfile> =
    scope.future {
      val credential = authRepository.getValidCredential(activity).await()
      val request = UpdateUserRequest(displayName, email)

      // Get existing user from local database to preserve the Room ID
      val existingUser = userDao.getByOauthKey(credential.id)
        ?: throw IllegalStateException("User not found in local database")

      val updatedUser = webService
        .updateUserProfile("Bearer ${credential.idToken}", request)
        .copy(
          oauthKey = credential.id,
          id = existingUser.id  // Preserve the local database ID
        )

      userDao.update(updatedUser)
      updatedUser
    }

  /**
   * Uploads a new avatar image for the current user.
   *
   * Reads the image bytes from the provided [Uri], wraps them in a multipart
   * request body, and sends them to the backend using the expected form field
   * name `"avatar"`. After a successful upload, the updated user profile is
   * cached locally and returned.
   *
   * @param activity Activity used to refresh Google credentials if needed.
   * @param uri URI of the image to upload.
   * @return Future completing with the updated [UserProfile].
   *
   * @throws IllegalArgumentException if the URI cannot be opened.
   */
  override fun uploadAvatar(
    activity: Activity,
    uri: Uri
  ): CompletableFuture<UserProfile> =
    scope.future {
      val credential = authRepository.getValidCredential(activity).await()

      // Get existing user from local database to preserve the Room ID
      val existingUser = userDao.getByOauthKey(credential.id)
        ?: throw IllegalStateException("User not found in local database")

      val inputStream = activity.contentResolver.openInputStream(uri)
        ?: throw IllegalArgumentException("Cannot open input stream for URI: $uri")

      inputStream.use { input ->
        val bytes = input.readBytes()
        val resolvedContentType = activity.contentResolver.getType(uri) ?: "image/jpeg"
        val requestBody = bytes.toRequestBody(resolvedContentType.toMediaType())

        // IMPORTANT: Backend expects the field name "avatar"
        val part = MultipartBody.Part.createFormData(
          "avatar",          // <-- FIXED FIELD NAME
          "avatar.jpg",
          requestBody
        )

        // Upload avatar, then re-fetch canonical profile so avatarUrl reflects the server-backed
        // endpoint (e.g., /users/{externalId}/avatar) after successful replacement.
        webService.uploadUserAvatar("Bearer ${credential.idToken}", part)

        val refreshedUser = webService
          .getMe("Bearer ${credential.idToken}")
          .copy(
            oauthKey = credential.id,
            id = existingUser.id  // Preserve the local database ID
          )

        userDao.update(refreshedUser)
        refreshedUser
      }
    }

  override fun resolveAvatarUri(
    activity: Activity,
    user: UserProfile
  ): CompletableFuture<Uri?> =
    scope.future {
      val avatarUrl = user.avatar?.toString()?.trim()
      if (avatarUrl.isNullOrBlank()) {
        return@future null
      }

      if (!isProtectedBackendAvatarUrl(activity, avatarUrl, user.externalId.toString())) {
        return@future Uri.parse(avatarUrl)
      }

      val credential = authRepository.getValidCredential(activity).await()

      val cacheDir = File(activity.cacheDir, "avatars")
      //noinspection ResultOfMethodCallIgnored
      cacheDir.mkdirs()
      val cacheFile = File(cacheDir, "avatar-${user.externalId}.bin")

      val responseBody = webService.downloadUserAvatar("Bearer ${credential.idToken}", user.externalId)

      // Ensure the cached avatar file content changes (and its modification timestamp updates) so
      // downstream image caches can reliably detect a refresh for deterministic file paths.
      if (cacheFile.exists()) {
        //noinspection ResultOfMethodCallIgnored
        cacheFile.delete()
      }
      cacheFile.sink().buffer().use { sink ->
        sink.writeAll(responseBody.source())
      }
      // Best-effort: in case the filesystem doesn't update mtime as expected.
      //noinspection ResultOfMethodCallIgnored
      cacheFile.setLastModified(System.currentTimeMillis())

      Uri.fromFile(cacheFile)
    }

  private fun isProtectedBackendAvatarUrl(activity: Activity, url: String, externalId: String): Boolean {
    val baseUrl = activity.getString(R.string.base_url).toHttpUrlOrNull() ?: return false
    val avatar = url.toHttpUrlOrNull() ?: return false
    return isProtectedBackendAvatarUrl(baseUrl, avatar, externalId)
  }

  companion object {

    /**
     * Determines whether [avatar] points to our backend's protected avatar endpoint for [externalId].
     *
     * We intentionally ignore URL scheme and default-port differences when identifying backend avatar
     * URLs. In some deployments the server may emit `http://host/...` avatar URLs even though the
     * app is configured with an `https://host/...` Retrofit base URL (and vice versa). Since avatar
     * fetching must go through the authenticated Retrofit client, we detect backend avatar URLs by:
     * - Matching backend host.
     * - Matching the expected `/users/{externalId}/avatar` path (after any base path prefix).
     * - Allowing mismatch between default ports (80 for http, 443 for https).
     *
     * Non-default ports must still match exactly to avoid misclassifying unrelated services.
     */
    internal fun isProtectedBackendAvatarUrl(baseUrl: HttpUrl, avatar: HttpUrl, externalId: String): Boolean {
      val scheme = avatar.scheme
      if (scheme != "https" && scheme != "http") {
        return false
      }
      if (avatar.host != baseUrl.host) {
        return false
      }
      if (!arePortsCompatibleIgnoringDefaults(baseUrl, avatar)) {
        return false
      }

      // baseUrl may include a path prefix (e.g., /api/); avatar should share it.
      val baseSegments = baseUrl.encodedPathSegments.filter { it.isNotBlank() }
      val avatarSegments = avatar.encodedPathSegments.filter { it.isNotBlank() }

      if (avatarSegments.size < baseSegments.size + 3) {
        return false
      }
      if (avatarSegments.subList(0, baseSegments.size) != baseSegments) {
        return false
      }
      val remaining = avatarSegments.subList(baseSegments.size, avatarSegments.size)
      return remaining.size == 3
        && remaining[0] == "users"
        && remaining[1] == externalId
        && remaining[2] == "avatar"
    }

    private fun arePortsCompatibleIgnoringDefaults(baseUrl: HttpUrl, avatar: HttpUrl): Boolean {
      if (baseUrl.port == avatar.port) {
        return true
      }
      // Allow https:443 vs http:80 mismatch when both are default for their scheme.
      return isDefaultPort(baseUrl) && isDefaultPort(avatar)
    }

    private fun isDefaultPort(url: HttpUrl): Boolean =
      when (url.scheme) {
        "http" -> url.port == 80
        "https" -> url.port == 443
        else -> false
      }

    internal suspend fun refreshCachedUserFromServer(
      userDao: UserDao,
      oauthKey: String,
      serverUser: UserProfile
    ): UserProfile {
      val existingUser = userDao.getByOauthKey(oauthKey)
      return if (existingUser == null) {
        serverUser.copy(id = userDao.insert(serverUser))
      } else {
        // Currently, all fields in UserProfile are server-backed except the local Room PK and oauthKey.
        // If we add local-only fields in the future, merge them here instead of overwriting.
        val refreshed = serverUser.copy(id = existingUser.id)
        userDao.update(refreshed)
        refreshed
      }
    }

  }

}
