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
import edu.cnm.deepdive.seesomethingabq.databinding.FragmentLoginBinding;
import edu.cnm.deepdive.seesomethingabq.viewmodel.LoginViewModel;

@AndroidEntryPoint
public class LoginFragment extends Fragment {

  FragmentLoginBinding binding;
  LoginViewModel loginViewModel;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentLoginBinding.inflate(inflater, container, false);
    binding.loginButton.setOnClickListener((v) ->loginViewModel.signIn(requireActivity()));
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);
    loginViewModel
        .getCredential()
        .observe(getViewLifecycleOwner(), (credential) -> {
          if (credential != null) {
            Navigation.findNavController(binding.getRoot())
                .navigate(LoginFragmentDirections.navigateToMainFragment());
          }
        });
    loginViewModel
        .getThrowable()
        .observe(getViewLifecycleOwner(), (throwable) -> {
          if (throwable != null) {
            binding.loginButton.setEnabled(true);
            binding.loginButton.setVisibility(View.VISIBLE);
            // TODO: 3/12/2026 show a snackbar.
          }
        });
    loginViewModel.signInQuickly(requireActivity());
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }
}
