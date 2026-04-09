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

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.seesomethingabq.R;
import edu.cnm.deepdive.seesomethingabq.databinding.FragmentUserDashboardBinding;
import edu.cnm.deepdive.seesomethingabq.viewmodel.IssueReportViewModel;
import edu.cnm.deepdive.seesomethingabq.viewmodel.UserViewModel;
import java.util.Collections;

@AndroidEntryPoint
/**
 * Fragment showing the signed-in user's dashboard and navigation to report creation.
 */
public class UserDashboardFragment extends Fragment {

  private FragmentUserDashboardBinding binding;
  private UserViewModel viewModel;

  // 1️⃣ Java version of ActivityResultLauncher
  private final ActivityResultLauncher<String> pickImageLauncher =
      registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
        if (uri != null) {
          uploadTestImage(uri);
        }
      });

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    binding = FragmentUserDashboardBinding.inflate(inflater, container, false);

    // Existing button
    binding.createIssueButton.setOnClickListener((v) -> {
      NavController navController = Navigation.findNavController(v);
      navController.navigate(R.id.navigate_to_create_issue_report_fragment);
    });

    // 2️⃣ NEW: Test upload button
    binding.testUploadButton.setOnClickListener(v -> {
      pickImageLauncher.launch("image/*");
    });

    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

    viewModel.getUser()
        .observe(getViewLifecycleOwner(), user -> {
          if (user != null) {
            binding.displayName.setText(user.getDisplayName());
            binding.oauthKey.setText(user.getOauthKey());
            binding.externalKey.setText(user.getExternalId().toString());
          }
        });
  }

  // 3️⃣ Upload test image
  private void uploadTestImage(Uri uri) {

    // TODO: Replace with a real reportId from your backend
    String testReportId = "3c9c48ad-f3bc-4e73-ab1e-52a22239c044";

    IssueReportViewModel reportViewModel =
        new ViewModelProvider(requireActivity()).get(IssueReportViewModel.class);

    reportViewModel.uploadImages(requireActivity(), testReportId, Collections.singletonList(uri))
        .thenAccept(result -> requireActivity().runOnUiThread(() ->
            Toast.makeText(requireContext(), "Upload complete!", Toast.LENGTH_SHORT).show()
        ))
        .exceptionally(e -> {
          requireActivity().runOnUiThread(() ->
              Toast.makeText(requireContext(), "Upload failed: " + e.getMessage(),
                  Toast.LENGTH_LONG).show()
          );
          return null;
        });
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }
}
