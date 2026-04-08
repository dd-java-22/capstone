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
import androidx.navigation.Navigation;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.seesomethingabq.R;
import edu.cnm.deepdive.seesomethingabq.databinding.FragmentUserLoginBinding;
import edu.cnm.deepdive.seesomethingabq.viewmodel.UserViewModel;

@AndroidEntryPoint
/**
 * Fragment handling user sign-in and navigation into the user dashboard.
 */
public class UserLoginFragment extends Fragment {

  private FragmentUserLoginBinding binding;
  private UserViewModel viewModel;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentUserLoginBinding.inflate(inflater, container, false);
    binding.loginButton.setOnClickListener((v) -> viewModel.signIn(requireActivity()));
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    viewModel
        .getUser()
        .observe(getViewLifecycleOwner(), (user) -> {
          if (user != null) {
            Navigation.findNavController(binding.getRoot())
                .navigate(R.id.navigate_to_user_dashboard_fragment);
          }
        });
    viewModel
        .getThrowable()
        .observe(getViewLifecycleOwner(), (throwable) -> {
          if (throwable != null) {
            binding.loginButton.setEnabled(true);
            binding.loginButton.setVisibility(View.VISIBLE);
            // TODO: show a snackbar.
          }
        });
    viewModel.signIn(requireActivity());
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }
}
