package edu.cnm.deepdive.seesomethingabq.service

import android.app.Activity
import androidx.lifecycle.LiveData
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType
import edu.cnm.deepdive.seesomethingabq.service.dao.IssueTypeDao
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
/**
 * Default [IssueTypeService] implementation backed by [SeeSomethingWebService] and [IssueTypeDao].
 */
class IssueTypeServiceImpl @Inject constructor(
  private val authRepository: GoogleAuthRepository,
  private val webService: SeeSomethingWebService,
  private val issueTypeDao: IssueTypeDao
) : IssueTypeService {

  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

  override fun refresh(activity: Activity): CompletableFuture<List<IssueType>> =
    scope.future {
      val credential = getCredential(activity)
      val issueTypes = webService.getIssueTypes("Bearer ${credential.idToken}")
      issueTypeDao.replaceAll(issueTypes)
      issueTypes
    }

  override fun getIssueTypes(): LiveData<List<IssueType>> = issueTypeDao.getAll()

  private suspend fun getCredential(activity: Activity): GoogleIdTokenCredential =
    authRepository.getValidCredential(activity).await()

}

