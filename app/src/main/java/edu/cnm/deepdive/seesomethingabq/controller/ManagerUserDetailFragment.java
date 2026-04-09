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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
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
  private boolean mutationInProgress;
  private PendingMutation pendingMutation;
  private boolean pendingTargetValue;

  private final DateTimeFormatter dateTimeFormatter =
      DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withZone(ZoneId.systemDefault());

  private enum PendingMutation {
    MANAGER_STATUS,
    ENABLED_STATUS
  }

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
        if (mutationInProgress) {
          showMutationFailure();
          mutationInProgress = false;
          pendingMutation = null;
          setButtonsEnabled(true);
        } else {
          Snackbar.make(binding.getRoot(), "Failed to load user details", Snackbar.LENGTH_SHORT)
              .show();
        }
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
    binding.email.setText("Loading...");
    binding.authority.setText("Loading...");
    binding.created.setText("Created: Loading...");
    binding.disabledMessage.setVisibility(View.GONE);
    binding.managerAuthButton.setText("Authorize as Manager");
    binding.accountActivationButton.setText("Deactivate Account");
    binding.externalIdValue.setText(String.valueOf(externalId));
  }

  private void bindUser(UserProfileSummary user) {
    if (user == null || binding == null) {
      return;
    }
    String displayName = user.getDisplayName();
    binding.displayName.setText((displayName != null && !displayName.isBlank()) ? displayName : "(No name)");
    String email = user.getEmail();
    binding.email.setText((email != null && !email.isBlank()) ? email : "(No email)");
    binding.authority.setText(user.getManager() ? "Manager" : "User");
    binding.created.setText("Created: " + dateTimeFormatter.format(user.getTimeCreated()));
    binding.disabledMessage.setVisibility(user.getUserEnabled() ? View.GONE : View.VISIBLE);
    binding.managerAuthButton.setText(
        user.getManager() ? "Revoke Manager Authorization" : "Authorize as Manager");
    binding.accountActivationButton.setText(
        user.getUserEnabled() ? "Deactivate Account" : "Reactivate Account");

    if (mutationInProgress) {
      showMutationSuccess();
      mutationInProgress = false;
      pendingMutation = null;
      setButtonsEnabled(true);
    } else {
      setButtonsEnabled(true);
    }
  }

  private void setupButtons() {
    binding.managerAuthButton.setOnClickListener((v) -> {
      UserProfileSummary user = viewModel.getUser().getValue();
      if (user == null || mutationInProgress) {
        return;
      }
      boolean targetIsManager = !user.getManager();
      String title = user.getManager() ? "Revoke Manager Authorization?" : "Authorize as Manager?";
      String message = user.getManager()
          ? "This will revoke manager authorization for this user."
          : "This will authorize this user as a manager.";
      new MaterialAlertDialogBuilder(requireContext())
          .setTitle(title)
          .setMessage(message)
          .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
          .setPositiveButton("Confirm", (dialog, which) -> {
            mutationInProgress = true;
            pendingMutation = PendingMutation.MANAGER_STATUS;
            pendingTargetValue = targetIsManager;
            setButtonsEnabled(false);
            viewModel.setManagerStatus(requireActivity(), externalId, targetIsManager);
          })
          .show();
    });
    binding.accountActivationButton.setOnClickListener((v) -> {
      UserProfileSummary user = viewModel.getUser().getValue();
      if (user == null || mutationInProgress) {
        return;
      }
      boolean targetIsEnabled = !user.getUserEnabled();
      String title = user.getUserEnabled() ? "Deactivate Account?" : "Reactivate Account?";
      String message = user.getUserEnabled()
          ? "This will deactivate the account and prevent sign-in."
          : "This will reactivate the account and allow sign-in.";
      new MaterialAlertDialogBuilder(requireContext())
          .setTitle(title)
          .setMessage(message)
          .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
          .setPositiveButton("Confirm", (dialog, which) -> {
            mutationInProgress = true;
            pendingMutation = PendingMutation.ENABLED_STATUS;
            pendingTargetValue = targetIsEnabled;
            setButtonsEnabled(false);
            viewModel.setEnabledStatus(requireActivity(), externalId, targetIsEnabled);
          })
          .show();
    });
  }

  private void setButtonsEnabled(boolean enabled) {
    if (binding == null) {
      return;
    }
    binding.managerAuthButton.setEnabled(enabled);
    binding.accountActivationButton.setEnabled(enabled);
  }

  private void showMutationSuccess() {
    if (binding == null || pendingMutation == null) {
      return;
    }
    String message;
    if (pendingMutation == PendingMutation.MANAGER_STATUS) {
      message = pendingTargetValue ? "Manager authorization granted" : "Manager authorization revoked";
    } else {
      message = pendingTargetValue ? "Account reactivated" : "Account deactivated";
    }
    Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
  }

  private void showMutationFailure() {
    if (binding == null || pendingMutation == null) {
      return;
    }
    String message;
    if (pendingMutation == PendingMutation.MANAGER_STATUS) {
      message = "Failed to update manager authorization";
    } else {
      message = "Failed to update account activation";
    }
    Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
  }

}
