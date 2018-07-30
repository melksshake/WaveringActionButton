package com.natalia.melkonyan.waveringactionbutton;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.natalia.melkonyan.waveringactionbutton.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
  private ActivityMainBinding binding;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

    WaveringActionButton animatedFloatingButton = new WaveringActionButton(
      getWindowManager(),
      getApplication(),
      binding.popup.touchingButton,
      binding.popup.popupRoot,
      binding.popup.vPulse,
      v -> onWaveringActionButtonClicked());
    animatedFloatingButton.start();
  }

  private void onWaveringActionButtonClicked() {
    Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
  }
}
