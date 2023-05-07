package com.manuni.sunway;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.manuni.sunway.databinding.ActivityUpdateProfileBinding;
import com.squareup.picasso.Picasso;

public class UpdateProfileActivity extends AppCompatActivity {
    ActivityUpdateProfileBinding binding;
    private String userName,userPhone,userEmail,userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateProfileBinding.inflate(getLayoutInflater());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());

        userName = getIntent().getStringExtra("userName");
        userPhone = getIntent().getStringExtra("userPhone");
        userEmail = getIntent().getStringExtra("userEmail");
        userProfile = getIntent().getStringExtra("userProfile");



        binding.userNameET.setText(userName);
        binding.userEmailET.setText(userEmail);
        binding.userMobileET.setText(userPhone);

        binding.nameTxt.setText(userName);

        try {
            Picasso.get().load(userProfile).into(binding.profilePic);
        } catch (Exception e) {
            binding.profilePic.setImageResource(R.drawable.ic_person);
        }




        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}