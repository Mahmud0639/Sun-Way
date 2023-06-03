package com.manuni.sunway;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.manuni.sunway.databinding.ActivityNoInternetBinding;

public class NoInternetActivity extends AppCompatActivity {

    ActivityNoInternetBinding binding;
    private FirebaseAuth auth;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityNoInternetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        auth = FirebaseAuth.getInstance();

        dbRef = FirebaseDatabase.getInstance().getReference().child("Users");


        binding.refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.refresh.setText("Refreshing...");

                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                if (wifi.isConnected()){
                    loadAuthUser();
                }else if (mobile.isConnected()){
                    loadAuthUser();
                }else {
                    binding.refresh.setText("Refresh");
                    Toast.makeText(NoInternetActivity.this, "Connect your device with a connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void loadAuthUser(){
        if (auth.getCurrentUser()==null){
            startActivity(new Intent(NoInternetActivity.this,SignUpActivityForGoogle.class));
            finish();
        }else {


            dbRef.child(auth.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (!snapshot.exists()){
                        startActivity(new Intent(NoInternetActivity.this,UserInfoActivity.class));
                        finish();
                    }else {
                        startActivity(new Intent(NoInternetActivity.this,MainActivity.class));
                        finish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });
                    /*startActivity(new Intent(SplashActivity.this,SignUpActivityForGoogle.class));
                    finish();*/
        }
    }
}