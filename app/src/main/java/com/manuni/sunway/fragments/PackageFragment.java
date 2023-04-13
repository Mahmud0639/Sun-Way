package com.manuni.sunway.fragments;

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
import com.manuni.sunway.PackageAdapter;
import com.manuni.sunway.PackageModel;
import com.manuni.sunway.R;
import com.manuni.sunway.databinding.FragmentPackageBinding;

import java.util.ArrayList;

public class PackageFragment extends Fragment {
private Context mContext;

    public PackageFragment() {
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

    FragmentPackageBinding binding;
    PackageAdapter adapter;
    private ArrayList<PackageModel> list;
    private DatabaseReference reference;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentPackageBinding.inflate(inflater,container,false);

        binding.progressBar.setVisibility(View.VISIBLE);
        list = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference().child("PackageInfo");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (snapshot.exists()){

                    list.clear();
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                        PackageModel data = dataSnapshot.getValue(PackageModel.class);
                        list.add(data);
                    }

                    adapter = new PackageAdapter(mContext,list);

                    binding.packageRV.setAdapter(adapter);
                    binding.packageRV.setHasFixedSize(true);
                    binding.packageRV.setLayoutManager(new LinearLayoutManager(mContext));
                    binding.totalVip.setText("Total VIP: "+snapshot.getChildrenCount());

                    adapter.notifyDataSetChanged();
                    binding.progressBar.setVisibility(View.GONE);
                }


            }

            @Override
            public void onCancelled(DatabaseError error) {

                binding.progressBar.setVisibility(View.GONE);

            }
        });


        return binding.getRoot();
    }
}