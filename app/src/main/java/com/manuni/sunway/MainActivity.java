package com.manuni.sunway;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.manuni.sunway.databinding.ActivityMainBinding;
import com.manuni.sunway.fragments.HomeFragment;
import com.manuni.sunway.fragments.PackageFragment;
import com.manuni.sunway.fragments.ProfileFragment;
import com.manuni.sunway.fragments.WalletFragment;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {
    ActivityMainBinding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();


        binding.bottomNav.setOnItemSelectedListener(this);

        loadFragments(new HomeFragment());


        PopupMenu popupMenu = new PopupMenu(MainActivity.this,binding.moreBtn);
        popupMenu.getMenu().add("Logout");

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
//logout dorkar nai...ei app er khetre...na hole problem hobe..apatoto rakha holo...test er uddesshe...
                if (item.getTitle() == "Logout"){
                    auth.signOut();

                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    finish();
                }

                return true;
            }
        });

        binding.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu.show();
            }
        });




    }


    public boolean loadFragments(Fragment fragment){
        if (fragment != null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragContainer,fragment).commit();
        }

        return true;
    }




    @Override
    public void onBackPressed() {
        if (binding.bottomNav.getSelectedItemId()==R.id.home){
            super.onBackPressed();
            finish();
        }else {
            binding.bottomNav.setSelectedItemId(R.id.home);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Fragment myFragment = null;
        switch (item.getItemId()){
            case R.id.home:
                myFragment = new HomeFragment();
                break;
            case R.id.my_package:
                myFragment = new PackageFragment();
                break;
            case R.id.wallet:
                myFragment = new WalletFragment();
                break;
            case R.id.profile:
                myFragment = new ProfileFragment();
                break;
        }
        return loadFragments(myFragment);
    }
}