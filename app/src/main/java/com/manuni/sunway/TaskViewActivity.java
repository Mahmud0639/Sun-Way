package com.manuni.sunway;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.manuni.sunway.databinding.ActivityTaskViewBinding;

import java.util.ArrayList;

public class TaskViewActivity extends AppCompatActivity {
    ActivityTaskViewBinding binding;
    private DatabaseReference dbRef;
    private ArrayList<PackageModel> list;
    private BuyAdapter adapter;

    private String packageNameOfLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityTaskViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        packageNameOfLevel = getIntent().getStringExtra("myLevelName");

        list = new ArrayList<>();

    dbRef = FirebaseDatabase.getInstance().getReference().child("PackageInfo");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                    list.clear();
                     for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                         PackageModel data = dataSnapshot.getValue(PackageModel.class);
                         String levelNameOf = ""+dataSnapshot.child("levelName").getValue();
                         if (levelNameOf.equals(packageNameOfLevel)){
                             list.add(data);
                         }

                     }

                     binding.taskRV.setLayoutManager(new LinearLayoutManager(TaskViewActivity.this));
                     adapter = new BuyAdapter(TaskViewActivity.this,list);
                     adapter.notifyDataSetChanged();
                     binding.taskRV.setHasFixedSize(true);
                     binding.taskRV.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }
}