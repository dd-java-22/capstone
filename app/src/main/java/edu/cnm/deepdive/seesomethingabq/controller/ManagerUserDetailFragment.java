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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.seesomethingabq.databinding.FragmentManagerUserDetailBinding;
import edu.cnm.deepdive.seesomethingabq.model.dto.UserProfileSummary;
import edu.cnm.deepdive.seesomethingabq.viewmodel.ManagerUserDetailViewModel;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.UUID;

@AndroidEntryPoint
/**
 * Fragment displaying manager-facing details for a single user.
 *
 * Read-only first pass; edit/mutation actions are placeholders.
 */
public class ManagerUserDetailFragment extends Fragment {

  private static final String TAG = ManagerUserDetailFragment.class.getSimpleName();

  private FragmentManagerUserDetailBinding binding;
  private ManagerUserDetailViewModel viewModel;
  private UUID externalId;

  private final DateTimeFormatter dateTimeFormatter =
      DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withZone(ZoneId.systemDefault());

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentManagerUserDetailBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    externalId = ManagerUserDetailFragmentArgs.fromBundle(requireArguments()).getExternalId();
    viewModel = new ViewModelProvider(this).get(ManagerUserDetailViewModel.class);

    bindPlaceholders(externalId);
    setupButtons();

    viewModel.getUser().observe(getViewLifecycleOwner(), this::bindUser);
    viewModel.getThrowable().observe(getViewLifecycleOwner(), (throwable) -> {
      if (throwable != null) {
        Log.e(TAG, throwable.getMessage(), throwable);
        Toast.makeText(requireContext(), "Failed to load user details", Toast.LENGTH_SHORT).show();
      }
    });
    viewModel.load(requireActivity(), externalId);
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }

  private void bindPlaceholders(UUID externalId) {
    binding.displayName.setText("Loading...");
    binding.email.setText("");
    binding.authority.setText("");
    binding.created.setText("");
    binding.disabledMessage.setVisibility(View.GONE);
    binding.managerAuthButton.setText("Authorize as Manager");
    binding.accountActivationButton.setText("Deactivate Account");
    binding.externalIdValue.setText(String.valueOf(externalId));
  }

  private void bindUser(UserProfileSummary user) {
    if (user == null || binding == null) {
      return;
    }
    binding.displayName.setText(user.getDisplayName());
    binding.email.setText(user.getEmail());
    binding.authority.setText(user.getManager() ? "Manager" : "User");
    binding.created.setText(dateTimeFormatter.format(user.getTimeCreated()));
    binding.disabledMessage.setVisibility(user.getUserEnabled() ? View.GONE : View.VISIBLE);
    binding.managerAuthButton.setText(
        user.getManager() ? "Revoke Manager Authorization" : "Authorize as Manager");
    binding.accountActivationButton.setText(
        user.getUserEnabled() ? "Deactivate Account" : "Reactivate Account");
  }

  private void setupButtons() {
    binding.managerAuthButton.setOnClickListener((v) -> {
      Log.d(TAG, "Manager auth button tapped for externalId=" + externalId);
      Toast.makeText(requireContext(), "Not implemented yet", Toast.LENGTH_SHORT).show();
    });
    binding.accountActivationButton.setOnClickListener((v) -> {
      Log.d(TAG, "Account activation button tapped for externalId=" + externalId);
      Toast.makeText(requireContext(), "Not implemented yet", Toast.LENGTH_SHORT).show();
    });
  }

}
