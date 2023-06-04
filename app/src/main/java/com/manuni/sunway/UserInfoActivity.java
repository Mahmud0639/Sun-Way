package com.manuni.sunway;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.BlurMaskFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.model.content.BlurEffect;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.manuni.sunway.databinding.ActivityUserInfoBinding;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class UserInfoActivity extends AppCompatActivity {
    ActivityUserInfoBinding binding;
    private Uri imageUri;
    private String fullName, email;
    private FirebaseAuth auth;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private FirebaseFirestore firestore;

    private ProgressDialog progressDialog;

    private String userEmailGoogle,userNameEmailFull,userPhoneGoogle;

    SharedPreferences myPrefs;

    private String my_email,my_name;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityUserInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());





        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        userEmailGoogle = getIntent().getStringExtra("googleEmail");
        userNameEmailFull = getIntent().getStringExtra("userNameEmail");
        userPhoneGoogle = getIntent().getStringExtra("userPhoneGoogle");

        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        storageReference = FirebaseStorage.getInstance().getReference();

        progressDialog = new ProgressDialog(UserInfoActivity.this);
        progressDialog.setMessage("Creating account...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);




        binding.referSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    try {
                        binding.textInputReferCode.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    try {
                        binding.textInputReferCode.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });



        binding.personImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickDialog();
            }
        });
        binding.registerBtnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                inputDataToDatabaseAndStorage();

            }
        });


    }

    private void inputDataToDatabaseAndStorage() {


        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isConnected()) {
            try {
                createAccount();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (mobile.isConnected()) {
            try {
                createAccount();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show();
        }

    }

    private void createAccount() {


        saveToDatabase();


    }

    private void saveToDatabase() {


        myPrefs = getSharedPreferences(SignUpActivityForGoogle.SAVE_USER_INFO,Context.MODE_PRIVATE);
        my_email = myPrefs.getString("emailUser",my_email);
        my_name = myPrefs.getString("nameUser",my_name);


       // binding.loadingLottie.setVisibility(View.VISIBLE);

        String phoneNumber = binding.textInputPhone.getEditText().getText().toString().trim();
        String userRefer = binding.textInputReferCode.getEditText().getText().toString().trim();



        String ALLOWED_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

            StringBuilder codeBuilder = new StringBuilder(6);
            Random random = new Random();

            for (int i = 0; i < 6; i++) {
                int randomIndex = random.nextInt(ALLOWED_CHARACTERS.length());
                char randomChar = ALLOWED_CHARACTERS.charAt(randomIndex);
                codeBuilder.append(randomChar);
            }

            String referCode = codeBuilder.toString();


        if (TextUtils.isEmpty(phoneNumber)){
            binding.textInputPhone.setError("Field can't be empty");
        } else{
          //  progressDialog.show();
            binding.cons.setVisibility(View.INVISIBLE);
            binding.rel.setVisibility(View.INVISIBLE);
            binding.textInputPhone.setVisibility(View.INVISIBLE);
            binding.registerBtnUser.setEnabled(false);

            binding.loadLottie.setVisibility(View.VISIBLE);

            binding.phoneNumberET.setError(null);
            if (imageUri == null) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("fullName", "" +my_name);
                hashMap.put("phoneNumber", "" + phoneNumber);
                hashMap.put("email",""+my_email);
                hashMap.put("uid", "" + auth.getUid());
                hashMap.put("online", "true");
                hashMap.put("withdrawCount","0");
                hashMap.put("usedReferCode",userRefer);
                hashMap.put("balance","0.00");
                hashMap.put("referCode",""+referCode);
                hashMap.put("totalCount","0");
                hashMap.put("adminMessage","Hi,Thanks for using our App.");
                hashMap.put("timestamp", "" + System.currentTimeMillis());
                hashMap.put("profileImage", "");



                reference.child(Objects.requireNonNull(auth.getUid())).setValue(hashMap).addOnSuccessListener(unused -> {
                    //dialogForAccount.dismiss();
                    try {
                     //   binding.loadingLottie.setVisibility(View.GONE);

                        UsersData usersData = new UsersData(my_name,phoneNumber,my_email,auth.getUid(),
                                "true",""+referCode,"0","Hi,Thanks for using our App.",""+System.currentTimeMillis(),"",userRefer);

                        firestore.collection("users")
                                .document(auth.getUid())
                                .set(usersData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(Task<Void> task) {
                                if (task.isSuccessful()){
                                  // progressDialog.dismiss();

                                    incrementBalance(userRefer);
                                    startActivity(new Intent(UserInfoActivity.this,MainActivity.class));
                                    finish();
                                }
                            }
                        });
                    } catch (Exception e) {

                        binding.cons.setVisibility(View.VISIBLE);
                        binding.rel.setVisibility(View.VISIBLE);
                        binding.textInputPhone.setVisibility(View.VISIBLE);
                        binding.registerBtnUser.setEnabled(false);

                        binding.loadLottie.setVisibility(View.GONE);

                      //  progressDialog.dismiss();
                     //   binding.loadingLottie.setVisibility(View.GONE);
                        e.printStackTrace();
                    }

                });

            }else {
                //progressDialog.show();
                binding.cons.setVisibility(View.INVISIBLE);
                binding.rel.setVisibility(View.INVISIBLE);
                binding.textInputPhone.setVisibility(View.INVISIBLE);
                binding.registerBtnUser.setEnabled(false);

                binding.loadLottie.setVisibility(View.VISIBLE);



                String filePathAndName = "profile_images/"+""+auth.getUid();
                storageReference.child(filePathAndName).putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();


                    while (!uriTask.isSuccessful());
                    Uri downloadUrl = uriTask.getResult();

                    if (uriTask.isSuccessful()){
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("fullName",""+my_name);
                        hashMap.put("phoneNumber",""+phoneNumber);
                        hashMap.put("email",""+my_email);
                        hashMap.put("uid",""+auth.getUid());
                        hashMap.put("online","true");
                        hashMap.put("balance","0.00");
                        hashMap.put("totalCount","0");
                        hashMap.put("withdrawCount","0");
                        hashMap.put("usedReferCode",userRefer);
                        hashMap.put("referCode",""+referCode);
                        // hashMap.put("taskTaken","false");
                        hashMap.put("adminMessage","Hi,Thanks for using our App.");
                        hashMap.put("timestamp",""+System.currentTimeMillis());
                        hashMap.put("profileImage",""+downloadUrl);

                        reference.child(Objects.requireNonNull(auth.getUid())).setValue(hashMap).addOnSuccessListener(unused -> {
                            // dialogForAccount.dismiss();
                            try {
                              //  binding.loadingLottie.setVisibility(View.GONE);

                                UsersData usersData = new UsersData(my_name,phoneNumber,my_email,auth.getUid(),
                                        "true",""+referCode,"0","Hi,Thanks for using our App.",""+System.currentTimeMillis(),""+downloadUrl,userRefer);
                                firestore.collection("users")
                                        .document(auth.getUid())
                                        .set(usersData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(Task<Void> task) {
                                        if (task.isSuccessful()){

                                            incrementBalance(userRefer);
                                          //  progressDialog.dismiss();
                                            startActivity(new Intent(UserInfoActivity.this,MainActivity.class));
                                            finish();
                                        }
                                    }
                                });

                            } catch (Exception e) {

                                binding.rel.setVisibility(View.VISIBLE);
                                binding.cons.setVisibility(View.VISIBLE);
                                binding.textInputPhone.setVisibility(View.VISIBLE);
                                binding.registerBtnUser.setEnabled(false);

                                binding.loadLottie.setVisibility(View.GONE);

                              //  binding.loadingLottie.setVisibility(View.GONE);
                              //  progressDialog.dismiss();
                                e.printStackTrace();
                            }

                        });
                    }

                });
            }
        }



    }

    private void showImagePickDialog() {
        ImagePicker.with(this)
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)//Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            assert data != null;
            imageUri = data.getData();

            try {
                binding.personImageShow.setImageURI(imageUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "" + ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        }

    }

    boolean shouldStopLoop = false;

    private void incrementBalance(String usRefer){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot snapshot: queryDocumentSnapshots){
                    String referCode = snapshot.getString("referCode");
                   // String uid = snapshot.getString("uid");

                    if (!referCode.equals("")){
                        if (referCode.equals(usRefer)){

                            firebaseFirestore.collection("users").whereEqualTo("referCode",referCode).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (DocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                                        String uid = documentSnapshot.getString("uid");


                                        HashMap<String,Object> hashMap = new HashMap<>();
                                        hashMap.put("referUid",""+uid);

                                        FirebaseDatabase.getInstance().getReference().child("Users")
                                                .child(auth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                shouldStopLoop = true;
                                                FirebaseFirestore.getInstance().collection("users").document(auth.getUid()).update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(UserInfoActivity.this, "referUid set.", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                            }
                                        });
                                   /* firebaseFirestore.collection("users").document(uid).update("balance", FieldValue.increment(Double.parseDouble("3"))).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                          //  updateRealtimeDatabase(uid);

                                            Toast.makeText(UserInfoActivity.this, "Field value incremented.", Toast.LENGTH_SHORT).show();


                                        }
                                    });*/

                                    }
                                }
                            });


                        }
                    }



                    if (shouldStopLoop){
                        break;
                    }

                }

            }
        });

    }

   /* private void updateRealtimeDatabase(String userId) {

        double giveDollar = 3.00;

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");

        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String balance = ""+snapshot.child("balance").getValue();

                double totalBalance = giveDollar + Double.parseDouble(balance);


                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("balance",totalBalance);
                reference.child(userId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(UserInfoActivity.this, "Dollar added.", Toast.LENGTH_SHORT).show();
                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });




    }*/

}
