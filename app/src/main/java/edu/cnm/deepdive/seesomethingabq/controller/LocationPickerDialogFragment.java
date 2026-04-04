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

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.seesomethingabq.R;
import edu.cnm.deepdive.seesomethingabq.databinding.FragmentLocationPickerBinding;
import edu.cnm.deepdive.seesomethingabq.model.domain.PickedLocation;

/**
 * Dialog-based location picker. Allows the user to search for a location and confirm a selection
 * that is returned to the calling fragment via the Fragment Result API.
 */
@AndroidEntryPoint
public class LocationPickerDialogFragment extends DialogFragment {

  // TODO: Inject LocationSearchProvider for address/place search.
  // TODO: Inject CurrentLocationProvider for device-location bootstrap.

  private FragmentLocationPickerBinding binding;

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    binding = FragmentLocationPickerBinding.inflate(LayoutInflater.from(requireContext()));
    // TODO: Wire search input to LocationSearchProvider when available.
    // TODO: Populate location_results_list with search results via adapter.
    // TODO: Add "Use Current Location" action using CurrentLocationProvider.
    return new MaterialAlertDialogBuilder(requireContext())
        .setTitle(R.string.location_picker_title)
        .setView(binding.getRoot())
        .setNegativeButton(android.R.string.cancel, (dialog, which) -> dismiss())
        .create();
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }

  private void confirmLocation(PickedLocation location) {
    Bundle result = new Bundle();
    result.putParcelable(LocationPickerResult.KEY_PICKED_LOCATION, location);
    getParentFragmentManager()
        .setFragmentResult(LocationPickerResult.REQUEST_KEY, result);
    dismiss();
  }
}
