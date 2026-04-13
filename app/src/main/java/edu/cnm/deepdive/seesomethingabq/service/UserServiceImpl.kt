package edu.cnm.deepdive.seesomethingabq.service

import android.app.Activity
import android.net.Uri
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
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
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

      // Cache locally or return existing cached record
      userDao.getByOauthKey(oauthKey) ?: serverUser.copy(id = userDao.insert(serverUser))
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
        val requestBody = bytes.toRequestBody("image/*".toMediaType())

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

}
