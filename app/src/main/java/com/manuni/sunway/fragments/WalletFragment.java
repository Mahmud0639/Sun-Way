package com.manuni.sunway.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.manuni.sunway.R;
import com.manuni.sunway.databinding.FragmentWalletBinding;
import com.manuni.sunway.tabadapter.TabPagerAdapter;
import com.manuni.sunway.tabfragments.RankingFragment;
import com.manuni.sunway.tabfragments.WithdrawFragment;

import org.checkerframework.checker.units.qual.C;

import java.text.DecimalFormat;

public class WalletFragment extends Fragment {
    private Context mContext;

    public WalletFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    FragmentWalletBinding binding;
    private DatabaseReference dbRef;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWalletBinding.inflate(inflater,container,false);

        dbRef = FirebaseDatabase.getInstance().getReference().child("Users");
        auth = FirebaseAuth.getInstance();

        dbRef.child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String messageAdmin = ""+snapshot.child("adminMessage").getValue();
                String balanceOf = ""+snapshot.child("balance").getValue();

                Double balanceDouble = Double.parseDouble(balanceOf);
                binding.currentCoins.setText(String.format("$%.2f",balanceDouble));

                if (balanceDouble <= 40.0){
                    binding.healthyAccount.setText("Unhealthy balance");
                    binding.healthyAccount.setTextColor(Color.RED);
                }else {
                    binding.healthyAccount.setText("Healthy balance");
                    binding.healthyAccount.setTextColor(Color.GREEN);
                }

               // binding.currentCoins.setText("$"+String.valueOf(Double.valueOf( decimalFormat.format(balanceOf))));
               // binding.currentCoins
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        binding.paypalEmailBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 34){
                   // binding.invisibleBtn.setVisibility(View.VISIBLE);
                    binding.sendRequest.setEnabled(false);
                }else {
                  //  binding.invisibleBtn.setVisibility(View.INVISIBLE);
                    binding.sendRequest.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "I am send btn.", Toast.LENGTH_SHORT).show();
                binding.sendRequest.setEnabled(false);

            }
        });






        return binding.getRoot();
    }

}