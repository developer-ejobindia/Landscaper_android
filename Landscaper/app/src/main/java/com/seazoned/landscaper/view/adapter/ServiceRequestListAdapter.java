package com.seazoned.landscaper.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.seazoned.landscaper.R;
import com.seazoned.landscaper.app.AppData;
import com.seazoned.landscaper.databinding.ServiceRequestRowBinding;
import com.seazoned.landscaper.service.api.Api;
import com.seazoned.landscaper.service.parser.PostDataParser;
import com.seazoned.landscaper.service.util.Util;
import com.seazoned.landscaper.view.activity.ServiceHistoryDetailsActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class ServiceRequestListAdapter extends RecyclerView.Adapter<ServiceRequestListAdapter.BindingHolder> {
    Context mContext;
    ArrayList<HashMap<String,String>> mServiceList;
    boolean flag;

    public ServiceRequestListAdapter(Context mContext, ArrayList<HashMap<String, String>> mServiceList, boolean flag) {
        this.mContext = mContext;
        this.mServiceList = mServiceList;
        this.flag = flag;
    }

    @Override
    public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ServiceRequestRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.service_request_row, parent, false);
        return new BindingHolder(binding);
    }

    @Override
    public void onBindViewHolder(final BindingHolder holder, final int position) {
        if (flag){
            holder.binding.lnAction.setVisibility(View.VISIBLE);
        }
        else {
            holder.binding.lnAction.setVisibility(View.GONE);
        }
        holder.binding.tvServiceName.setText(mServiceList.get(position).get("serviceName"));
        holder.binding.tvServiceDate.setText(Util.changeAnyDateFormat(mServiceList.get(position).get("serviceDate"),"yyyy-MM-dd","MMM-dd-yyyy"));
        holder.binding.tvTime.setText(Util.changeAnyDateFormat(mServiceList.get(position).get("serviceTime"),"hh:mm:ss","hh:mm a"));
        holder.binding.tvName.setText(mServiceList.get(position).get("name"));
        holder.binding.tvAddress.setText(mServiceList.get(position).get("address"));
        holder.binding.tvServicePrice.setText("$"+mServiceList.get(position).get("servicePrice"));
        holder.binding.tvAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String,String> params=new HashMap<>();
                params.put("book_service_id",mServiceList.get(position).get("serviceId"));
                params.put("status","1");
                params.put("accept_time",Util.getSystemDateTime());
                acceptOrDecline(position,Api.sAcceptOrDeclineRequest,params,holder.binding.tvAccept);
            }
        });
        holder.binding.tvDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String,String> params=new HashMap<>();
                params.put("book_service_id",mServiceList.get(position).get("serviceId"));
                params.put("status","-1");
                params.put("reject_time",Util.getSystemDateTime());
                acceptOrDecline(position,Api.sAcceptOrDeclineRequest,params,holder.binding.tvDecline);
            }
        });
        holder.binding.lnServiceRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppData.sServiceHistoryFlag="DashBoard";
                Intent i=new Intent(mContext,ServiceHistoryDetailsActivity.class);
                i.putExtra("bookingId",mServiceList.get(position).get("serviceId"));
                i.putExtra("landscaper_id",mServiceList.get(position).get("landscaper_id"));
                mContext.startActivity(i);
                ((Activity)mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mServiceList.size();
    }

    public static class BindingHolder extends RecyclerView.ViewHolder {
        private ServiceRequestRowBinding binding;

        public BindingHolder(ServiceRequestRowBinding binding) {
            super(binding.lnServiceRequest);
            this.binding = binding;
        }
    }

    private void acceptOrDecline(final int position, String sAcceptOrDeclineRequest, HashMap<String, String> params, View view) {

        new PostDataParser(mContext, sAcceptOrDeclineRequest, params, true, view, new PostDataParser.OnGetResponseListner() {
            public String msg;
            public String success;

            @Override
            public void onGetResponse(JSONObject response) {
                if (response!=null){
                    try {
                        success=response.optString("success");
                        msg=response.optString("msg");
                        if (success.equalsIgnoreCase("1")){
                            //mServiceList.remove(position);
                            //notifyDataSetChanged();
                            Activity a = (Activity) mContext;
                            Intent i = a.getIntent();
                            a.overridePendingTransition(0, 0);
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            a.finish();
                            a.overridePendingTransition(0, 0);
                            a.startActivity(i);
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });

    }


}
