package com.seazoned.landscaper.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.seazoned.landscaper.R;
import com.seazoned.landscaper.app.AppData;
import com.seazoned.landscaper.databinding.BookingHistoryListRowBinding;
import com.seazoned.landscaper.service.util.Util;
import com.seazoned.landscaper.view.activity.ProfileActivity;
import com.seazoned.landscaper.view.activity.ServiceHistoryDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;


public class BookingHistoryListAdapter extends RecyclerView.Adapter<BookingHistoryListAdapter.BindingHolder> {
    Context mContext;
    ArrayList<HashMap<String, String>> mHistoryList;

    public BookingHistoryListAdapter(Context mContext, ArrayList<HashMap<String, String>> mHistoryList) {
        this.mContext = mContext;
        this.mHistoryList = mHistoryList;
    }

    @Override
    public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BookingHistoryListRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.booking_history_list_row, parent, false);
        return new BindingHolder(binding);
    }

    @Override
    public void onBindViewHolder(final BindingHolder holder, final int position) {
        final HashMap<String, String> hashMap = mHistoryList.get(position);
        holder.binding.tvOrderNo.setText(hashMap.get("order_no"));
        holder.binding.tvServiceName.setText(hashMap.get("service_name"));
        holder.binding.tvLandscaperName.setText(hashMap.get("landscaper_name"));
        //holder.binding.tvWorkingStatus.setText(hashMap.get("status_name"));
        holder.binding.tvTime.setText(Util.changeAnyDateFormat(hashMap.get("booking_time"), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd hh:mm a"));


        if (hashMap.get("profile_image").equalsIgnoreCase("") ||
                hashMap.get("profile_image").equalsIgnoreCase("null") ||
                hashMap.get("profile_image").equalsIgnoreCase(null)) {
            Picasso.with(mContext).load(R.drawable.ic_user).into(holder.binding.ivLandscaperPic);
        } else {
            holder.binding.ivLandscaperPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Util.getPopUpWindow(mContext, hashMap.get("profile_image"));
                }
            });
            Picasso.with(mContext).load(hashMap.get("profile_image")).error(R.drawable.ic_user).resize(150, 150).into(holder.binding.ivLandscaperPic);
        }

        holder.binding.lnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //((Activity)mContext).finish();
                AppData.sServiceHistoryFlag = "BookingHistory";
                Intent i = new Intent(mContext, ServiceHistoryDetailsActivity.class);
                i.putExtra("bookingId", hashMap.get("bookingId"));
                i.putExtra("landscaper_id", hashMap.get("landscaper_id"));
                mContext.startActivity(i);
                ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        String status=hashMap.get("status");
        String is_completed = hashMap.get("is_completed");
        holder.binding.tvPaymentStatus.setVisibility(View.VISIBLE);
        if (status.equalsIgnoreCase("-1")){
            holder.binding.tvPaymentStatus.setVisibility(View.GONE);
            Drawable img = mContext.getResources().getDrawable(R.drawable.ic_dot_red);
            holder.binding.tvWorkingStatus.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            holder.binding.tvWorkingStatus.setText("Service request rejected");
            holder.binding.tvWorkingStatus.setTextColor(mContext.getResources().getColor(R.color.colorWarning));
        }
        if (status.equalsIgnoreCase("0") &&
                is_completed.equalsIgnoreCase("0")) {
            holder.binding.tvPaymentStatus.setVisibility(View.GONE);
            Drawable img = mContext.getResources().getDrawable(R.drawable.ic_dot);
            holder.binding.tvWorkingStatus.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            holder.binding.tvWorkingStatus.setText("Request received");
            holder.binding.tvWorkingStatus.setTextColor(mContext.getResources().getColor(R.color.colorWarning));
        } else if (status.equalsIgnoreCase("1") &&
                is_completed.equalsIgnoreCase("0")) {
            //if (hashMap.get("transaction_id").equalsIgnoreCase("")||hashMap.get("transaction_id").equalsIgnoreCase("null")||hashMap.get("transaction_id").equalsIgnoreCase(null)) {
            Drawable img1 = mContext.getResources().getDrawable(R.drawable.ic_dot_green);
            holder.binding.tvWorkingStatus.setCompoundDrawablesWithIntrinsicBounds(img1, null, null, null);
            holder.binding.tvWorkingStatus.setText("Request accepted");
            holder.binding.tvWorkingStatus.setTextColor(mContext.getResources().getColor(R.color.colorWarning));

            Drawable img = mContext.getResources().getDrawable(R.drawable.ic_pending);
            holder.binding.tvPaymentStatus.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            holder.binding.tvPaymentStatus.setText("Payment pending");
            holder.binding.tvPaymentStatus.setTextColor(mContext.getResources().getColor(R.color.colorWarning));
        } else if (status.equalsIgnoreCase("2") &&
                is_completed.equalsIgnoreCase("1")) {
            Drawable img = mContext.getResources().getDrawable(R.drawable.ic_dot);
            holder.binding.tvPaymentStatus.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            holder.binding.tvPaymentStatus.setText("Payment is in Escrow");
            holder.binding.tvPaymentStatus.setTextColor(mContext.getResources().getColor(R.color.colorWarning));


            Drawable img1 = mContext.getResources().getDrawable(R.drawable.ic_dot);
            holder.binding.tvWorkingStatus.setCompoundDrawablesWithIntrinsicBounds(img1, null, null, null);
            holder.binding.tvWorkingStatus.setText("Work in progress");
            holder.binding.tvWorkingStatus.setTextColor(mContext.getResources().getColor(R.color.colorWarning));
        } else if (status.equalsIgnoreCase("3") &&
                is_completed.equalsIgnoreCase("1")) {
            Drawable img = mContext.getResources().getDrawable(R.drawable.ic_dot);
            holder.binding.tvPaymentStatus.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            holder.binding.tvPaymentStatus.setText(" Escrow release request has been sent");
            holder.binding.tvPaymentStatus.setTextColor(mContext.getResources().getColor(R.color.colorWarning));

            Drawable img1 = mContext.getResources().getDrawable(R.drawable.ic_dot_green);
            holder.binding.tvWorkingStatus.setCompoundDrawablesWithIntrinsicBounds(img1, null, null, null);
            holder.binding.tvWorkingStatus.setText("Job complete");
            holder.binding.tvWorkingStatus.setTextColor(mContext.getResources().getColor(R.color.colorActiveNavText));
        } else if (status.equalsIgnoreCase("3") &&
                is_completed.equalsIgnoreCase("2")) {
            Drawable img = mContext.getResources().getDrawable(R.drawable.ic_success);
            holder.binding.tvPaymentStatus.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            holder.binding.tvPaymentStatus.setText("Payment success");
            holder.binding.tvPaymentStatus.setTextColor(mContext.getResources().getColor(R.color.colorActiveNavText));
            //}

            Drawable img1 = mContext.getResources().getDrawable(R.drawable.ic_dot_green);
            holder.binding.tvWorkingStatus.setCompoundDrawablesWithIntrinsicBounds(img1, null, null, null);
            holder.binding.tvWorkingStatus.setText("Job complete");
            holder.binding.tvWorkingStatus.setTextColor(mContext.getResources().getColor(R.color.colorActiveNavText));
        }

