package edu.cnm.deepdive.seesomethingabq.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.cnm.deepdive.seesomethingabq.model.dto.UserProfileSummary
import edu.cnm.deepdive.seesomethingabq.service.ManagerUserService
import jakarta.inject.Inject

/**
 * ViewModel exposing a paged list of manager-visible users.
 *
 * Uses [cachedIn] to ensure the paging flow can be collected across lifecycle restarts
 * (e.g., navigating away to detail and back) without triggering Paging's double-collection error.
 */
@HiltViewModel
class ManageUsersViewModel @Inject constructor(
  private val managerUserService: ManagerUserService
) : ViewModel() {

  private var users: LiveData<PagingData<UserProfileSummary>>? = null

  /**
   * Returns a cached paged list of manager-visible users.
   *
   * @param activity activity used for authentication flows.
   * @return live data stream of paging data for users.
   */
  fun getUsers(activity: Activity): LiveData<PagingData<UserProfileSummary>> {
    if (users == null) {
      users = managerUserService
        .getManagerUsersPager(activity)
        .flow
        .cachedIn(viewModelScope)
        .asLiveData()
    }
    return users!!
  }

}

