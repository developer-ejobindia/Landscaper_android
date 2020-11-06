package com.seazoned.landscaper.view.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.iid.FirebaseInstanceId;
import com.seazoned.landscaper.R;
import com.seazoned.landscaper.app.AppData;
import com.seazoned.landscaper.databinding.ActivityDashBoardBinding;
import com.seazoned.landscaper.databinding.WeatherRowBinding;
import com.seazoned.landscaper.other.GPSTracker;
import com.seazoned.landscaper.other.MyCustomProgressDialog;
import com.seazoned.landscaper.service.api.Api;
import com.seazoned.landscaper.service.parser.GetDataParser;
import com.seazoned.landscaper.service.parser.PostDataParser;
import com.seazoned.landscaper.service.preference.SharedPreferenceHelper;
import com.seazoned.landscaper.service.util.Util;
import com.seazoned.landscaper.view.adapter.ViewPagerAdapter;
import com.seazoned.landscaper.view.fragment.PendingJobFragment;
import com.seazoned.landscaper.view.fragment.ServiceRequestFragment;


import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DashBoardActivity extends AppCompatActivity {
    private static final int LOCATION_CODE = 1;
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private ActivityDashBoardBinding mBinding;
    private Toast toast = null;
    private Location location = null;
    private GPSTracker gps;
    private double my_longitude = 0.0;
    private double my_latitude = 0.0;
    private ProgressDialog dialog;
    private ViewPagerAdapter mAdapter;
    private TextView tabOne;
    private TextView tabTwo;
    private TextView tvCount1;
    private String TAG="LOCATION";
    private static final String CONSUMER_KEY = "dj0yJmk9b01UcTRRRjFLbnRUJnM9Y29uc3VtZXJzZWNyZXQmc3Y9MCZ4PWZh";
    private static final String CONSUMER_SECRET = "7b6d14d662288371cce993f2660dc237dbe00288";
    private static final String appId = "QZYW3s5i";

    private void showpDialog() {
        if (!dialog.isShowing())
            dialog.show();
    }

    private void hidepDialog() {
        if (dialog.isShowing())
            dialog.dismiss();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_dash_board);
        Util.setPadding(this, mBinding.mainLayout);
        LocalBroadcastManager.getInstance(DashBoardActivity.this).registerReceiver(
                gpsLocationReceiver, new IntentFilter("GPS_LOCATION"));
        SharedPreferenceHelper helper = SharedPreferenceHelper.getInstance(getApplicationContext());
        String userId = helper.getUserId();
        String userToken = helper.getUserToken();
        if (!userId.equalsIgnoreCase("") && !userToken.equalsIgnoreCase("")) {
            AppData.sToken = userToken;
            AppData.sUserId = userId;
        }

        getAccountInfo();

        /*else {
            finish();
            Intent intent = new Intent(this, SplashScreen.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }*/
        mBinding.lnNotificationBell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashBoardActivity.this, Notification.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });
        mBinding.lnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.drawer.openDrawer(Gravity.LEFT);
            }
        });
        checkLocationPermission();

        Locale l = Locale.getDefault();
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mAdapter.addFragment(new ServiceRequestFragment(), "Service Requests");
        mAdapter.addFragment(new PendingJobFragment(), "Pending Jobs");
        //mAdapter.addFragment(new Students(), "STUDENTS".toUpperCase(l));
        mBinding.mContainer.setAdapter(mAdapter);
        mBinding.tabLayout.setupWithViewPager(mBinding.mContainer);
        setupTabIcons();
        saveDeviceToken();
        mBinding.mContainer.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 0) {
                    tabOne.setTextColor(getResources().getColor(R.color.colorActiveNavText));
                    tabTwo.setTextColor(getResources().getColor(R.color.colorText));
                } else if (position == 1) {
                    tabTwo.setTextColor(getResources().getColor(R.color.colorActiveNavText));
                    tabOne.setTextColor(getResources().getColor(R.color.colorText));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }
    private void getAccountInfo() {
        new GetDataParser(DashBoardActivity.this, Api.sViewPaypalAccount, true, new GetDataParser.OnGetResponseListner() {
            @Override
            public void onGetResponse(JSONObject response) {
                if (response != null) {
                    try {
                        String msg, success;
                        success = response.optString("success");
                        msg = response.optString("msg");
                        if (!success.equalsIgnoreCase("1")) {
                            AlertDialog.Builder builder=new AlertDialog.Builder(DashBoardActivity.this);
                            builder.setMessage("Please setup payment account to get service request");
                            builder.setCancelable(false);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    startActivity(new Intent(DashBoardActivity.this,PaymentInfoActivity.class));
                                }
                            });
                            builder.show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }


    private void checkLocationPermission() {
        int mLocationPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (Build.VERSION.SDK_INT >= 23) {
            if (mLocationPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_CODE);
            } else {
                my_location();
            }
        } else {
            my_location();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_CODE) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    my_location();
                } else {
                    checkLocationPermission();
                }
            }
        }
    }
    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        gps = new GPSTracker(DashBoardActivity.this);
                        if (gps.canGetLocation) {
                            my_longitude = gps.getLongitude();
                            my_latitude = gps.getLatitude();
                            if (my_latitude==0.0&&my_longitude==0.0){

                                displayLocationSettingsRequest(DashBoardActivity.this);
                            }
                            else
                            getWeatherForecast(my_latitude, my_longitude);

                            //List<Address> addressList=gps.getGeocoderAddress(DashBoardActivity.this);


                        }
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(DashBoardActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }
    private BroadcastReceiver gpsLocationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    gps = new GPSTracker(DashBoardActivity.this);
                    if (gps.canGetLocation) {
                        my_longitude = gps.getLongitude();
                        my_latitude = gps.getLatitude();
                        if (my_latitude==0.0&&my_longitude==0.0){
                            Intent i = getIntent();
                            overridePendingTransition(0, 0);
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(i);
                        }
                        else
                            getWeatherForecast(my_latitude, my_longitude);


                    }
                }
            },2000);

            //If Action is Location

        }
    };
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // This log is never called
        Log.d("onActivityResult()", Integer.toString(resultCode));

        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode)
        {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                    {
                        // All required changes were successfully made
                        Intent intent = new Intent("GPS_LOCATION");
                        // You can also include some extra data.

                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                        break;
                    }
                    case Activity.RESULT_CANCELED:
                    {
                        // The user was asked to change settings, but chose not to
                        Intent i = getIntent();
                        overridePendingTransition(0, 0);
                        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(i);
                        break;
                    }
                    default:
                    {
                        break;
                    }
                }
                break;
        }
    }

    public void my_location() {
        displayLocationSettingsRequest(DashBoardActivity.this);

    }

    /*private void getWoeid(double my_latitude, double my_longitude) {
        String query = "select woeid from geo.places where text=\"(" + my_latitude + "," + my_longitude + ")\" limit 1";
        query = Uri.encode(query);

        String woeidUrl = "https://query.yahooapis.com/v1/public/yql?format=json&q=" + query + "&diagnostics=false";

        if (!Util.isConnected(DashBoardActivity.this)) {
            Util.showSnakBar(DashBoardActivity.this, getResources().getString(R.string.internectconnectionerror));
            return;
        }
        dialog = MyCustomProgressDialog.ctor(DashBoardActivity.this);
        dialog.setCancelable(false);
        dialog.setMessage("Please wait...");
        //showpDialog();


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, woeidUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                if (response != null) {
                    try {
                        if (response.has("query")) {
                            JSONObject jsonObject = response.getJSONObject("query");
                            String count = jsonObject.optString("count");
                            if (count.equalsIgnoreCase("1")) {
                                JSONObject result = jsonObject.getJSONObject("results");
                                JSONObject place = result.getJSONObject("place");
                                String woeid = place.optString("woeid");
                                getWeatherForecast(woeid);

                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //hidepDialog();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //hidepDialog();
                Util.showSnakBar(DashBoardActivity.this, getResources().getString(R.string.networkerror));
                VolleyLog.d("Error: " + error.getMessage());
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //AppController.getInstance().addToRequestQueue(jsonObjReq);
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }*/

    private void getWeatherForecast(double my_latitude, double my_longitude) {

        /*String query1 = "select woeid from geo.places where text=\"(" + my_latitude + "," + my_longitude + ")\" limit 1";
        String query = "select * from weather.forecast where woeid in (" + query1 + ") and u='f'";
        query = Uri.encode(query);
        String weatherUrl = "https://query.yahooapis.com/v1/public/yql?q=" + query + "&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";

        if (!Util.isConnected(DashBoardActivity.this)) {
            Util.showSnakBar(DashBoardActivity.this, getResources().getString(R.string.internectconnectionerror));
            return;
        }
        dialog = MyCustomProgressDialog.ctor(DashBoardActivity.this);
        dialog.setCancelable(false);
        dialog.setMessage("Please wait...");
        showpDialog();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, weatherUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                if (response != null) {
                    try {
                        JSONObject query = response.getJSONObject("query");
                        String mCount = query.optString("count");
                        if (mCount.equalsIgnoreCase("1")) {
                            JSONObject results = query.getJSONObject("results");

                            JSONObject channel = results.getJSONObject("channel");

                            JSONObject location = channel.getJSONObject("location");
                            String city = location.optString("city");
                            String country = location.optString("country");
                            String region = location.optString("region");
                            //set location
                            mBinding.tvAddress.setText(city + "," + region + ", " + country);

                            JSONObject item = channel.getJSONObject("item");

                            JSONObject condition = item.getJSONObject("condition");

                            String text = condition.optString("text");
                            String temp = condition.optString("temp");
                            String code = condition.optString("code");
                            mBinding.lnCurrentTemp.setVisibility(View.VISIBLE);
                            mBinding.tvCondition.setText(text);
                            mBinding.tvTemparature.setText(temp);
                            int resID = getResources().getIdentifier("ic_" + code, "drawable", getPackageName());
                            mBinding.ivWeatherIcon.setImageResource(resID);

                            JSONArray forecast = item.getJSONArray("forecast");
                            if (forecast.length() > 0) {
                                JSONObject jsonObject = forecast.getJSONObject(0);

                                String date = jsonObject.optString("date");
                                mBinding.tvDate.setText(Util.changeAnyDateFormat(date, "dd MMM yyyy", "EEEE MMM dd"));

                                mBinding.lnWeatherForecast.removeAllViews();
                                if (forecast.length() > 1) {
                                    for (int i = 1; i < forecast.length() && i < 6; i++) {
                                        JSONObject jobjforecast = forecast.getJSONObject(i);
                                        WeatherRowBinding mRowBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.weather_row, mBinding.lnWeatherForecast, true);

                                        String imagecode = jobjforecast.optString("code");
                                        String day = jobjforecast.optString("day");
                                        String highTemp = jobjforecast.optString("high");
                                        String lowTemp = jobjforecast.optString("low");

                                        mRowBinding.tvDayName.setText(day);
                                        mRowBinding.tvHighTemp.setText(highTemp);
                                        mRowBinding.tvLowTemp.setText(lowTemp);
                                        int rowResID = getResources().getIdentifier("ic_" + imagecode, "drawable", getPackageName());
                                        mRowBinding.ivWeatherImage.setImageResource(rowResID);


                                    }
                                }

                            }

                        }


                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                    hidepDialog();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidepDialog();
                Util.showSnakBar(DashBoardActivity.this, getResources().getString(R.string.networkerror));
                VolleyLog.d("Error: " + error.getMessage());
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //AppController.getInstance().addToRequestQueue(jsonObjReq);
        Volley.newRequestQueue(this).add(jsonObjectRequest);*/

        String url = "https://weather-ydn-yql.media.yahoo.com/forecastrss?lat=" + my_latitude + "&lon=" + my_longitude + "&format=json&u=f";
        if (!Util.isConnected(DashBoardActivity.this)) {
            Util.showSnakBar(DashBoardActivity.this, getResources().getString(R.string.internectconnectionerror));
            return;
        }
        dialog = MyCustomProgressDialog.ctor(DashBoardActivity.this);
        dialog.setCancelable(false);
        dialog.setMessage("Please wait...");
        showpDialog();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                if (response != null) {
                    try {
                        //JSONObject query = response.getJSONObject("query");
                        //String mCount = query.optString("count");
                        //if (mCount.equalsIgnoreCase("1")) {
                        //JSONObject results = query.getJSONObject("results");

                        //JSONObject channel = results.getJSONObject("channel");

                        JSONObject location = response.getJSONObject("location");
                        String city = location.optString("city");
                        String country = location.optString("country");
                        String region = location.optString("region");
                        //set location
                        mBinding.tvAddress.setText(city + "," + region + ", " + country);

                        JSONObject current_observation = response.getJSONObject("current_observation");

                        DateFormat formatter = new SimpleDateFormat("EEEE MMM dd");
                        mBinding.tvDate.setText(formatter.format(new Date()));

                        JSONObject condition = current_observation.getJSONObject("condition");

                        String text = condition.optString("text");
                        String temp = condition.optString("temperature");
                        String code = condition.optString("code");
                        mBinding.lnCurrentTemp.setVisibility(View.VISIBLE);
                        mBinding.tvCondition.setText(text);
                        mBinding.tvTemparature.setText(temp);
                        int resID = getResources().getIdentifier("ic_" + code, "drawable", getPackageName());
                        mBinding.ivWeatherIcon.setImageResource(resID);

                        JSONArray forecast = response.getJSONArray("forecasts");
                        if (forecast.length() > 0) {
                            //JSONObject jsonObject = forecast.getJSONObject(0);


                            mBinding.lnWeatherForecast.removeAllViews();
                            if (forecast.length() > 1) {
                                mBinding.lnWeatherForecast.setVisibility(View.VISIBLE);
                                for (int i = 1; i < forecast.length() && i < 6; i++) {
                                    JSONObject jobjforecast = forecast.getJSONObject(i);
                                    WeatherRowBinding mRowBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.weather_row, mBinding.lnWeatherForecast, true);

                                    String imagecode = jobjforecast.optString("code");
                                    String day = jobjforecast.optString("day");
                                    String highTemp = jobjforecast.optString("high");
                                    String lowTemp = jobjforecast.optString("low");

                                    mRowBinding.tvDayName.setText(day);
                                    mRowBinding.tvHighTemp.setText(highTemp);
                                    mRowBinding.tvLowTemp.setText(lowTemp);
                                    int rowResID = getResources().getIdentifier("ic_" + imagecode, "drawable", getPackageName());
                                    mRowBinding.ivWeatherImage.setImageResource(rowResID);


                                }
                            }

                        }


                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                    hidepDialog();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidepDialog();
                Util.showSnakBar(DashBoardActivity.this, getResources().getString(R.string.networkerror));
                VolleyLog.d("Error: " + error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                OAuthConsumer consumer = new OAuthConsumer(null, CONSUMER_KEY, CONSUMER_SECRET, null);
                consumer.setProperty(OAuth.OAUTH_SIGNATURE_METHOD, OAuth.HMAC_SHA1);
                OAuthAccessor accessor = new OAuthAccessor(consumer);
                try {
                    OAuthMessage request = accessor.newRequestMessage(OAuthMessage.GET, getUrl(), null);
                    String authorization = request.getAuthorizationHeader(null);
                    headers.put("Authorization", authorization);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new AuthFailureError(e.getMessage());
                }

                headers.put("Yahoo-App-Id", appId);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //AppController.getInstance().addToRequestQueue(jsonObjReq);
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (toast != null && toast.getView().isShown()) {
            AppExit();
            return;
        }
        toast = Toast.makeText(DashBoardActivity.this, "Touch again to exit", Toast.LENGTH_SHORT);
        toast.show();
    }

    private void AppExit() {
        this.finish();
        startActivity(new Intent(this, SplashScreen.class));
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void setupTabIcons() {

        final View view = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne = (TextView) view.findViewById(R.id.tabText);
        //tvCount1=(TextView)view.findViewById(R.id.tvCount);
        tabOne.setTextColor(getResources().getColor(R.color.colorActiveNavText));
        tabOne.setText("Service Requests");
        //tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_email, 0, 0);
        mBinding.tabLayout.getTabAt(0).setCustomView(view);

        final View view1 = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo = (TextView) view1.findViewById(R.id.tabText);
        tabTwo.setText("Pending Jobs");
        //tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_friends, 0, 0);
        mBinding.tabLayout.getTabAt(1).setCustomView(view1);


    }

    private void saveDeviceToken() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        if (refreshedToken == null)
            return;
        HashMap<String, String> params = new HashMap<>();
        params.put("device_type", "android");
        params.put("device_token", refreshedToken);
        params.put("user_type", "landscaper");
        new PostDataParser(DashBoardActivity.this, Api.sSaveDeviceToken, params, false, new PostDataParser.OnGetResponseListner() {
            @Override
            public void onGetResponse(JSONObject response) {
                String success, msg;
                if (response != null) {
                    try {
                        success = response.getString("success");
                        msg = response.getString("msg");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        getNotiStatus();
    }

    private void getNotiStatus() {

        new GetDataParser(DashBoardActivity.this, Api.sGetNotificationStatus, true, new GetDataParser.OnGetResponseListner() {
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
