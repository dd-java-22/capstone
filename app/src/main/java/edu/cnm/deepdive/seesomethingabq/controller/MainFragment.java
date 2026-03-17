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
import edu.cnm.deepdive.seesomethingabq.databinding.FragmentMainBinding;
import edu.cnm.deepdive.seesomethingabq.viewmodel.LoginViewModel;

@AndroidEntryPoint
public class MainFragment extends Fragment {

  private FragmentMainBinding binding;
  private LoginViewModel viewModel;


  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentMainBinding.inflate(inflater, container, false);
    binding.signOut.setOnClickListener((v) -> viewModel.signOut());
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
    viewModel
        .getCredential()
        .observe(getViewLifecycleOwner(), (credential) -> {
          if (credential != null) {
            binding.displayName.setText(credential.getDisplayName());
            binding.idToken.setText(credential.getIdToken());
            // TODO 2026-03-16: replace with server login function!!
          } else {
            Navigation.findNavController(binding.getRoot())
                .navigate(MainFragmentDirections.navigateToLoginFragment());
          }
        });
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }
}
