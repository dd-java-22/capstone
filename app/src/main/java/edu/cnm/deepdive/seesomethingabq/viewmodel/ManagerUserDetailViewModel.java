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
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import edu.cnm.deepdive.seesomethingabq.model.dto.UserProfileSummary;
import edu.cnm.deepdive.seesomethingabq.service.ManagerUserService;
import jakarta.inject.Inject;
import java.util.UUID;

/**
 * ViewModel responsible for loading and exposing a manager-visible user profile.
 */
@HiltViewModel
public class ManagerUserDetailViewModel extends ViewModel {

  private static final String TAG = ManagerUserDetailViewModel.class.getSimpleName();

  private final ManagerUserService managerUserService;

  private final MutableLiveData<UserProfileSummary> user;
  private final MutableLiveData<Throwable> throwable;

  @Inject
  ManagerUserDetailViewModel(ManagerUserService managerUserService) {
    this.managerUserService = managerUserService;
    user = new MutableLiveData<>();
    throwable = new MutableLiveData<>();
  }

  public LiveData<UserProfileSummary> getUser() {
    return user;
  }

  public LiveData<Throwable> getThrowable() {
    return throwable;
  }

  public void load(Activity activity, UUID externalId) {
    throwable.setValue(null);
    managerUserService.getManagerUser(activity, externalId)
        .whenComplete((user, throwable) -> {
          if (throwable == null) {
            this.user.postValue(user);
          } else {
            postThrowable(throwable);
          }
        });
  }

  public void setManagerStatus(Activity activity, UUID externalId, boolean isManager) {
    throwable.setValue(null);
    managerUserService.setManagerStatus(activity, externalId, isManager)
        .thenCompose((updated) -> managerUserService
            .getManagerUser(activity, externalId)
            // If refresh fails, keep the server-confirmed mutation response.
            .exceptionally((ignored) -> updated))
        .whenComplete((user, throwable) -> {
          if (throwable == null) {
            this.user.postValue(user);
          } else {
            postThrowable(throwable);
          }
        });
  }

  public void setEnabledStatus(Activity activity, UUID externalId, boolean isEnabled) {
    throwable.setValue(null);
    managerUserService.setEnabledStatus(activity, externalId, isEnabled)
        .thenCompose((updated) -> managerUserService
            .getManagerUser(activity, externalId)
            // If refresh fails, keep the server-confirmed mutation response.
            .exceptionally((ignored) -> updated))
        .whenComplete((user, throwable) -> {
          if (throwable == null) {
            this.user.postValue(user);
          } else {
            postThrowable(throwable);
          }
        });
  }

  private void postThrowable(Throwable throwable) {
    Log.e(TAG, throwable.getMessage(), throwable);
    this.throwable.postValue(throwable);
  }

}

