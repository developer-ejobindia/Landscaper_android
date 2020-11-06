package com.seazoned.landscaper.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;


import com.seazoned.landscaper.R;
import com.seazoned.landscaper.app.AppData;
import com.seazoned.landscaper.databinding.ActivityNotificationBinding;
import com.seazoned.landscaper.service.api.Api;
import com.seazoned.landscaper.service.parser.GetDataParser;
import com.seazoned.landscaper.service.preference.SharedPreferenceHelper;
import com.seazoned.landscaper.view.adapter.NotificationListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Notification extends AppCompatActivity implements View.OnClickListener {

    ActivityNotificationBinding mBinding;
    ArrayList<HashMap<String, String>> mNotificationList;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(Notification.this, R.layout.activity_notification);
        SharedPreferences sp = getSharedPreferences("Noti", MODE_PRIVATE);
        type = sp.getString("type", "");
        sp.edit().clear().apply();


        SharedPreferenceHelper helper = SharedPreferenceHelper.getInstance(getApplicationContext());
        String userId = helper.getUserId();
        String userToken = helper.getUserToken();
        if (!userId.equalsIgnoreCase("") && !userToken.equalsIgnoreCase("")) {
            AppData.sToken = userToken;
            AppData.sUserId = userId;

        }

        mBinding.rcvNotificationList.setLayoutManager(new LinearLayoutManager(this));
        mBinding.rcvNotificationList.setNestedScrollingEnabled(false);
        mBinding.lnBack.setOnClickListener(this);

        getNotificationList();
    }

    private void getNotificationList() {

        new GetDataParser(Notification.this, Api.sNotificationList, true, new GetDataParser.OnGetResponseListner() {
            @Override
            public void onGetResponse(JSONObject response) {
                String msg, success;
                if (response != null) {
                    try {
                        msg = response.optString("msg");
                        success = response.optString("success");
                        if (success.equalsIgnoreCase("1")) {


                            JSONObject data = response.optJSONObject("data");
                            mNotificationList = new ArrayList<HashMap<String, String>>();

                            JSONArray notificationListUser = data.optJSONArray("notification_list_landcaper");
                            for (int i = 0; i < notificationListUser.length(); i++) {

                                JSONObject jsonObject = notificationListUser.optJSONObject(i);

                                String status = jsonObject.optString("status");
                                String name = jsonObject.optString("service_name");
                                String landscaper_name = jsonObject.optString("customer_name");
                                String profile_image = jsonObject.optString("profile_image");
                                String landscaper_id = jsonObject.optString("landscaper_id");
                                String order_id = jsonObject.optString("order_id");
                                String timestamp = jsonObject.optString("timestamp");

                                HashMap<String, String> hashMap = new HashMap<>();

                                hashMap.put("Status", status);
                                hashMap.put("Name", name);
                                hashMap.put("LandscaperName", landscaper_name);
                                hashMap.put("ProfileImage", profile_image);
                                hashMap.put("landscaper_id", landscaper_id);
                                hashMap.put("bookingId", order_id);
                                hashMap.put("timestamp", timestamp);

                                mNotificationList.add(hashMap);

                            }
                            if (mNotificationList.size() > 0) {
                                mBinding.rcvNotificationList.setVisibility(View.VISIBLE);
                                mBinding.tvAlert.setVisibility(View.GONE);
                                mBinding.rcvNotificationList.setAdapter(new NotificationListAdapter(Notification.this, mNotificationList));
                            } else {
                                mBinding.rcvNotificationList.setVisibility(View.GONE);
                                mBinding.tvAlert.setVisibility(View.VISIBLE);
                            }

                        } else {
                            mBinding.rcvNotificationList.setVisibility(View.GONE);
                            mBinding.tvAlert.setVisibility(View.VISIBLE);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }


            }
        });


    }

    @Override
    public void onClick(View view) {
        if (view == mBinding.lnBack) {
            if (type.equalsIgnoreCase("")) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            } else {
                finish();
                startActivity(new Intent(Notification.this, DashBoardActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }


        }
    }

    @Override
    public void onBackPressed() {
        mBinding.lnBack.performClick();
    }
}
