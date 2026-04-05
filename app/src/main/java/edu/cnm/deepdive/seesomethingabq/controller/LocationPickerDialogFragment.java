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

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import edu.cnm.deepdive.seesomethingabq.BuildConfig;
import edu.cnm.deepdive.seesomethingabq.R;
import edu.cnm.deepdive.seesomethingabq.databinding.FragmentLocationPickerBinding;
import edu.cnm.deepdive.seesomethingabq.model.domain.PickedLocation;
import edu.cnm.deepdive.seesomethingabq.model.domain.PlacePredictionCandidate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Dialog-based location picker backed by Google Places autocomplete. The dialog performs debounced
 * text-based prediction searches and fetches place details after the user taps a prediction.
 * A confirmed selection is returned to the calling fragment via the Fragment Result API as a
 * {@link PickedLocation}.
 */
public class LocationPickerDialogFragment extends DialogFragment {

  private static final String TAG = LocationPickerDialogFragment.class.getSimpleName();
  private static final int SEARCH_DEBOUNCE_MS = 500;
  private static final int MIN_QUERY_LENGTH = 3;

  private FragmentLocationPickerBinding binding;
  private LocationCandidateAdapter adapter;
  private final Handler debounceHandler = new Handler(Looper.getMainLooper());
  private Runnable pendingSearch;

  private PlacesClient placesClient;
  private AutocompleteSessionToken sessionToken;

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    initializePlaces();
    binding = FragmentLocationPickerBinding.inflate(LayoutInflater.from(requireContext()));
    setupResultsList();
    setupSearchInput();
    showPlaceholder(getString(R.string.location_search_placeholder));
    return new MaterialAlertDialogBuilder(requireContext())
        .setTitle(R.string.location_picker_title)
        .setView(binding.getRoot())
        .setNegativeButton(android.R.string.cancel, (dialog, which) -> dismiss())
        .create();
  }

  @Override
  public void onDestroyView() {
    if (pendingSearch != null) {
      debounceHandler.removeCallbacks(pendingSearch);
      pendingSearch = null;
    }
    binding = null;
    super.onDestroyView();
  }

  private void initializePlaces() {
    String apiKey = BuildConfig.PLACES_API_KEY;
    if (TextUtils.isEmpty(apiKey) || "DEFAULT_API_KEY".equals(apiKey)) {
      throw new IllegalStateException("Places API key is missing.");
    }
    if (!Places.isInitialized()) {
      Places.initializeWithNewPlacesApiEnabled(
          requireContext().getApplicationContext(), apiKey);
    }
    placesClient = Places.createClient(requireContext());
    sessionToken = AutocompleteSessionToken.newInstance();
  }

  private void setupResultsList() {
    adapter = new LocationCandidateAdapter(this::fetchSelectedPlace);
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
          pendingSearch = null;
        }
        String query = s.toString().trim();
        if (query.length() >= MIN_QUERY_LENGTH) {
          pendingSearch = () -> performSearch(query);
          debounceHandler.postDelayed(pendingSearch, SEARCH_DEBOUNCE_MS);
        } else {
          adapter.setCandidates(Collections.emptyList());
          showPlaceholder(getString(R.string.location_search_placeholder));
        }
      }

      @Override
      public void afterTextChanged(Editable s) {
      }
    });
  }

  private void performSearch(String query) {
    showLoading();
    FindAutocompletePredictionsRequest request =
        FindAutocompletePredictionsRequest.builder()
            .setSessionToken(sessionToken)
            .setQuery(query)
            .build();

    placesClient.findAutocompletePredictions(request)
        .addOnSuccessListener(this::handlePredictions)
        .addOnFailureListener(error -> {
          Log.e(TAG, "Autocomplete failed.", error);
          postToUi(() -> showPlaceholder(getString(R.string.location_search_failed)));
        });
  }

  private void handlePredictions(FindAutocompletePredictionsResponse response) {
    postToUi(() -> {
      List<PlacePredictionCandidate> candidates =
          response.getAutocompletePredictions().stream()
              .map(this::toCandidate)
              .collect(Collectors.toList());

      if (candidates.isEmpty()) {
        showPlaceholder(getString(R.string.location_no_results));
      } else {
        showCandidates(candidates);
      }
    });
  }

  private PlacePredictionCandidate toCandidate(AutocompletePrediction prediction) {
    return new PlacePredictionCandidate(
        prediction.getPlaceId(),
        prediction.getFullText(null).toString()
    );
  }

  private void fetchSelectedPlace(PlacePredictionCandidate candidate) {
    showLoading();
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
          postToUi(() -> showPlaceholder(getString(R.string.location_search_failed)));
        });
  }

  private void handleFetchedPlace(FetchPlaceResponse response) {
    postToUi(() -> {
      Place place = response.getPlace();
      if (place.getLocation() == null) {
        showPlaceholder(getString(R.string.location_search_failed));
        return;
      }
      String displayText = place.getFormattedAddress();
      if (TextUtils.isEmpty(displayText)) {
        CharSequence displayName = place.getDisplayName();
        displayText = displayName != null ? displayName.toString() : null;
      }
      if (TextUtils.isEmpty(displayText)) {
        showPlaceholder(getString(R.string.location_search_failed));
        return;
      }
      PickedLocation location = new PickedLocation(
          displayText,
          place.getLocation().latitude,
          place.getLocation().longitude
      );
      confirmLocation(location);
      sessionToken = AutocompleteSessionToken.newInstance();
    });
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

  private void showCandidates(List<PlacePredictionCandidate> candidates) {
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
    if (binding != null) {
      Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }
  }

  private void confirmLocation(PickedLocation location) {
    Bundle result = new Bundle();
    result.putParcelable(LocationPickerResult.KEY_PICKED_LOCATION, location);
    getParentFragmentManager().setFragmentResult(LocationPickerResult.REQUEST_KEY, result);
    dismiss();
  }
}