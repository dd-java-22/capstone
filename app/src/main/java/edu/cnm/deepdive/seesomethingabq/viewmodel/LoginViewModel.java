package edu.cnm.deepdive.seesomethingabq.viewmodel;

import android.app.Activity;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import dagger.hilt.android.lifecycle.HiltViewModel;
import edu.cnm.deepdive.seesomethingabq.BuildConfig;
import edu.cnm.deepdive.seesomethingabq.service.repository.GoogleAuthRepository;
import java.util.function.BiConsumer;
import javax.inject.Inject;

/**
 * ViewModel coordinating Google sign-in flows and exposing credential and error state.
 */
@HiltViewModel
public class LoginViewModel extends ViewModel {

  private static final String TAG = LoginViewModel.class.getSimpleName();

  private final GoogleAuthRepository repository;
  private final MutableLiveData<GoogleIdTokenCredential> credential;
  private final MutableLiveData<Throwable> throwable;
  private final BiConsumer<GoogleIdTokenCredential, Throwable> signInHandler;

  /**
   * Creates a ViewModel using the provided authentication repository.
   *
   * @param repository auth repository.
   */
  @Inject
  public LoginViewModel(GoogleAuthRepository repository) {
    this.repository = repository;
    credential = new MutableLiveData<>();
    throwable = new MutableLiveData<>();
    signInHandler = (cred, ex) -> {
      if (ex != null) {
        Log.e(TAG, "Sign in failure", ex);
        this.throwable.postValue(ex);
      } else {
        if (BuildConfig.DEBUG) {
          // FIXME: 3/25/2026 remove this someday!
          Log.d(TAG, ">>>> token: " + cred.getIdToken());
        }

        this.credential.postValue(cred);
      }
    };
  }

  /**
   * Returns the most recent sign-in credential, if any.
   *
   * @return live data stream of credentials.
   */
  public LiveData<GoogleIdTokenCredential> getCredential() {
    return credential;
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
   * Attempts a non-interactive sign-in where possible.
   *
   * @param activity activity used to launch sign-in flows.
   */
  public void signInQuickly(Activity activity) {
    throwable.setValue(null);
    repository.signInQuickly(activity)
        .whenComplete(signInHandler);
  }

  /**
   * Performs an interactive sign-in.
   *
   * @param activity activity used to launch sign-in flows.
   */
  public void signIn(Activity activity) {
    throwable.setValue(null);
    repository.signIn(activity)
        .whenComplete(signInHandler);
  }

  /**
   * Refreshes the provided credential.
   *
   * @param activity activity used to launch refresh flows.
   * @param credential credential to refresh.
   */
  public void refreshToken(Activity activity, GoogleIdTokenCredential credential) {
    throwable.setValue(null);
    repository.refreshToken(activity, credential)
        .whenComplete(signInHandler);
  }

  /**
   * Signs out of the current session.
   */
  public void signOut() {
    throwable.setValue(null);
    repository.signOut()
        .whenComplete((result, ex) -> {
          if (ex != null) {
            Log.e(TAG, "Error during sign-out", ex);
            throwable.postValue(ex);
          } else {
            credential.postValue(null);
          }
        });
  }
}

