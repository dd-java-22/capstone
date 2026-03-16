package edu.cnm.deepdive.seesomethingabq.controller;

import android.os.Bundle;
import android.view.ViewGroup.MarginLayoutParams;
import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.seesomething.databinding.ActivityMainBinding;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

  ActivityMainBinding binding;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
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
}
