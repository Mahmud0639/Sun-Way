package com.manuni.sunway;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.manuni.sunway.databinding.ActivityGetOtpactivityBinding;

import java.util.concurrent.TimeUnit;

public class GetOTPActivity extends AppCompatActivity {
    ActivityGetOtpactivityBinding binding;
    private FirebaseAuth auth;
    private PhoneAuthProvider.ForceResendingToken forceResendingTokenOf;

    private String phoneNumber;
    private String otpId;
    CountDownTimer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGetOtpactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        timer = new CountDownTimer(60000,1000) {
            @Override
            public void onTick(long l) {
                if (l/1000<10){
                    binding.countTimer.setText("00:0"+l/1000);
                }else {
                    binding.countTimer.setText(String.valueOf("00:"+l / 1000));
                }


            }

            @Override
            public void onFinish() {
                binding.countTimer.setText("00:00");
                binding.resendCodeBtn.setVisibility(View.VISIBLE);
            }
        };


        timer.start();


        auth = FirebaseAuth.getInstance();

        phoneNumber = getIntent().getStringExtra("mobile");
      initOTP();

        binding.verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.otpET.getText().toString().isEmpty()){
                    Toast.makeText(GetOTPActivity.this, "Field can't be empty.", Toast.LENGTH_SHORT).show();
                }else if (binding.otpET.getText().toString().length()!=6){
                    Toast.makeText(GetOTPActivity.this, "Invalid OTP.", Toast.LENGTH_SHORT).show();
                }else {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(otpId,binding.otpET.getText().toString());
                    signInWithPhoneAuthCredential(credential);
                    timer.cancel();
                }
            }
        });

        binding.resendCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    resendVerificationCode(phoneNumber,forceResendingTokenOf);
            }
        });

        //auth.getCurrentUser().getPhoneNumber();



    }

    public void resendVerificationCode(String phoneNumber,
                                       PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,           //a reference to an activity if this method is in a custom service
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {

                    }
                },
                token);        // resending with token got at previous call's `callbacks` method `onCodeSent`
        // [END start_phone_auth]
    }

    private void initOTP() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onCodeSent(String s,PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                otpId = s;
                forceResendingTokenOf = forceResendingToken;
            }

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(GetOTPActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential){
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user = task.getResult().getUser();
                    startActivity(new Intent(GetOTPActivity.this,UserInfoActivity.class));
                    finish();
                }else {
                    Toast.makeText(GetOTPActivity.this, "Sign in code error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}