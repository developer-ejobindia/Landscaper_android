package com.seazoned.landscaper.pushnotification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.seazoned.landscaper.R;
import com.seazoned.landscaper.app.AppData;
import com.seazoned.landscaper.service.api.Api;
import com.seazoned.landscaper.service.preference.SharedPreferenceHelper;
import com.seazoned.landscaper.view.activity.ChatListActivity;
import com.seazoned.landscaper.view.activity.DashBoardActivity;
import com.seazoned.landscaper.view.activity.Notification;
import com.seazoned.landscaper.view.activity.SplashScreen;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by root on 24/11/16.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    private String type;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        SharedPreferences sp=getSharedPreferences("Noti",MODE_PRIVATE);
        sp.edit().putString("type","noti").commit();

        SharedPreferenceHelper helper = SharedPreferenceHelper.getInstance(getApplicationContext());
        String userId = helper.getUserId();
        String userToken = helper.getUserToken();
        if (!userId.equalsIgnoreCase("") && !userToken.equalsIgnoreCase("")) {
            AppData.sToken = userToken;
            AppData.sUserId = userId;

            if (remoteMessage.getData().size() > 0) {
                JSONObject json = new JSONObject(remoteMessage.getData());
                type = json.optString("type");
                if (type.equalsIgnoreCase("chat")){
                    SharedPreferences spChat=getSharedPreferences("Chat",MODE_PRIVATE);
                    spChat.edit().putString(json.optString("orderNo"),json.optString("orderNo")).apply();
                    Intent intent = new Intent(this, ChatListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getChatList();
                    sendNotification(intent, remoteMessage);
                }
                else {
                    Intent intent = new Intent(this, Notification.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    sendNotification(intent, remoteMessage);
                }
            }
        }
        else {
            Intent intent = new Intent(this, SplashScreen.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            sendNotification(intent, remoteMessage);
        }



    }

    public void sendNotification(Intent intent, RemoteMessage remoteMessage) {
        String channelId="CH1";
        int importance=NotificationManager.IMPORTANCE_HIGH;
        if (remoteMessage.getData().size() > 0) {
            try {
                int requestCode = 0;
                JSONObject json = new JSONObject(remoteMessage.getData());
                String status_name=json.optString("status_name");
                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
                Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this,channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(largeIcon)
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(status_name))
                        .setContentText(status_name)
                        .setAutoCancel(true)
                        .setSound(sound)
                        .setContentIntent(pendingIntent);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationChannel notificationChannel = new NotificationChannel(channelId, "Seazoned", importance);
                    notificationManager.createNotificationChannel(notificationChannel);
                }
                notificationManager.notify(0, noBuilder.build()); //0 = ID of notification
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else {
            try {
                int requestCode = 0;
                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
                Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder noBuilder = new NotificationCompat.Builder(this,channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(largeIcon)
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setContentText(remoteMessage.getNotification().getBody())
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getNotification().getBody()))
                        .setAutoCancel(true)
                        .setSound(sound)
                        //.addAction(R.mipmap.ic_launcher, "Dismiss", pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_MAX)//HIGH, MAX, FULL_SCREEN and setDefaults(MyNotification.DEFAULT_ALL) will make it a Heads Up Display Style
                        .setContentIntent(pendingIntent);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationChannel notificationChannel = new NotificationChannel(channelId, "Seazoned", importance);
                    notificationManager.createNotificationChannel(notificationChannel);
                }
                notificationManager.notify(0, noBuilder.build()); //0 = ID of notification

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void getChatList() {

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, Api.sChatList, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        String success = response.optString("success");
                        if (success.equalsIgnoreCase("1")) {
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
    private void sendMessageToActivity(boolean status) {

        Intent intent = new Intent("RedDotStatus");
        // You can also include some extra data.
        intent.putExtra("status", status);

        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

}