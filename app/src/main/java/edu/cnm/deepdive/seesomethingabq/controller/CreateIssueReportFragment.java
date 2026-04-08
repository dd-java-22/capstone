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

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.snackbar.Snackbar;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.seesomethingabq.BuildConfig;
import edu.cnm.deepdive.seesomethingabq.R;
import edu.cnm.deepdive.seesomethingabq.databinding.FragmentCreateIssueReportBinding;
import edu.cnm.deepdive.seesomethingabq.model.domain.PickedLocation;
import edu.cnm.deepdive.seesomethingabq.model.domain.PlacePredictionCandidate;
import edu.cnm.deepdive.seesomethingabq.model.dto.IssueReportRequest;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType;
import edu.cnm.deepdive.seesomethingabq.viewmodel.IssueReportViewModel;
import edu.cnm.deepdive.seesomethingabq.viewmodel.IssueTypeViewModel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@AndroidEntryPoint
/**
 * Fragment that collects issue report details (description, issue types, location, and attachments)
 * and submits a new report via the {@link IssueReportViewModel}.
 */
public class CreateIssueReportFragment extends Fragment {

  //region Fields
  private static final String TAG = CreateIssueReportFragment.class.getSimpleName();
  private static final int SEARCH_DEBOUNCE_MS = 500;
  private static final int MIN_QUERY_LENGTH = 3;

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

  private LocationCandidateAdapter locationCandidateAdapter;
  private final Handler debounceHandler = new Handler(Looper.getMainLooper());
  private Runnable pendingSearch;
  private CancellationTokenSource currentLocationCancellationTokenSource;

