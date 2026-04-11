package edu.cnm.deepdive.seesomethingabq.controller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.seesomethingabq.databinding.FragmentUserProfileBinding;
import edu.cnm.deepdive.seesomethingabq.viewmodel.UserViewModel;

@AndroidEntryPoint
public class UserProfileFragment extends Fragment {

  private FragmentUserProfileBinding binding;
  private UserViewModel userViewModel;

  private final ActivityResultLauncher<String> pickAvatarLauncher =
      registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
        if (uri != null) {
          binding.avatarImage.setImageURI(uri);
          userViewModel.updateAvatar(requireActivity(), uri);
        }
      });

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    binding = FragmentUserProfileBinding.inflate(inflater, container, false);

    binding.editAvatarButton.setOnClickListener(v ->
        pickAvatarLauncher.launch("image/*")
    );

    binding.saveProfileButton.setOnClickListener(v -> {
      String displayName = binding.displayNameInput.getText().toString().trim();
      String email = binding.emailInput.getText().toString().trim();

      if (!displayName.isEmpty() && !email.isEmpty()) {
        userViewModel.updateProfile(requireActivity(), displayName, email);
      }
    });

    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

    userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
      if (user != null) {
        binding.displayNameInput.setText(user.getDisplayName());
        binding.emailInput.setText(user.getEmail());
        binding.usernameValue.setText(user.getDisplayName());
        binding.emailValue.setText(user.getEmail());
        // TODO: Load avatar image from user.getAvatar()
      }
    });
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }
}
