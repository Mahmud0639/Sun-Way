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
import com.manuni.sunway.PackageModel;
import com.manuni.sunway.R;
import com.manuni.sunway.TaskAdapter;
import com.manuni.sunway.TaskModel;
import com.manuni.sunway.databinding.FragmentHomeBinding;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private Context mContext;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }
    FragmentHomeBinding binding;
    private FirebaseAuth auth;
    private ArrayList<PackageModel> list;
    private TaskAdapter taskAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater,container,false);

        auth = FirebaseAuth.getInstance();
        loadTaskPack();






        return binding.getRoot();
    }

    private void loadTaskPack() {
        binding.progressBar.setVisibility(View.VISIBLE);
        list = new ArrayList<>();
        DatabaseReference taskRef = FirebaseDatabase.getInstance().getReference().child("PackageInfo");
        taskRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()){
                    list.clear();
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        PackageModel data = dataSnapshot.getValue(PackageModel.class);
                        list.add(data);
                        /*String myPackName = ""+dataSnapshot.child("packName").getValue();

                        if (myPackName.equals("unlocked")){

                        }*/
                    }
                    taskAdapter = new TaskAdapter(mContext,list);
                    binding.workPackRV.setAdapter(taskAdapter);
                    taskAdapter.notifyDataSetChanged();
                    binding.workPackRV.setHasFixedSize(true);
                    binding.progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

    }
}