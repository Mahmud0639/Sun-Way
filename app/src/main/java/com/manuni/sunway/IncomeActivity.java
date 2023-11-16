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
        Toast.makeText(this, "Income activity packKey is: "+packKey, Toast.LENGTH_SHORT).show();


        list = new ArrayList<>();


        //toKnowDateDatabase();

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
       /* long lastCompletionTime = getLastCompletionTime(this);
        long currentTimeMillis = System.currentTimeMillis();
        //long twentyFourHoursInMillis = 24 * 60 * 60 * 1000; // 24 hours in milliseconds
        long twentyFourHoursInMillis = 24 * 60 * 60 * 1000; // 24 hours in milliseconds


        // Check if 24 hours have passed since the last completion
        return (currentTimeMillis - lastCompletionTime) >= twentyFourHoursInMillis;*/
        /*Calendar currentDateCalendar = Calendar.getInstance();
        Calendar nextDateCalendar = Calendar.getInstance();
        nextDateCalendar.add(Calendar.DAY_OF_YEAR,1);

        if (currentDateCalendar.before(nextDateCalendar)){
            return false;
        }else {
            return true;
        }*/


        /*Date lastCompletionDate = new Date(lastCompletionTime);

        Calendar lastCompletionCalendar = Calendar.getInstance();
        lastCompletionCalendar.setTime(lastCompletionDate);

        Calendar nextDateCalendar = Calendar.getInstance();
        nextDateCalendar.add(Calendar.DAY_OF_YEAR, 1);*/

        // Comparing the two Calendar objects
