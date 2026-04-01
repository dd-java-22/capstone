package edu.cnm.deepdive.seesomethingabq.viewmodel;

import android.app.Activity;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
import edu.cnm.deepdive.seesomethingabq.service.UserService;
import jakarta.inject.Inject;
import java.util.function.BiConsumer;
import org.jetbrains.annotations.NotNull;

@HiltViewModel
public class UserViewModel extends ViewModel {

  private static final String TAG = UserViewModel.class.getSimpleName();

  private final UserService userService;

  private final MutableLiveData<UserProfile> user;
  private final MutableLiveData<Throwable> throwable;

  @Inject
  UserViewModel(UserService userService) {
    this.userService = userService;

    user = new MutableLiveData<>();
    throwable = new MutableLiveData<>();

    // TODO: 3/31/2026 explore starting log in here
  }

  public LiveData<UserProfile> getUser() {
    return user;
  }

  public LiveData<Throwable> getThrowable() {
    return throwable;
  }

  public void signIn(Activity activity) {
    throwable.setValue(null);

    userService.signIn(activity)
        .whenComplete(this::handleResult);
  }

  public void signOut() {
    throwable.setValue(null);

    userService.signOut()
        .whenComplete((ignored, throwable) -> {
          if (throwable != null) {
            postThrowable(throwable);
          }

          user.postValue(null);
        });
  }

  private void handleResult(UserProfile user, Throwable throwable) {
      if (throwable == null) {
        this.user.postValue(user);
      } else {
        postThrowable(throwable);
      }
    }

  private void postThrowable(Throwable throwable) {
    Log.e(TAG, throwable.getMessage(), throwable);
    this.throwable.postValue(throwable);
  }
}
