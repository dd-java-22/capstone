package edu.cnm.deepdive.seesomethingabq.controller;

import android.os.Bundle;
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
import com.bumptech.glide.Glide;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.seesomethingabq.R;
import edu.cnm.deepdive.seesomethingabq.databinding.FragmentUserProfileBinding;
import edu.cnm.deepdive.seesomethingabq.viewmodel.UserViewModel;

@AndroidEntryPoint
public class UserProfileFragment extends Fragment {

  private FragmentUserProfileBinding binding;
  private UserViewModel userViewModel;
  private String lastKnownAvatarUrl;

  // Avatar picker launcher
  private final ActivityResultLauncher<String> pickAvatarLauncher =
      registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
        if (uri != null) {
          // Optional local preview; do not show success until upload completes.
          binding.avatarImage.setImageURI(uri);

          // Upload avatar to backend
          userViewModel.updateAvatar(requireActivity(), uri);
        }
      });

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    binding = FragmentUserProfileBinding.inflate(inflater, container, false);

    // Avatar edit button
    binding.editAvatarButton.setOnClickListener(v ->
        pickAvatarLauncher.launch("image/*")
    );

    // Save button
    binding.saveProfileButton.setOnClickListener(v -> {
      String displayName = binding.displayNameInput.getText().toString().trim();
      String email = binding.emailInput.getText().toString().trim();

      if (displayName.isEmpty() || email.isEmpty()) {
        Toast.makeText(requireContext(), "Display name and email cannot be empty", Toast.LENGTH_SHORT).show();
        return;
      }

      userViewModel.updateProfile(requireActivity(), displayName, email);
      Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show();
    });

    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

    // Observe user data
    userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
      if (user != null) {

        // Editable fields
        binding.displayNameInput.setText(user.getDisplayName());
        binding.emailInput.setText(user.getEmail());

        // Read-only fields
        binding.usernameValue.setText(user.getDisplayName());
        binding.emailValue.setText(user.getEmail());

        // Load avatar (URL → String)
        if (user.getAvatar() != null) {
          lastKnownAvatarUrl = user.getAvatar().toString();
          Glide.with(this)
              .load(user.getAvatar().toString())
              .placeholder(R.drawable.ic_default_avatar)
              .error(R.drawable.ic_default_avatar)
              .into(binding.avatarImage);
        } else {
          lastKnownAvatarUrl = null;
          binding.avatarImage.setImageResource(R.drawable.ic_default_avatar);
        }
      }
    });

    userViewModel.getAvatarUploadSucceeded().observe(getViewLifecycleOwner(), succeeded -> {
      if (succeeded == null) {
        return;
      }
      if (Boolean.TRUE.equals(succeeded)) {
        Toast.makeText(requireContext(), "Avatar updated", Toast.LENGTH_SHORT).show();
      } else {
        Toast.makeText(requireContext(), "Avatar upload failed", Toast.LENGTH_SHORT).show();
        // Revert any local preview to last known server-backed avatar (or placeholder).
        if (lastKnownAvatarUrl != null) {
          Glide.with(this)
              .load(lastKnownAvatarUrl)
              .placeholder(R.drawable.ic_default_avatar)
              .error(R.drawable.ic_default_avatar)
              .into(binding.avatarImage);
        } else {
          binding.avatarImage.setImageResource(R.drawable.ic_default_avatar);
        }
      }
    });
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }
}
