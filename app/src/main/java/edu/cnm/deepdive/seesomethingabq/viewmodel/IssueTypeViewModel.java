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

@HiltViewModel
public class IssueTypeViewModel extends ViewModel {

  private static final String TAG = IssueTypeViewModel.class.getSimpleName();

  private final IssueTypeService issueTypeService;

  private final LiveData<List<IssueType>> issueTypes;
  private final MutableLiveData<Throwable> throwable;

  @Inject
  IssueTypeViewModel(IssueTypeService issueTypeService) {
    this.issueTypeService = issueTypeService;
    issueTypes = issueTypeService.getIssueTypes();
    throwable = new MutableLiveData<>();
  }

  public LiveData<List<IssueType>> getIssueTypes() {
    return issueTypes;
  }

  public LiveData<Throwable> getThrowable() {
    return throwable;
  }

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

