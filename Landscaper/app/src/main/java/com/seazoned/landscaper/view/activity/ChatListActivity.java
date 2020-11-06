package com.seazoned.landscaper.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.View;

import com.seazoned.landscaper.R;
import com.seazoned.landscaper.app.AppData;
import com.seazoned.landscaper.databinding.ActivityChatListBinding;
import com.seazoned.landscaper.service.api.Api;
import com.seazoned.landscaper.service.parser.GetDataParser;
import com.seazoned.landscaper.service.preference.SharedPreferenceHelper;
import com.seazoned.landscaper.service.util.Util;
import com.seazoned.landscaper.view.adapter.ChatListAdapter;
import com.seazoned.landscaper.view.fragment.FootrFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatListActivity extends AppCompatActivity {
    ActivityChatListBinding mBinding;
    ArrayList<HashMap<String, String>> mChatList = new ArrayList<>();
    private boolean redDotMsgStatus=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat_list);
        Util.setPadding(this, mBinding.mainLayout);
        SharedPreferenceHelper helper = SharedPreferenceHelper.getInstance(getApplicationContext());
        String userId = helper.getUserId();
        String userToken = helper.getUserToken();
        if (!userId.equalsIgnoreCase("") && !userToken.equalsIgnoreCase("")) {
            AppData.sToken = userToken;
            AppData.sUserId = userId;

        }

        mBinding.rcvChatList.setLayoutManager(new LinearLayoutManager(this));
        mBinding.lnNotificationBell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatListActivity.this, Notification.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });
        mBinding.lnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.drawer.openDrawer(Gravity.LEFT);
            }
        });

    }

    private void getBookingHistoryDetails() {
        new GetDataParser(ChatListActivity.this, Api.sChatList, true, new GetDataParser.OnGetResponseListner() {
            public String success;

            @Override
            public void onGetResponse(JSONObject response) {
                if (response != null) {
                    try {
                        success = response.optString("success");
                        if (success.equalsIgnoreCase("1")) {
                            mChatList = new ArrayList<>();
                            JSONObject data = response.optJSONObject("data");
                            JSONArray message = data.optJSONArray("message");
                            for (int i = 0; i < message.length(); i++) {
                                JSONObject jsonObject = message.optJSONObject(i);
                                //if (!jsonObject.optString("status").trim().equalsIgnoreCase("-1")) {
                                HashMap<String, String> hashMap = new HashMap<>();

                                hashMap.put("order_no", jsonObject.optString("order_no").trim());
                                SharedPreferences spChat = getSharedPreferences("Chat", MODE_PRIVATE);
                                if (!spChat.getString(jsonObject.optString("order_no"), "").equalsIgnoreCase("")) {
                                    redDotMsgStatus=true;
                                }
                                hashMap.put("user_profile_image", jsonObject.optString("user_profile_image").trim());
                                hashMap.put("service_name", jsonObject.optString("service_name").trim());
                                hashMap.put("receiver_id", jsonObject.optString("customer_id").trim());
                                hashMap.put("service_id", jsonObject.optString("service_id").trim());
                                hashMap.put("customer_first_name", jsonObject.optString("customer_first_name").trim());
                                hashMap.put("customer_last_name", jsonObject.optString("customer_last_name").trim());
                                hashMap.put("user_profile_image", jsonObject.optString("user_profile_image").trim());
                                hashMap.put("android_firebase_token", jsonObject.optString("android_firebase_token").trim());
                                hashMap.put("iphone_firebase_token", jsonObject.optString("iphone_firebase_token").trim());
                                hashMap.put("device_type", jsonObject.optString("device_type").trim());

                                mChatList.add(hashMap);
                                //}
                            }
                            if (mChatList.size() > 0) {
                                mBinding.rcvChatList.setVisibility(View.VISIBLE);
                                mBinding.tvAlert.setVisibility(View.GONE);
                                mBinding.rcvChatList.setAdapter(new ChatListAdapter(ChatListActivity.this, mChatList));
                                ((FootrFragment)getSupportFragmentManager().findFragmentById(R.id.footerFragment)).showRedDot(redDotMsgStatus);

                            } else {
                                mBinding.rcvChatList.setVisibility(View.GONE);
                                mBinding.tvAlert.setVisibility(View.VISIBLE);
                            }
                        } else {
                            mBinding.rcvChatList.setVisibility(View.GONE);
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
    protected void onResume() {
        super.onResume();
        redDotMsgStatus=false;
        getBookingHistoryDetails();
        getNotiStatus();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
        startActivity(new Intent(ChatListActivity.this, DashBoardActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void getNotiStatus() {

        new GetDataParser(ChatListActivity.this, Api.sGetNotificationStatus, true, new GetDataParser.OnGetResponseListner() {
            @Override
            public void onGetResponse(JSONObject response) {

                if (response != null) {
                    try {

                        String success = response.optString("success");
                        String notification_count = response.optString("notification_count");

                        if (success.equalsIgnoreCase("1")) {

                            if (notification_count.length() > 0) {
                                mBinding.ivNotiDot.setVisibility(View.VISIBLE);
                            } else {
                                mBinding.ivNotiDot.setVisibility(View.GONE);
                            }

                        } else {
                            mBinding.ivNotiDot.setVisibility(View.GONE);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
