package com.manuni.sunway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.manuni.sunway.databinding.ActivityIncomeBinding;

import java.util.ArrayList;

public class IncomeActivity extends AppCompatActivity {
    ActivityIncomeBinding binding;
    private ArrayList<product_model> list;
    private ProductAdapter adapter;
    private ProgressDialog dialog;

    private String packKey;
    private DatabaseReference myDB;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityIncomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        dialog = new ProgressDialog(IncomeActivity.this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Fetching...");

        dialog.show();

        packKey = getIntent().getStringExtra("packKey");

        list = new ArrayList<>();


        binding.nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = binding.viewPageContent.getCurrentItem();
                int nextPosition = currentPosition + 1;

                if (nextPosition < list.size()) {
                    binding.viewPageContent.setCurrentItem(nextPosition, true);
                }

                if (currentPosition == list.size()-1){
                    Toast.makeText(IncomeActivity.this, "Last Item.Can't go to next.", Toast.LENGTH_SHORT).show();
                    binding.nextBtn.setVisibility(View.INVISIBLE);
                    binding.previousBtn.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = binding.viewPageContent.getCurrentItem();
                int previousPosition = currentPosition - 1;

                if (previousPosition >= 0) {
                    binding.viewPageContent.setCurrentItem(previousPosition, true);
                } else {
                    Toast.makeText(IncomeActivity.this, "First Item. Can't go to previous.", Toast.LENGTH_SHORT).show();
                    binding.previousBtn.setVisibility(View.INVISIBLE);
                    binding.nextBtn.setVisibility(View.VISIBLE);
                }
            }
        });


        myDB = FirebaseDatabase.getInstance().getReference().child("SpecificUsersIncomePack");
        myDB.child(auth.getUid()).child(packKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    product_model data = dataSnapshot.getValue(product_model.class);

                    list.add(data);
                }

                adapter = new ProductAdapter(IncomeActivity.this,list);
                binding.viewPageContent.setAdapter(adapter);
                binding.viewPageContent.setClipToPadding(false);
                binding.viewPageContent.setClipChildren(false);
                binding.viewPageContent.setOffscreenPageLimit(2);
                binding.viewPageContent.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);

              /*  binding.incomeRecyclerView.setLayoutManager(new LinearLayoutManager(IncomeActivity.this));
                binding.incomeRecyclerView.setHasFixedSize(true);
                adapter = new ProductAdapter(IncomeActivity.this,list);
                binding.incomeRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();*/
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });

    }

}