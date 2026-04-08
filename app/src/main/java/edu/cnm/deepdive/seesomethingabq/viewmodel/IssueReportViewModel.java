package edu.cnm.deepdive.seesomethingabq.viewmodel;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;
import dagger.hilt.android.lifecycle.HiltViewModel;
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReport;
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportRequest;
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportSummary;
import edu.cnm.deepdive.seesomethingabq.service.IssueReportService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import jakarta.inject.Inject;

/**
 * ViewModel for creating and listing issue reports.
 *
 * This ViewModel coordinates report submission, image attachment tracking,
 * and paging of existing reports.
 */
@HiltViewModel
public class IssueReportViewModel extends ViewModel {

  private static final String TAG = IssueReportViewModel.class.getSimpleName();

  private final IssueReportService issueReportService;

  private final MutableLiveData<Boolean> submitted;
  private final MutableLiveData<Throwable> throwable;
  private LiveData<PagingData<IssueReportSummary>> issueReports;
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

  public LiveData<PagingData<IssueReportSummary>> getIssueReports(Activity activity) {
    if (issueReports == null) {
      issueReports = PagingLiveData.getLiveData(
          issueReportService.getIssueReportsPager(activity)
      );
    }
    return issueReports;
  }

  public LiveData<List<Uri>> getAttachedImages() {
    return attachedImages;
  }

  public void resetState() {
    submitted.setValue(null);
    throwable.setValue(null);
  }

  /**
   * Submits a new issue report and uploads any attached images.
   *
   * The report is created first; then, if any image URIs are present,
   * they are uploaded and associated with the created report.
   */
  public void submit(Activity activity, IssueReportRequest request) {
    throwable.setValue(null);
    submitted.setValue(null);

    List<Uri> uris = attachedImages.getValue();
    if (uris == null) {
      uris = Collections.emptyList();
    }
    final List<Uri> finalUris = uris;

    issueReportService.submit(activity, request)
        .thenCompose((IssueReport report) -> {
          if (finalUris.isEmpty()) {
            return CompletableFuture.completedFuture(null);
          }
          return issueReportService.uploadImages(
              activity,
              report.getExternalId(),
              finalUris
          );
        })
        .whenComplete((ignored, thrown) -> {
          if (thrown == null) {
            submitted.postValue(true);
          } else {
            postThrowable(thrown);
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

  public void addAttachedImages(Uri[] uris) {
    if (uris == null || uris.length == 0) {
      return;
    }
    List<Uri> current = attachedImages.getValue();
    List<Uri> updated = (current != null) ? new ArrayList<>(current) : new ArrayList<>();
    Arrays.stream(uris)
        .filter((uri) -> uri != null)
        .forEach(updated::add);
    attachedImages.setValue(Collections.unmodifiableList(updated));
  }

  public void removeAttachedImage(Uri uri) {
    if (uri == null) {
      return;
    }
    List<Uri> current = attachedImages.getValue();
    if (current == null || current.isEmpty()) {
      return;
    }
    List<Uri> updated = new ArrayList<>(current);
    if (updated.remove(uri)) {
      attachedImages.setValue(Collections.unmodifiableList(updated));
    }
  }

  public void clearAttachedImages() {
    attachedImages.setValue(Collections.emptyList());
  }

  private void postThrowable(Throwable throwable) {
    Log.e(TAG, throwable.getMessage(), throwable);
    this.throwable.postValue(throwable);
  }
}
