package edu.cnm.deepdive.seesomethingabq.viewmodel;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportRequest;
import edu.cnm.deepdive.seesomethingabq.service.IssueReportService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jakarta.inject.Inject;

@HiltViewModel
public class IssueReportViewModel extends ViewModel {

  private static final String TAG = IssueReportViewModel.class.getSimpleName();

  private final IssueReportService issueReportService;

  private final MutableLiveData<Boolean> submitted;
  private final MutableLiveData<Throwable> throwable;
  private final MutableLiveData<List<Uri>> attachedImages;

  @Inject
  IssueReportViewModel(IssueReportService issueReportService) {
    this.issueReportService = issueReportService;
    submitted = new MutableLiveData<>();
    throwable = new MutableLiveData<>();
    attachedImages = new MutableLiveData<>(Collections.emptyList());
  }

  public LiveData<Boolean> getSubmitted() {
    return submitted;
  }

  public LiveData<Throwable> getThrowable() {
    return throwable;
  }

  public LiveData<List<Uri>> getAttachedImages() {
    return attachedImages;
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

  public void addAttachedImage(Uri uri) {
    if (uri == null) {
      return;
    }
    List<Uri> current = attachedImages.getValue();
    List<Uri> updated = (current != null) ? new ArrayList<>(current) : new ArrayList<>();
    updated.add(uri);
    attachedImages.setValue(Collections.unmodifiableList(updated));
  }

  public void clearAttachedImages() {
    attachedImages.setValue(Collections.emptyList());
  }

  private void postThrowable(Throwable throwable) {
    Log.e(TAG, throwable.getMessage(), throwable);
    this.throwable.postValue(throwable);
  }

}
