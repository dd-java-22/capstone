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
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportStatusUpdateRequest;
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportSummary;
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportTypesUpdateRequest;
import edu.cnm.deepdive.seesomethingabq.service.IssueReportService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.io.File;
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
  private LiveData<PagingData<IssueReportSummary>> myIssueReports;
  private final MutableLiveData<List<Uri>> attachedImages;

  /**
   * Creates a ViewModel using the provided issue report service.
   *
   * @param issueReportService service used to submit and load reports.
   */
  @Inject
  IssueReportViewModel(IssueReportService issueReportService) {
    this.issueReportService = issueReportService;
    submitted = new MutableLiveData<>();
    throwable = new MutableLiveData<>();
    attachedImages = new MutableLiveData<>(Collections.emptyList());
  }

  /**
   * Returns whether the last submission completed successfully.
   *
   * @return live data stream of submission state.
   */
  public LiveData<Boolean> getSubmitted() {
    return submitted;
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
   * Returns a paged stream of issue report summaries.
   *
   * @param activity activity used for authentication flows.
   * @return live data stream of paging data.
   */
  public LiveData<PagingData<IssueReportSummary>> getIssueReports(Activity activity) {
    if (issueReports == null) {
      issueReports = PagingLiveData.cachedIn(
          PagingLiveData.getLiveData(issueReportService.getAllIssueReportsPager(activity)),
          this
      );
    }
    return issueReports;
  }

  /**
   * Returns a paged stream of issue report summaries belonging to the current user.
   *
   * @param activity activity used for authentication flows.
   * @return live data stream of paging data for the current user's reports.
   */
  public LiveData<PagingData<IssueReportSummary>> getMyIssueReports(Activity activity) {
    if (myIssueReports == null) {
      myIssueReports = PagingLiveData.cachedIn(
          PagingLiveData.getLiveData(issueReportService.getMyIssueReportsPager(activity)),
          this
      );
    }
    return myIssueReports;
  }

  /**
   * Returns the currently attached image URIs for a new report submission.
   *
   * @return live data stream of attached image URIs.
   */
  public LiveData<List<Uri>> getAttachedImages() {
    return attachedImages;
  }

  /**
   * Clears submission and error state.
   */
  public void resetState() {
    submitted.setValue(null);
    throwable.setValue(null);
  }

  /**
   * Loads a full issue report by identifier.
   *
   * @param activity activity used for authentication flows.
   * @param reportId external report identifier.
   * @return future completed with the loaded report.
   */
  public CompletableFuture<IssueReport> getReport(Activity activity, String reportId) {
    return issueReportService.getReport(activity, reportId);
  }

  /**
   * Updates an existing issue report.
   *
   * @param activity activity used for authentication flows.
   * @param reportId external report identifier.
   * @param request request payload describing desired report updates.
   * @return future completed with the updated report.
   */
  public CompletableFuture<IssueReport> updateReport(
      Activity activity,
      String reportId,
      IssueReportRequest request
  ) {
    return issueReportService.updateReport(activity, reportId, request);
  }

  /**
   * Updates an issue report's status as a manager-only operation.
   *
   * @param activity activity used for authentication flows.
   * @param reportId external report identifier.
   * @param request request payload containing the desired status.
   * @return future completed when the update has been applied.
   */
  public CompletableFuture<Void> updateManagerReportStatus(
      Activity activity,
      String reportId,
      IssueReportStatusUpdateRequest request
  ) {
    return issueReportService.updateManagerReportStatus(activity, reportId, request);
  }

  /**
   * Replaces the issue-type tags for a report as a manager-only operation.
   *
   * @param activity activity used for authentication flows.
   * @param reportId external report identifier.
   * @param request request payload containing the replacement tag list.
   * @return future completed when the update has been applied.
   */
  public CompletableFuture<Void> replaceManagerReportIssueTypes(
      Activity activity,
      String reportId,
      IssueReportTypesUpdateRequest request
  ) {
    return issueReportService.replaceManagerReportIssueTypes(activity, reportId, request);
  }

  /**
   * Downloads a report image from the server.
   *
   * @param activity activity used for authentication flows.
   * @param reportId external report identifier.
   * @param imageId external image identifier.
   * @return future completed with the image bytes.
   */
  public CompletableFuture<byte[]> downloadImage(
      Activity activity,
      String reportId,
      String imageId
  ) {
    return issueReportService.downloadImageFile(activity, reportId, imageId)
        .thenApply(responseBody -> {
          try (responseBody) {
            return responseBody.bytes();
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        });
  }

  /**
   * Downloads a report image and stores it in the local cache.
   *
   * @param activity activity used for authentication flows.
   * @param reportId external report identifier.
   * @param imageId external image identifier.
   * @param mimeType MIME type of the image (used for cache naming).
   * @return future completed with the cached file.
   */
  public CompletableFuture<File> downloadImageToCache(
      Activity activity,
      String reportId,
      String imageId,
      String mimeType
  ) {
    return issueReportService.downloadImageToCache(activity, reportId, imageId, mimeType);
  }

  /**
   * Uploads one or more local images for an existing report.
   *
   * @param activity activity used for authentication flows.
   * @param reportId external report identifier.
   * @param uris local image URIs to upload.
   * @return future completed when uploads finish.
   */
  public CompletableFuture<Void> uploadImages(Activity activity, String reportId, List<Uri> uris) {
    return issueReportService.uploadImages(activity, reportId, uris);
  }

  /**
   * Deletes an image associated with a report.
   *
   * @param activity activity used for authentication flows.
   * @param reportId external report identifier.
   * @param imageId external image identifier.
   * @return future completed when deletion finishes.
   */
  public CompletableFuture<Void> deleteImage(Activity activity, String reportId, String imageId) {
    return issueReportService.deleteImage(activity, reportId, imageId);
  }


  /**
   * Submits a new issue report.
   *
   * @param activity activity used for authentication flows.
   * @param request report request payload.
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


  /**
   * Adds a single attached image URI.
   *
   * @param uri image URI to attach.
   */
  public void addAttachedImage(Uri uri) {
    if (uri == null) {
      return;
    }
    List<Uri> current = attachedImages.getValue();
    List<Uri> updated = (current != null) ? new ArrayList<>(current) : new ArrayList<>();
    updated.add(uri);
    attachedImages.setValue(Collections.unmodifiableList(updated));
  }

  /**
   * Adds multiple attached image URIs.
   *
   * @param uris image URIs to attach.
   */
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

  /**
   * Removes a single attached image URI.
   *
   * @param uri image URI to remove.
   */
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

  /**
   * Removes all attached images.
   */
  public void clearAttachedImages() {
    attachedImages.setValue(Collections.emptyList());
  }

  private void postThrowable(Throwable throwable) {
    Log.e(TAG, throwable.getMessage(), throwable);
    this.throwable.postValue(throwable);
  }
}
