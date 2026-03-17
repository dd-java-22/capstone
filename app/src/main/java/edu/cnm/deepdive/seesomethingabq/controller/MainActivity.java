package edu.cnm.deepdive.seesomethingabq.controller;

import android.os.Bundle;
import android.view.ViewGroup.MarginLayoutParams;
import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.seesomethingabq.R;
import edu.cnm.deepdive.seesomethingabq.databinding.ActivityMainBinding;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

  private ActivityMainBinding binding;
  private NavController navController;
  private AppBarConfiguration appBarConfig;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setupUI();
    setupNavigation();
  }

  @Override
  public boolean onSupportNavigateUp() {
    return NavigationUI.navigateUp(navController, appBarConfig) || super.onSupportNavigateUp();
  }

  private void setupUI() {
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    EdgeToEdge.enable(this);
    ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (view, insets) -> {
      Insets bounds = insets.getInsets(WindowInsetsCompat.Type.systemBars());
      MarginLayoutParams params = (MarginLayoutParams) view.getLayoutParams();
      params.setMargins(bounds.left, bounds.top, bounds.right, bounds.bottom);
      view.setLayoutParams(params);
      return WindowInsetsCompat.CONSUMED;
    });
  }
  private void setupNavigation() {
    appBarConfig = new AppBarConfiguration.Builder(R.id.login_fragment, R.id.main_fragment).build();
    NavHostFragment host = binding.navHostFragmentContainer.getFragment();
    navController = host.getNavController();
    NavigationUI.setupActionBarWithNavController(this, navController, appBarConfig);
  }
}
