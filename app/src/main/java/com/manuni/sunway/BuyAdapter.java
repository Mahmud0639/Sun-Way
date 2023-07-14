package com.manuni.sunway;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.manuni.sunway.databinding.IncomePlateBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class BuyAdapter extends RecyclerView.Adapter<BuyAdapter.BuyViewHolder> {
    private Context context;
    private ArrayList<PackageModel> list;
    private FirebaseAuth auth;
    private String balanceOf, adminMessageOf, emailOf, fullNameOf, onlineOf, phoneNumberOf, profileImageOf, taskTakenOf, referCodeOf, timestampOf, uidOf;
    private String packKeyOf;
    private  String packKeyOfI,packId;

    public BuyAdapter(Context context, ArrayList<PackageModel> list) {
        this.context = context;
        this.list = list;
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public BuyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.income_plate, parent, false);
        return new BuyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BuyViewHolder holder, int position) {
        PackageModel data = list.get(position);
        String packageId = data.getId();
        String packageImage = data.getPackImage();
        String packageLevelName = data.getLevelName();
        String packagePerOrder = data.getPerOrderQuantity();

        try {
            Picasso.get().load(packageImage).placeholder(R.drawable.impl1).into(holder.binding.image);
        } catch (Exception e) {
            holder.binding.image.setImageResource(R.drawable.impl1);
        }

        holder.binding.lvTxt.setText(packageLevelName);
        holder.binding.perOrder.setText("$" + packagePerOrder);
        holder.binding.dailyProductPrice.setText("$" + packagePerOrder);

        double perOrderDouble = Double.parseDouble(packagePerOrder);
        double monthlyPackDouble = perOrderDouble * 30;

        holder.binding.monthlyIncomeTxt.setText(String.format("$%.2f", monthlyPackDouble));


        holder.binding.takeDollarBtn.setText(String.format("Take $%.2f", perOrderDouble));

      /*  DatabaseReference myDB = FirebaseDatabase.getInstance().getReference().child("Users");
        myDB.child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String myTaskStatus = ""+snapshot.child("taskTaken").getValue();
                    if (myTaskStatus.equals("true")){
                        holder.binding.takeDollarBtn.setText("Taken");
                        holder.binding.takeDollarBtn.setEnabled(false);
                    }else {
                        holder.binding.takeDollarBtn.setText(String.format("Take $%.2f",perOrderDouble));
                        holder.binding.takeDollarBtn.setEnabled(true);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        Toast.makeText(context, ""+taskTakenOf, Toast.LENGTH_SHORT).show();*/

   /*     holder.binding.buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/

//ekhane problem ta thik korte hobe...problem ta holo back press korle bar bar same tai ashe...

        holder.binding.buyBtn.setOnClickListener(new View.OnClickListener() {
            // .orderByChild("packName").equalTo(packageLevelName).
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid()).child("userPackInfo").orderByChild("packName").equalTo(packageLevelName).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                packKeyOfI = "" + dataSnapshot.child("packKey").getValue();
                                packId = ""+dataSnapshot.child("packId").getValue();


                            }

                            FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid()).child("userPackInfo").child(packKeyOfI).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    String taskS = "" + snapshot.child("taskTaken").getValue();
                                    if (taskS.equals("true")) {
                                        holder.binding.takeDollarBtn.setEnabled(false);
                                        holder.binding.takeDollarBtn.setText("Taken");
                                    } else if (taskS.equals("false")) {
                                        holder.binding.takeDollarBtn.setEnabled(true);
                                        holder.binding.takeDollarBtn.setText(String.format("Take $%.2f", perOrderDouble));
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {

                                }
                            });
                        }else {
                            Toast.makeText(context, "Buy first to get into it.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });

            }
        });


        holder.binding.takeDollarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.binding.takeDollarBtn.setEnabled(false);
                holder.binding.myProgress.setVisibility(View.VISIBLE);

                Intent incomeIntent = new Intent(context,IncomeActivity.class);
                incomeIntent.putExtra("packKey",""+packId);
                context.startActivity(incomeIntent);

/*
                DatabaseReference myDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

                myDatabase.child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            balanceOf = "" + snapshot.child("balance").getValue();
                           *//* adminMessageOf = "" + snapshot.child("adminMessage").getValue();
                            emailOf = "" + snapshot.child("email").getValue();
                            fullNameOf = "" + snapshot.child("fullName").getValue();
                            onlineOf = "" + snapshot.child("online").getValue();
                            phoneNumberOf = "" + snapshot.child("phoneNumber").getValue();
                            profileImageOf = "" + snapshot.child("profileImage").getValue();
                            //  taskTakenOf = ""+snapshot.child("taskTaken").getValue();
                            referCodeOf = "" + snapshot.child("referCode").getValue();
                            timestampOf = "" + snapshot.child("timestamp").getValue();
                            uidOf = "" + snapshot.child("uid").getValue();*//*

                            HashMap<String, Object> hashMap = new HashMap<>();

                            double balanceDouble = Double.parseDouble(balanceOf);
                            double incomeBalanceDouble = Double.parseDouble(packagePerOrder);

                            double totalIncome = balanceDouble + incomeBalanceDouble;

                         *//*   hashMap.put("adminMessage", "" + adminMessageOf);
                            hashMap.put("email", "" + emailOf);
                            hashMap.put("fullName", "" + fullNameOf);
                            hashMap.put("online", "" + onlineOf);
                            hashMap.put("phoneNumber", "" + phoneNumberOf);
                            hashMap.put("profileImage", "" + profileImageOf);
                            hashMap.put("referCode", "" + referCodeOf);
                            hashMap.put("timestamp", "" + timestampOf);
                            hashMap.put("uid", "" + uidOf);*//*
                            // hashMap.put("taskTaken","true");
                            hashMap.put("balance",totalIncome);
                            FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    holder.binding.myProgress.setVisibility(View.GONE);

                                    FirebaseFirestore.getInstance().collection("users")
                                            .document(auth.getUid())
                                            .update("balance", FieldValue.increment(incomeBalanceDouble)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(Task<Void> task) {
                                            if (task.isSuccessful()){
                                            FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid()).child("userPackInfo").child(packKeyOfI).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(context, "Income added.", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                            }

                                        }
                                    });



                                    // Toast.makeText(context, "" + packageLevelName, Toast.LENGTH_SHORT).show();


                              *//*      FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid()).child("userPackInfo").child(packKeyOfI).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {
                                            String paKey = "" + snapshot.child("packKey").getValue();
                                          //  Toast.makeText(context, ""+paKey, Toast.LENGTH_SHORT).show();

                                            FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid()).child("userPackInfo").child(paKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot snapshot) {
                                                    if (snapshot.exists()) {
                                                        String packKeyOfM = "" + snapshot.child("packKey").getValue();
                                                        String userIdOfM = "" + snapshot.child("userId").getValue();
                                                        String packNameOfM = "" + snapshot.child("packName").getValue();
                                                        String packIdOfM = "" + snapshot.child("packId").getValue();
                                                        String joinStatusOfM = "" + snapshot.child("status").getValue();
                                                        String taskTakenOfM = "" + snapshot.child("taskTaken").getValue();
                                                        String statusOfM = "" + snapshot.child(packNameOfM).getValue();

                                                        HashMap<String, Object> hashMap1 = new HashMap<>();
//
                                                        hashMap1.put("taskTaken", "true");

                                                        // hashMap1.put("userId",""+userIdOfM);

                                                        FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid()).child("userPackInfo").child(packKeyOfM).updateChildren(hashMap1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                //Toast.makeText(context, "Pack data updated.", Toast.LENGTH_SHORT).show();

                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError error) {

                                                }
                                            });

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError error) {

                                        }
                                    });*//*
                 *//*                       FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid()).child("userPackInfo").orderByChild("packName").equalTo(packageLevelName).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot snapshot) {
                                                if (snapshot.exists()){
                                                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                                        String taskStatus = ""+dataSnapshot.child("taskTaken").getValue();
                                                        packKeyOf = ""+dataSnapshot.child("packKey").getValue();
                                                        Toast.makeText(context, ""+taskStatus, Toast.LENGTH_SHORT).show();

                                                    //    if (taskStatus.equals("false")){

                                                    }

                                                    FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid()).child("userPackInfo").child(packKeyOf).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot snapshot) {
                                                            if (snapshot.exists()){
                                                                String packKeyOfM = ""+snapshot.child("packKey").getValue();
                                                                String userIdOfM = ""+snapshot.child("userId").getValue();
                                                                String packNameOfM = ""+snapshot.child("packName").getValue();
                                                                String packIdOfM = ""+snapshot.child("packId").getValue();
                                                                String joinStatusOfM = ""+snapshot.child("status").getValue();
                                                                String taskTakenOfM = ""+snapshot.child("taskTaken").getValue();
                                                                String statusOfM = ""+snapshot.child(packNameOfM).getValue();

                                                                HashMap<String,Object> hashMap1 = new HashMap<>();
//                                                                hashMap1.put(""+packNameOfM,""+statusOfM);
//                                                                hashMap1.put("packId",""+packIdOfM);
//                                                                hashMap1.put("packKey",""+packKeyOfM);
//                                                                hashMap1.put("packName",""+packNameOfM);
//                                                                hashMap1.put("status",""+joinStatusOfM);
                                                                hashMap1.put("taskTaken","true");
                                                               // hashMap1.put("userId",""+userIdOfM);

                                                                FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid()).child("userPackInfo").child(packKeyOfM).updateChildren(hashMap1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        Toast.makeText(context, "Pack data updated.", Toast.LENGTH_SHORT).show();

                                                                    }
                                                                });
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError error) {

                                                        }
                                                    });
                                                       *//**//* }else if (taskStatus.equals("true")){
                                                            return;
                                                        }*//**//*

                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError error) {

                                            }
                                        });*//*

                                }
                            });


                            //Toast.makeText(context, "" + balanceOf, Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });*/


              /*  myDatabase.orderByChild("uid").equalTo(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                       for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                            balanceOf = ""+dataSnapshot.child("balance").getValue();
                            adminMessageOf = ""+dataSnapshot.child("adminMessage").getValue();
                            emailOf = ""+dataSnapshot.child("email").getValue();
                            fullNameOf = ""+dataSnapshot.child("fullName").getValue();
                            onlineOf = ""+dataSnapshot.child("online").getValue();
                            phoneNumberOf = ""+dataSnapshot.child("phoneNumber").getValue();
                            profileImageOf = ""+dataSnapshot.child("profileImage").getValue();
                            taskTakenOf = ""+dataSnapshot.child("taskTaken").getValue();
                            referCodeOf = ""+dataSnapshot.child("referCode").getValue();
                            timestampOf = ""+dataSnapshot.child("timestamp").getValue();
                            uidOf = ""+dataSnapshot.child("uid").getValue();



                          //  Toast.makeText(context, ""+balanceOf, Toast.LENGTH_SHORT).show();
                        }


                      //  Toast.makeText(context, ""+totalIncome, Toast.LENGTH_SHORT).show();

                        HashMap<String,Object> hashMap = new HashMap<>();

                        double balanceDouble = Double.parseDouble(balanceOf);
                        double incomeBalanceDouble = Double.parseDouble(packagePerOrder);

                        double totalIncome = balanceDouble+incomeBalanceDouble;

                        hashMap.put("adminMessage",""+adminMessageOf);
                        hashMap.put("email",""+emailOf);
                        hashMap.put("fullName",""+fullNameOf);
                        hashMap.put("online",""+onlineOf);
                        hashMap.put("phoneNumber",""+phoneNumberOf);
                        hashMap.put("profileImage",""+profileImageOf);
                        hashMap.put("referCode",""+referCodeOf);
                        hashMap.put("timestamp",""+timestampOf);
                        hashMap.put("uid",""+uidOf);
                        hashMap.put("taskTaken","true");
                        hashMap.put("balance",""+totalIncome);
                        FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid()).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                holder.binding.myProgress.setVisibility(View.GONE);
                                Toast.makeText(context, "Income added.", Toast.LENGTH_SHORT).show();


                            }
                        });

                      //  Toast.makeText(context, ""+balanceOf, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });
*/


            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class BuyViewHolder extends RecyclerView.ViewHolder {

        IncomePlateBinding binding;

        public BuyViewHolder(View itemView) {
            super(itemView);
            binding = IncomePlateBinding.bind(itemView);
        }
    }

}