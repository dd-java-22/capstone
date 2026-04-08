package edu.cnm.deepdive.seesomethingabq.service

import android.app.Activity
import androidx.lifecycle.LiveData
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType
import java.util.concurrent.CompletableFuture

/**
 * Service providing cached issue types backed by the server API and local persistence.
 */
interface IssueTypeService {

  /**
   * Refreshes issue types from the server API and updates local storage.
   *
   * @param activity activity used for authentication flows.
   * @return future completing with the refreshed issue type list.
   */
  fun refresh(activity: Activity): CompletableFuture<List<IssueType>>

  /**
   * Returns observable issue types from local storage.
   *
   * @return live data stream of issue types.
   */
  fun getIssueTypes(): LiveData<List<IssueType>>

}

