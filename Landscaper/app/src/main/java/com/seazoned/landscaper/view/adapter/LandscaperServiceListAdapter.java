package com.seazoned.landscaper.view.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.seazoned.landscaper.R;
import com.seazoned.landscaper.databinding.BookingHistoryListRowBinding;
import com.seazoned.landscaper.databinding.ServiceListRowBinding;
import com.seazoned.landscaper.other.MyCustomProgressDialog;
import com.seazoned.landscaper.service.api.Api;
import com.seazoned.landscaper.service.util.Util;
import com.seazoned.landscaper.view.activity.AddServiceActivity;
import com.seazoned.landscaper.view.activity.ServiceHistoryDetailsActivity;
import com.seazoned.landscaper.view.activity.ServiceSettings;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class LandscaperServiceListAdapter extends RecyclerView.Adapter<LandscaperServiceListAdapter.BindingHolder> {
    Context mContext;
    ArrayList<HashMap<String, String>> mServiceList = new ArrayList<>();
    private ProgressDialog dialog;


    public LandscaperServiceListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ServiceListRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.service_list_row, parent, false);
        return new BindingHolder(binding);
    }

    @Override
    public void onBindViewHolder(final BindingHolder holder, final int position) {
        holder.binding.tvServiceName.setText(mServiceList.get(position).get("serviceName"));
        if (((Activity) mContext).getClass().getSimpleName().equalsIgnoreCase("RegistrationStep2Activity")) {
            holder.binding.lnCross.setVisibility(View.VISIBLE);
            holder.binding.lnOptions.setVisibility(View.GONE);
        } else if (((Activity) mContext).getClass().getSimpleName().equalsIgnoreCase("ServiceSettings")) {
            holder.binding.lnCross.setVisibility(View.GONE);
            holder.binding.lnOptions.setVisibility(View.VISIBLE);
        }
        holder.binding.lnCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> params = new HashMap<>();
                params.put("added_service_id", mServiceList.get(position).get("addedServiceId"));
                deleteRow(position, params);
            }
        });
        final PopupMenu popupMenu = new PopupMenu(mContext, holder.binding.lnOptions);
        popupMenu.inflate(R.menu.popup);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.edit:
                        ((Activity) mContext).finish();
                        mContext.startActivity(new Intent(mContext, AddServiceActivity.class)
                                .putExtra("page", "serviceSettings")
                                .putExtra("mode", "edit")
                                .putExtra("addedServiceId", mServiceList.get(position).get("addedServiceId"))
                                .putExtra("serviceId", mServiceList.get(position).get("serviceId"))
                                .putExtra("serviceName", mServiceList.get(position).get("serviceName"))
                        );
                        ((Activity) mContext).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        break;
                    case R.id.delete:
                        HashMap<String, String> params = new HashMap<>();
                        params.put("added_service_id", mServiceList.get(position).get("addedServiceId"));
                        deleteRow(position, params);

                        break;

                }
                return true;
            }
        });
        holder.binding.lnOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupMenu.show();
            }
        });


        switch (mServiceList.get(position).get("serviceId")) {
            case "1":
                holder.binding.ivLogo.setImageResource(R.drawable.ic_mowing_edging);
                break;
            case "2":
                holder.binding.ivLogo.setImageResource(R.drawable.ic_leaf_removal);
                break;
            case "3":
                holder.binding.ivLogo.setImageResource(R.drawable.ic_lawn_treatment);
                break;
            case "4":
                holder.binding.ivLogo.setImageResource(R.drawable.aeration);
                break;
            case "5":
                holder.binding.ivLogo.setImageResource(R.drawable.ic_sprinkler);
                break;
            case "6":
                holder.binding.ivLogo.setImageResource(R.drawable.ic_pool_cleaning);
                break;
            case "7":
                holder.binding.ivLogo.setImageResource(R.drawable.ic_snow_removal);
                break;


        }
    }


    @Override
    public int getItemCount() {
        return mServiceList.size();
    }

    public static class BindingHolder extends RecyclerView.ViewHolder {
        private ServiceListRowBinding binding;

        public BindingHolder(ServiceListRowBinding binding) {
            super(binding.lnList);
            this.binding = binding;
        }
    }

    public void addRow(HashMap<String, String> hashMap) {
        mServiceList.add(hashMap);
    }
    public ArrayList<HashMap<String,String>> getAlldata() {
        return mServiceList;
    }

    public void deleteRow(final int position, final HashMap<String, String> params) {

        if (!Util.isConnected(mContext)) {
            Util.showSnakBar(mContext, mContext.getResources().getString(R.string.internectconnectionerror));
            return;
        }
        dialog = MyCustomProgressDialog.ctor(mContext);
        dialog.setCancelable(false);
        dialog.setMessage("Please wait...");
        showpDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.sLandscaperDeleteService, new Response.Listener<String>() {
            public String msg;
            public String success;

            @Override
            public void onResponse(String data) {

                try {
                    JSONObject response = Util.getjsonobject(data);
                    success = response.optString("success");
                    if (success.equalsIgnoreCase("1")) {
                        mServiceList.remove(position);
                        notifyDataSetChanged();
                    }
                    msg = response.optString("msg");
                    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidepDialog();
                Util.showSnakBar(mContext, mContext.getResources().getString(R.string.networkerror));
                VolleyLog.d("Error: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(mContext).add(stringRequest);
        //mServiceList.remove(position);
    }
    public void clearData(){
        mServiceList.clear();
    }

    private void showpDialog() {
        if (!dialog.isShowing())
            dialog.show();
    }

    private void hidepDialog() {
        if (dialog.isShowing())
            dialog.dismiss();
    }


}
