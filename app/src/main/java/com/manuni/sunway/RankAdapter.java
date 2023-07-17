package com.manuni.sunway;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.manuni.sunway.databinding.RowLeaderboardsBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.RankViewHolder>{

    private Context context;
    private ArrayList<UsersData> list;

    public RankAdapter(Context context, ArrayList<UsersData> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public RankViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_leaderboards,parent,false);
        return new RankViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RankViewHolder holder, int position) {
        UsersData data = list.get(position);
        String fullNameUser = data.getFullName();
        String adminMess = data.getAdminMessage();
        String email = data.getEmail();
        String online = data.getOnline();
        String phoneNumber = data.getPhoneNumber();
        String timestamp = data.getTimestamp();
        String uid = data.getUid();
        String profileImage = data.getProfileImage();
        double balance = data.getBalance();


        holder.binding.name.setText(fullNameUser);
        holder.binding.coins.setText(String.format("$%.2f",balance));
        try {
            Picasso.get().load(profileImage).placeholder(R.drawable.ic_person).into(holder.binding.imageView8);
        } catch (Exception e) {
            holder.binding.imageView8.setImageResource(R.drawable.ic_person);
        }

        holder.binding.index.setText(String.format("#%d",position+1));


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class RankViewHolder extends RecyclerView.ViewHolder{

        RowLeaderboardsBinding binding;

        public RankViewHolder(View itemView) {
            super(itemView);
            binding = RowLeaderboardsBinding.bind(itemView);
        }
    }
}
