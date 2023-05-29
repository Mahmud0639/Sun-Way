package com.manuni.sunway;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.manuni.sunway.databinding.ActivityReferCodeBinding;

public class ReferCodeActivity extends AppCompatActivity {
    ActivityReferCodeBinding binding;
    private FirebaseAuth auth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReferCodeBinding.inflate(getLayoutInflater());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(ReferCodeActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching your refer code...");
        progressDialog.setCanceledOnTouchOutside(false);

        loadMyReferCode();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.referCopyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               String clData = binding.referCodeET.getText().toString().trim();

                ClipboardManager clipboardManager = (ClipboardManager) getApplicationContext().getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text",clData);
                clipboardManager.setPrimaryClip(clipData);

                Toast.makeText(ReferCodeActivity.this, "Text copied!", Toast.LENGTH_SHORT).show();
            }
        });




    }

    private void loadMyReferCode(){
        progressDialog.show();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").document(auth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String referCode = documentSnapshot.getString("referCode");

                binding.referCodeET.setText(referCode);
                progressDialog.dismiss();
            }
        });


    }
}