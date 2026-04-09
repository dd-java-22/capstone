/*
 *  Copyright 2026 CNM Ingenuity, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package edu.cnm.deepdive.seesomethingabq.viewmodel;

import android.app.Activity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;
import dagger.hilt.android.lifecycle.HiltViewModel;
import edu.cnm.deepdive.seesomethingabq.model.dto.UserProfileSummary;
import edu.cnm.deepdive.seesomethingabq.service.ManagerUserService;
import jakarta.inject.Inject;

/**
 * ViewModel exposing a paged list of manager-visible users.
 */
@HiltViewModel
public class ManageUsersViewModel extends ViewModel {

  private final ManagerUserService managerUserService;

  private LiveData<PagingData<UserProfileSummary>> users;

  @Inject
  ManageUsersViewModel(ManagerUserService managerUserService) {
    this.managerUserService = managerUserService;
  }

  /**
   * Returns a paged stream of user profile summaries.
   *
   * @param activity activity used for authentication flows.
   * @return live data stream of paging data.
   */
  public LiveData<PagingData<UserProfileSummary>> getUsers(Activity activity) {
    if (users == null) {
      users = PagingLiveData.getLiveData(managerUserService.getManagerUsersPager(activity));
    }
    return users;
  }

}

