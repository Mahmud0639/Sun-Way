package com.manuni.sunway.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.manuni.sunway.R;
import com.manuni.sunway.databinding.FragmentWalletBinding;
import com.manuni.sunway.tabadapter.TabPagerAdapter;
import com.manuni.sunway.tabfragments.RankingFragment;
import com.manuni.sunway.tabfragments.WithdrawFragment;

import org.checkerframework.checker.units.qual.C;

import java.text.DecimalFormat;
import java.util.HashMap;

public class WalletFragment extends Fragment {
    private Context mContext;
    private ProgressDialog progressDialog;

    public WalletFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    FragmentWalletBinding binding;
    private DatabaseReference dbRef;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentWalletBinding.inflate(inflater,container,false);

        dbRef = FirebaseDatabase.getInstance().getReference().child("Users");
        auth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Sending request to withdraw...");

        dbRef.child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String messageAdmin = ""+snapshot.child("adminMessage").getValue();
                String balanceOf = ""+snapshot.child("balance").getValue();

                Double balanceDouble = Double.parseDouble(balanceOf);
                binding.currentCoins.setText(String.format("$%.2f",balanceDouble));

                if (balanceDouble <= 10.0){
                    binding.healthyAccount.setText("Unhealthy balance");
                    binding.healthyAccount.setTextColor(Color.parseColor("#D0EB3C3C"));
                }else {
                    binding.healthyAccount.setText("Healthy balance");
                    binding.healthyAccount.setTextColor(Color.parseColor("#4CAF50"));
                }

               // binding.currentCoins.setText("$"+String.valueOf(Double.valueOf( decimalFormat.format(balanceOf))));
               // binding.currentCoins
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

      /*  binding.paypalEmailBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 34){
                   // binding.invisibleBtn.setVisibility(View.VISIBLE);
                    binding.sendRequest.setEnabled(false);
                }else {
                  //  binding.invisibleBtn.setVisibility(View.INVISIBLE);
                    binding.sendRequest.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/

        binding.sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Toast.makeText(mContext, "I am send btn.", Toast.LENGTH_SHORT).show();
                binding.sendRequest.setEnabled(false);*/
               /* String withdrawBalance = binding.paypalEmailBox.getText().toString().trim();
                String currentDollar = binding.currentCoins.getText().toString().trim();

               // withdrawBalance.replace("$","");
                int withdrawInt = Integer.parseInt(withdrawBalance);
                int currentDollarInt = Integer.parseInt(currentDollar);

                if (withdrawInt < 40){

                    if (currentDollarInt >= 0 && currentDollarInt < 40){
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Warning")
                                .setMessage("You can't withdraw less than $40.")
                                .setPositiveButton("Got It", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
                    }

                }else {
                    Toast.makeText(mContext, "You are ready to go.", Toast.LENGTH_SHORT).show();
                }*/

                String editTxtBalance = binding.paypalEmailBox.getText().toString().trim();
                String editTxtTrc = binding.trcAddressET.getText().toString().trim();

                if (TextUtils.isEmpty(editTxtBalance)){
                    Toast.makeText(mContext, "Field can't be empty!", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(editTxtTrc)){
                    Toast.makeText(mContext, "Field can't be empty!", Toast.LENGTH_SHORT).show();
                } else{
                    ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                    if (wifi.isConnected() || mobile.isConnected()){
                        withdrawMyDollar(editTxtBalance,editTxtTrc);

                    }else {
                        Toast.makeText(mContext, "No connection.", Toast.LENGTH_SHORT).show();
                    }
                }





            }
        });




        return binding.getRoot();
    }

    private void withdrawMyDollar(String myGivenDollar,String eTrc) {

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("Users");
        myRef.child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String dollarBalance = ""+snapshot.child("balance").getValue();
                String withCount = ""+snapshot.child("withdrawCount").getValue();
                int withCountInt = Integer.parseInt(withCount);

                double dollarBalanceInt = Double.parseDouble(dollarBalance);

                double myGivenDollarInt = Double.parseDouble(myGivenDollar);

                if (myGivenDollarInt>=0.00 && myGivenDollarInt<10.00){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Warning")
                            .setMessage("You can't withdraw less than $10.")
                            .setPositiveButton("Got It", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
                }else {

                    if (dollarBalanceInt>=myGivenDollarInt && withCountInt==0){
                        progressDialog.show();

                        double myRemainedBalance = dollarBalanceInt - myGivenDollarInt;

                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("balance",""+myRemainedBalance);

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
                        ref.child(auth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                FirebaseFirestore store = FirebaseFirestore.getInstance();
                                store.collection("users").document(auth.getUid()).update("balance",myRemainedBalance).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        saveAsWithdrawInfo(myGivenDollar,myRemainedBalance,eTrc);
                                    }
                                });
                            }
                        });





                    }else {
                        Toast.makeText(mContext, "You can't withdraw.", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    private void saveAsWithdrawInfo(String withdrawDollar,double remainedBalance,String trc) {

        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users");
        db.child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String name = ""+snapshot.child("fullName").getValue();
                String image = ""+snapshot.child("profileImage").getValue();


                DatabaseReference myDB = FirebaseDatabase.getInstance().getReference().child("Withdraws");

                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("uid",""+auth.getUid());
                hashMap.put("name",""+name);
                hashMap.put("image",""+image);
                hashMap.put("timestamp",""+System.currentTimeMillis());
                hashMap.put("status","Pending");
                hashMap.put("withdraw",""+withdrawDollar);
                hashMap.put("remainBalance",""+remainedBalance);
                hashMap.put("complete","false");
                hashMap.put("trcAddress",""+trc);
                hashMap.put("count","0");

                myDB.child(auth.getUid()).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();


                        HashMap<String,Object> hashMap1 = new HashMap<>();
                        hashMap1.put("withdrawCount","1");

                        //jokhon admin withdraw approve korbe tokhon again withdrawCount = "0" kora hobe..jate user again
                        //withdraw korte pare

                        db.child(auth.getUid()).updateChildren(hashMap1).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(mContext, "Your withdraw is now in pending.", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });


    }

}