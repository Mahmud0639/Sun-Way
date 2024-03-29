package com.manuni.sunway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.manuni.sunway.databinding.ActivityPayToJoinBinding;

import java.util.HashMap;
import java.util.Objects;

public class PayToJoinActivity extends AppCompatActivity {
    ActivityPayToJoinBinding binding;
    private String packId,packName,price;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPayToJoinBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        progressDialog = new ProgressDialog(PayToJoinActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Submitting...");

        packId = getIntent().getStringExtra("packId");
        packName = getIntent().getStringExtra("packName");
        price = getIntent().getStringExtra("price");


        getSupportActionBar().setTitle("Recharge: "+"$"+price);
      //  String uidUser = data.getId();

        binding.copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String clData = binding.trcAddress.getText().toString().trim();

                ClipboardManager clipboardManager = (ClipboardManager) getApplicationContext().getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text",clData);
                clipboardManager.setPrimaryClip(clipData);

                Toast.makeText(PayToJoinActivity.this, "Text copied!", Toast.LENGTH_SHORT).show();
            }
        });


        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                binding.submitBtn.setEnabled(false);

                String accountNumberTRC = binding.accountNumberET.getText().toString().trim();

                if (TextUtils.isEmpty(accountNumberTRC)){
                    Toast.makeText(PayToJoinActivity.this, "Give your TRC-20 address.", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog.show();
                    // Toast.makeText(PayToJoinActivity.this, ""+packId, Toast.LENGTH_SHORT).show();

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
                    auth = FirebaseAuth.getInstance();
                    String myKey = databaseReference.push().getKey();

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("" + packName, "locked");
                    hashMap.put("packKey",""+myKey);
                    hashMap.put("packId",""+packId);
                    hashMap.put("status","Pending");
                    hashMap.put("price",""+price);
                    hashMap.put("taskTaken","false");
                    hashMap.put("packName",""+packName);
                    hashMap.put("accountNumber",""+accountNumberTRC);
                    hashMap.put("userId",""+auth.getUid());

                    assert myKey != null;
                    databaseReference.child(Objects.requireNonNull(auth.getUid())).child("userPackInfo").child(myKey).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                            DatabaseReference myD = FirebaseDatabase.getInstance().getReference().child("Users");
                            myD.child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String totalOf = ""+snapshot.child("totalCount").getValue();
                                    int myTotalInt = Integer.parseInt(totalOf);

                                    int addOne = myTotalInt + 1;

                                    HashMap<String,Object> hashMap1 = new HashMap<>();
                                    hashMap1.put("totalCount",""+addOne);

                                    myD.child(auth.getUid()).updateChildren(hashMap1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                           // Toast.makeText(PayToJoinActivity.this, "Added one value to totalCount child.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {

                                }
                            });


                            Toast.makeText(PayToJoinActivity.this, "Package info updated.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(PayToJoinActivity.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }




            }
        });




    }


}