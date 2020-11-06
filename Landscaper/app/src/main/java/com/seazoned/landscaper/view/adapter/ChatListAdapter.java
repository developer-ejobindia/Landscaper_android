package com.seazoned.landscaper.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.seazoned.landscaper.R;
import com.seazoned.landscaper.databinding.BookingHistoryListRowBinding;
import com.seazoned.landscaper.databinding.ChatlistRowBinding;
import com.seazoned.landscaper.view.activity.ChatDetailsActivity;
import com.seazoned.landscaper.view.activity.ServiceHistoryDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;


public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.BindingHolder> {
    Context mContext;
    ArrayList<HashMap<String, String>> mChatList;

    public ChatListAdapter(Context mContext, ArrayList<HashMap<String, String>> mChatList) {
        this.mContext = mContext;
        this.mChatList = mChatList;
    }

    @Override
    public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ChatlistRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.chatlist_row, parent, false);
        return new BindingHolder(binding);
    }

    @Override
    public void onBindViewHolder(final BindingHolder holder, final int position) {
        final HashMap<String, String> hashMap = mChatList.get(position);

        SharedPreferences spChat=mContext.getSharedPreferences("Chat",Context.MODE_PRIVATE);
        if (spChat.getString(hashMap.get("order_no"),"").equalsIgnoreCase("")){
            holder.binding.ivNewMessage.setVisibility(View.GONE);
        }
        else {
            holder.binding.ivNewMessage.setVisibility(View.VISIBLE);
        }
        final String sServiceId=hashMap.get("service_id");
        /*if(sServiceId.equals("1")){
            holder.binding.ivServiceLogo.setImageResource(R.drawable.ic_mowing_edging);
        }
        if(sServiceId.equals("2")){
            holder.binding.ivServiceLogo.setImageResource(R.drawable.ic_leaf_removal);
        }
        if(sServiceId.equals("3")){
            holder.binding.ivServiceLogo.setImageResource(R.drawable.ic_lawn_treatment);
        } if(sServiceId.equals("4")){
            holder.binding.ivServiceLogo.setImageResource(R.drawable.aeration);
        } if(sServiceId.equals("5")){
            holder.binding.ivServiceLogo.setImageResource(R.drawable.ic_sprinkler);
        } if(sServiceId.equals("6")){
            holder.binding.ivServiceLogo.setImageResource(R.drawable.ic_pool_cleaning);
        } if(sServiceId.equals("7")){
            holder.binding.ivServiceLogo.setImageResource(R.drawable.ic_snow_removal);
        }*/
        if (hashMap.get("user_profile_image") != null && !hashMap.get("user_profile_image").equals("")) {
            Picasso.with(mContext).load(hashMap.get("user_profile_image")).error(R.drawable.ic_mowing_edging).into(holder.binding.ivServiceLogo);
        } else {
            if (sServiceId.equals("1")) {
                holder.binding.ivServiceLogo.setImageResource(R.drawable.ic_mowing_edging);
            }
            if (sServiceId.equals("2")) {
                holder.binding.ivServiceLogo.setImageResource(R.drawable.ic_leaf_removal);
            }
            if (sServiceId.equals("3")) {
                holder.binding.ivServiceLogo.setImageResource(R.drawable.ic_lawn_treatment);
            }
            if (sServiceId.equals("4")) {
                holder.binding.ivServiceLogo.setImageResource(R.drawable.aeration);
            }
            if (sServiceId.equals("5")) {
                holder.binding.ivServiceLogo.setImageResource(R.drawable.ic_sprinkler);
            }
            if (sServiceId.equals("6")) {
                holder.binding.ivServiceLogo.setImageResource(R.drawable.ic_pool_cleaning);
            }
            if (sServiceId.equals("7")) {

                holder.binding.ivServiceLogo.setImageResource(R.drawable.ic_snow_removal);
            }
        }

        holder.binding.tvName.setText(hashMap.get("customer_first_name")+" "+hashMap.get("customer_last_name"));
        holder.binding.tvServiceName.setText(hashMap.get("service_name"));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mContext, ChatDetailsActivity.class);
                intent.putExtra("orderno",hashMap.get("order_no"));
                intent.putExtra("name",hashMap.get("customer_first_name")+" "+hashMap.get("customer_last_name"));
                intent.putExtra("receiverid",hashMap.get("receiver_id"));
                intent.putExtra("service_id",sServiceId);
                intent.putExtra("user_profile_image",hashMap.get("user_profile_image"));
                intent.putExtra("android_firebase_token", hashMap.get("android_firebase_token"));
                intent.putExtra("iphone_firebase_token", hashMap.get("iphone_firebase_token"));
                intent.putExtra("device_type",hashMap.get("device_type"));
                ((Activity)mContext).startActivity(intent);
            }
        });

    }


    @Override
    public int getItemCount() {
        return mChatList.size();
    }

    public static class BindingHolder extends RecyclerView.ViewHolder {
        private ChatlistRowBinding binding;

        public BindingHolder(ChatlistRowBinding binding) {
            super(binding.lnMain);
            this.binding = binding;
        }
    }


}
