package com.seazoned.landscaper.view.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.seazoned.landscaper.R;
import com.seazoned.landscaper.databinding.TransactionHistoryRowBinding;
import com.seazoned.landscaper.service.util.Util;
import com.seazoned.landscaper.view.activity.ProfileActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by root on 27/3/18.
 */

public class TransactionHistoryListAdapter extends RecyclerView.Adapter<TransactionHistoryListAdapter.BindingHolder> {
    Context mContext;
    ArrayList<HashMap<String, String>> mTransactionHistoryList;



    public TransactionHistoryListAdapter(Context mContext, ArrayList<HashMap<String,String>> mTransactionHistoryList) {
        this.mContext = mContext;
        this.mTransactionHistoryList = mTransactionHistoryList;

    }

    @Override
    public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TransactionHistoryRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.transaction_history_row, parent, false);
        return new BindingHolder(binding);

    }

    @Override
    public void onBindViewHolder(BindingHolder holder, int position) {
        final HashMap<String,String> hashMap=mTransactionHistoryList.get(position);

        holder.binding.tvFullName.setText(hashMap.get("full_name"));
        holder.binding.tvOrderNo.setText(hashMap.get("order_no"));
        holder.binding.tvPrice.setText("$ "+hashMap.get("landscaper_payment"));
        holder.binding.tvPaymentDate.setText(Util.changeAnyDateFormat(hashMap.get("payment_date"),"dd MMM yyyy, hh:mm","MMM-dd-yyyy, hh:mm a"));
        if (hashMap.get("profile_image").equalsIgnoreCase("")||
                hashMap.get("profile_image").equalsIgnoreCase("null")||
                hashMap.get("profile_image").equalsIgnoreCase(null)){
            Picasso.with(mContext).load(R.drawable.ic_user).into(holder.binding.ivProfileImage);
        }
        else {
            holder.binding.ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Util.getPopUpWindow(mContext,hashMap.get("profile_image"));
                }
            });
            Picasso.with(mContext).load(hashMap.get("profile_image")).error(R.drawable.ic_user).resize(150,150).into(holder.binding.ivProfileImage);
        }

        if((hashMap.get("transaction_id")).equalsIgnoreCase("")){
            holder.binding.tvStatus.setText("Processing");
            holder.binding.tvStatus.setBackgroundResource(R.drawable.yellow_gradient_round);
        }
        else {
            holder.binding.tvStatus.setText("Success");
            holder.binding.tvStatus.setBackgroundResource(R.drawable.green_gradient_round);
        }

    }

    @Override
    public int getItemCount() {
        return mTransactionHistoryList.size();
    }
    public static class BindingHolder extends RecyclerView.ViewHolder {
        private TransactionHistoryRowBinding binding;

        public BindingHolder(TransactionHistoryRowBinding binding) {
            super(binding.lnHistory);
            this.binding = binding;
        }
    }

}
