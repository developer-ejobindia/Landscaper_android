package com.seazoned.landscaper.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.seazoned.landscaper.R;
import com.seazoned.landscaper.databinding.NotificationRowBinding;
import com.seazoned.landscaper.service.util.Util;
import com.seazoned.landscaper.view.activity.ServiceHistoryDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by root on 13/4/18.
 */

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.ViewHolder> {
    ArrayList<HashMap<String,String>> mNotificationList;
    Context context;


    public NotificationListAdapter(Context context, ArrayList<HashMap<String, String>> mNotificationList) {
        this.context=context;
        this.mNotificationList=mNotificationList;
    }

    @Override
    public NotificationListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        NotificationRowBinding binding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.notification_row,parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(NotificationListAdapter.ViewHolder holder, final int position) {


        Spannable wordtoSpan = new SpannableString(mNotificationList.get(position).get("LandscaperName")
                +"\n"+mNotificationList.get(position).get("Status")+" for "+mNotificationList.get(position).get("Name"));
        wordtoSpan.setSpan(new RelativeSizeSpan(1f), 0,mNotificationList.get(position).get("LandscaperName").length(), 0);
        wordtoSpan.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0,mNotificationList.get(position).get("LandscaperName").length(), 0);
        wordtoSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorBlack)), 0, mNotificationList.get(position).get("LandscaperName").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.binding.tvNotification.setText(wordtoSpan);


        if (mNotificationList.get(position).get("ProfileImage").equalsIgnoreCase("") ||
        mNotificationList.get(position).get("ProfileImage").equalsIgnoreCase("null") ||

        mNotificationList.get(position).get("ProfileImage").equalsIgnoreCase(null)) {
            Picasso.with(context).load(R.drawable.icon_user).placeholder(R.drawable.icon_user).into(holder.binding.ivPrfpic);
        }
        else {
            Picasso.with(context).load(mNotificationList.get(position).get("ProfileImage")).resize(150,150).error(R.drawable.icon_user).into(holder.binding.ivPrfpic);
        }
        holder.binding.lnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(context,ServiceHistoryDetailsActivity.class);
                i.putExtra("bookingId",mNotificationList.get(position).get("bookingId"));
                i.putExtra("landscaper_id",mNotificationList.get(position).get("landscaper_id"));
                context.startActivity(i);
                ((Activity)context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        holder.binding.tvNotificationTime.setText(Util.changeAnyDateFormat(mNotificationList.get(position).get("timestamp"),"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd hh:mm a"));
    }

    @Override
    public int getItemCount() {
        return mNotificationList.size();
    }
    public  class ViewHolder extends RecyclerView.ViewHolder{
            NotificationRowBinding binding;

        public ViewHolder(NotificationRowBinding itemView) {
            super(itemView.lnMain);
            this.binding=itemView;
        }
    }
}
