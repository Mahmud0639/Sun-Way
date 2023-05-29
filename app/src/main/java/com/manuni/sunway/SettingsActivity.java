package com.manuni.sunway;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.CompoundButton;

import com.manuni.sunway.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {
    ActivitySettingsBinding binding;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int themeMode = sharedPreferences.getInt("theme_mode",0);

        // Set the theme based on the selected theme mode
        if (themeMode == 0){
            setTheme(R.style.AppTheme_Light);
        }else{
            setTheme(R.style.AppTheme_Dark);
        }


        setContentView(binding.getRoot());


        binding.themeSwitch.setChecked(themeMode == 1);

        binding.themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                // Update the selected theme mode in SharedPreferences

             SharedPreferences.Editor editor =   sharedPreferences.edit();
             editor.putInt("theme_mode",isChecked ? 1:0);
             editor.apply();


                // Restart the activity to apply the new theme

                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });




    }
}