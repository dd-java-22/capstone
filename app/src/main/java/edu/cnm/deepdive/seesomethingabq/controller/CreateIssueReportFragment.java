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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.seesomethingabq.R;
import edu.cnm.deepdive.seesomethingabq.databinding.FragmentCreateIssueReportBinding;

@AndroidEntryPoint
public class CreateIssueReportFragment extends Fragment {

  private FragmentCreateIssueReportBinding binding;

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
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }
}
