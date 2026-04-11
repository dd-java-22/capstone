package edu.cnm.deepdive.seesomethingabq.viewmodel;

import android.app.Activity;
import android.net.Uri;
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

/**
 * ViewModel coordinating user sign-in/sign-out and exposing user and error state.
 */
@HiltViewModel
public class UserViewModel extends ViewModel {

  private static final String TAG = UserViewModel.class.getSimpleName();

  private final UserService userService;

  private final MutableLiveData<UserProfile> user;
  private final MutableLiveData<Throwable> throwable;

  /**
   * Creates a ViewModel using the provided user service.
   *
   * @param userService user service.
   */
  @Inject
  UserViewModel(UserService userService) {
    this.userService = userService;

    user = new MutableLiveData<>();
    throwable = new MutableLiveData<>();

    // TODO: 3/31/2026 explore starting log in here
  }

  /**
   * Returns the current user profile, if signed in.
   *
   * @return live data stream of user profiles.
   */
  public LiveData<UserProfile> getUser() {
    return user;
  }

  /**
   * Returns the most recent error, if any.
   *
   * @return live data stream of errors.
   */
  public LiveData<Throwable> getThrowable() {
    return throwable;
  }

  /**
   * Signs the user in and publishes the resulting profile.
   *
   * @param activity activity used to launch sign-in flows.
   */
  public void signIn(Activity activity) {
    throwable.setValue(null);

    userService.signIn(activity)
        .whenComplete(this::handleResult);
  }

  /**
   * Signs the user out and clears the current user.
   */
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


  public void updateProfile(String displayName, String email) {
    repository.updateUser(displayName, email)
        .thenAccept(updated -> user.postValue(updated));
  }

  public void updateAvatar(Activity activity, Uri uri) {
    repository.uploadAvatar(activity, uri)
        .thenAccept(updated -> user.postValue(updated));
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
