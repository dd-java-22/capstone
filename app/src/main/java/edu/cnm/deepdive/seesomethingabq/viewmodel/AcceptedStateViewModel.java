package edu.cnm.deepdive.seesomethingabq.viewmodel;

import android.app.Activity;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import edu.cnm.deepdive.seesomethingabq.model.entity.AcceptedState;
import edu.cnm.deepdive.seesomethingabq.service.AcceptedStateService;
import jakarta.inject.Inject;
import java.util.List;

@HiltViewModel
/**
 * ViewModel exposing accepted states and refresh operations to manager UIs.
 */
public class AcceptedStateViewModel extends ViewModel {

  private static final String TAG = AcceptedStateViewModel.class.getSimpleName();

  private final AcceptedStateService acceptedStateService;
  private final LiveData<List<AcceptedState>> acceptedStates;
  private final MutableLiveData<Throwable> throwable;

  @Inject
  AcceptedStateViewModel(AcceptedStateService acceptedStateService) {
    this.acceptedStateService = acceptedStateService;
    acceptedStates = acceptedStateService.getAcceptedStates();
    throwable = new MutableLiveData<>();
  }

  public LiveData<List<AcceptedState>> getAcceptedStates() {
    return acceptedStates;
  }

  public LiveData<Throwable> getThrowable() {
    return throwable;
  }

  public void refresh(Activity activity) {
    throwable.setValue(null);
    acceptedStateService.refresh(activity)
        .whenComplete((acceptedStates, throwable) -> {
          if (throwable != null) {
            postThrowable(throwable);
          }
        });
  }

  private void postThrowable(Throwable throwable) {
    Log.e(TAG, throwable.getMessage(), throwable);
    this.throwable.postValue(throwable);
  }

}

