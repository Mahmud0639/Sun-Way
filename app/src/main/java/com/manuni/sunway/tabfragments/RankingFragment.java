package com.manuni.sunway.tabfragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.manuni.sunway.R;
import com.manuni.sunway.RankAdapter;
import com.manuni.sunway.UsersData;
import com.manuni.sunway.databinding.FragmentRankingBinding;

import java.util.ArrayList;


public class RankingFragment extends Fragment {
private Context mContext;

    public RankingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
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

    FragmentRankingBinding binding;
    private ArrayList<UsersData> data;
    private RankAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentRankingBinding.inflate(inflater,container,false);

        data = new ArrayList<>();
        binding.progressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore.getInstance().collection("users")
                .orderBy("balance", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (DocumentSnapshot snapshot: queryDocumentSnapshots){
                    UsersData usersData = snapshot.toObject(UsersData.class);
                    data.add(usersData);
                }
                adapter = new RankAdapter(getContext(),data);
                binding.rankRV.setAdapter(adapter);
                binding.rankRV.setHasFixedSize(true);
                adapter.notifyDataSetChanged();

                binding.progressBar.setVisibility(View.INVISIBLE);

            }
        });




        return binding.getRoot();
    }
}