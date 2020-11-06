package com.seazoned.landscaper.view.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.google.android.gms.vision.barcode.Barcode;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.seazoned.landscaper.R;
import com.seazoned.landscaper.app.AppData;
import com.seazoned.landscaper.databinding.ActivityRegistartionStep2Binding;
import com.seazoned.landscaper.databinding.AddServiceDayRowBinding;
import com.seazoned.landscaper.model.DayModel;
import com.seazoned.landscaper.other.GPSTracker;
import com.seazoned.landscaper.other.MyCustomProgressDialog;
import com.seazoned.landscaper.service.api.Api;
import com.seazoned.landscaper.service.parser.GetDataParser;
import com.seazoned.landscaper.service.preference.SharedPreferenceHelper;
import com.seazoned.landscaper.service.util.Util;
import com.seazoned.landscaper.view.adapter.LandscaperServiceListAdapter;
import com.seazoned.landscaper.view.adapter.ServiceAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistrationStep2Activity extends AppCompatActivity implements View.OnClickListener {
    private static final int LOCATION_CODE = 1;
    ActivityRegistartionStep2Binding mBinding;
    private ProgressDialog dialog;
    private ArrayList<HashMap<String, String>> serviceArrayList = null;
    private ArrayList<DayModel> dayModelArrayList = null;
    private String dayList[] = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    private LandscaperServiceListAdapter mAdapter;
    private String type = "";
    private ArrayList<HashMap<String, String>> landscaperServiceIds = null;
    private double lat = 0.0;
    private double lang = 0.0;
    /*private String mSevenDaysRS = "0";
    private String mTenDaysRS = "0";
    private String mFourteenDaysRS = "0";*/


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
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_registartion_step2);
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            Window window = getWindow(); // in Activity's onCreate() for instance
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);

            tintManager.setTintColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mBinding.rcvServiceList.setLayoutManager(layoutManager);

        mAdapter = new LandscaperServiceListAdapter(this);
        mBinding.rcvServiceList.setAdapter(mAdapter);
        mBinding.rcvServiceList.setNestedScrollingEnabled(false);

        mBinding.tvAddService.setOnClickListener(this);
        mBinding.tvFinish.setOnClickListener(this);
        addServiceDay();
        Bundle bundle = getIntent().getExtras();
        type = AppData.type;
        if (type.equalsIgnoreCase("login")) {
            String address = AppData.address;
            getLatLang(address);
        } else {
            lat = AppData.lat;
            lang = AppData.lang;
        }
        if (bundle != null) {
            String name = bundle.getString("name");
            mBinding.tvName.setText(name);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", AppData.sUserId);
        getLandscaperServiceList(params);
    }

    private void getLatLang(String address1) {
        if(address1.equalsIgnoreCase("")){
            checkLocationPermission();
        }
        else {
            Geocoder coder = new Geocoder(this);
            List<Address> address;
            Barcode.GeoPoint p1 = null;

            try {
                address = coder.getFromLocationName(address1, 5);
                if (address == null) {
                    return;
                }
                Address location = address.get(0);
                lat = location.getLatitude();
                lang = location.getLongitude();


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        /*String url="http://maps.google.com/maps/api/geocode/json?address="+address+"&sensor=false";
        new GetDataParser(RegistrationStep2Activity.this, url, true, true, new GetDataParser.OnGetResponseListner() {
            @Override
            public void onGetResponse(JSONObject response) {
                if (response!=null){
                    try{

                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });*/
    }

    private void getLandscaperServiceList(final HashMap<String, String> params) {
        if (!Util.isConnected(RegistrationStep2Activity.this)) {
            Util.showSnakBar(RegistrationStep2Activity.this, getResources().getString(R.string.internectconnectionerror));
            return;
        }
        dialog = MyCustomProgressDialog.ctor(RegistrationStep2Activity.this);
        dialog.setCancelable(false);
        dialog.setMessage("Please wait...");
        showpDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.sLandscaperServiceList, new Response.Listener<String>() {
            public String msg;
            public String success;

            @Override
            public void onResponse(String data) {

                try {
                    JSONObject response = Util.getjsonobject(data);
                    success = response.optString("success");
                    msg = response.optString("msg");
                    if (success.equalsIgnoreCase("1")) {
                        mAdapter.clearData();
                        //landscaperServiceIds = new ArrayList<>();
                        JSONObject dataObject = response.optJSONObject("data");
                        JSONArray servies = dataObject.optJSONArray("servies");
                        for (int i = 0; i < servies.length(); i++) {
                            JSONObject serviceJsonObject = servies.optJSONObject(i);
                            HashMap<String, String> hashMap = new HashMap<>();

                            //landscaperServiceIds.add(serviceJsonObject.optString("service_id"));
                            hashMap.put("serviceId", serviceJsonObject.optString("service_id"));
                            hashMap.put("addedServiceId", serviceJsonObject.optString("added_service_id"));
                            hashMap.put("serviceName", serviceJsonObject.optString("service_name"));
                            mAdapter.addRow(hashMap);
                        }
                        mAdapter.notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                hidepDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidepDialog();
                Util.showSnakBar(RegistrationStep2Activity.this, getResources().getString(R.string.networkerror));
                VolleyLog.d("Error: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(RegistrationStep2Activity.this).add(stringRequest);

    }

    @Override
    public void onClick(View view) {
        if (view == mBinding.tvAddService) {
            if (mAdapter.getItemCount() == 7) {
                Toast.makeText(this, "You have maximum number of services.", Toast.LENGTH_SHORT).show();
            } else {
                final AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationStep2Activity.this);
                builder.setCancelable(true);
                final View v = getLayoutInflater().inflate(R.layout.custom_service_dialog, null);
                TextView tvCancel = (TextView) v.findViewById(R.id.tvCancel);
                TextView tvOk = (TextView) v.findViewById(R.id.tvOk);
                final Spinner spService = (Spinner) v.findViewById(R.id.spService);

                builder.setView(v);
                builder.setCancelable(false);
                final AlertDialog mAlert = builder.create();
                mAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                mAlert.show();
                tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAlert.dismiss();
                    }
                });
                tvOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //addService();
                        //finish();
                        startActivity(new Intent(RegistrationStep2Activity.this, AddServiceActivity.class)
                                .putExtra("page", "registration")
                                .putExtra("mode", "add")
                                .putExtra("serviceId", serviceArrayList.get(spService.getSelectedItemPosition()).get("serviceId"))
                                .putExtra("serviceName", serviceArrayList.get(spService.getSelectedItemPosition()).get("serviceName"))
                        );
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        mAlert.dismiss();
                    }
                });
                getServiceList(spService);
            }
        } else if (view == mBinding.tvFinish) {
            String mProviderName = mBinding.etProvidername.getText().toString();
            /*if (mBinding.cbCheckRecurring.isChecked()) {

                mSevenDaysRS = mBinding.etSevenDays.getText().toString();
                if (TextUtils.isEmpty(mSevenDaysRS))
                    mSevenDaysRS = "0";

                mTenDaysRS = mBinding.etTenDays.getText().toString();
                if (TextUtils.isEmpty(mTenDaysRS))
                    mTenDaysRS = "0";

                mFourteenDaysRS = mBinding.etFourteenDays.getText().toString();
                if (TextUtils.isEmpty(mFourteenDaysRS))
                    mFourteenDaysRS = "0";
            } else {
                mSevenDaysRS = "0";
                mTenDaysRS = "0";
                mFourteenDaysRS = "0";
            }*/


            String mDescription = mBinding.etDescription.getText().toString();

            if (mAdapter.getItemCount() == 0) {
                Toast.makeText(this, "Please add at least one service.", Toast.LENGTH_SHORT).show();
                return;
            }


            if (TextUtils.isEmpty(mProviderName)) {
                Toast.makeText(RegistrationStep2Activity.this, "Input Business name.", Toast.LENGTH_SHORT).show();
                return;
            }
            /*if (TextUtils.isEmpty(mSevenDaysRS)) {
                Toast.makeText(RegistrationStep2Activity.this, "Input 7 days discount price.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(mTenDaysRS)) {
                Toast.makeText(RegistrationStep2Activity.this, "Input 10 days discount price.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(mFourteenDaysRS)) {
                Toast.makeText(RegistrationStep2Activity.this, "Input 14 days discount price.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(mJustOnceRS)) {
                Toast.makeText(RegistrationStep2Activity.this, "Input just once discount price.", Toast.LENGTH_SHORT).show();
                return;
            }*/

            try {

                /*recurring services*/
                /*JSONArray jsonArrayRecService = new JSONArray();
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("rs", mSevenDaysRS);

                JSONObject jsonObject2 = new JSONObject();
                jsonObject2.put("rs", mTenDaysRS);

                JSONObject jsonObject3 = new JSONObject();
                jsonObject3.put("rs", mFourteenDaysRS);

                jsonArrayRecService.put(jsonObject1);
                jsonArrayRecService.put(jsonObject2);
                jsonArrayRecService.put(jsonObject3);*/

                JSONArray jsonDayArray = new JSONArray();
                JSONArray jsonStartTimeArray = new JSONArray();
                JSONArray jsonEndTimeArray = new JSONArray();
                for (int i = 0; i < dayModelArrayList.size(); i++) {
                    JSONObject jsonDayObject = new JSONObject();
                    JSONObject jsonStartObject = new JSONObject();
                    JSONObject jsonEndObject = new JSONObject();
                    if (dayModelArrayList.get(i).getCheckBox().isChecked()) {
                        jsonDayObject.put("day", dayModelArrayList.get(i).getCheckBox().getText().toString());
                        jsonDayArray.put(jsonDayObject);

                        String startTime = dayModelArrayList.get(i).getTvHideStartTime().getText().toString();
                        jsonStartObject.put("s", startTime);
                        jsonStartTimeArray.put(jsonStartObject);

                        String endTime = dayModelArrayList.get(i).getTvHideEndTime().getText().toString();
                        jsonEndObject.put("e", endTime);
                        jsonEndTimeArray.put(jsonEndObject);
                    }
                }
                if (jsonDayArray.length() == 0) {
                    Toast.makeText(this, "Input service day", Toast.LENGTH_SHORT).show();
                    return;
                }
                String distance=mBinding.etDistance.getText().toString();
                if (TextUtils.isEmpty(distance)){
                    Toast.makeText(this, "Input distance", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Double.parseDouble(distance)<=0||Double.parseDouble(distance)>50){
                    Toast.makeText(this, "Input distance between 1-50 miles.", Toast.LENGTH_SHORT).show();
                    return;
                }
                //String recService = jsonArrayRecService.toString();

                String days = jsonDayArray.toString();
                String start = jsonStartTimeArray.toString();
                String end = jsonEndTimeArray.toString();
                //call w/s
                HashMap<String, String> params = new HashMap<>();
                params.put("user_id", AppData.sUserId);
                params.put("provider_name", mProviderName);
                params.put("latitude", "" + lat);
                params.put("longitude", "" + lang);
                params.put("description", mDescription);
                params.put("days", days);
                params.put("start", start);
                params.put("end", end);
                params.put("distance_provided", distance);
                /*params.put("recurring_services", recService);*/
                addProviderDetails(params);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addProviderDetails(final HashMap<String, String> params) {
        if (!Util.isConnected(RegistrationStep2Activity.this)) {
            Util.showSnakBar(RegistrationStep2Activity.this, getResources().getString(R.string.internectconnectionerror));
            return;
        }
        dialog = MyCustomProgressDialog.ctor(RegistrationStep2Activity.this);
        dialog.setCancelable(false);
        dialog.setMessage("Please wait...");
        showpDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Api.sAddProviderDetails, new Response.Listener<String>() {
            public String msg;
            public String success;

            @Override
            public void onResponse(String data) {
                try {
                    JSONObject response = Util.getjsonobject(data);
                    success = response.optString("success");
                    msg = response.optString("msg");
                    if (success.equalsIgnoreCase("1")) {
                        if (type.equalsIgnoreCase("login")) {
                            SharedPreferenceHelper helper = SharedPreferenceHelper.getInstance(getApplicationContext());
                            helper.saveUserInfo(AppData.user);
                            finish();
                            startActivity(new Intent(RegistrationStep2Activity.this, DashBoardActivity.class));
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        } else {
                            finish();
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                hidepDialog();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidepDialog();
                Util.showSnakBar(RegistrationStep2Activity.this, getResources().getString(R.string.networkerror));
                VolleyLog.d("Error: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(RegistrationStep2Activity.this).add(stringRequest);
    }

    private void getServiceList(final Spinner spService) {
        if (!Util.isConnected(RegistrationStep2Activity.this)) {
            Util.showSnakBar(RegistrationStep2Activity.this, getResources().getString(R.string.internectconnectionerror));
            return;
        }
        dialog = MyCustomProgressDialog.ctor(RegistrationStep2Activity.this);
        dialog.setCancelable(false);
        dialog.setMessage("Please wait...");
        showpDialog();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Api.sServiceList, null, new Response.Listener<JSONObject>() {
            public String mSuccess;

            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        mSuccess = response.optString("success");
                        if (mSuccess.equalsIgnoreCase("1")) {
                            landscaperServiceIds = mAdapter.getAlldata();
                            serviceArrayList = new ArrayList<>();
                            JSONArray data = response.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject jobjdata = data.getJSONObject(i);
                                HashMap<String, String> hashMap = new HashMap<>();
                                if (landscaperServiceIds != null && landscaperServiceIds.size() > 0) {
                                    int j = 0;

                                    for (j = 0; j < landscaperServiceIds.size(); j++) {
                                        if (jobjdata.optString("id").equalsIgnoreCase(landscaperServiceIds.get(j).get("serviceId"))) {
                                            break;
                                        }
                                    }
                                    if (j == landscaperServiceIds.size()) {
                                        hashMap.put("serviceId", jobjdata.optString("id"));
                                        hashMap.put("serviceName", jobjdata.optString("service_name"));
                                        serviceArrayList.add(hashMap);
                                    }

                                } else {
                                    hashMap.put("serviceId", jobjdata.optString("id"));
                                    hashMap.put("serviceName", jobjdata.optString("service_name"));
                                    serviceArrayList.add(hashMap);
                                }

                            }
                            //set adapter to spinner
                            spService.setAdapter(new ServiceAdapter(RegistrationStep2Activity.this, R.layout.spinner_item, serviceArrayList));

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
                Util.showSnakBar(RegistrationStep2Activity.this, getResources().getString(R.string.networkerror));
                VolleyLog.d("Error: " + error.getMessage());
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(RegistrationStep2Activity.this).add(jsonObjectRequest);
    }

    private void addServiceDay() {
        mBinding.lnDayList.removeAllViews();
        dayModelArrayList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            final AddServiceDayRowBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.add_service_day_row, mBinding.lnDayList, true);
            binding.cbDay.setText(dayList[i]);

            DayModel model = new DayModel(binding.cbDay, binding.tvStartTime, binding.tvEndTime,binding.tvHideStartTime,binding.tvHideEndTime);
            dayModelArrayList.add(model);
            binding.cbDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        binding.lnStartTime.setVisibility(View.VISIBLE);
                        binding.lnEndTime.setVisibility(View.VISIBLE);
                    } else {
                        binding.lnStartTime.setVisibility(View.GONE);
                        binding.lnEndTime.setVisibility(View.GONE);
                    }
                }
            });
            binding.lnStartTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showTimePicker(binding.tvStartTime,binding.tvHideStartTime);
                }
            });
            binding.lnEndTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showTimePicker(binding.tvEndTime,binding.tvHideEndTime);
                }
            });

        }
    }

    private void showTimePicker(final TextView tvTime, final TextView tvHideTime) {
        int h=Calendar.getInstance().get(Calendar.HOUR);
        int m=Calendar.getInstance().get(Calendar.MINUTE);
        String time=tvHideTime.getText().toString().trim();
        if (!time.equalsIgnoreCase("")){
            if (time.contains(":")){
                try {
                    h = Integer.parseInt(time.split(":")[0]);
                    m = Integer.parseInt(time.split(":")[1]);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationStep2Activity.this);
        View view = getLayoutInflater().inflate(R.layout.custom_timepicker, null);
        builder.setView(view);
        builder.setCancelable(false);

        final AlertDialog mAlert = builder.create();
        mAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mAlert.show();
        TextView tvDone=view.findViewById(R.id.tvDone);
        TextView tvCancel=view.findViewById(R.id.tvCancel);
        final TimePicker timePicker=view.findViewById(R.id.timePicker);
        timePicker.setCurrentHour(h);
        timePicker.setCurrentMinute(m);
        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour, minute;

                if (Build.VERSION.SDK_INT >= 23 ){
                    hour = timePicker.getHour();
                    minute = timePicker.getMinute();
                }
                else{
                    hour = timePicker.getCurrentHour();
                    minute = timePicker.getCurrentMinute();
                }
                tvHideTime.setText(hour + ":" + minute + ":00");
                String showTime = "";
                String showMinute = "";
                if (minute < 10) {
                    showMinute = "0" + minute;
                } else {
                    showMinute = "" + minute;
                }
                //AM
                if (hour == 0) {
                    showTime = "12:" + showMinute + " am";
                } else if (hour != 0 && hour < 12) {
                    if (hour < 10)
                        showTime = "0" + hour + ":" + showMinute + " am";
                    else
                        showTime = hour + ":" + showMinute + " am";
                }
                //PM
                else if (hour==12){
                    showTime = hour + ":" + showMinute + " pm";
                }
                else if (hour>12){
                    hour=hour-12;
                    if (hour < 10)
                        showTime = "0" + hour + ":" + showMinute + " pm";
                    else
                        showTime = hour + ":" + showMinute + " pm";
                }
                tvTime.setText(showTime);
                mAlert.dismiss();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlert.dismiss();
            }
        });
        /*new TimePickerDialog(RegistrationStep2Activity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                tvTime.setText(Util.changeAnyDateFormat((i + ":" + i1), "hh:mm", "hh:mm a"));
                tvHideTime.setText(i + ":" + i1+":00");
            }
        }, h, m, false).show();*/
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (type.equalsIgnoreCase("login")) {
            finish();
            startActivity(new Intent(RegistrationStep2Activity.this, LoginActivity.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
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
    public void my_location() {
        try {
            GPSTracker gps = new GPSTracker(RegistrationStep2Activity.this);
            if (gps.canGetLocation) {
                lat = gps.getLongitude();
                lang = gps.getLatitude();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}