  private PlacesClient placesClient;
  private AutocompleteSessionToken sessionToken;
  private FusedLocationProviderClient fusedLocationClient;
  private ActivityResultLauncher<String> locationPermissionLauncher;
  //endregion

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    pickGalleryImageLauncher = registerForActivityResult(
        new ActivityResultContracts.PickMultipleVisualMedia(5),
        uris -> {
          if (uris != null && !uris.isEmpty()) {
            issueReportViewModel.addAttachedImages(uris.toArray(new Uri[0]));
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

    binding.useCurrentLocationButton.setOnClickListener((v) -> requestCurrentLocation());
    binding.takePhotoButton.setOnClickListener((v) -> launchCamera());
    binding.submitButton.setOnClickListener((v) -> submitReport());

    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    issueTypeViewModel = new ViewModelProvider(requireActivity()).get(IssueTypeViewModel.class);
    issueReportViewModel = new ViewModelProvider(requireActivity()).get(IssueReportViewModel.class);

    setupLocationVisibilityAssist();

    takePhotoLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(),
        (success) -> {
          if (Boolean.TRUE.equals(success)) {
            if (pendingCaptureUri != null) {
              issueReportViewModel.addAttachedImage(pendingCaptureUri);
            }
          } else if (pendingCaptureFile != null) {
            //noinspection ResultOfMethodCallIgnored
            pendingCaptureFile.delete();
          }
          pendingCaptureUri = null;
          pendingCaptureFile = null;
        });

    initializePlaces();
    initializeLocationServices();
    setupLocationResultsList();
    setupInlineLocationSearch();
    binding.locationLayout.setEndIconOnClickListener(v -> {
      binding.locationInput.setText(null);
      confirmedLocation = null;
      binding.locationLayout.setError(null);
      binding.locationLayout.setHelperText(null);
      hideLocationResults();
    });
    showLocationPlaceholder(getString(R.string.location_search_placeholder));
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
        .observe(getViewLifecycleOwner(), this::renderAttachedImages);
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
    if (pendingSearch != null) {
      debounceHandler.removeCallbacks(pendingSearch);
      pendingSearch = null;
    }
    if (currentLocationCancellationTokenSource != null) {
      currentLocationCancellationTokenSource.cancel();
      currentLocationCancellationTokenSource = null;
    }
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

  private void initializePlaces() {
    String apiKey = BuildConfig.PLACES_API_KEY;
    if (apiKey == null || apiKey.isBlank() || "DEFAULT_API_KEY".equals(apiKey)) {
      throw new IllegalStateException("Places API key is missing.");
    }
    if (!Places.isInitialized()) {
      Places.initializeWithNewPlacesApiEnabled(
          requireContext().getApplicationContext(), apiKey);
    }
    placesClient = Places.createClient(requireContext());
    sessionToken = AutocompleteSessionToken.newInstance();
  }

  private void initializeLocationServices() {
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
    locationPermissionLauncher = registerForActivityResult(new RequestPermission(), granted -> {
      if (Boolean.TRUE.equals(granted)) {
        fetchCurrentLocation();
      } else if (binding != null) {
        Snackbar.make(binding.getRoot(), R.string.location_permission_denied,
            Snackbar.LENGTH_SHORT).show();
      }
    });
  }

  private void setupLocationVisibilityAssist() {
    binding.locationInput.setOnFocusChangeListener((v, hasFocus) -> {
      if (hasFocus) {
        scrollLocationSectionIntoView();
      }
    });
  }

  private void scrollLocationSectionIntoView() {
    if (binding == null) {
      return;
    }
    binding.getRoot().post(() -> {
      int extraTopSpace = getResources().getDimensionPixelSize(R.dimen.full_dynamic_spacing);
      int targetY = Math.max(0, binding.locationLayout.getTop() - extraTopSpace);
      binding.getRoot().smoothScrollTo(0, targetY);
    });
  }

  private void setupLocationResultsList() {
    locationCandidateAdapter = new LocationCandidateAdapter(this::fetchSelectedPlace);
    binding.locationResultsList.setLayoutManager(new LinearLayoutManager(requireContext()));
    binding.locationResultsList.setAdapter(locationCandidateAdapter);
  }

  private void setupInlineLocationSearch() {
    binding.locationInput.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!applyingPickedLocation) {
          invalidateConfirmedLocation();
        }
        if (pendingSearch != null) {
          debounceHandler.removeCallbacks(pendingSearch);
          pendingSearch = null;
        }
        String query = Objects.toString(s, "").trim();
        if (applyingPickedLocation) {
          hideLocationResults();
          return;
        }
        if (query.length() >= MIN_QUERY_LENGTH) {
          pendingSearch = () -> performLocationSearch(query);
          debounceHandler.postDelayed(pendingSearch, SEARCH_DEBOUNCE_MS);
        } else {
          locationCandidateAdapter.setCandidates(Collections.emptyList());
          showLocationPlaceholder(getString(R.string.location_search_placeholder));
        }
      }

      @Override
      public void afterTextChanged(Editable s) {
      }
    });
  }

  private void performLocationSearch(String query) {
    showLocationLoading();
    FindAutocompletePredictionsRequest request =
        FindAutocompletePredictionsRequest.builder()
            .setSessionToken(sessionToken)
            .setQuery(query)
            .build();

    placesClient.findAutocompletePredictions(request)
        .addOnSuccessListener(this::handleLocationPredictions)
        .addOnFailureListener(error -> {
          Log.e(TAG, "Autocomplete failed.", error);
          if (binding != null) {
            showLocationPlaceholder(getString(R.string.location_search_failed));
          }
        });
  }

  private void handleLocationPredictions(FindAutocompletePredictionsResponse response) {
    if (binding == null) {
      return;
    }
    List<PlacePredictionCandidate> candidates =
        response.getAutocompletePredictions().stream()
            .map(this::toCandidate)
            .collect(Collectors.toList());

    if (candidates.isEmpty()) {
      showLocationPlaceholder(getString(R.string.location_no_results));
    } else {
      showLocationCandidates(candidates);
    }
  }

  private PlacePredictionCandidate toCandidate(AutocompletePrediction prediction) {
    return new PlacePredictionCandidate(
        prediction.getPlaceId(),
        prediction.getFullText(null).toString()
    );
  }

  private void fetchSelectedPlace(PlacePredictionCandidate candidate) {
    showLocationLoading();
    List<Place.Field> fields = Arrays.asList(
        Place.Field.ID,
        Place.Field.DISPLAY_NAME,
        Place.Field.FORMATTED_ADDRESS,
        Place.Field.LOCATION
    );
    FetchPlaceRequest request = FetchPlaceRequest.builder(candidate.getPlaceId(), fields)
        .setSessionToken(sessionToken)
        .build();

    placesClient.fetchPlace(request)
        .addOnSuccessListener(this::handleFetchedPlace)
        .addOnFailureListener(error -> {
          Log.e(TAG, "Fetch place failed.", error);
          if (binding != null) {
            showLocationPlaceholder(getString(R.string.location_search_failed));
          }
        });
  }

  private void handleFetchedPlace(FetchPlaceResponse response) {
    if (binding == null) {
      return;
    }
    Place place = response.getPlace();
    if (place.getLocation() == null) {
      showLocationPlaceholder(getString(R.string.location_search_failed));
      return;
    }
    String displayText = place.getFormattedAddress();
    if (displayText == null || displayText.isBlank()) {
      CharSequence displayName = place.getDisplayName();
      displayText = displayName != null ? displayName.toString() : null;
    }
    if (displayText == null || displayText.isBlank()) {
      showLocationPlaceholder(getString(R.string.location_search_failed));
      return;
    }
    PickedLocation location = new PickedLocation(
        displayText,
        place.getLocation().latitude,
        place.getLocation().longitude
    );
    applyConfirmedLocation(location);
    sessionToken = AutocompleteSessionToken.newInstance();
    hideLocationResults();
  }

  private void requestCurrentLocation() {
    if (ContextCompat.checkSelfPermission(requireContext(),
        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        || ContextCompat.checkSelfPermission(requireContext(),
        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      fetchCurrentLocation();
    } else {
      locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }
  }

  private void fetchCurrentLocation() {
    if (binding == null) {
      return;
    }

    boolean fineGranted = ContextCompat.checkSelfPermission(
        requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED;
    boolean coarseGranted = ContextCompat.checkSelfPermission(
        requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
        == PackageManager.PERMISSION_GRANTED;

    if (!fineGranted && !coarseGranted) {
      Snackbar.make(binding.getRoot(), R.string.location_permission_denied,
          Snackbar.LENGTH_SHORT).show();
      return;
    }

    showLocationLoading();
    binding.locationResultsPlaceholder.setText(R.string.location_fetching_current);

    CurrentLocationRequest request = new CurrentLocationRequest.Builder()
        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
        .setMaxUpdateAgeMillis(10000)
        .build();

    currentLocationCancellationTokenSource = new CancellationTokenSource();

    fusedLocationClient.getCurrentLocation(request,
            currentLocationCancellationTokenSource.getToken())
        .addOnSuccessListener(location -> {
          if (binding == null) {
            return;
          }
          if (location == null) {
            showLocationPlaceholder(getString(R.string.location_unavailable));
            return;
          }
          reverseGeocodeCurrentLocation(location);
        })
        .addOnFailureListener(error -> {
          Log.e(TAG, "Current location lookup failed.", error);
          if (binding != null) {
            showLocationPlaceholder(getString(R.string.location_unavailable));
          }
        });
  }

  private void reverseGeocodeCurrentLocation(Location location) {
    Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
      geocoder.getFromLocation(
          location.getLatitude(),
          location.getLongitude(),
          1,
          new Geocoder.GeocodeListener() {
            @Override
            public void onGeocode(@NonNull List<Address> addresses) {
              requireActivity().runOnUiThread(() -> {
                if (binding != null) {
                  handleReverseGeocodeResult(location, addresses);
                }
              });
            }

            @Override
            public void onError(@Nullable String errorMessage) {
              Log.e(TAG, "Reverse geocoding failed: " + errorMessage);
              requireActivity().runOnUiThread(() -> {
                if (binding != null) {
                  showLocationPlaceholder(getString(R.string.location_unavailable));
                }
              });
            }
          });
    } else {
      new Thread(() -> {
        List<Address> addresses = Collections.emptyList();
        try {
          List<Address> result = geocoder.getFromLocation(
              location.getLatitude(),
              location.getLongitude(),
              1);
          if (result != null) {
            addresses = result;
          }
        } catch (IOException | RuntimeException e) {
          Log.e(TAG, "Reverse geocoding failed.", e);
        }

        List<Address> finalAddresses = addresses;
        requireActivity().runOnUiThread(() ->
            handleReverseGeocodeResult(location, finalAddresses));
      }).start();
    }
  }

  private void handleReverseGeocodeResult(Location location, List<Address> addresses) {
    if (binding == null) {
      return;
    }

    String displayText = null;
    if (addresses != null && !addresses.isEmpty()) {
      Address address = addresses.get(0);
      displayText = address.getAddressLine(0);
      if (displayText == null || displayText.isBlank()) {
        List<String> parts = new ArrayList<>();
        if (address.getFeatureName() != null) {
          parts.add(address.getFeatureName());
        }
        if (address.getThoroughfare() != null) {
          parts.add(address.getThoroughfare());
        }
        if (address.getLocality() != null) {
          parts.add(address.getLocality());
        }
        displayText = String.join(", ", parts);
      }
    }

    if (displayText == null || displayText.isBlank()) {
      showLocationPlaceholder(getString(R.string.location_unavailable));
      return;
    }

    PickedLocation pickedLocation = new PickedLocation(
        displayText,
        location.getLatitude(),
        location.getLongitude()
    );
    applyConfirmedLocation(pickedLocation);
    hideLocationResults();
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
      binding.locationLayout.setHelperText(null);
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
    binding.locationInput.clearFocus();
    hideKeyboard();
    hideLocationResults();
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

  private void renderAttachedImages(List<Uri> uris) {
    if (binding == null) {
      return;
    }

    binding.attachedImagesContainer.removeAllViews();

    if (uris == null || uris.isEmpty()) {
      binding.attachedImagesScroll.setVisibility(View.GONE);
      return;
    }

    LayoutInflater inflater = LayoutInflater.from(requireContext());
    binding.attachedImagesScroll.setVisibility(View.VISIBLE);

    for (Uri uri : uris) {
      if (uri == null) {
        continue;
      }
      View itemView = inflater.inflate(
          R.layout.item_attached_image_thumbnail,
          binding.attachedImagesContainer,
          false
      );

      ImageView thumbnail = itemView.findViewById(R.id.attached_image_thumbnail);
      ImageButton removeButton = itemView.findViewById(R.id.remove_attached_image_button);

      Glide.with(thumbnail)
          .load(uri)
          .placeholder(R.drawable.ic_image_placeholder)
          .error(R.drawable.ic_broken_image)
          .centerCrop()
          .override(240, 240)
          .thumbnail(0.25f)
          .into(thumbnail);

      removeButton.setOnClickListener(v -> issueReportViewModel.removeAttachedImage(uri));

      binding.attachedImagesContainer.addView(itemView);
    }
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

  private void showLocationLoading() {
    if (binding == null) {
      return;
    }
    binding.locationLoadingIndicator.setVisibility(View.VISIBLE);
    binding.locationResultsList.setVisibility(View.GONE);
    binding.locationResultsPlaceholder.setVisibility(View.GONE);
  }

  private void showLocationCandidates(List<PlacePredictionCandidate> candidates) {
    if (binding == null) {
      return;
    }
    binding.locationLoadingIndicator.setVisibility(View.GONE);
    binding.locationResultsPlaceholder.setVisibility(View.GONE);
    binding.locationResultsList.setVisibility(View.VISIBLE);
    locationCandidateAdapter.setCandidates(candidates);
    scrollLocationSectionIntoView();
  }

  private void showLocationPlaceholder(String message) {
    if (binding == null) {
      return;
    }
    binding.locationLoadingIndicator.setVisibility(View.GONE);
    binding.locationResultsList.setVisibility(View.GONE);
    binding.locationResultsPlaceholder.setVisibility(View.VISIBLE);
    binding.locationResultsPlaceholder.setText(message);
  }

  private void hideLocationResults() {
    if (binding == null) {
      return;
    }
    binding.locationLoadingIndicator.setVisibility(View.GONE);
    binding.locationResultsList.setVisibility(View.GONE);
    binding.locationResultsPlaceholder.setVisibility(View.GONE);
    if (locationCandidateAdapter != null) {
      locationCandidateAdapter.setCandidates(Collections.emptyList());
    }
  }

  private void hideKeyboard() {
    if (binding == null) {
      return;
    }
    View focusedView = binding.locationInput;
    InputMethodManager imm = (InputMethodManager) requireContext()
        .getSystemService(Context.INPUT_METHOD_SERVICE);
    if (imm != null) {
      imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
    }
  }
}
