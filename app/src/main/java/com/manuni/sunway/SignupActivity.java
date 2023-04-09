package com.manuni.sunway;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.manuni.sunway.databinding.ActivitySignupBinding;

public class SignupActivity extends AppCompatActivity {
    ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.countryCodePicker.registerCarrierNumberEditText(binding.phoneNumberET);

        binding.sendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this,GetOTPActivity.class);
                intent.putExtra("mobile",binding.countryCodePicker.getFullNumberWithPlus().replace(" ",""));
                startActivity(intent);
            }
        });
    }
}