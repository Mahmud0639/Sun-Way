package com.manuni.sunway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.manuni.sunway.databinding.ActivityIncomeBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class IncomeActivity extends AppCompatActivity {
    ActivityIncomeBinding binding;
    private ArrayList<product_model> list;
    private ProductAdapter adapter;
    private ProgressDialog dialog;

    private String packKey;
    private static String myPack;
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
        myPack = packKey;


        list = new ArrayList<>();


        ObjectAnimator fadeOutAnimator = ObjectAnimator.ofFloat(binding.viewPageContent, "alpha", 1f, 0f);
        fadeOutAnimator.setDuration(500); // Animation duration in milliseconds

        fadeOutAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                // Animation started
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                // Animation ended
                // Remove the item from the ViewPager2 here

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

            @Override
            public void onAnimationCancel(Animator animator) {
                // Animation canceled
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                // Animation repeated
            }
        });


        binding.nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = binding.viewPageContent.getCurrentItem();
                int nextPosition = currentPosition + 1;

                if (nextPosition < list.size()) {
                    binding.viewPageContent.setCurrentItem(nextPosition, true);
                }

                if (currentPosition == list.size() - 1) {
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

                if (snapshot.exists()) {

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        product_model data = dataSnapshot.getValue(product_model.class);

                        list.add(data);
                    }

                    adapter = new ProductAdapter(IncomeActivity.this, list);
                    binding.viewPageContent.setAdapter(adapter);
                    binding.viewPageContent.setClipToPadding(false);
                    binding.viewPageContent.setClipChildren(false);
                    binding.viewPageContent.setOffscreenPageLimit(2);
                    binding.viewPageContent.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);
                } else {

                    if (canPerformTask()) {
                        performTask(packKey);
                    } else {
                        binding.againEarnTxt.setVisibility(View.VISIBLE);
                       /* binding.hourTime.setVisibility(View.VISIBLE);
                        Toast.makeText(IncomeActivity.this, "Data to be shown", Toast.LENGTH_SHORT).show();
                        binding.dotTime.setVisibility(View.VISIBLE);
                        binding.minuteTime.setVisibility(View.VISIBLE);
                        binding.dotTimeSecond.setVisibility(View.VISIBLE);
                        binding.secondTime.setVisibility(View.VISIBLE);*/
                        binding.nextBtn.setVisibility(View.INVISIBLE);

                      //  startTimer();

                    }

                   /* binding.againEarnTxt.setVisibility(View.VISIBLE);
                    binding.nextBtn.setVisibility(View.INVISIBLE);*/
                }

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


    private void performTask(String paKey) {
        // Your task logic goes here
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference();

        //Toast.makeText(this, "" + paKey, Toast.LENGTH_SHORT).show();

        dRef.child("AllProductsPriceInfo").child(paKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String perOrder = "" + dataSnapshot.child("perOrder").getValue();
                        String productId = "" + dataSnapshot.child("productId").getValue();
                        String productImage = "" + dataSnapshot.child("productImage").getValue();
                        String productNumber = "" + dataSnapshot.child("productNumber").getValue();
                        String productSellingPrice = "" + dataSnapshot.child("productSellingPrice").getValue();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("perOrder", "" + perOrder);
                        hashMap.put("productId", "" + productId);
                        hashMap.put("productImage", "" + productImage);
                        hashMap.put("productNumber", "" + productNumber);
                        hashMap.put("productSellingPrice", "" + productSellingPrice);
                        hashMap.put("packId", "" + paKey);


                        dRef.child("SpecificUsersIncomePack").child(auth.getUid()).child(paKey).child(productId).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(IncomeActivity.this, "All Packages updated successfully.", Toast.LENGTH_SHORT).show();
                            }
                        });


                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }


    private boolean canPerformTask() {


        final String[] lastCompletionTime = {getLastCompletionTime(this,packKey)};
        if (lastCompletionTime[0].equals("")){

             SharedPreferences sp = getSharedPreferences(ProductAdapter.getPrefsName()+packKey,Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(ProductAdapter.getLastCompletionTimeKey()+packKey,getCurrentDate());
            editor.apply();

            return false;
        }else {

            lastCompletionTime[0] = getLastCompletionTime(this,packKey);

        }

        String currentDate = getCurrentDate();


        if (!lastCompletionTime[0].equals(currentDate)){

            return true;
        }else {
            return false;
        }


    }
    private static String getCurrentDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date currentDate = Calendar.getInstance().getTime();
        return simpleDateFormat.format(currentDate);

    }


    public static String getLastCompletionTime(Context context,String packL) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ProductAdapter.getPrefsName()+packL, Context.MODE_PRIVATE);
        return sharedPreferences.getString(ProductAdapter.getLastCompletionTimeKey()+packL,"");
    }


}