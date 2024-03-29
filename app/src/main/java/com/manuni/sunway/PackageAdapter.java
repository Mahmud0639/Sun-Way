package com.manuni.sunway;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.manuni.sunway.databinding.VipBoxBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.PackageAdapterViewHolder> {

    private Context context;
    private ArrayList<PackageModel> list;
    private FirebaseAuth auth;
    boolean isUnlocked = false;
    boolean isLocked = false;

    public PackageAdapter(Context context, ArrayList<PackageModel> list) {
        this.context = context;
        this.list = list;
        auth = FirebaseAuth.getInstance();


    }

    @Override
    public PackageAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.vip_box, parent, false);
        return new PackageAdapterViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(PackageAdapterViewHolder holder, int position) {
        PackageModel data = list.get(position);

        String levelNameTxt = data.getLevelName();
        String perOrderTxt = data.getPerOrderQuantity();
        String sellPrice = data.getSellingPrice();

     /*   FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid()).child("userPackInfo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    String statusOf = ""+dataSnapshot.child("status").getValue();
                    if (statusOf.equals("Pending")){
                       holder.itemView.setVisibility(View.GONE);
                    }else if (statusOf.equals("Joined")){
                        holder.itemView.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });*/



        FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid()).child("userPackInfo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String levelValue = ""+dataSnapshot.child(levelNameTxt).getValue();
                        String statusValue = ""+dataSnapshot.child("status").getValue();

                        if (levelValue.equals("unlocked")&&statusValue.equals("Joined")){
                            holder.binding.joinNowBtn.setVisibility(View.INVISIBLE);
                            holder.binding.joinedBtn.setVisibility(View.VISIBLE);
                            holder.binding.lottieLockSmall.setVisibility(View.INVISIBLE);
                            holder.binding.joinNowBtn.setText("Joined");

                           // isUnlocked = true;

                        }else if (levelValue.equals("locked")&&statusValue.equals("Pending")){
                            holder.binding.joinNowBtn.setVisibility(View.VISIBLE);
                            holder.binding.joinedBtn.setVisibility(View.INVISIBLE);
                            holder.binding.lottieLockSmall.setVisibility(View.VISIBLE);
                            holder.binding.joinNowBtn.setText("Pending");

                           // isLocked = true;
                        }

                       /* if (isUnlocked){
                            break;
                        }
                        if (isLocked){
                            break;
                        }*/
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        holder.binding.lvTxt.setText(levelNameTxt);

        double perOrderOf = Double.parseDouble(perOrderTxt);
        double sellPriceOf = Double.parseDouble(sellPrice);


       // holder.binding.perOrder.setText("$" + perOrderTxt + ".00");
        holder.binding.perOrder.setText(String.format("$%.2f",perOrderOf));

      //  holder.binding.dailyProductPrice.setText("$" + perOrderTxt + ".00");

        int perOrderAsInt = Integer.parseInt(perOrderTxt);

        if (perOrderAsInt==1){
            holder.binding.dailyProductPrice.setText("3%");
        }else if (perOrderAsInt==2){
            holder.binding.dailyProductPrice.setText("3.5%");
        }else if (perOrderAsInt==3){
            holder.binding.dailyProductPrice.setText("4%");
        }else if (perOrderAsInt==4){
            holder.binding.dailyProductPrice.setText("4.5%");
        }else if (perOrderAsInt==5){
            holder.binding.dailyProductPrice.setText("5%");
        }else if (perOrderAsInt==6){
            holder.binding.dailyProductPrice.setText("5.5%");
        }else {
            holder.binding.dailyProductPrice.setText(String.valueOf(Integer.parseInt(perOrderTxt)+"%"));
        }

        holder.binding.priceBtn.setText(String.format("Price: $%.2f",sellPriceOf));




        try {
            double perOrderInDouble = Double.parseDouble(perOrderTxt);
            double monthlyIncome = perOrderInDouble * 30;

            holder.binding.monthlyIncomeTxt.setText(String.format("$%.2f", monthlyIncome));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        holder.binding.joinNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String text = holder.binding.joinNowBtn.getText().toString();
                if (text.equals("Pending")){
                    return;
                }else {
                    String uidUser = data.getId();
                    String price = data.getSellingPrice();

                    Intent joinIntent = new Intent(context,PayToJoinActivity.class);
                    joinIntent.putExtra("packId",""+uidUser);
                    joinIntent.putExtra("packName",""+levelNameTxt);
                    joinIntent.putExtra("price",""+price);
                    context.startActivity(joinIntent);

                }



               /* if (holder.binding.joinNowBtn.getText().toString().equals("Pending")){
                    Toast.makeText(context, "Your order is in processing...", Toast.LENGTH_SHORT).show();
                }else if (holder.binding.joinNowBtn.getText().toString().equals("Joined")){
                    Toast.makeText(context, "You have already joined.", Toast.LENGTH_SHORT).show();
                }  else{
                    holder.binding.joinNowBtn.setText("Pending");

                    String uidUser = data.getId();



                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

                    String myKey = databaseReference.push().getKey();

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("" + levelNameTxt, "locked");
                    hashMap.put("packKey",""+myKey);
                    hashMap.put("packId",""+uidUser);
                    hashMap.put("status","Pending");
                    hashMap.put("taskTaken","false");
                    hashMap.put("packName",""+levelNameTxt);
                    hashMap.put("userId",""+auth.getUid());

                    databaseReference.child(auth.getUid()).child("userPackInfo").child(myKey).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context, "Package info updated.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(context, "Something went wrong.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }*/


            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class PackageAdapterViewHolder extends RecyclerView.ViewHolder {
        VipBoxBinding binding;

        public PackageAdapterViewHolder(View itemView) {
            super(itemView);

            binding = VipBoxBinding.bind(itemView);
        }
    }
}
