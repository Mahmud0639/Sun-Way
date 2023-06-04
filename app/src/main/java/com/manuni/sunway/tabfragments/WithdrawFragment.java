package com.manuni.sunway.tabfragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.manuni.sunway.R;
import com.manuni.sunway.WithdrawAdapter;
import com.manuni.sunway.WithdrawModel;
import com.manuni.sunway.databinding.FragmentWithdrawBinding;

import java.util.ArrayList;

public class WithdrawFragment extends Fragment {


    private ArrayList<WithdrawModel> list;
    private WithdrawAdapter adapter;

    public WithdrawFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    private FragmentWithdrawBinding binding;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentWithdrawBinding.inflate(inflater,container,false);



        loadWithdrawList();


        return binding.getRoot();
    }
    private void loadWithdrawList(){

        list = new ArrayList<>();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Withdraws");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()){
                    binding.noDataFound.setVisibility(View.GONE);
                    binding.lottieLoopDot.setVisibility(View.VISIBLE);


                    list.clear();

                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        WithdrawModel data = dataSnapshot.getValue(WithdrawModel.class);
                        list.add(0,data);

                    }

                    binding.withdrawRV.setLayoutManager(new LinearLayoutManager(getContext()));
                    binding.withdrawRV.setHasFixedSize(true);
                    adapter = new WithdrawAdapter(list,getContext());
                    binding.withdrawRV.setAdapter(adapter);

                    binding.lottieLoopDot.setVisibility(View.INVISIBLE);


                }else {
                    binding.noDataFound.setVisibility(View.VISIBLE);
                    binding.lottieLoopDot.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

    }
}