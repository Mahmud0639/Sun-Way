package com.manuni.sunway;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.manuni.sunway.databinding.ActivityUserInfoBinding;

import java.util.HashMap;
import java.util.Objects;

public class UserInfoActivity extends AppCompatActivity {
    ActivityUserInfoBinding binding;
    private Uri imageUri;
    private String fullName, email;
    private FirebaseAuth auth;
    private DatabaseReference reference;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        storageReference = FirebaseStorage.getInstance().getReference();

        binding.nameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() <= 3) {
                    binding.textInputName.setError("Name is too small.");

                } else if (charSequence.length() >= 20) {
                    binding.textInputName.setError("Name is too long. Put under 20 char.");

                } else {
                    binding.textInputName.setError(null);
                    binding.textInputName.setHelperText("Valid Name.");
                    binding.textInputName.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorGreen)));

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.emailEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!Patterns.EMAIL_ADDRESS.matcher(charSequence).matches()) {
                    binding.textInputEmail.setError("Invalid Email Pattern.");

                } else {
                    binding.textInputEmail.setError(null);
                    binding.textInputEmail.setErrorEnabled(false);
                    binding.textInputEmail.setHelperText("Valid email address.");
                    binding.textInputEmail.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorGreen)));

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

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

        if (!validateFullName() | !validateEmail()){
            return;
        }

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

        binding.loadingLottie.setVisibility(View.VISIBLE);

        String phoneNumber = Objects.requireNonNull(auth.getCurrentUser()).getPhoneNumber();

        if (imageUri == null) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("fullName", "" + binding.nameET.getText().toString().trim());
            hashMap.put("phoneNumber", "" + phoneNumber);
            hashMap.put("email",""+binding.emailEt.getText().toString().trim());
            hashMap.put("uid", "" + auth.getUid());
            hashMap.put("online", "true");
            hashMap.put("balance","5.00");
            hashMap.put("referCode","");
            hashMap.put("adminMessage","Hi,Thanks for using our App.");
            hashMap.put("timestamp", "" + System.currentTimeMillis());
            hashMap.put("profileImage", "");



            reference.child(Objects.requireNonNull(auth.getUid())).setValue(hashMap).addOnSuccessListener(unused -> {
                //dialogForAccount.dismiss();
                try {
                    binding.loadingLottie.setVisibility(View.GONE);
                    startActivity(new Intent(UserInfoActivity.this,MainActivity.class));
                    finish();
                } catch (Exception e) {
                    binding.loadingLottie.setVisibility(View.GONE);
                    e.printStackTrace();
                }

            });

        }else {
       String filePathAndName = "profile_images/"+""+auth.getUid();
            storageReference.child(filePathAndName).putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                while (!uriTask.isSuccessful());
                Uri downloadUrl = uriTask.getResult();

                if (uriTask.isSuccessful()){
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("fullName",""+binding.nameET.getText().toString().trim());
                    hashMap.put("phoneNumber",""+phoneNumber);
                    hashMap.put("email",""+binding.emailEt.getText().toString().trim());
                    hashMap.put("uid",""+auth.getUid());
                    hashMap.put("online","true");
                    hashMap.put("balance","5.00");
                    hashMap.put("referCode","");
                   // hashMap.put("taskTaken","false");
                    hashMap.put("adminMessage","Hi,Thanks for using our App.");
                    hashMap.put("timestamp",""+System.currentTimeMillis());
                    hashMap.put("profileImage",""+downloadUrl);

                    reference.child(Objects.requireNonNull(auth.getUid())).setValue(hashMap).addOnSuccessListener(unused -> {
                       // dialogForAccount.dismiss();
                        try {
                            binding.loadingLottie.setVisibility(View.GONE);
                            startActivity(new Intent(UserInfoActivity.this,MainActivity.class));
                            finish();
                        } catch (Exception e) {
                            binding.loadingLottie.setVisibility(View.GONE);
                            e.printStackTrace();
                        }

                    });
                }

            });
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

    private boolean validateFullName() {
        fullName = binding.nameET.getText().toString().trim();

        if (fullName.length() <= 3) {
            binding.textInputName.setError("Name field can't be empty or too small.");
            return false;

        }else if (fullName.length() >= 20) {
            binding.textInputName.setError("Name is too long. Put under 20 char.");
            return false;

        } else {
            binding.textInputName.setError(null);
            binding.textInputName.setHelperText("Valid Name.");
            binding.textInputName.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorGreen)));
            return true;

        }
    }

    private boolean validateEmail() {


        email = binding.emailEt.getText().toString().trim();

        if (email.isEmpty()) {
            binding.textInputEmail.setError("Field can't be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.textInputEmail.setError("Invalid Email Pattern");
            return false;
        } else {
            binding.textInputEmail.setError(null);
            binding.textInputEmail.setErrorEnabled(false);
            binding.textInputEmail.setHelperText("Valid email address.");
            binding.textInputEmail.setHelperTextColor(ColorStateList.valueOf(getResources().getColor(R.color.colorGreen)));
            return true;
        }


    }
}
