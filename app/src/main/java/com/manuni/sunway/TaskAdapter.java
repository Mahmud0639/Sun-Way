package com.manuni.sunway;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.manuni.sunway.databinding.WorkingPackSampleBinding;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder>{

    private Context context;
    private ArrayList<PackageModel> list;
    private FirebaseAuth auth;

    public TaskAdapter(Context context, ArrayList<PackageModel> list) {
        this.context = context;
        this.list = list;
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.working_pack_sample,parent,false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        PackageModel data = list.get(position);

        String packName = data.getLevelName();


        FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getUid()).child("userPackInfo")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                //Toast.makeText(context, ""+packName, Toast.LENGTH_SHORT).show();

                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    String status = ""+dataSnapshot.child(packName).getValue();
                  //  Toast.makeText(context, ""+status, Toast.LENGTH_SHORT).show();
                    if (status.equals("unlocked")){
                        holder.binding.viewBack.setEnabled(true);
                        holder.binding.viewBack.setVisibility(View.INVISIBLE);
                        holder.binding.lottieLock.setVisibility(View.INVISIBLE);
                    }else if (status.equals("locked")){
                        holder.binding.viewBack.setEnabled(false);
                        holder.binding.viewBack.setVisibility(View.VISIBLE);
                        holder.binding.lottieLock.setVisibility(View.VISIBLE);
                    }

                }



            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        holder.binding.levelName.setText(data.getLevelName());



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, ""+data.getLevelName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder{
        WorkingPackSampleBinding binding;

        public TaskViewHolder(View itemView) {
            super(itemView);

            binding = WorkingPackSampleBinding.bind(itemView);
        }
    }
}
