package com.manuni.sunway;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.manuni.sunway.databinding.ActivitySignUpForGoogleBinding;

public class SignUpActivityForGoogle extends AppCompatActivity {
    public static final String SAVE_USER_INFO = "save_user_info";
    ActivitySignUpForGoogleBinding binding;
    private FirebaseAuth auth;

    private DatabaseReference dbRef;

    public static final int RC_SIGN_IN = 100;
    GoogleSignInClient mGoogleSignInClient;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivitySignUpForGoogleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(SignUpActivityForGoogle.this,googleSignInOptions);

        dbRef = FirebaseDatabase.getInstance().getReference().child("Users");


        dialog = new ProgressDialog(SignUpActivityForGoogle.this);
        auth = FirebaseAuth.getInstance();


        binding.googleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setMessage("Please wait...");
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent,RC_SIGN_IN);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                dialog.dismiss();
                Toast.makeText(this, "Not possible to reach your destination. It can be connection issue.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acc){
        dialog.dismiss();
        AuthCredential credential = GoogleAuthProvider.getCredential(acc.getIdToken(),null);
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user = auth.getCurrentUser();


                    if (task.getResult().getAdditionalUserInfo().isNewUser()){
                        String email = user.getEmail();
                        String uid = user.getUid();
                        String fullName = user.getDisplayName();
                        String phoneNumber = user.getPhoneNumber();


                        SharedPreferences sharedPreferences = getSharedPreferences(SAVE_USER_INFO, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("emailUser",email);
                        editor.putString("nameUser",fullName);

                        editor.apply();



                        Intent sendDataIntent = new Intent(SignUpActivityForGoogle.this,UserInfoActivity.class);
                        sendDataIntent.putExtra("googleEmail",""+email);
                        sendDataIntent.putExtra("uid",""+uid);
                        sendDataIntent.putExtra("userNameEmail",""+fullName);
                        startActivity(sendDataIntent);
                        finishAffinity();

                      //  startActivity(new Intent(SignUpActivityForGoogle.this,UserInfoActivity.class));

                    }else {
                      /*  HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("online","true");
                        hashMap.put("")*/

                        dbRef.child(auth.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if (!snapshot.exists()){

                                    startActivity(new Intent(SignUpActivityForGoogle.this,UserInfoActivity.class));
                                    finish();
                                }else {
                                    startActivity(new Intent(SignUpActivityForGoogle.this, MainActivity.class));
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {

                            }
                        });

                    }



                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {

            }
        });
    }
}