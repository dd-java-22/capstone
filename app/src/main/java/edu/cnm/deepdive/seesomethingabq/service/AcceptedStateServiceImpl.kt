package edu.cnm.deepdive.seesomethingabq.service

import android.app.Activity
import androidx.lifecycle.LiveData
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import edu.cnm.deepdive.seesomethingabq.model.entity.AcceptedState
import edu.cnm.deepdive.seesomethingabq.service.dao.AcceptedStateDao
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

/**
 * Default implementation of [AcceptedStateService] backed by the API and Room persistence.
 */
@Singleton
class AcceptedStateServiceImpl @Inject constructor(
  private val authRepository: GoogleAuthRepository,
  private val webService: SeeSomethingWebService,
  private val acceptedStateDao: AcceptedStateDao
) : AcceptedStateService {

  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

  override fun refresh(activity: Activity): CompletableFuture<List<AcceptedState>> =
    scope.future {
      val credential = getCredential(activity)
      val acceptedStates = webService.getAcceptedStates("Bearer ${credential.idToken}")
      acceptedStateDao.replaceAll(acceptedStates)
      acceptedStates
    }

  override fun getAcceptedStates(): LiveData<List<AcceptedState>> = acceptedStateDao.getAll()

  private suspend fun getCredential(activity: Activity): GoogleIdTokenCredential =
    authRepository.getValidCredential(activity).await()

}

