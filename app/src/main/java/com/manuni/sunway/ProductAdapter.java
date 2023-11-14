package com.manuni.sunway;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.manuni.sunway.databinding.BuyProductIncomeBinding;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder>{

    private static Context context;
    private ArrayList<product_model> list;
    private final DatabaseReference myD;
    private final DatabaseReference myUserDatabase;

    private ProgressDialog progressDialog,anoProgressDialog;

    ValueEventListener evenForDelete;

    private int currentPosition = 0;

    private static final String PREFS_NAME = "TaskPrefs";
    private static final String PREFS_NAME_NEW = "MyPrefs";
    private static final String LAST_COMPLETION_DATE_KEY = "lastCompletionDate";
    private static final String LAST_COMPLETION_TIME_KEY = "lastCompletionTime";

    private boolean runV;

    public ProductAdapter(Context context, ArrayList<product_model> list) {
        this.context = context;
        this.list = list;
        myD = FirebaseDatabase.getInstance().getReference();
        myUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Processing sale...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        //anoProgressDialog = new ProgressDialog(context);

    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.buy_product_income,parent,false);
        return new ProductViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, @SuppressLint("RecyclerView") int position) {

        product_model data = list.get(position);

/*        if (getItemCount()==1){
            //ei part a temporary balance adding and then delete, realtime database a balance add, and also to firestore
            Toast.makeText(context, "This is the last item. and item number is: "+getItemCount(), Toast.LENGTH_SHORT).show();
        }else {
            //ei part a only delete and temporary balance adding
            Toast.makeText(context, "One more Items available.Items are: "+getItemCount(), Toast.LENGTH_SHORT).show();
        }*/


      //  makeButtonDisabled(holder,data);

        try {
            Picasso.get().load(data.getProductImage()).into(holder.binding.taskImage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, ""+position, Toast.LENGTH_SHORT).show();
            }
        });*/

      //  final boolean[] isStop = {false};



        holder.binding.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.binding.card.animate()
                        .alpha(0)
                        .setDuration(1500)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                // Handle button click logic after the animation ends
                                // Your existing code here



                                if (getItemCount()==1){

                                    progressDialog.show();

                                    myD.child("SpecificUsersIncomePack").child(FirebaseAuth.getInstance().getUid())
                                            .child(data.getPackId()).child(data.getProductId())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    String balanceAsStringOfPack = ""+snapshot.child("productSellingPrice").getValue();

                                                    myD.child("UsersTemporaryBalance").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            String bal = ""+snapshot.child("balance").getValue();

                                                            double dBal = Double.parseDouble(bal);
                                                            double dBalOfPack = Double.parseDouble(balanceAsStringOfPack);

                                                            double resultModified = dBalOfPack - (int) dBalOfPack;

                                                            double totalBal = dBal + resultModified;

                                                            HashMap<String,Object> hashMap = new HashMap<>();
                                                            hashMap.put("balance",""+totalBal);

                                                            myD.child("UsersTemporaryBalance").child(FirebaseAuth.getInstance().getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {

                                                                    myD.child("UsersTemporaryBalance").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                            String myBalance = ""+snapshot.child("balance").getValue();
                                                                            double dMyBalance = Double.parseDouble(myBalance);

                                                                            HashMap<String,Object> hashMap1 = new HashMap<>();
                                                                            hashMap1.put("balance",""+myBalance);

                                                                            myD.child("Users").child(FirebaseAuth.getInstance().getUid()).updateChildren(hashMap1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {

                                                                                    HashMap<String,Object> hashMap2 = new HashMap<>();
                                                                                    hashMap2.put("balance",dMyBalance);
                                                                                    FirebaseFirestore.getInstance().collection("users")
                                                                                            .document(FirebaseAuth.getInstance().getUid())
                                                                                            .update(hashMap2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void unused) {
                                                                                                    myD.child("SpecificUsersIncomePack").child(FirebaseAuth.getInstance().getUid()).child(data.getPackId()).child(data.getProductId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onSuccess(Void unused) {
                                                                                                                progressDialog.dismiss();
                                                                                                            evenForDelete = new ValueEventListener() {
                                                                                                                @Override
                                                                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                                                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                                                                                                                        String pacKey = ""+dataSnapshot.getKey();
                                                                                                                        //Toast.makeText(context, "packKey: "+pacKey, Toast.LENGTH_SHORT).show();
                                                                                                                        myD.child("Users").child(FirebaseAuth.getInstance().getUid())
                                                                                                                                .child("userPackInfo").child(pacKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                    @Override
                                                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                        if (task.isSuccessful()){
                                                                                                                                            myUserDatabase.child(FirebaseAuth.getInstance().getUid()).child("userPackInfo")
                                                                                                                                                    .orderByChild("packId").equalTo(data.getPackId()).removeEventListener(evenForDelete);
                                                                                                                            /*runV = true;
                                                                                                                            if (runV){
                                                                                                                                return;
                                                                                                                            }*/
                                                                                                                                            Toast.makeText(context, "You have successfully sold this product.", Toast.LENGTH_SHORT).show();
                                                                                                                                            // ((Activity)context).finish();

                                                                                                                                            // ((Activity) context).finishAffinity();
                                                                                                                                            progressDialog.dismiss();

                                                                                                                                            saveLastCompletionTime(context);

                                                                                                                                        }
                                                                                                                                    }
                                                                                                                                });


                                                                                                                    }
                                                                                                                }

                                                                                                                @Override
                                                                                                                public void onCancelled(@NonNull DatabaseError error) {
                                                                                                                    progressDialog.dismiss();
                                                                                                                }
                                                                                                            };

                                                                                                          /*  //er pore package ti delete diye dite hobe
                                                                                                            myUserDatabase.child(FirebaseAuth.getInstance().getUid()).child("userPackInfo")
                                                                                                                    .orderByChild("packId").equalTo(data.getPackId()).addValueEventListener(evenForDelete);*/

                                                                                                            //myUserDatabase.removeEventListener(evenForDelete);
                                                                                                            // Toast.makeText(context, "Datachanged but not updating automatically.", Toast.LENGTH_SHORT).show();


                                                                                                        }
                                                                                                    });
                                                                                                }
                                                                                            });


                                                                                }
                                                                            });

                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                                            progressDialog.dismiss();
                                                                        }
                                                                    });


                                                                }
                                                            });

                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                                progressDialog.dismiss();
                                                        }
                                                    });


                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    progressDialog.dismiss();

                                                }
                                            });
                                }else {

                                    progressDialog.show();


                                    myD.child("SpecificUsersIncomePack").child(FirebaseAuth.getInstance().getUid())
                                            .child(data.getPackId()).child(data.getProductId())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    String balanceAsStringOfPack = ""+snapshot.child("productSellingPrice").getValue();
                                                    // String perOrderPrice = ""+snapshot.child("perOrder").getValue();

                                                    myD.child("UsersTemporaryBalance").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            String bal = ""+snapshot.child("balance").getValue();

                                                            double dBal = Double.parseDouble(bal);
                                                            double dBalOfPack = Double.parseDouble(balanceAsStringOfPack);

                                                            double resultModified = dBalOfPack - (int) dBalOfPack;

                                                            double totalBal = dBal + resultModified;

                                                            HashMap<String,Object> hashMap = new HashMap<>();
                                                            hashMap.put("balance",""+totalBal);

                                                            myD.child("UsersTemporaryBalance").child(FirebaseAuth.getInstance().getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    myD.child("SpecificUsersIncomePack").child(FirebaseAuth.getInstance().getUid()).child(data.getPackId()).child(data.getProductId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void unused) {

                                                                            Toast.makeText(context, "You have successfully sold this product.", Toast.LENGTH_SHORT).show();
                                                                            progressDialog.dismiss();
                                                                            //after this we should store all the info of 24 hours task
                                                                            //PREFS_NAME = ""+totalBal;
                                                                           /* saveLastCompletionTime(context);
                                                                          saveLastCompletionTimeToDatabase();*/
                                                                            saveLastCompletionTimeToDatabase();

                                                                        }
                                                                    });
                                                                }
                                                            });

                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            progressDialog.dismiss();
                                                        }
                                                    });


                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    progressDialog.dismiss();
                                                }
                                            });
                                }

                            }
                        });

              //  Toast.makeText(context, "Button clicked at position: "+position+" and list size is "+list.size(), Toast.LENGTH_SHORT).show();


            }
        });

        holder.binding.sellText.setText("Sell:$"+data.getProductSellingPrice());
        holder.binding.buyText.setText("Buy:$"+data.getPerOrder()+".00");



        /*// myD = FirebaseDatabase.getInstance().getReference().child("Users");
        myD.child("Users").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String mEqualPos = ""+snapshot.child("equalPosition").getValue();
                Toast.makeText(context, ""+mEqualPos, Toast.LENGTH_SHORT).show();
                equalPosition = Integer.parseInt(mEqualPos);


                int productNum = Integer.parseInt(data.getProductNumber());

                Toast.makeText(context, ""+equalPosition, Toast.LENGTH_SHORT).show();

                if (productNum==equalPosition){
                    holder.itemView.setVisibility(View.VISIBLE);
                    // isRunning = true;


            *//*HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("visibility","Yes");

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("SpecificUsersIncomePack");
            ref.child(FirebaseAuth.getInstance().getUid()).child(data.getPackId()).child(data.getProductId()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(context, "Visibility is Yes now for "+productNum, Toast.LENGTH_SHORT).show();
                }
            });*//*
                    // if (data.getProductNumber().equals("1"))
                    //  makeVisible();
                }else {
                    holder.itemView.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
*/





