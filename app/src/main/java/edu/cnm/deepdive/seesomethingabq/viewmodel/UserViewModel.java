package edu.cnm.deepdive.seesomethingabq.viewmodel;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import edu.cnm.deepdive.seesomethingabq.model.entity.UserProfile;
import edu.cnm.deepdive.seesomethingabq.service.UserService;
import jakarta.inject.Inject;

/**
 * ViewModel coordinating user sign-in/sign-out and exposing user and error state.
 * <p>
 * This ViewModel also manages profile updates, including display name, email,
 * and avatar image uploads. All operations delegate to {@link UserService},
 * and results are published to LiveData streams for UI observation.
 */
@HiltViewModel
public class UserViewModel extends ViewModel {

  private static final String TAG = UserViewModel.class.getSimpleName();

  private final UserService userService;

  private final MutableLiveData<UserProfile> user;
  private final MutableLiveData<Throwable> throwable;
  private final MutableLiveData<Boolean> avatarUploadInProgress;
  private final MutableLiveData<Boolean> avatarUploadSucceeded;

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
    avatarUploadInProgress = new MutableLiveData<>(false);
    avatarUploadSucceeded = new MutableLiveData<>();

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
   * Returns whether an avatar upload is currently in progress.
   *
   * @return live data stream of upload-in-progress state.
   */
  public LiveData<Boolean> getAvatarUploadInProgress() {
    return avatarUploadInProgress;
  }

  /**
   * Returns the result of the most recent avatar upload attempt.
   *
   * @return live data stream that posts {@code true} on success, {@code false} on failure.
   */
  public LiveData<Boolean> getAvatarUploadSucceeded() {
    return avatarUploadSucceeded;
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

  /**
   * Updates the current user's profile information, including display name
   * and email address. The updated profile is published to observers.
   *
   * @param activity    activity used for authentication flows.
   * @param displayName new display name.
   * @param email       new email address.
   */
  public void updateProfile(Activity activity, String displayName, String email) {
    throwable.setValue(null);

    userService.updateProfile(activity, displayName, email)
        .whenComplete(this::handleResult);
  }

  /**
   * Uploads a new avatar image for the current user. The updated profile
   * (including the new avatar URL) is published to observers.
   *
   * @param activity activity used for authentication flows.
   * @param uri      URI of the image to upload.
   */
  public void updateAvatar(Activity activity, Uri uri) {
    throwable.setValue(null);

    avatarUploadInProgress.setValue(true);
    avatarUploadSucceeded.setValue(null);

    userService.uploadAvatar(activity, uri)
        .whenComplete((user, throwable) -> {
          avatarUploadInProgress.postValue(false);
          if (throwable == null) {
            avatarUploadSucceeded.postValue(true);
            this.user.postValue(user);
          } else {
            avatarUploadSucceeded.postValue(false);
            postThrowable(throwable);
          }
        });
  }

  /**
   * Handles the result of any user-related asynchronous operation.
   * If successful, publishes the updated user profile; otherwise,
   * publishes the encountered error.
   *
   * @param user       updated user profile.
   * @param throwable  error thrown during the operation, if any.
   */
  private void handleResult(UserProfile user, Throwable throwable) {
    if (throwable == null) {
      this.user.postValue(user);
    } else {
      postThrowable(throwable);
    }
  }

  /**
   * Logs and publishes the provided error.
   *
   * @param throwable error to publish.
   */
  private void postThrowable(Throwable throwable) {
    Log.e(TAG, throwable.getMessage(), throwable);
    this.throwable.postValue(throwable);
  }
}