//previous
        /*if (hashMap.get("status").equalsIgnoreCase("-1")) {
            holder.binding.tvPaymentStatus.setVisibility(View.GONE);
            Drawable img = mContext.getResources().getDrawable(R.drawable.ic_dot_red);
            holder.binding.tvWorkingStatus.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            holder.binding.tvWorkingStatus.setText("Service Request Rejected");
            holder.binding.tvWorkingStatus.setTextColor(mContext.getResources().getColor(R.color.colorWarning));
        }
        if (hashMap.get("status").equalsIgnoreCase("0")) {
            holder.binding.tvPaymentStatus.setVisibility(View.GONE);
            Drawable img = mContext.getResources().getDrawable(R.drawable.ic_dot);
            holder.binding.tvWorkingStatus.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            holder.binding.tvWorkingStatus.setText("Service Request Received");
            holder.binding.tvWorkingStatus.setTextColor(mContext.getResources().getColor(R.color.colorWarning));
        }
        *//*else if (hashMap.get("status").equalsIgnoreCase("0")){
            holder.binding.tvPaymentStatus.setVisibility(View.GONE);
            Drawable img1 = mContext.getResources().getDrawable(R.drawable.ic_dot_green);
            holder.binding.tvWorkingStatus.setCompoundDrawablesWithIntrinsicBounds(img1, null, null, null);
            holder.binding.tvWorkingStatus.setText("Service Request Accepted");
            holder.binding.tvWorkingStatus.setTextColor(mContext.getResources().getColor(R.color.colorWarning));
            if (hashMap.get("transaction_id").equalsIgnoreCase("")||hashMap.get("transaction_id").equalsIgnoreCase("null")||hashMap.get("transaction_id").equalsIgnoreCase(null)) {
                Drawable img = mContext.getResources().getDrawable(R.drawable.ic_pending);
                holder.binding.tvPaymentStatus.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                holder.binding.tvPaymentStatus.setText("Payment Pending");
                holder.binding.tvPaymentStatus.setTextColor(mContext.getResources().getColor(R.color.colorWarning));

            }
            else {
                Drawable img = mContext.getResources().getDrawable(R.drawable.ic_success);
                holder.binding.tvPaymentStatus.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                holder.binding.tvPaymentStatus.setText("Payment Success");
                holder.binding.tvPaymentStatus.setTextColor(mContext.getResources().getColor(R.color.colorActiveNavText));

            }

        }*//*
        else if (hashMap.get("status").equalsIgnoreCase("1")) {
            holder.binding.tvPaymentStatus.setVisibility(View.VISIBLE);
            //if (hashMap.get("transaction_id").equalsIgnoreCase("")||hashMap.get("transaction_id").equalsIgnoreCase("null")||hashMap.get("transaction_id").equalsIgnoreCase(null)) {
            Drawable img1 = mContext.getResources().getDrawable(R.drawable.ic_dot_green);
            holder.binding.tvWorkingStatus.setCompoundDrawablesWithIntrinsicBounds(img1, null, null, null);
            holder.binding.tvWorkingStatus.setText("Service Request Accepted");
            holder.binding.tvWorkingStatus.setTextColor(mContext.getResources().getColor(R.color.colorWarning));

            Drawable img = mContext.getResources().getDrawable(R.drawable.ic_pending);
            holder.binding.tvPaymentStatus.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            holder.binding.tvPaymentStatus.setText("Payment Pending");
            holder.binding.tvPaymentStatus.setTextColor(mContext.getResources().getColor(R.color.colorWarning));

            *//*}
            else {
        Drawable img1 = mContext.getResources().getDrawable(R.drawable.ic_dot);
        holder.binding.tvWorkingStatus.setCompoundDrawablesWithIntrinsicBounds(img1, null, null, null);
        holder.binding.tvWorkingStatus.setText("Work In Progress");
        holder.binding.tvWorkingStatus.setTextColor(mContext.getResources().getColor(R.color.colorWarning));

        Drawable img = mContext.getResources().getDrawable(R.drawable.ic_success);
        holder.binding.tvPaymentStatus.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        holder.binding.tvPaymentStatus.setText("Payment Success");
        holder.binding.tvPaymentStatus.setTextColor(mContext.getResources().getColor(R.color.colorActiveNavText));

    }*//*


        } else if (hashMap.get("status").equalsIgnoreCase("2")) {
            holder.binding.tvPaymentStatus.setVisibility(View.VISIBLE);
            if (hashMap.get("transaction_id").equalsIgnoreCase("") || hashMap.get("transaction_id").equalsIgnoreCase("null") || hashMap.get("transaction_id").equalsIgnoreCase(null)) {
                Drawable img = mContext.getResources().getDrawable(R.drawable.ic_pending);
                holder.binding.tvPaymentStatus.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                holder.binding.tvPaymentStatus.setText("Payment Pending");
                holder.binding.tvPaymentStatus.setTextColor(mContext.getResources().getColor(R.color.colorWarning));
            } else {
                Drawable img = mContext.getResources().getDrawable(R.drawable.ic_success);
                holder.binding.tvPaymentStatus.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                holder.binding.tvPaymentStatus.setText("Payment Success");
                holder.binding.tvPaymentStatus.setTextColor(mContext.getResources().getColor(R.color.colorActiveNavText));
            }

            Drawable img1 = mContext.getResources().getDrawable(R.drawable.ic_dot);
            holder.binding.tvWorkingStatus.setCompoundDrawablesWithIntrinsicBounds(img1, null, null, null);
            holder.binding.tvWorkingStatus.setText("Work In Progress");
            holder.binding.tvWorkingStatus.setTextColor(mContext.getResources().getColor(R.color.colorWarning));

        } else if (hashMap.get("status").equalsIgnoreCase("3")) {
            holder.binding.tvPaymentStatus.setVisibility(View.VISIBLE);
            if (hashMap.get("transaction_id").equalsIgnoreCase("") || hashMap.get("transaction_id").equalsIgnoreCase("null") || hashMap.get("transaction_id").equalsIgnoreCase(null)) {
                Drawable img = mContext.getResources().getDrawable(R.drawable.ic_pending);
                holder.binding.tvPaymentStatus.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                holder.binding.tvPaymentStatus.setText("Payment Pending");
                holder.binding.tvPaymentStatus.setTextColor(mContext.getResources().getColor(R.color.colorWarning));
            } else {
                Drawable img = mContext.getResources().getDrawable(R.drawable.ic_success);
                holder.binding.tvPaymentStatus.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                holder.binding.tvPaymentStatus.setText("Payment Success");
                holder.binding.tvPaymentStatus.setTextColor(mContext.getResources().getColor(R.color.colorActiveNavText));

            }

            Drawable img1 = mContext.getResources().getDrawable(R.drawable.ic_dot_green);
            holder.binding.tvWorkingStatus.setCompoundDrawablesWithIntrinsicBounds(img1, null, null, null);
            holder.binding.tvWorkingStatus.setText("Success");
            holder.binding.tvWorkingStatus.setTextColor(mContext.getResources().getColor(R.color.colorActiveNavText));
        }*///previous

        /*if (position % 2 == 0) {
            holder.binding.tvPaymentStatus.setText("Payment Success");
            holder.binding.tvPaymentStatus.setTextColor(mContext.getResources().getColor(R.color.colorActiveNavText));
            Drawable img = mContext.getResources().getDrawable(R.drawable.ic_success);
            holder.binding.tvPaymentStatus.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        } else {
            holder.binding.tvPaymentStatus.setText("Pending Payment");
            holder.binding.tvPaymentStatus.setTextColor(mContext.getResources().getColor(R.color.colorWarning));
            Drawable img = mContext.getResources().getDrawable(R.drawable.ic_pending);
            holder.binding.tvPaymentStatus.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                *//*((Activity)mContext).finish();
                mContext.startActivity(new Intent(mContext,ServiceHistoryDetailsActivity.class));
                ((Activity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);*//*
            }
        });*/

    }


    @Override
    public int getItemCount() {
        return mHistoryList.size();
    }

    public static class BindingHolder extends RecyclerView.ViewHolder {
        private BookingHistoryListRowBinding binding;

        public BindingHolder(BookingHistoryListRowBinding binding) {
            super(binding.lnHistory);
            this.binding = binding;
        }
    }


}
