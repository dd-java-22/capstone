/*
 *  Copyright 2026 CNM Ingenuity, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package edu.cnm.deepdive.seesomethingabq.controller;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.seesomethingabq.R;
import edu.cnm.deepdive.seesomethingabq.databinding.FragmentLocationPickerBinding;
import edu.cnm.deepdive.seesomethingabq.model.domain.PickedLocation;
import edu.cnm.deepdive.seesomethingabq.service.CurrentLocationProvider;
import edu.cnm.deepdive.seesomethingabq.service.LocationSearchProvider;
import jakarta.inject.Inject;
import java.util.List;

/**
 * Dialog-based location picker. On open, attempts to bootstrap from the device's current location
 * via reverse geocoding. The user can also type a search query to find locations by address or place
 * name. Tapping a candidate confirms the selection and returns a {@link PickedLocation} to the
 * calling fragment via the Fragment Result API.
 */
@AndroidEntryPoint
public class LocationPickerDialogFragment extends DialogFragment {

  private static final int SEARCH_DEBOUNCE_MS = 500;
  private static final int MIN_QUERY_LENGTH = 3;

  @Inject
  LocationSearchProvider searchProvider;

  @Inject
  CurrentLocationProvider currentLocationProvider;

  private FragmentLocationPickerBinding binding;
  private LocationCandidateAdapter adapter;
  private final Handler debounceHandler = new Handler(Looper.getMainLooper());
  private Runnable pendingSearch;
  private boolean bootstrapAttempted;

  private final ActivityResultLauncher<String> permissionLauncher =
      registerForActivityResult(new RequestPermission(), this::onPermissionResult);

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    binding = FragmentLocationPickerBinding.inflate(LayoutInflater.from(requireContext()));
    setupResultsList();
    setupSearchInput();
    return new MaterialAlertDialogBuilder(requireContext())
        .setTitle(R.string.location_picker_title)
        .setView(binding.getRoot())
        .setNegativeButton(android.R.string.cancel, (dialog, which) -> dismiss())
        .create();
  }

  @Override
  public void onStart() {
    super.onStart();
    if (!bootstrapAttempted) {
      bootstrapAttempted = true;
      checkPermissionAndBootstrap();
    }
  }

  @Override
  public void onDestroyView() {
    if (pendingSearch != null) {
      debounceHandler.removeCallbacks(pendingSearch);
    }
    binding = null;
    super.onDestroyView();
  }

  private void setupResultsList() {
    adapter = new LocationCandidateAdapter(this::confirmLocation);
    binding.locationResultsList.setLayoutManager(new LinearLayoutManager(requireContext()));
    binding.locationResultsList.setAdapter(adapter);
  }

  private void setupSearchInput() {
    binding.locationSearchInput.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (pendingSearch != null) {
          debounceHandler.removeCallbacks(pendingSearch);
        }
        String query = s.toString().trim();
        if (query.length() >= MIN_QUERY_LENGTH) {
          pendingSearch = () -> performSearch(query);
          debounceHandler.postDelayed(pendingSearch, SEARCH_DEBOUNCE_MS);
        } else {
          showPlaceholder(getString(R.string.location_search_placeholder));
        }
      }

      @Override
      public void afterTextChanged(Editable s) {
      }
    });
  }

  private void checkPermissionAndBootstrap() {
    if (hasLocationPermission()) {
      bootstrapCurrentLocation();
    } else {
      // Request fine location. On Android 12+, the user may choose "Approximate", which grants
      // only ACCESS_COARSE_LOCATION. The callback checks for that fallback.
      permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }
  }

  private void onPermissionResult(boolean fineGranted) {
    if (fineGranted || hasLocationPermission()) {
      bootstrapCurrentLocation();
    } else if (binding != null) {
      showSnackbar(getString(R.string.location_permission_denied));
    }
  }

  private boolean hasLocationPermission() {
    return ContextCompat.checkSelfPermission(requireContext(),
        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission(requireContext(),
        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
  }

  private void bootstrapCurrentLocation() {
    showLoading();
    currentLocationProvider.getCurrentLocation()
        .thenCompose(location ->
            searchProvider.reverseGeocode(location.getLatitude(), location.getLongitude()))
        .whenComplete((results, error) -> postToUi(() -> {
          if (error != null) {
            showPlaceholder(getString(R.string.location_current_unavailable));
          } else if (results.isEmpty()) {
            showPlaceholder(getString(R.string.location_no_results));
          } else {
            showCandidates(results);
          }
        }));
  }

  private void performSearch(String query) {
    showLoading();
    searchProvider.search(query)
        .whenComplete((results, error) -> postToUi(() -> {
          if (error != null) {
            showPlaceholder(getString(R.string.location_search_failed));
          } else if (results.isEmpty()) {
            showPlaceholder(getString(R.string.location_no_results));
          } else {
            showCandidates(results);
          }
        }));
  }

  private void postToUi(Runnable action) {
    Activity activity = getActivity();
    if (activity == null || binding == null) {
      return;
    }
    activity.runOnUiThread(() -> {
      if (binding != null) {
        action.run();
      }
    });
  }

  private void showLoading() {
    binding.locationLoadingIndicator.setVisibility(View.VISIBLE);
    binding.locationResultsList.setVisibility(View.GONE);
    binding.locationResultsPlaceholder.setVisibility(View.GONE);
  }

  private void showCandidates(List<PickedLocation> candidates) {
    binding.locationLoadingIndicator.setVisibility(View.GONE);
    binding.locationResultsPlaceholder.setVisibility(View.GONE);
    binding.locationResultsList.setVisibility(View.VISIBLE);
    adapter.setCandidates(candidates);
  }

  private void showPlaceholder(String message) {
    binding.locationLoadingIndicator.setVisibility(View.GONE);
    binding.locationResultsList.setVisibility(View.GONE);
    binding.locationResultsPlaceholder.setVisibility(View.VISIBLE);
    binding.locationResultsPlaceholder.setText(message);
  }

  private void showSnackbar(String message) {
    Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
  }

  private void confirmLocation(PickedLocation location) {
    Bundle result = new Bundle();
    result.putParcelable(LocationPickerResult.KEY_PICKED_LOCATION, location);
    getParentFragmentManager()
        .setFragmentResult(LocationPickerResult.REQUEST_KEY, result);
    dismiss();
  }
}
