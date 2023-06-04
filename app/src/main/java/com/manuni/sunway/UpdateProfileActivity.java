package com.manuni.sunway;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.manuni.sunway.databinding.ActivityUpdateProfileBinding;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class UpdateProfileActivity extends AppCompatActivity {
    ActivityUpdateProfileBinding binding;
    private String userName,userPhone,userEmail,userProfile;
    private Uri imageUri;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateProfileBinding.inflate(getLayoutInflater());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        userName = getIntent().getStringExtra("userName");
        userPhone = getIntent().getStringExtra("userPhone");
        userEmail = getIntent().getStringExtra("userEmail");
        userProfile = getIntent().getStringExtra("userProfile");

        progressDialog = new ProgressDialog(UpdateProfileActivity.this);
        progressDialog.setMessage("Updating profile...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);



        binding.userNameET.setText(userName);
        binding.userEmailET.setText(userEmail);
        binding.userMobileET.setText(userPhone);

        binding.nameTxt.setText(userName);

        try {
            Picasso.get().load(userProfile).into(binding.profilePic);
        } catch (Exception e) {
            binding.profilePic.setImageResource(R.drawable.ic_person);
        }




        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(UpdateProfileActivity.this)
                        .crop()                    //Crop image(Optional), Check Customization for more option
                        .compress(1024)            //Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();

            }
        });


        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usName = binding.userNameET.getText().toString().trim();
                String usEmail = binding.userEmailET.getText().toString().trim();
                String usMobile = binding.userMobileET.getText().toString().trim();

                if (TextUtils.isEmpty(usName)){
                    Toast.makeText(UpdateProfileActivity.this, "User name required!", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(usEmail)){
                    Toast.makeText(UpdateProfileActivity.this, "User email required!", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(usMobile)){
                    Toast.makeText(UpdateProfileActivity.this,"User mobile required!",Toast.LENGTH_SHORT).show();
                }else if (imageUri == null){
                    gotoDatabase(usName,usEmail,usMobile);
                }else {
                    gotoStorage(usName,usEmail,usMobile);
                }
            }
        });

    }

    private void gotoDatabase(String myName,String myEmail,String myMobile) {
        progressDialog.show();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("fullName",""+myName);
        hashMap.put("email",""+myEmail);
        hashMap.put("phoneNumber",""+myMobile);

        FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid())
                .updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                FirebaseFirestore.getInstance().collection("users").document(auth.getUid()).update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        HashMap<String,Object> myHash = new HashMap<>();
                        myHash.put("name",""+myName);
                        FirebaseDatabase.getInstance().getReference().child("Withdraws").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    FirebaseDatabase.getInstance().getReference().child("Withdraws")
                                            .child(auth.getUid())
                                            .updateChildren(myHash).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            progressDialog.dismiss();
                                            Toast.makeText(UpdateProfileActivity.this, "Your personal info has been updated!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else {
                                    progressDialog.dismiss();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {

                            }
                        });


                    }
                });
            }
        });
    }

    private void gotoStorage(String myName,String myEmail,String myMobile) {
        progressDialog.show();
        StorageReference ref = FirebaseStorage.getInstance().getReference();
        String filePathAndName = "updated_profile_images/" + System.currentTimeMillis();
        ref.child(filePathAndName).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                Uri downloadUrl = uriTask.getResult();

                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("profileImage",""+downloadUrl);
                hashMap.put("fullName",""+myName);
                hashMap.put("email",""+myEmail);
                hashMap.put("phoneNumber",""+myMobile);

                FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid())
                        .updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        FirebaseFirestore.getInstance().collection("users").document(auth.getUid())
                                .update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                HashMap<String,Object> hash = new HashMap<>();
                                hash.put("name",""+myName);
                                hash.put("image",""+downloadUrl);

                                FirebaseDatabase.getInstance().getReference().child("Withdraws").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        if (snapshot.exists()){
                                            FirebaseDatabase.getInstance().getReference().child("Withdraws")
                                                    .child(auth.getUid())
                                                    .updateChildren(hash).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(UpdateProfileActivity.this, "Your personal info has been updated!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }else {
                                            progressDialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {

                                    }
                                });

                           /*     FirebaseDatabase.getInstance().getReference().child("Withdraws")
                                        .child(auth.getUid())
                                        .updateChildren(hash).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressDialog.dismiss();
                                        Toast.makeText(UpdateProfileActivity.this, "Your personal info has been updated!", Toast.LENGTH_SHORT).show();
                                    }
                                });*/

                            }
                        });
                    }
                });
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            assert data != null;
            imageUri = data.getData();

            try {
                binding.profilePic.setImageURI(imageUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "" + ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        }

    }
}