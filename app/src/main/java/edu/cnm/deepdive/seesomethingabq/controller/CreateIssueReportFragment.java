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

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.BundleCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.seesomethingabq.R;
import edu.cnm.deepdive.seesomethingabq.databinding.FragmentCreateIssueReportBinding;
import edu.cnm.deepdive.seesomethingabq.model.domain.PickedLocation;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType;
import edu.cnm.deepdive.seesomethingabq.viewmodel.IssueTypeViewModel;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@AndroidEntryPoint
public class CreateIssueReportFragment extends Fragment {

  private FragmentCreateIssueReportBinding binding;
  private IssueTypeViewModel issueTypeViewModel;
  private final Set<String> selectedIssueTypeTags = new HashSet<>();
  private PickedLocation confirmedLocation;
  private boolean applyingPickedLocation;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentCreateIssueReportBinding.inflate(inflater, container, false);
    binding.backToDashboardButton.setOnClickListener((v) -> {
      NavController navController = Navigation.findNavController(v);
      navController.navigate(R.id.navigate_to_user_dashboard_fragment);
    });
    binding.useCurrentLocationButton.setOnClickListener((v) -> {
      NavController navController = Navigation.findNavController(v);
      navController.navigate(R.id.navigate_to_location_picker_dialog);
    });
    binding.submitButton.setOnClickListener((v) -> onSubmitClicked());
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
    issueTypeViewModel.getIssueTypes()
        .observe(getViewLifecycleOwner(), this::populateIssueTypeChips);
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
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
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

  private void onSubmitClicked() {
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
    // TODO: pass buildLocationPayload() to submission service
    buildLocationPayload();
  }

  private Bundle buildLocationPayload() {
    Bundle payload = new Bundle();
    payload.putString("streetCoordinate", confirmedLocation.getDisplayText());
    payload.putDouble("latitude", confirmedLocation.getLatitude());
    payload.putDouble("longitude", confirmedLocation.getLongitude());
    return payload;
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
    if (binding != null) {
      binding.locationLayout.setHelperText(
          hadConfirmed ? getString(R.string.location_unconfirmed_edit) : null);
    }
  }
}
