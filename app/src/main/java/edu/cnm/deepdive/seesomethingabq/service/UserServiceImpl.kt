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

@Singleton
/**
 * Default [UserService] implementation combining Google auth and server profile retrieval.
 */
class UserServiceImpl @Inject constructor(
  private val authRepository: GoogleAuthRepository,
  private val webService: SeeSomethingWebService,
  private val userDao: UserDao
) : UserService {

  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

  override fun signIn(activity: Activity): CompletableFuture<UserProfile> =
    scope.future {
      val credential = try {
        authRepository.signInQuickly(activity).await()
      } catch (e: GoogleAuthRepository.SignInRequiredException) {
        authRepository.signIn(activity).await()
      }

      val oauthKey = credential.id

      val serverUser = webService
        .getMe("Bearer ${credential.idToken}")
        .copy(oauthKey = oauthKey)

      userDao.getByOauthKey(oauthKey) ?: serverUser.copy(id = userDao.insert(serverUser))
    }

  override fun signOut(): CompletableFuture<Void?> = authRepository.signOut()

  override fun updateProfile(
    activity: Activity,
    displayName: String?,
    email: String?
  ): CompletableFuture<UserProfile> =
    scope.future {
      val credential = authRepository.getValidCredential(activity).await()
      val request = UpdateUserRequest(displayName, email)

      val updatedUser = webService
        .updateUserProfile("Bearer ${credential.idToken}", request)
        .copy(oauthKey = credential.id)

      userDao.update(updatedUser)
      updatedUser
    }

  override fun uploadAvatar(
    activity: Activity,
    uri: Uri
  ): CompletableFuture<UserProfile> =
    scope.future {
      val credential = authRepository.getValidCredential(activity).await()

      val inputStream = activity.contentResolver.openInputStream(uri)
        ?: throw IllegalArgumentException("Cannot open input stream for URI: $uri")

      inputStream.use { input ->
        val bytes = input.readBytes()
        val requestBody = bytes.toRequestBody("image/*".toMediaType())
        val part = MultipartBody.Part.createFormData("file", "avatar.jpg", requestBody)

        val updatedUser = webService
          .uploadUserAvatar("Bearer ${credential.idToken}", part)
          .copy(oauthKey = credential.id)

        userDao.update(updatedUser)
        updatedUser
      }
    }
}
