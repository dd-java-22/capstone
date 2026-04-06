/*
 * Copyright 2026 CNM Ingenuity, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.cnm.deepdive.seesomethingabq.controller;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.core.os.BundleCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.snackbar.Snackbar;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.seesomethingabq.R;
import edu.cnm.deepdive.seesomethingabq.databinding.FragmentCreateIssueReportBinding;
import edu.cnm.deepdive.seesomethingabq.model.domain.PickedLocation;
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportRequest;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType;
import edu.cnm.deepdive.seesomethingabq.viewmodel.IssueReportViewModel;
import edu.cnm.deepdive.seesomethingabq.viewmodel.IssueTypeViewModel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@AndroidEntryPoint
public class CreateIssueReportFragment extends Fragment {

  private static final String TAG = CreateIssueReportFragment.class.getSimpleName();

  private FragmentCreateIssueReportBinding binding;
  private IssueTypeViewModel issueTypeViewModel;
  private IssueReportViewModel issueReportViewModel;
  private final Set<String> selectedIssueTypeTags = new HashSet<>();
  private PickedLocation confirmedLocation;
  private boolean applyingPickedLocation;
  private ActivityResultLauncher<Uri> takePhotoLauncher;
  private Uri pendingCaptureUri;
  private File pendingCaptureFile;
  private boolean reportSubmitted;
  private boolean cleanedUpOnExit;
  private ActivityResultLauncher<PickVisualMediaRequest> pickGalleryImageLauncher;
  private final List<Uri> selectedGalleryImageUri = new ArrayList<>();

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    pickGalleryImageLauncher = registerForActivityResult(
        new ActivityResultContracts.PickMultipleVisualMedia(5),
        uris -> {
          selectedGalleryImageUri.clear();
          if (uris != null && !uris.isEmpty()) {
            selectedGalleryImageUri.addAll(uris);
          }
        });

    binding = FragmentCreateIssueReportBinding.inflate(inflater, container, false);

    binding.attachGalleryImageButton.setOnClickListener(v ->
        pickGalleryImageLauncher.launch(
            new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build()));

    binding.backToDashboardButton.setOnClickListener((v) -> {
      clearPendingAttachments();
      NavController navController = Navigation.findNavController(v);
      navController.navigate(R.id.navigate_to_user_dashboard_fragment);
    });
    binding.useCurrentLocationButton.setOnClickListener((v) -> {
      NavController navController = Navigation.findNavController(v);
      navController.navigate(R.id.navigate_to_location_picker_dialog);
    });
    binding.takePhotoButton.setOnClickListener((v) -> launchCamera());
    binding.submitButton.setOnClickListener((v) -> submitReport());
    binding.locationInput.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!applyingPickedLocation) {
          invalidateConfirmedLocation();
        }
      }

      @Override
      public void afterTextChanged(Editable s) {
      }
    });
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    issueTypeViewModel = new ViewModelProvider(requireActivity()).get(IssueTypeViewModel.class);
    issueReportViewModel = new ViewModelProvider(requireActivity()).get(IssueReportViewModel.class);

    takePhotoLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(),
        (success) -> {
          if (Boolean.TRUE.equals(success)) {
            if (pendingCaptureUri != null) {
              issueReportViewModel.addAttachedImage(pendingCaptureUri);
            }
          } else {
            if (pendingCaptureFile != null) {
              //noinspection ResultOfMethodCallIgnored
              pendingCaptureFile.delete();
            }
          }
          pendingCaptureUri = null;
          pendingCaptureFile = null;
        });

    getParentFragmentManager().setFragmentResultListener(
        LocationPickerResult.REQUEST_KEY, getViewLifecycleOwner(), (requestKey, result) -> {
          PickedLocation location = BundleCompat.getParcelable(
              result, LocationPickerResult.KEY_PICKED_LOCATION, PickedLocation.class);
          if (location != null) {
            applyConfirmedLocation(location);
          }
        });
  }

  @Override
  public void onResume() {
    super.onResume();
    issueTypeViewModel.refresh(requireActivity());
  }

  @Override
  public void onStart() {
    super.onStart();
    issueReportViewModel.resetState();
    issueTypeViewModel.getIssueTypes()
        .observe(getViewLifecycleOwner(), this::populateIssueTypeChips);
    issueReportViewModel.getSubmitted()
        .observe(getViewLifecycleOwner(), this::handleSubmitSuccess);
    issueReportViewModel.getThrowable()
        .observe(getViewLifecycleOwner(), this::handleSubmitFailure);
    issueReportViewModel.getAttachedImages()
        .observe(getViewLifecycleOwner(), (uris) -> Log.d(TAG, "attachedImages=" + uris));
  }

  @Override
  public void onStop() {
    issueTypeViewModel.getIssueTypes().removeObservers(getViewLifecycleOwner());
    issueReportViewModel.getSubmitted().removeObservers(getViewLifecycleOwner());
    issueReportViewModel.getThrowable().removeObservers(getViewLifecycleOwner());
    issueReportViewModel.getAttachedImages().removeObservers(getViewLifecycleOwner());
    super.onStop();
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }

  @Override
  public void onDestroy() {
    if (!reportSubmitted && !cleanedUpOnExit && shouldCleanupOnExit()) {
      clearPendingAttachments();
    }
    super.onDestroy();
  }

  private void populateIssueTypeChips(List<IssueType> issueTypes) {
    binding.issueTypeChipGroup.removeAllViews();
    for (IssueType issueType : issueTypes) {
      Chip chip = new Chip(requireContext());
      chip.setChipDrawable(ChipDrawable.createFromAttributes(requireContext(), null, 0,
          com.google.android.material.R.style.Widget_Material3_Chip_Filter));
      String tag = issueType.getIssueTypeTag();
      chip.setText(tag);
      chip.setCheckable(true);
      chip.setChecked(selectedIssueTypeTags.contains(tag));
      chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
        if (isChecked) {
          selectedIssueTypeTags.add(tag);
        } else {
          selectedIssueTypeTags.remove(tag);
        }
      });
      binding.issueTypeChipGroup.addView(chip);
    }
  }

  private void submitReport() {
    if (confirmedLocation == null) {
      String locationText = Objects.toString(binding.locationInput.getText(), "").trim();
      if (locationText.isEmpty()) {
        binding.locationLayout.setError(getString(R.string.location_required));
      } else {
        binding.locationLayout.setError(getString(R.string.location_not_confirmed));
      }
      return;
    }

    binding.locationLayout.setError(null);

    CharSequence descriptionInput = binding.descriptionInput.getText();
    String description = (descriptionInput != null) ? descriptionInput.toString() : "";
    List<String> issueTypes = new ArrayList<>(selectedIssueTypeTags);

    IssueReportRequest request = new IssueReportRequest(
        description,
        confirmedLocation.getLatitude(),
        confirmedLocation.getLongitude(),
        confirmedLocation.getDisplayText(),
        null,
        issueTypes
    );
    issueReportViewModel.submit(requireActivity(), request);
  }

  private void applyConfirmedLocation(PickedLocation location) {
    confirmedLocation = location;
    applyingPickedLocation = true;
    binding.locationInput.setText(location.getDisplayText());
    binding.locationLayout.setError(null);
    binding.locationLayout.setHelperText(getString(R.string.location_confirmed));
    applyingPickedLocation = false;
  }

  private void invalidateConfirmedLocation() {
    boolean hadConfirmed = confirmedLocation != null;
    confirmedLocation = null;
    if (binding != null && hadConfirmed) {
      binding.locationLayout.setError(null);
      binding.locationLayout.setHelperText(getString(R.string.location_unconfirmed_edit));
    }
  }

  private void handleSubmitSuccess(Boolean submitted) {
    if (Boolean.TRUE.equals(submitted)) {
      Snackbar.make(binding.getRoot(), R.string.submit_report_success, Snackbar.LENGTH_SHORT)
          .show();
      reportSubmitted = true;
      clearPendingAttachments();
      NavController navController = Navigation.findNavController(binding.getRoot());
      navController.navigate(R.id.navigate_to_user_dashboard_fragment);
    }
  }

  private void handleSubmitFailure(Throwable throwable) {
    if (throwable != null) {
      Snackbar.make(binding.getRoot(), R.string.submit_report_failure, Snackbar.LENGTH_SHORT)
          .show();
    }
  }

  private void launchCamera() {
    if (takePhotoLauncher == null) {
      return;
    }
    try {
      pendingCaptureFile = createTempCameraFile();
      pendingCaptureUri = FileProvider.getUriForFile(
          requireContext(),
          requireContext().getPackageName() + ".fileprovider",
          pendingCaptureFile
      );
      takePhotoLauncher.launch(pendingCaptureUri);
    } catch (IOException e) {
      Log.e(TAG, "Unable to create temp camera file", e);
      pendingCaptureUri = null;
      pendingCaptureFile = null;
      Snackbar.make(binding.getRoot(), R.string.take_photo_failure, Snackbar.LENGTH_SHORT).show();
    }
  }

  private File createTempCameraFile() throws IOException {
    File cacheDir = requireContext().getCacheDir();
    File cameraDir = new File(cacheDir, "camera");
    //noinspection ResultOfMethodCallIgnored
    cameraDir.mkdirs();
    return File.createTempFile("issue_report_", ".jpg", cameraDir);
  }

  private void clearPendingAttachments() {
    cleanedUpOnExit = true;
    if (issueReportViewModel == null) {
      return;
    }
    List<Uri> uris = issueReportViewModel.getAttachedImages().getValue();
    String authority = requireContext().getPackageName() + ".fileprovider";
    if (uris != null) {
      for (Uri uri : uris) {
        if (uri != null && authority.equals(uri.getAuthority())) {
          try {
            requireContext().getContentResolver().delete(uri, null, null);
          } catch (RuntimeException e) {
            Log.w(TAG, "Unable to delete temp attachment " + uri, e);
          }
        }
      }
    }
    issueReportViewModel.clearAttachedImages();
  }

  private boolean shouldCleanupOnExit() {
    if (getActivity() != null && getActivity().isChangingConfigurations()) {
      return false;
    }
    return isRemoving() || (getActivity() != null && getActivity().isFinishing());
  }
}
