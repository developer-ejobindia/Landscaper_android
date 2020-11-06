package com.seazoned.landscaper.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.seazoned.landscaper.app.AppData;
import com.seazoned.landscaper.service.api.Api;
import com.seazoned.landscaper.service.util.Util;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by os4ed on 9/4/2018.
 */

public class RedDotService extends Service {
    // constant
    public static final long NOTIFY_INTERVAL = 2 * 1000; // 10 seconds

    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;
    private ArrayList<Object> mChatList;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getChatList();
        /*if (mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        // schedule task
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);*/
    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    if (Util.isConnected(getApplicationContext()))
                        getChatList();
                }

            });
        }
    }

    private void sendMessageToActivity(boolean status) {

        Intent intent = new Intent("RedDotStatus");
        // You can also include some extra data.
        intent.putExtra("status", status);

        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    private void getChatList() {

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, Api.sChatList, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        String success = response.optString("success");
                        if (success.equalsIgnoreCase("1")) {
                            mChatList = new ArrayList<>();
                            JSONObject data = response.optJSONObject("data");
                            JSONArray message = data.optJSONArray("message");
                            int i = 0;
                            for (i = 0; i < message.length(); i++) {
                                JSONObject jsonObject = message.optJSONObject(i);


                                SharedPreferences spChat = getApplicationContext().getSharedPreferences("Chat", Context.MODE_PRIVATE);
                                if (!spChat.getString(jsonObject.optString("order_no"), "").equalsIgnoreCase("")) {
                                    sendMessageToActivity(true);
                                    break;
                                }


                                //}
                            }
                            if (i == message.length()) {
                                sendMessageToActivity(false);
                            }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                VolleyLog.d("Error: " + error.getMessage());
                //TastyToast.makeText(context, "Network error.", TastyToast.LENGTH_SHORT, TastyToast.ERROR);


            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                if (AppData.sToken != null) {
                    headers.put("token", AppData.sToken);
                }
                return headers;
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getApplicationContext()).add(jsonObjReq);


    }

}
