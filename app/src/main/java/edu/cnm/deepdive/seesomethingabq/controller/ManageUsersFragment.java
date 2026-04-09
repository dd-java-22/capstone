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
import androidx.navigation.Navigation;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.seesomethingabq.controller.adapter.ManagerUserAdapter;
import edu.cnm.deepdive.seesomethingabq.databinding.FragmentManageUsersBinding;
import edu.cnm.deepdive.seesomethingabq.viewmodel.ManageUsersViewModel;
import java.util.Locale;

@AndroidEntryPoint
/**
 * Fragment providing a manager-facing entry point for user management screens.
 */
public class ManageUsersFragment extends Fragment {

  private static final String TAG = ManageUsersFragment.class.getSimpleName();

  private FragmentManageUsersBinding binding;
  private ManageUsersViewModel viewModel;
  private ManagerUserAdapter adapter;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentManageUsersBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewModel = new ViewModelProvider(this).get(ManageUsersViewModel.class);
    adapter = new ManagerUserAdapter(user -> {
      Log.d(
          TAG,
          String.format(
              Locale.US,
              "Tapped user: externalId=%s; name=%s; email=%s; manager=%s; enabled=%s",
              user.getExternalId(),
              user.getDisplayName(),
              user.getEmail(),
              user.getManager(),
              user.getUserEnabled()
          )
      );
      Navigation.findNavController(view)
          .navigate(ManageUsersFragmentDirections
              .navigateToManagerUserDetailFragment(user.getExternalId()));
    });
    binding.managerUsersRecycler.setAdapter(adapter);
    viewModel.getUsers(requireActivity()).observe(getViewLifecycleOwner(), pagingData -> {
      adapter.submitData(getViewLifecycleOwner().getLifecycle(), pagingData);
    });
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }
}
