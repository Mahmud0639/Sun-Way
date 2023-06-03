package com.manuni.sunway;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.manuni.sunway.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {
    ActivitySplashBinding binding;
    private FirebaseAuth auth;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        dbRef = FirebaseDatabase.getInstance().getReference().child("Users");


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                if (wifi.isConnected()){
                   loadAuthUser();
                }else if (mobile.isConnected()){
                    loadAuthUser();
                }else {
                    startActivity(new Intent(SplashActivity.this,NoInternetActivity.class));
                    finish();
                }



            }
        },3000);


    }
    private void loadAuthUser(){
        if (auth.getCurrentUser()==null){
            startActivity(new Intent(SplashActivity.this,SignUpActivityForGoogle.class));
            finish();
        }else {


            dbRef.child(auth.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (!snapshot.exists()){
                        startActivity(new Intent(SplashActivity.this,UserInfoActivity.class));
                        finish();
                    }else {
                        startActivity(new Intent(SplashActivity.this,MainActivity.class));
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