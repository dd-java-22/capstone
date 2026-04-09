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
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.seesomethingabq.R;
import edu.cnm.deepdive.seesomethingabq.controller.adapter.IssueReportAdapter;
import edu.cnm.deepdive.seesomethingabq.databinding.FragmentUserDashboardBinding;
import edu.cnm.deepdive.seesomethingabq.viewmodel.IssueReportViewModel;
import edu.cnm.deepdive.seesomethingabq.viewmodel.UserViewModel;
import kotlin.Unit;

@AndroidEntryPoint
/**
 * Fragment showing the signed-in user's dashboard and navigation to report creation.
 */
public class UserDashboardFragment extends Fragment {

  private FragmentUserDashboardBinding binding;
  private UserViewModel userViewModel;
  private IssueReportViewModel issueReportViewModel;
  private IssueReportAdapter adapter;


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
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    issueReportViewModel = new ViewModelProvider(requireActivity()).get(IssueReportViewModel.class);

    userViewModel.getUser()
        .observe(getViewLifecycleOwner(), user -> {
          if (user != null) {
            binding.displayName.setText(user.getDisplayName());
            binding.oauthKey.setText(user.getOauthKey());
            binding.externalKey.setText(user.getExternalId().toString());
          }
        });

    adapter = new IssueReportAdapter(issueReport -> {
      Navigation.findNavController(view)
          .navigate(UserDashboardFragmentDirections.navigateToReportDetailFragment(
              issueReport.getExternalId().toString()
          ));
      return Unit.INSTANCE;
    });
    binding.issueReportsRecycler.setAdapter(adapter);

    NavController navController = Navigation.findNavController(view);
    NavBackStackEntry currentEntry = navController.getCurrentBackStackEntry();
    if (currentEntry != null) {
      currentEntry.getSavedStateHandle()
          .<Boolean>getLiveData(UserDashboardRefresh.USER_REPORTS_REFRESH_REQUIRED, false)
          .observe(getViewLifecycleOwner(), refreshRequired -> {
            if (Boolean.TRUE.equals(refreshRequired)) {
              currentEntry.getSavedStateHandle()
                  .set(UserDashboardRefresh.USER_REPORTS_REFRESH_REQUIRED, false);
              refreshMyReports();
            }
          });
    }

    issueReportViewModel
        .getMyIssueReports(requireActivity())
        .observe(
            getViewLifecycleOwner(),
            pagingData ->
                adapter.submitData(getViewLifecycleOwner().getLifecycle(), pagingData)
        );
  }

  private void refreshMyReports() {
    if (adapter != null) {
      adapter.refresh();
    }
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }
}
