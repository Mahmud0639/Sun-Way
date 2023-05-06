package com.manuni.sunway.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.manuni.sunway.R;
import com.manuni.sunway.databinding.FragmentProfileBinding;
import com.manuni.sunway.tabadapter.TabPagerAdapter;
import com.manuni.sunway.tabfragments.RankingFragment;
import com.manuni.sunway.tabfragments.WithdrawFragment;
import com.squareup.picasso.Picasso;


public class ProfileFragment extends Fragment {

private Context mContext;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mContext = context;
    }
    private int currentApiVersion;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    FragmentProfileBinding binding;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //below code is for making an activity full screen while using fragment.

       /* getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        currentApiVersion = android.os.Build.VERSION.SDK_INT;
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        if(currentApiVersion >= Build.VERSION_CODES.KITKAT) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(flags);
            final View decorView = getActivity().getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(flags);
                    }
                }
            });
        }*/
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater,container,false);
        auth = FirebaseAuth.getInstance();


        FirebaseFirestore.getInstance().collection("users")
                .document(auth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                binding.progressBar.setVisibility(View.VISIBLE);
                if (documentSnapshot.exists()){
                    String fullNameUser = documentSnapshot.getString("fullName");
                    String phoneNumberUser = documentSnapshot.getString("phoneNumber");
                    String emailUser = documentSnapshot.getString("email");
                    String profileUser = documentSnapshot.getString("profileImage");

                    binding.appsinpp.setText(fullNameUser);
                    binding.userName.setText(fullNameUser);
                    binding.userPhone.setText(phoneNumberUser);
                    binding.userEmail.setText(emailUser);

                    try {
                        Picasso.get().load(profileUser).placeholder(R.drawable.ic_person).into(binding.myProfile);
                    } catch (Exception e) {
                        binding.myProfile.setImageResource(R.drawable.ic_person);
                    }
                    binding.progressBar.setVisibility(View.GONE);
                }



            }
        });





        return binding.getRoot();
    }

 /*   @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpViewPager(binding.viewPager);
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setUpViewPager(ViewPager viewPagerl) {
        TabPagerAdapter tabPagerAdapter = new TabPagerAdapter(getChildFragmentManager());
        tabPagerAdapter.addFragment(new WithdrawFragment(),"Withdraw");
        tabPagerAdapter.addFragment(new RankingFragment(),"Rank");

        viewPagerl.setAdapter(tabPagerAdapter);
    }*/
}