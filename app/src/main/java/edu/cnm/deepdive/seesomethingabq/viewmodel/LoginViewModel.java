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

@HiltViewModel
public class LoginViewModel extends ViewModel {

  private static final String TAG = LoginViewModel.class.getSimpleName();

  private final GoogleAuthRepository repository;
  private final MutableLiveData<GoogleIdTokenCredential> credential;
  private final MutableLiveData<Throwable> throwable;
  private final BiConsumer<GoogleIdTokenCredential, Throwable> signInHandler;

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
//        if (BuildConfig.DEBUG) {
          // FIXME: 3/25/2026 remove this someday!
          Log.d(TAG, ">>>> token: " + cred.getIdToken());
//        }

        this.credential.postValue(cred);
      }
    };
  }

  public LiveData<GoogleIdTokenCredential> getCredential() {
    return credential;
  }

  public LiveData<Throwable> getThrowable() {
    return throwable;
  }

  public void signInQuickly(Activity activity) {
    throwable.setValue(null);
    repository.signInQuickly(activity)
        .whenComplete(signInHandler);
  }

  public void signIn(Activity activity) {
    throwable.setValue(null);
    repository.signIn(activity)
        .whenComplete(signInHandler);
  }

  public void refreshToken(Activity activity, GoogleIdTokenCredential credential) {
    throwable.setValue(null);
    repository.refreshToken(activity, credential)
        .whenComplete(signInHandler);
  }

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

