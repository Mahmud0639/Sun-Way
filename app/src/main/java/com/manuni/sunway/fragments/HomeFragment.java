package com.manuni.sunway.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.manuni.sunway.ReferCodeActivity;
import com.manuni.sunway.SignUpActivityForGoogle;
import com.manuni.sunway.TaskAdapter;
import com.manuni.sunway.TaskModel;
import com.manuni.sunway.databinding.FragmentHomeBinding;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

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
        carouselData();

        PopupMenu popupMenu = new PopupMenu(getContext(),binding.moreBtn);
        popupMenu.getMenu().add("Refer Code");
        popupMenu.getMenu().add("Logout");

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getTitle()=="Logout"){
                    auth.signOut();
                    startActivity(new Intent(getContext(), SignUpActivityForGoogle.class));
                    getActivity().finish();
                }else if (item.getTitle()=="Refer Code"){
                    startActivity(new Intent(getContext(), ReferCodeActivity.class));
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




        return binding.getRoot();
    }

    private void carouselData() {

        DatabaseReference myD = FirebaseDatabase.getInstance().getReference().child("SliderImages");
        myD.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                        String slierImages = ""+dataSnapshot.child("slider").getValue();
                        String titleText = ""+dataSnapshot.child("title").getValue();
                        binding.carousel.addData(new CarouselItem(slierImages,titleText));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });



    }

    private void loadTaskPack() {
       // binding.progressBar.setVisibility(View.VISIBLE);
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
                   // binding.progressBar.setVisibility(View.GONE);

                    binding.shimmerViewContainer.stopShimmer();
                    binding.shimmerViewLayout.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        binding.shimmerViewContainer.stopShimmer();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.shimmerViewContainer.startShimmer();
    }
}