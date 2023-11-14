package com.manuni.sunway;

import android.app.Activity;
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


        holder.binding.takeDollarBtn.setText(String.format("Sell and Earn $%.2f", perOrderDouble));

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
                                        holder.binding.takeDollarBtn.setText(String.format("Sell and Earn $%.2f", perOrderDouble));
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
                holder.binding.myProgress.setVisibility(View.INVISIBLE);



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