/*
        holder.binding.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
*//*                Toast.makeText(context, "First can be deleted now.", Toast.LENGTH_SHORT).show();


                int incrementedEqPos = equalPosition + 1;

                HashMap<String,Object> hashMap = new HashMap<>();

                hashMap.put("equalPosition",""+incrementedEqPos);


                myD.child("Users").child(FirebaseAuth.getInstance().getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "after incrementing equalPosition is : "+equalPosition, Toast.LENGTH_SHORT).show();
                    }
                });*//*

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, ""+position, Toast.LENGTH_SHORT).show();
            }
        });

        try {
            Picasso.get().load(data.getProductImage()).into(holder.binding.taskImage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }*/


    }

    private void makeButtonDisabled(ProductViewHolder myHolder,product_model myData) {

       myD.child("SpecificUsersIncomePack").child(FirebaseAuth.getInstance().getUid()).child(myData.getPackId())
               .orderByChild("sellingStatus").equalTo("true").addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       if (snapshot.exists()){
                           for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                String sellStatus = ""+dataSnapshot.child("sellingStatus").getValue();
                                if (sellStatus.equals("true")){
                                    myHolder.binding.confirm.setEnabled(false);
                                    myHolder.binding.levelName.setText("Sold");
                                }else {
                                    myHolder.binding.confirm.setEnabled(true);
                                    myHolder.binding.levelName.setText("Confirm");
                                }
                           }
                       }

                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {

                   }
               });
    }

    public void moveToNextItem() {
        currentPosition++;
        if (currentPosition >= getItemCount()) {
            // Handle reaching the end of the list
            currentPosition = 0;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder{
        BuyProductIncomeBinding binding;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = BuyProductIncomeBinding.bind(itemView);
        }

     /*   public void bind(product_model item) {
            try {
                Picasso.get().load(item.getProductImage()).into(binding.taskImage);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            binding.sellText.setText(item.getProductSellingPrice());

            binding.confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle button click event
                    // ...

                    // Move to the next item
                    moveToNextItem();
                }
            });
        }*/
    }
    public static String getPrefsName(){
        //return PREFS_NAME;
        return PREFS_NAME_NEW;
    }
    public static String getLastCompletionTimeKey(){
       // return LAST_COMPLETION_TIME_KEY;
        return LAST_COMPLETION_DATE_KEY;
    }

    public static void saveLastCompletionTime(Context context) {

      //  Toast.makeText(context, "Pref Name is : "+PREFS_NAME, Toast.LENGTH_SHORT).show();

       /* SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        long currentTimeMillis = System.currentTimeMillis();
        editor.putLong(LAST_COMPLETION_TIME_KEY, currentTimeMillis);
        editor.apply();*/

        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME_NEW,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        //Toast.makeText(context, "The last time is: "+sp.getString(PREFS_NAME_NEW,""), Toast.LENGTH_SHORT).show();

        String currentDate = getCurrentDate();

        editor.putString(LAST_COMPLETION_DATE_KEY,currentDate);
        editor.apply();
        Toast.makeText(context, "Time has saved successfully!", Toast.LENGTH_SHORT).show();
        //saveLastCompletionTimeToDatabase();
    }

    private static String getCurrentDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date currentDate = Calendar.getInstance().getTime();
        return simpleDateFormat.format(currentDate);

    }
    private void saveLastCompletionTimeToDatabase(){
        progressDialog.setMessage("Updating your time...");

        HashMap<String,Object> hm = new HashMap<>();
        String currentDate = getCurrentDate();
        hm.put("lastCompletionTime",""+currentDate);
        DatabaseReference myD = FirebaseDatabase.getInstance().getReference();
        myD.child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).updateChildren(hm)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
               progressDialog.dismiss();
               // Toast.makeText(context, "You will be able to income again after midnight", Toast.LENGTH_SHORT).show();
                //Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
progressDialog.dismiss();
            }
        });
    }


}
