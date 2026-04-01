package edu.cnm.deepdive.seesomethingabq.service

import android.app.Activity
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
import java.util.concurrent.CompletableFuture

@Singleton
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
}