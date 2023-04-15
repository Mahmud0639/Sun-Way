package com.manuni.sunway.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.manuni.sunway.R;
import com.manuni.sunway.databinding.FragmentWalletBinding;

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

               // binding.currentCoins.setText("$"+String.valueOf(Double.valueOf( decimalFormat.format(balanceOf))));
               // binding.currentCoins
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });






        return binding.getRoot();
    }
}