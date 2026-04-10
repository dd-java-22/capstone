package edu.cnm.deepdive.seesomethingabq.service

import android.app.Activity
import androidx.lifecycle.LiveData
import edu.cnm.deepdive.seesomethingabq.model.entity.AcceptedState
import java.util.concurrent.CompletableFuture

/**
 * Service providing cached accepted states backed by the server API and local persistence.
 */
interface AcceptedStateService {

  fun refresh(activity: Activity): CompletableFuture<List<AcceptedState>>

  fun getAcceptedStates(): LiveData<List<AcceptedState>>

}

