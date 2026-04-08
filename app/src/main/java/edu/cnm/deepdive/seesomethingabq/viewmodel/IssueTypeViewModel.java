package edu.cnm.deepdive.seesomethingabq.viewmodel;

import android.app.Activity;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType;
import edu.cnm.deepdive.seesomethingabq.service.IssueTypeService;
import jakarta.inject.Inject;
import java.util.List;

/**
 * ViewModel exposing issue types and refresh operations to the UI.
 */
@HiltViewModel
public class IssueTypeViewModel extends ViewModel {

  private static final String TAG = IssueTypeViewModel.class.getSimpleName();

  private final IssueTypeService issueTypeService;

  private final LiveData<List<IssueType>> issueTypes;
  private final MutableLiveData<Throwable> throwable;

  /**
   * Creates a ViewModel using the provided issue type service.
   *
   * @param issueTypeService service used to load and refresh issue types.
   */
  @Inject
  IssueTypeViewModel(IssueTypeService issueTypeService) {
    this.issueTypeService = issueTypeService;
    issueTypes = issueTypeService.getIssueTypes();
    throwable = new MutableLiveData<>();
  }

  /**
   * Returns observable issue types.
   *
   * @return live data stream of issue types.
   */
  public LiveData<List<IssueType>> getIssueTypes() {
    return issueTypes;
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
   * Refreshes issue types from the server.
   *
   * @param activity activity used for authentication flows.
   */
  public void refresh(Activity activity) {
    throwable.setValue(null);
    issueTypeService.refresh(activity)
        .whenComplete((issueTypes, throwable) -> {
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

