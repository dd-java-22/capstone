package edu.cnm.deepdive.seesomethingabq.service

import android.app.Activity
import androidx.lifecycle.LiveData
import edu.cnm.deepdive.seesomethingabq.model.entity.AcceptedState
import java.util.concurrent.CompletableFuture

/**
 * Service providing cached accepted states backed by the server API and local persistence.
 */
interface AcceptedStateService {

  /**
   * Refreshes accepted states from the server and updates local storage.
   *
   * @param activity activity used for authentication flows.
   * @return future completed with the refreshed list of accepted states.
   */
  fun refresh(activity: Activity): CompletableFuture<List<AcceptedState>>

  /**
   * Returns the locally cached list of accepted states.
   *
   * @return live data stream of accepted states.
   */
  fun getAcceptedStates(): LiveData<List<AcceptedState>>

}

