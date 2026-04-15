package edu.cnm.deepdive.seesomethingabq.controller;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.drawable.Drawable;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.seesomethingabq.R;
import edu.cnm.deepdive.seesomethingabq.databinding.FragmentUserProfileBinding;
import edu.cnm.deepdive.seesomethingabq.viewmodel.UserViewModel;
import java.io.File;

/**
 * Fragment that displays and allows updates to the current user's profile.
 *
 * This screen supports editing basic profile fields and uploading an avatar image.
 */
@AndroidEntryPoint
public class UserProfileFragment extends Fragment {

  private FragmentUserProfileBinding binding;
  private UserViewModel userViewModel;
  private Uri lastKnownAvatarUri;
  private String lastAvatarSourceKey;

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
    // avatarDisplayUri is activity-scoped and can replay a previously resolved URI from the prior
    // fragment instance. Clear it before observing to prevent stale avatar replay on re-entry.
    userViewModel.clearResolvedAvatar();

    // Observe user data
    userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
      if (user != null) {

        // Editable fields
        binding.displayNameInput.setText(user.getDisplayName());
        binding.emailInput.setText(user.getEmail());

        // Read-only fields
        binding.authorityLevelValue.setText(
            String.format("Authority Level: %s", user.getManager() ? "Manager" : "User")
        );
        binding.issueReportCountValue.setText(
            String.format("Issue Reports: %d", user.getReportCount())
        );

        // Resolve avatar for display (public URL vs protected backend URL -> cached file).
        // Avoid unnecessary re-resolves when profile fields (e.g., displayName/email/reportCount)
        // change but the avatar source has not.
        String avatarSourceKey = (user.getExternalId() != null ? user.getExternalId().toString() : "")
            + "|" + (user.getAvatar() != null ? user.getAvatar().toString() : "");
        if (!avatarSourceKey.equals(lastAvatarSourceKey)) {
          lastAvatarSourceKey = avatarSourceKey;
          userViewModel.resolveAvatar(requireActivity(), user);
        }
      }
    });

    userViewModel.getAvatarDisplayUri().observe(getViewLifecycleOwner(), uri -> {
      if (uri != null) {
        var request = Glide.with(this)
            .load(uri)
            .error(R.drawable.ic_default_avatar);
        // Keep the currently displayed drawable visible during loads/refreshes to avoid flicker.
        // If there isn't one yet, fall back to the default avatar placeholder.
        Drawable current = binding.avatarImage.getDrawable();
        request = (current != null)
            ? request.placeholder(current)
            : request.placeholder(R.drawable.ic_default_avatar);
        // When loading a deterministic file URI (e.g., cached protected backend avatar), Glide can
        // otherwise reuse a stale cached image. Using the file's mtime as a signature reliably
        // busts Glide's cache when the file is rewritten.
        if ("file".equals(uri.getScheme()) && uri.getPath() != null) {
          File file = new File(uri.getPath());
          request = request.signature(new ObjectKey(file.lastModified()));
        }
        request.into(binding.avatarImage);
        lastKnownAvatarUri = uri;
      } else {
        lastKnownAvatarUri = null;
        lastAvatarSourceKey = null;
        binding.avatarImage.setImageResource(R.drawable.ic_default_avatar);
      }
    });

    userViewModel.getAvatarUploadSucceeded().observe(getViewLifecycleOwner(), succeeded -> {
      if (succeeded == null) {
        return;
      }
      Boolean wasSuccessful = succeeded.getContentIfNotHandled();
      if (wasSuccessful == null) {
        return;
      }
      if (Boolean.TRUE.equals(wasSuccessful)) {
        Toast.makeText(requireContext(), "Avatar updated", Toast.LENGTH_SHORT).show();
      } else {
        Toast.makeText(requireContext(), "Avatar upload failed", Toast.LENGTH_SHORT).show();
        // Revert any local preview to last known server-backed avatar (or placeholder).
        if (lastKnownAvatarUri != null) {
          Glide.with(this)
              .load(lastKnownAvatarUri)
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
