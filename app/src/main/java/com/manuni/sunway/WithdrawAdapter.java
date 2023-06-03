package com.manuni.sunway;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.manuni.sunway.databinding.WithdrawItemBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

public class WithdrawAdapter extends RecyclerView.Adapter<WithdrawAdapter.WithdrawViewHolder>{

    private ArrayList<WithdrawModel> list;
    private Context context;

    public WithdrawAdapter(ArrayList<WithdrawModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public WithdrawViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.withdraw_item,parent,false);
        return new WithdrawViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(WithdrawViewHolder holder, int position) {

        WithdrawModel data = list.get(position);
        holder.binding.nameWithdraw.setText(data.getName());
        holder.binding.balanceWithdraw.setText("$"+data.getWithdraw());
        if (data.getStatus().equals("Pending")){
            holder.binding.approveStatus.setText("Pending");
            holder.binding.approveStatus.setTextColor(Color.parseColor("#F44336"));
            holder.binding.approveStatus.setBackground(context.getResources().getDrawable(R.drawable.text_pending_bg));
        }else {
            holder.binding.approveStatus.setText("Approved");
          //  holder.binding.approveStatus.setTextColor(Color.GREEN);
            holder.binding.approveStatus.setTextColor(Color.parseColor("#4CAF50"));
            holder.binding.approveStatus.setBackground(context.getResources().getDrawable(R.drawable.text_approved_bg));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                holder.binding.balanceWithdraw.setBackground(context.getResources().getDrawable(R.drawable.withdraw_background_approved));
            }else {
                holder.binding.balanceWithdraw.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.withdraw_background_approved));
            }

        }

        try {
            Picasso.get().load(data.getImage()).placeholder(R.drawable.impl6).into(holder.binding.imageWithdraw);
        } catch (Exception e) {
            holder.binding.imageWithdraw.setImageResource(R.drawable.impl6);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(data.getTimestamp()));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();

        holder.binding.dateTime.setText(dateTime);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class WithdrawViewHolder extends RecyclerView.ViewHolder{

        WithdrawItemBinding binding;

        public WithdrawViewHolder(View itemView) {
            super(itemView);

            binding = WithdrawItemBinding.bind(itemView);
        }
    }
}
