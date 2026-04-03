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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.google.android.material.chip.Chip;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.seesomethingabq.R;
import edu.cnm.deepdive.seesomethingabq.databinding.FragmentCreateIssueReportBinding;
import edu.cnm.deepdive.seesomethingabq.model.entity.IssueType;
import edu.cnm.deepdive.seesomethingabq.viewmodel.IssueTypeViewModel;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AndroidEntryPoint
public class CreateIssueReportFragment extends Fragment {

  private FragmentCreateIssueReportBinding binding;
  private IssueTypeViewModel issueTypeViewModel;
  private final Set<Long> selectedIssueTypeIds = new HashSet<>();

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentCreateIssueReportBinding.inflate(inflater, container, false);
    binding.backToDashboardButton.setOnClickListener((v) -> {
      NavController navController = Navigation.findNavController(v);
      navController.navigate(R.id.navigate_to_user_dashboard_fragment);
    });
    return binding.getRoot();
  }

  // TODO: 4/3/2026 implement dialog fragment

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    issueTypeViewModel = new ViewModelProvider(requireActivity()).get(IssueTypeViewModel.class);
    issueTypeViewModel.getIssueTypes()
        .observe(getViewLifecycleOwner(), this::populateIssueTypeChips);
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
      Chip chip = new Chip(requireContext(), null,
          com.google.android.material.R.attr.chipGroupChoiceStyle);
      chip.setText(issueType.getIssueTypeTag());
      chip.setCheckable(true);
      chip.setTag(issueType.getId());
      chip.setChecked(selectedIssueTypeIds.contains(issueType.getId()));
      chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
        long id = (long) buttonView.getTag();
        if (isChecked) {
          selectedIssueTypeIds.add(id);
        } else {
          selectedIssueTypeIds.remove(id);
        }
      });
      binding.issueTypeChipGroup.addView(chip);
    }
  }
}
