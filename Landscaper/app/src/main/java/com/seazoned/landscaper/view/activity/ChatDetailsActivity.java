package com.seazoned.landscaper.view.activity;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.seazoned.landscaper.R;
import com.seazoned.landscaper.databinding.ActivityChatDetailsBinding;
import com.seazoned.landscaper.model.ChatModel;
import com.seazoned.landscaper.other.Test;
import com.seazoned.landscaper.service.preference.SharedPreferenceHelper;
import com.seazoned.landscaper.service.util.Util;
import com.seazoned.landscaper.view.adapter.ConversationDetailsAdapter;
import com.squareup.picasso.Picasso;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChatDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityChatDetailsBinding mBinding;
    private String orderNo;
    private FirebaseDatabase mFirebaseInstance;
    private DatabaseReference mFirebaseDatabase;
    private String receiverid;
    private String senderid;
    ConversationDetailsAdapter mAdapter = null;
    private LinearLayoutManager layoutManager;
    private String sServiceId;
    private String user_profile_image;
    private String device_type;
    private String android_firebase_token;
    private String iphone_firebase_token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat_details);
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            Window window = getWindow(); // in Activity's onCreate() for instance
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);

            tintManager.setTintColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("orders");

        orderNo = getIntent().getExtras().getString("orderno");
        //clear chat notify
        SharedPreferences spChat = getSharedPreferences("Chat", MODE_PRIVATE);
        spChat.edit().remove(orderNo).apply();

        receiverid = getIntent().getExtras().getString("receiverid");
        sServiceId = getIntent().getExtras().getString("service_id");
        String name = getIntent().getExtras().getString("name");
        android_firebase_token = getIntent().getExtras().getString("android_firebase_token");
        iphone_firebase_token = getIntent().getExtras().getString("iphone_firebase_token");
        user_profile_image = getIntent().getExtras().getString("user_profile_image");
        device_type = getIntent().getExtras().getString("device_type");

        /*if(sServiceId.equals("1")){
            mBinding.ivServiceLogo.setImageResource(R.drawable.ic_mowing_edging);
        }
        if(sServiceId.equals("2")){
            mBinding.ivServiceLogo.setImageResource(R.drawable.ic_leaf_removal);
        }
        if(sServiceId.equals("3")){
            mBinding.ivServiceLogo.setImageResource(R.drawable.ic_lawn_treatment);
        } if(sServiceId.equals("4")){
            mBinding.ivServiceLogo.setImageResource(R.drawable.aeration);
        } if(sServiceId.equals("5")){
            mBinding.ivServiceLogo.setImageResource(R.drawable.ic_sprinkler);
        } if(sServiceId.equals("6")){
            mBinding.ivServiceLogo.setImageResource(R.drawable.ic_pool_cleaning);
        } if(sServiceId.equals("7")){
            mBinding.ivServiceLogo.setImageResource(R.drawable.ic_snow_removal);
        }*/
        if (user_profile_image.equalsIgnoreCase("") || user_profile_image.equalsIgnoreCase("null")) {
            Picasso.with(this).load(R.drawable.icon_user).into(mBinding.ivServiceLogo);
        } else {
            Picasso.with(this).load(user_profile_image).placeholder(R.drawable.icon_user).error(R.drawable.icon_user).into(mBinding.ivServiceLogo);
        }
        senderid = new SharedPreferenceHelper().getUserId();
        mBinding.tvOrderNo.setText(name);
        mBinding.lnSend.setOnClickListener(this);
        mBinding.lnBack.setOnClickListener(this);

        mAdapter = new ConversationDetailsAdapter(ChatDetailsActivity.this, user_profile_image);
        layoutManager = new LinearLayoutManager(ChatDetailsActivity.this);
        layoutManager.setStackFromEnd(true);
        mBinding.rcvChatDetails.setLayoutManager(layoutManager);
        mBinding.rcvChatDetails.setAdapter(mAdapter);
        //mBinding.rcvChatDetails.setNestedScrollingEnabled(false);
        getChatDetails();
        /*mBinding.rcvChatDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.rcvChatDetails.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBinding.rcvChatDetails.scrollToPosition(mBinding.rcvChatDetails.getAdapter().getItemCount() - 1);
                    }
                }, 1000);
            }
        });*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseInstance.goOffline();
        SharedPreferences spChat = getSharedPreferences("Chat", MODE_PRIVATE);
        spChat.edit().remove(orderNo).apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseInstance.goOnline();
    }

    private void getChatDetails() {
        mFirebaseDatabase.child(orderNo).child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //int mPrevLength=mAdapter.getItemCount();
                try {
                    if (mAdapter != null)
                        mAdapter.removeAll();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        LinearLayoutManager layoutManager = ((LinearLayoutManager) mBinding.rcvChatDetails.getLayoutManager());
                        /*int pos = layoutManager.findLastCompletelyVisibleItemPosition();
                        if (pos==mPrevLength-1){
                            layoutManager=new LinearLayoutManager(ChatDetailsActivity.this);
                            layoutManager.setStackFromEnd(true);
                            mBinding.rcvChatDetails.setLayoutManager(layoutManager);
                        }*/
                        ChatModel model = dataSnapshot1.getValue(ChatModel.class);
                        String messg = model.getText();
                        mAdapter.addRow(model);
                    }
                    if (mAdapter != null)
                        mAdapter.notifyDataSetChanged();
                    if (mAdapter.getItemCount() > 0) {
                        int p = mAdapter.getItemCount();
                        mBinding.rcvChatDetails.smoothScrollToPosition(p);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == mBinding.lnSend) {
            String message = mBinding.etMessage.getText().toString().trim();
            if (TextUtils.isEmpty(message)) {
                Toast.makeText(this, "Write something here..", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                String currentDateandTime = sdf.format(new Date());
                ChatModel model = new ChatModel(senderid, receiverid, message, "" + currentDateandTime, "");
                mFirebaseDatabase.child(orderNo).child("messages").push().setValue(model);

                mBinding.etMessage.setText("");
                JSONObject jsonObject = new JSONObject();
                /*
                try {
                    jsonObject.put("to",firebase_token);
                    JSONObject data=new JSONObject();
                    data.put("status_name",message);
                    jsonObject.put("data",data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
                //if device type is ios
                try {
                    JSONObject data = new JSONObject();
                    data.put("status_name", message);
                    data.put("type", "chat");
                    data.put("orderNo", orderNo);
                    data.put("gcm.notification.status", "100");
                    jsonObject.put("data", data);

                    JSONArray firebase_token = new JSONArray();

                    if (!android_firebase_token.equalsIgnoreCase("")) {
                        device_type = "android";

                        firebase_token.put(android_firebase_token);
                        //jsonObject.put("registration_ids", firebase_token);
                        jsonObject.put("to", android_firebase_token);
                        sendNotification(jsonObject);
                    }
                    if (!iphone_firebase_token.equalsIgnoreCase("")) {
                        device_type = "iphone";

                        firebase_token.put(iphone_firebase_token);
                        //jsonObject.put("registration_ids", firebase_token);
                        jsonObject.put("to", iphone_firebase_token);
                        JSONObject notification = new JSONObject();
                        notification.put("title", "Seazoned");
                        notification.put("body", message);
                        jsonObject.put("notification", notification);
                        sendNotification(jsonObject);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                mFirebaseInstance.getReference("notification_"+receiverid).setValue(new Test("1"));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (view == mBinding.lnBack) {
                Util.closeKeyBoard(ChatDetailsActivity.this);
                finish();
            }
        }

        @Override
        public void onBackPressed () {
            mBinding.lnBack.performClick();
        }

    private void sendNotification(JSONObject jsonObject) {
        String url = "https://fcm.googleapis.com/fcm/send";

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(url, jsonObject, new Response.Listener<JSONObject>() {
            public String success;

            @Override
            public void onResponse(JSONObject response) {
                if (response!=null){
                    try {
                        success=response.optString("success");
                        if (success.equalsIgnoreCase("1")){

                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String> header=new HashMap<>();
                header.put("Content-Type","application/json");
                header.put("Authorization","key=AIzaSyD1cp8WZE-Cuv5PV07WS9tw5s_FSYRtrbQ");
                return header;
            }

        };
        Volley.newRequestQueue(ChatDetailsActivity.this).add(jsonObjectRequest);
    }

}


