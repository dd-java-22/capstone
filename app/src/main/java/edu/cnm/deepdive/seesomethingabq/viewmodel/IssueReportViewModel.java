package edu.cnm.deepdive.seesomethingabq.viewmodel;

import android.app.Activity;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportRequest;
import edu.cnm.deepdive.seesomethingabq.service.IssueReportService;
import jakarta.inject.Inject;

@HiltViewModel
public class IssueReportViewModel extends ViewModel {

  private static final String TAG = IssueReportViewModel.class.getSimpleName();

  private final IssueReportService issueReportService;

  private final MutableLiveData<Boolean> submitted;
  private final MutableLiveData<Throwable> throwable;

  @Inject
  IssueReportViewModel(IssueReportService issueReportService) {
    this.issueReportService = issueReportService;
    submitted = new MutableLiveData<>();
    throwable = new MutableLiveData<>();
  }

  public LiveData<Boolean> getSubmitted() {
    return submitted;
  }

  public LiveData<Throwable> getThrowable() {
    return throwable;
  }

  public void resetState() {
    submitted.setValue(null);
    throwable.setValue(null);
  }

  public void submit(Activity activity, IssueReportRequest request) {
    throwable.setValue(null);
    submitted.setValue(null);
    issueReportService.submit(activity, request)
        .whenComplete((ignored, throwable) -> {
          if (throwable == null) {
            submitted.postValue(true);
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
