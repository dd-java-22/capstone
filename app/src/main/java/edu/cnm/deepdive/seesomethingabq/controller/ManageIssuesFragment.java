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
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.seesomethingabq.controller.adapter.IssueReportAdapter;
import edu.cnm.deepdive.seesomethingabq.databinding.FragmentManageIssuesBinding;
import edu.cnm.deepdive.seesomethingabq.viewmodel.IssueReportViewModel;

@AndroidEntryPoint
/**
 * Fragment displaying a paged list of issue reports for manager review.
 */
public class ManageIssuesFragment extends Fragment {

  private FragmentManageIssuesBinding binding;
  private IssueReportViewModel issueReportViewModel;
  private IssueReportAdapter adapter;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentManageIssuesBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    issueReportViewModel = new ViewModelProvider(requireActivity()).get(IssueReportViewModel.class);
    adapter = new IssueReportAdapter();
    binding.issueReportsRecycler.setAdapter(adapter);
    issueReportViewModel
        .getIssueReports(requireActivity())
        .observe(getViewLifecycleOwner(), pagingData ->
            adapter.submitData(getViewLifecycleOwner().getLifecycle(), pagingData));
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }
}