//        int comparisonResult = lastCompletionCalendar.compareTo(nextDateCalendar);
//
//        if (comparisonResult < 0) {
//            System.out.println("lastCompletionTime is before nextDateCalendar.");
//        } else if (comparisonResult > 0) {
//            System.out.println("lastCompletionTime is after nextDateCalendar.");
//        } else {
//            System.out.println("lastCompletionTime and nextDateCalendar are equal.");
//        }

       /* long lastCompletionTime = getLastCompletionTime(this);// Assuming this method returns a long value
        long finalLastCompletionTime = System.currentTimeMillis() - lastCompletionTime;
        long currentTime = System.currentTimeMillis();

        long midNightTime = getMidNightTime();

        if (currentTime>=midNightTime && finalLastCompletionTime>midNightTime){
            return true;
        }else {
            return false;
        }*/
        //Toast.makeText(this, "Last completion time is: "+lastCompletionTime, Toast.LENGTH_LONG).show();
      /* Calendar calendar = Calendar.getInstance();
       calendar.set(Calendar.HOUR_OF_DAY,0);
       calendar.set(Calendar.MINUTE,0);
       calendar.set(Calendar.SECOND,0);

       long midNightTimeInMillis = calendar.getTimeInMillis();*/
       //long timeDiffFromCompletionTime = midNightTimeInMillis  - lastCompletionTime;

      /* if (System.currentTimeMillis()-lastCompletionTime >= midNightTimeInMillis){
           return true;
       }else {
           return false;
       }*/

       // return (System.currentTimeMillis()-lastCompletionTime) >= midNightTimeInMillis;

        //final long[] lastCompletionTime = {getLastCompletionTime(this)};
       /* long lastCompletionTime = getLastCompletionTime(this);


        *//*if (lastCompletionTime[0] == 0){
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Users");
                   dbRef.child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot snapshot) {
                           if (snapshot.exists()){
                               String lastComTimeInString = ""+snapshot.child("lastCompletionTime").getValue();
                                lastCompletionTime[0] = Long.parseLong(lastComTimeInString);

                           }


                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError error) {

                       }
                   });
        }else {
            lastCompletionTime[0] = getLastCompletionTime(this);

        }*//*
        long currentTime = System.currentTimeMillis();
        if (is24HoursPassedSinceMidnight(lastCompletionTime, currentTime)) {
            //System.out.println("24 hours have passed since the last completion.");
            Toast.makeText(this, "24 hours have passed since the last completion.", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            //System.out.println("24 hours have not passed since the last completion.");
            Toast.makeText(this, "24 hours have not passed since the last completion.", Toast.LENGTH_SHORT).show();
            return false;
        }*/
        final String[] dbTime = {""};

        final String[] lastCompletionTime = {getLastCompletionTime(this,packKey)};
        if (lastCompletionTime[0].equals("")){

            Toast.makeText(this, "Yes last completion time is null", Toast.LENGTH_SHORT).show();

             SharedPreferences sp = getSharedPreferences(ProductAdapter.getPrefsName()+packKey,Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(ProductAdapter.getLastCompletionTimeKey()+packKey,getCurrentDate());
            editor.apply();
            Toast.makeText(this, "Error last time is saved. As we need it.", Toast.LENGTH_SHORT).show();
           /* DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Users");
            dbRef.child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("userPackUpdateTime").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                            String packId = ""+dataSnapshot.child("packId").getValue();
                            if (packId.equals(packKey)){
                                 dbTime[0] = ""+dataSnapshot.child("lastCompletionTime").getValue();
                                Toast.makeText(IncomeActivity.this, "Database time is: "+ dbTime[0], Toast.LENGTH_SHORT).show();
                                //lastCompletionTime[0] = dbTime;



                            }
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            doAsDatabase(dbTime[0]);*/
            return false;
        }else {

            lastCompletionTime[0] = getLastCompletionTime(this,packKey);
            Toast.makeText(this, "No last completion time is not null", Toast.LENGTH_SHORT).show();
        }












       // String lastCompletionDate = getLastCompletionTime(this,packKey);
        String currentDate = getCurrentDate();

        //Toast.makeText(this, "At last : "+packKey, Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, "Last completion Date: "+lastCompletionDate, Toast.LENGTH_SHORT).show();

        if (!lastCompletionTime[0].equals(currentDate)){
           /*SharedPreferences sp = getSharedPreferences(ProductAdapter.getPrefsName()+packKey,Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(ProductAdapter.getLastCompletionTimeKey()+packKey,currentDate);
            editor.apply();*/

           // saveLastCompletionTimeToDatabase();*/

            Toast.makeText(this, "At last packKey is: "+packKey, Toast.LENGTH_SHORT).show();

            return true;
        }else {
            Toast.makeText(this, "At last packKey is: "+packKey, Toast.LENGTH_SHORT).show();
            return false;
        }


    }
    private static String getCurrentDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date currentDate = Calendar.getInstance().getTime();
        return simpleDateFormat.format(currentDate);

    }

    private boolean doAsDatabase(String dataTime){
        String curDate = getCurrentDate();
        if (!dataTime.equals(curDate)){

           /*SharedPreferences sp = getSharedPreferences(ProductAdapter.getPrefsName()+packKey,Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(ProductAdapter.getLastCompletionTimeKey()+packKey,currentDate);
            editor.apply();*/

            // saveLastCompletionTimeToDatabase();*/

            Toast.makeText(this, "At last packKey is: "+packKey, Toast.LENGTH_SHORT).show();

            return true;
        }else {
            Toast.makeText(this, "At last packKey is: "+packKey, Toast.LENGTH_SHORT).show();
            return false;
        }
    }


   /* private static boolean is24HoursPassedSinceMidnight(long lastCompletionTimeMillis, long currentTimeMillis) {
        // Get the midnight time in milliseconds
        long midnightMillis = getMidnightMillis(currentTimeMillis);

        // Check if 24 hours have passed since the last completion
        return (currentTimeMillis - lastCompletionTimeMillis) >= (midnightMillis - lastCompletionTimeMillis);
    }*/

    public static String getLastCompletionTime(Context context,String packL) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ProductAdapter.getPrefsName()+packL, Context.MODE_PRIVATE);
        //return sharedPreferences.getLong(ProductAdapter.getLastCompletionTimeKey(), 0);
        return sharedPreferences.getString(ProductAdapter.getLastCompletionTimeKey()+packL,"");
    }

   /* private static long getMidnightMillis(long currentTimeMillis) {
        // Get the current date
        Calendar calendar = Calendar.getInstance();

        // Set the time to midnight
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // If the current time is already after midnight, move to the next day
        if (currentTimeMillis > calendar.getTimeInMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Return the time in milliseconds
        return calendar.getTimeInMillis();

    }*/

   /* private static long getMidNightTime(){
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);

        return calendar.getTimeInMillis();
    }*/

   /* private void startTimer(){
        long lastCompleteTime = getLastCompletionTime(this);
        long timeLeft = calculateUntilMidnight(System.currentTimeMillis());
        Toast.makeText(this, ""+timeLeft, Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, ""+Math.abs(timeLeft), Toast.LENGTH_SHORT).show();
       // long timeRemain = 12 - timeLeft;
       // Toast.makeText(this, "Time left "+timeRemain, Toast.LENGTH_SHORT).show();
        new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long l) {
                //hours
                long hours = l/(1000*60*60);
                long minutes = (l%(1000*60*60))/(1000*60);
                long seconds = (l%(1000*60))/1000;

                long mainTime = 24-hours;
                long min = 60-minutes;
                long sec = 60-seconds;
*//*
                if (mainTime<10){
                    binding.hourTime.setText("0"+mainTime);
                }else {
                    binding.hourTime.setText(""+mainTime);
                }

                binding.minuteTime.setText(String.valueOf(minutes));
                binding.secondTime.setText(String.valueOf(seconds));
                if (mainTime<=1){
                    binding.againEarnTxt.setText("Please wait less than an hour for again earning");
                }else if (mainTime>1 && mainTime<2){
                    binding.againEarnTxt.setText("Please wait about an hour or more for again earning");
                }else {
                    binding.againEarnTxt.setText("Please wait "+mainTime+" hours for again earning");
                }*//*

            }

            @Override
            public void onFinish() {
               *//* binding.hourTime.setVisibility(View.GONE);
                binding.dotTime.setVisibility(View.GONE);
                binding.minuteTime.setVisibility(View.GONE);
                binding.dotTimeSecond.setVisibility(View.GONE);
                binding.secondTime.setVisibility(View.GONE);*//*
            }
        }.start();


    }*/
   /* private static long calculateUntilMidnight(long lComTime){
        Calendar cl = Calendar.getInstance();
        cl.set(Calendar.HOUR_OF_DAY,0);
        cl.set(Calendar.MINUTE,0);
        cl.set(Calendar.SECOND,0);

        long midNightMill = cl.getTimeInMillis();
        long diff = midNightMill - lComTime;

       // Toast.makeText(IncomeActivity.this, ""+diff, Toast.LENGTH_SHORT).show();


        return Math.abs(midNightMill - lComTime);//to avoid the negative time(24-06)=17 hours 59 minutes..here lComTime means 'System.currentTimeMillis()'
        //return Math.abs(System.currentTimeMillis()-lComTime);//here lComTime means 'lastCompletionTime'

    }*/

    private void saveLastCompletionTimeToDatabase(){
        DatabaseReference myD = FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> hashMap1 = new HashMap<>();
        String currentDate = getCurrentDate();
        hashMap1.put("lastCompletionTime",""+currentDate);
        myD.child("Users").child(FirebaseAuth.getInstance().getUid()).updateChildren(hashMap1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(IncomeActivity.this, "You will be able to income again after midnight", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

   /* private void toKnowDateDatabase(){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Users");
        dbRef.child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("userPackUpdateTime").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String packId = ""+dataSnapshot.child("packId").getValue();
                        if (packId.equals(packKey)){
                            String lastComTime = ""+dataSnapshot.child("lastCompletionTime").getValue();
                            String myPaKey = ""+dataSnapshot.child("packId").getValue();
                            Toast.makeText(IncomeActivity.this, "Database packKey is: "+myPaKey, Toast.LENGTH_SHORT).show();
                            Toast.makeText(IncomeActivity.this, "Database date is: "+lastComTime, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/

}