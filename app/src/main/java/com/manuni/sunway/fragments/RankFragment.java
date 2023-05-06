package com.manuni.sunway.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.manuni.sunway.R;
import com.manuni.sunway.databinding.FragmentRankBinding;
import com.manuni.sunway.tabadapter.TabPagerAdapter;
import com.manuni.sunway.tabfragments.RankingFragment;
import com.manuni.sunway.tabfragments.WithdrawFragment;

public class RankFragment extends Fragment {
    private Context mContext;

    public RankFragment() {
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
    FragmentRankBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentRankBinding.inflate(inflater,container,false);





        return binding.getRoot();
    }

    @Override
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
        tabPagerAdapter.addFragment(new RankingFragment(),"Rank");
        tabPagerAdapter.addFragment(new WithdrawFragment(),"Withdraw");

        viewPagerl.setAdapter(tabPagerAdapter);
    }
}