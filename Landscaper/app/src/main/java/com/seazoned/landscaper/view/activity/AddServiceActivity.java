package com.seazoned.landscaper.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.seazoned.landscaper.R;
import com.seazoned.landscaper.app.AppData;
import com.seazoned.landscaper.databinding.ActivityAddServiceBinding;
import com.seazoned.landscaper.databinding.ActivityAerationBinding;
import com.seazoned.landscaper.databinding.ActivityLawnTreatmentBinding;
import com.seazoned.landscaper.databinding.ActivityLeafRemovalBinding;
import com.seazoned.landscaper.databinding.ActivityMowingEdgingBinding;
import com.seazoned.landscaper.databinding.ActivityPoolCleaningBinding;
import com.seazoned.landscaper.databinding.ActivitySnowRemovalBinding;
import com.seazoned.landscaper.databinding.ActivitySprinklerBinding;
import com.seazoned.landscaper.databinding.LeafRemovalRowBinding;
import com.seazoned.landscaper.databinding.PoolCleaningRowBinding;
import com.seazoned.landscaper.databinding.SnowRemovalRowBinding;
import com.seazoned.landscaper.other.MyCustomProgressDialog;
import com.seazoned.landscaper.service.api.Api;
import com.seazoned.landscaper.service.parser.PostDataParser;
import com.seazoned.landscaper.service.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddServiceActivity extends AppCompatActivity implements View.OnClickListener {

    private String serviceId;
    private String serviceName;
    private ActivityAddServiceBinding mBinding;
    private ProgressDialog dialog;
    private String page = "";
    private String mode = "";
    private String mSevenDaysRS = "0";
    private String mTenDaysRS = "0";
    private String mFourteenDaysRS = "0";

    private int priceFlagSprinkler = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_service);
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            Window window = getWindow(); // in Activity's onCreate() for instance
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);

            tintManager.setTintColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        serviceId = getIntent().getExtras().getString("serviceId");
        serviceName = getIntent().getExtras().getString("serviceName");
        page = getIntent().getExtras().getString("page");
        mode = getIntent().getExtras().getString("mode");

        mBinding.tvTitle.setText(serviceName);

        mBinding.lnContainer.removeAllViews();

        mBinding.lnBack.setOnClickListener(this);

        if (serviceId.equalsIgnoreCase("1")) {
            setMowingAndEdging();
        } else if (serviceId.equalsIgnoreCase("2")) {
            setLeafRemoval();
        } else if (serviceId.equalsIgnoreCase("3")) {
            setLawnTreatment();
        } else if (serviceId.equalsIgnoreCase("4")) {
            setAeration();
        } else if (serviceId.equalsIgnoreCase("5")) {
            setSprinkler();
        } else if (serviceId.equalsIgnoreCase("6")) {
            setPoolCleaning();
        } else if (serviceId.equalsIgnoreCase("7")) {
            setSnowRemoval();
        }
    }

    private void setSnowRemoval() {
        final ActivitySnowRemovalBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_snow_removal, mBinding.lnContainer, true);
        binding.cbCheckRecurring.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                /*if (b) {
                    binding.lnRecurring.setVisibility(View.VISIBLE);
                } else
                    binding.lnRecurring.setVisibility(View.GONE);*/
            }
        });
        if (mode.equalsIgnoreCase("edit")) {
            binding.tvSave.setText("Update");
            HashMap<String, String> params = new HashMap<>();
            params.put("added_service_id", getIntent().getExtras().getString("addedServiceId"));
            params.put("service_id", serviceId);

            new PostDataParser(AddServiceActivity.this, Api.sViewServiceDetails, params, true, true, new PostDataParser.OnGetResponseListner() {
                public String success;

                @Override
                public void onGetResponse(JSONObject response) {

                    if (response != null) {
                        try {
                            success = response.optString("success");
                            if (success.equalsIgnoreCase("1")) {
                                JSONObject data = response.optJSONObject("data");
                                JSONArray snowCar = data.optJSONArray("snow_car");

                                String firstPrice = null;
                                if (snowCar.length() > 0) {
                                    JSONObject snowCarObject = snowCar.optJSONObject(0);
                                    firstPrice = snowCarObject.optString("service_field_price");
                                    binding.etFirstPrice.setText(firstPrice);
                                }
                                if (snowCar.length() > 1) {
                                    JSONObject snowCarObject1 = snowCar.optJSONObject(1);
                                    String secondPrice = snowCarObject1.optString("service_field_price");
                                    binding.etNextPrice.setText("" + (Double.parseDouble(secondPrice) - Double.parseDouble(firstPrice)));

                                    binding.etUpperLimit.setText("" + (snowCar.length() * 2));
                                }

                                JSONArray snowDrivewayType = data.optJSONArray("snow_driveway_type");
                                if (snowDrivewayType.length() > 0) {
                                    JSONObject jsonObject = snowDrivewayType.optJSONObject(0);
                                    binding.etStraight.setText(jsonObject.optString("service_field_price"));
                                }
                                if (snowDrivewayType.length() > 1) {
                                    JSONObject jsonObject = snowDrivewayType.optJSONObject(1);
                                    binding.etCircular.setText(jsonObject.optString("service_field_price"));
                                }
                                if (snowDrivewayType.length() > 2) {
                                    JSONObject jsonObject = snowDrivewayType.optJSONObject(2);
                                    binding.etIncline.setText(jsonObject.optString("service_field_price"));
                                }
                                JSONArray snowServiceType = data.optJSONArray("snow_service_type");
                                if (snowServiceType.length() > 0) {
                                    JSONObject jsonObject = snowServiceType.optJSONObject(0);
                                    binding.etFront.setText(jsonObject.optString("service_field_price"));
                                }
                                if (snowServiceType.length() > 1) {
                                    JSONObject jsonObject = snowServiceType.optJSONObject(1);
                                    binding.etStairs.setText(jsonObject.optString("service_field_price"));
                                }
                                if (snowServiceType.length() > 2) {
                                    JSONObject jsonObject = snowServiceType.optJSONObject(2);
                                    binding.etSide.setText(jsonObject.optString("service_field_price"));
                                }
                                //show recurring service
                                boolean recurringFlag = false;
                                JSONArray servicePrices = data.optJSONArray("service_prices");
                                for (int i = 0; i < servicePrices.length(); i++) {
                                    JSONObject jsonObject = servicePrices.optJSONObject(i);
                                    String price = jsonObject.optString("discount_price");
                                    if (!jsonObject.optString("service_frequency").equalsIgnoreCase("just once"))
                                        if (!price.equals("")
                                                && !price.equals("0")
                                                && !price.equals("0.0")
                                                && !price.equals("0.00")
                                                ) {
                                            recurringFlag = true;
                                        }
                                    switch (i) {
                                        case 0:
                                            binding.etSevenDays.setText(jsonObject.optString("discount_price"));
                                            break;
                                        case 1:
                                            binding.etTenDays.setText(jsonObject.optString("discount_price"));
                                            break;
                                        case 2:
                                            binding.etFourteenDays.setText(jsonObject.optString("discount_price"));
                                            break;

                                    }
                                }
                                if (recurringFlag) {
                                    binding.cbCheckRecurring.setChecked(true);
                                } else {
                                    binding.cbCheckRecurring.setChecked(false);
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
        binding.tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*recurring services*/
                /*if (binding.cbCheckRecurring.isChecked()) {

                    mSevenDaysRS = binding.etSevenDays.getText().toString();
                    if (TextUtils.isEmpty(mSevenDaysRS))
                        mSevenDaysRS = "0";

                    mTenDaysRS = binding.etTenDays.getText().toString();
                    if (TextUtils.isEmpty(mTenDaysRS))
                        mTenDaysRS = "0";

                    mFourteenDaysRS = binding.etFourteenDays.getText().toString();
                    if (TextUtils.isEmpty(mFourteenDaysRS))
                        mFourteenDaysRS = "0";
                } else {*/
                mSevenDaysRS = "0";
                mTenDaysRS = "0";
                mFourteenDaysRS = "0";
                //}
                JSONArray jsonArrayRecService = new JSONArray();

                try {

                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("rs", mSevenDaysRS);

                    JSONObject jsonObject2 = new JSONObject();
                    jsonObject2.put("rs", mTenDaysRS);

                    JSONObject jsonObject3 = new JSONObject();
                    jsonObject3.put("rs", mFourteenDaysRS);

                    jsonArrayRecService.put(jsonObject1);
                    jsonArrayRecService.put(jsonObject2);
                    jsonArrayRecService.put(jsonObject3);
                } catch (Exception e) {
                    e.printStackTrace();
                }

/*end recurring service*/
                String firstPrice = binding.etFirstPrice.getText().toString();
                String nextPrice = binding.etNextPrice.getText().toString();
                String straight = binding.etStraight.getText().toString();
                String circular = binding.etCircular.getText().toString();
                String incline = binding.etIncline.getText().toString();
                String front = binding.etFront.getText().toString();
                String stairs = binding.etStairs.getText().toString();
                String side = binding.etSide.getText().toString();

                String upperLimit = binding.etUpperLimit.getText().toString();
                if (TextUtils.isEmpty(firstPrice) ||
                        TextUtils.isEmpty(nextPrice) ||
                        TextUtils.isEmpty(straight) ||
                        TextUtils.isEmpty(circular) ||
                        TextUtils.isEmpty(incline) ||
                        TextUtils.isEmpty(front) ||
                        TextUtils.isEmpty(stairs) ||
                        TextUtils.isEmpty(side) ||
                        TextUtils.isEmpty(upperLimit)
                        ) {
                    Toast.makeText(AddServiceActivity.this, "Please fill up all fields.", Toast.LENGTH_SHORT).show();
                    return;
                }
                HashMap<String, String> params = new HashMap<>();
                params.put("service_id", serviceId);
                params.put("car_limit", upperLimit);
                params.put("first_car", firstPrice);
                params.put("next_car", nextPrice);
                params.put("straight", straight);
                params.put("circular", circular);
                params.put("incline", incline);
                params.put("front_door", front);
                params.put("stairs", stairs);
                params.put("side_door", side);
                params.put("recurring_services", jsonArrayRecService.toString());


                if (mode.equalsIgnoreCase("add")) {
                    params.put("user_id", AppData.sUserId);
                    saveService(Api.sAddService, params, binding.tvSave);
                } else if (mode.equalsIgnoreCase("edit")) {
                    params.put("added_service_id", getIntent().getExtras().getString("addedServiceId"));
                    saveService(Api.sEditService, params, binding.tvSave);
                }
            }
        });

        binding.tvPricingPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAndCloseTextview(binding.tvPricingPrev, binding.lnPrice);
                binding.lnPrice.removeAllViews();
                SnowRemovalRowBinding snowBindingFirstRow = DataBindingUtil.inflate(getLayoutInflater(), R.layout.snow_removal_row, binding.lnPrice, true);
                snowBindingFirstRow.tvTitle1.setText("Car fit Driveway");
                snowBindingFirstRow.tvTitle1.setTextColor(getResources().getColor(R.color.colorText));
                snowBindingFirstRow.tvTitle2.setText("Driveway type");
                snowBindingFirstRow.tvTitle2.setTextColor(getResources().getColor(R.color.colorText));
                snowBindingFirstRow.tvTitle3.setText("Service type");
                snowBindingFirstRow.tvTitle3.setTextColor(getResources().getColor(R.color.colorText));
                snowBindingFirstRow.tvTitle4.setText("Price");
                snowBindingFirstRow.tvTitle4.setTextColor(getResources().getColor(R.color.colorText));
                try {
                    double firstPrice = Double.parseDouble(binding.etFirstPrice.getText().toString());
                    double nextPrice = Double.parseDouble(binding.etNextPrice.getText().toString());
                    double straight = Double.parseDouble(binding.etStraight.getText().toString());
                    double circular = Double.parseDouble(binding.etCircular.getText().toString());
                    double incline = Double.parseDouble(binding.etIncline.getText().toString());
                    double front = Double.parseDouble(binding.etFront.getText().toString());
                    double stairs = Double.parseDouble(binding.etStairs.getText().toString());
                    double side = Double.parseDouble(binding.etSide.getText().toString());

                    double upperLimit = Double.parseDouble(binding.etUpperLimit.getText().toString());
                    double carPrice = 0;
                    for (double i = 0; i < upperLimit; i = i + 2) {
                        if (i < 0.25)
                            carPrice = firstPrice;
                        else
                            carPrice = carPrice + nextPrice;
                        for (int j = 1; j <= 3; j++) {
                            for (int k = 1; k <= 3; k++) {
                                SnowRemovalRowBinding snowBindingRow = DataBindingUtil.inflate(getLayoutInflater(), R.layout.snow_removal_row, binding.lnPrice, true);

                                snowBindingRow.tvTitle1.setText(i + " - " + (i + 2));

                                double driveWayPrice = 0;
                                if (j == 1) {
                                    snowBindingRow.tvTitle2.setText("Straight");
                                    driveWayPrice = straight;
                                } else if (j == 2) {
                                    snowBindingRow.tvTitle2.setText("Circular");
                                    driveWayPrice = circular;
                                } else if (j == 3) {
                                    snowBindingRow.tvTitle2.setText("Incline");
                                    driveWayPrice = incline;
                                }

                                if (k == 1) {
                                    snowBindingRow.tvTitle3.setText("Front Door Walk Way");
                                    snowBindingRow.tvTitle4.setText("$ " + Util.getDecimalTwoPoint(carPrice + driveWayPrice + front));
                                } else if (k == 2) {
                                    snowBindingRow.tvTitle3.setText("Stairs and Front Landing");
                                    snowBindingRow.tvTitle4.setText("$ " + Util.getDecimalTwoPoint(carPrice + driveWayPrice + stairs));
                                } else if (k == 3) {
                                    snowBindingRow.tvTitle3.setText("Side Door Walk Way");
                                    snowBindingRow.tvTitle4.setText("$ " + Util.getDecimalTwoPoint(carPrice + driveWayPrice + side));
                                }


                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setSprinkler() {


        final ActivitySprinklerBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_sprinkler, mBinding.lnContainer, true);
        binding.cbCheckRecurring.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
               /* if (b) {
                    binding.lnRecurring.setVisibility(View.VISIBLE);
                } else
                    binding.lnRecurring.setVisibility(View.GONE);*/
            }
        });

        binding.rgSprinkler.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (binding.rbAcerage.isChecked()) {
                    binding.llAcerage.setVisibility(View.VISIBLE);
                    binding.llZones.setVisibility(View.GONE);
                    priceFlagSprinkler = 0;
                } else {
                    binding.llAcerage.setVisibility(View.GONE);
                    binding.llZones.setVisibility(View.VISIBLE);
                    priceFlagSprinkler = 1;
                }
            }
        });
        if (mode.equalsIgnoreCase("edit")) {
            binding.tvSave.setText("Update");
            HashMap<String, String> params = new HashMap<>();
            params.put("added_service_id", getIntent().getExtras().getString("addedServiceId"));
            params.put("service_id", serviceId);

            new PostDataParser(AddServiceActivity.this, Api.sViewServiceDetails, params, true, true, new PostDataParser.OnGetResponseListner() {
                public String success;

                @Override
                public void onGetResponse(JSONObject response) {

                    if (response != null) {
                        try {
                            success = response.optString("success");
                            if (success.equalsIgnoreCase("1")) {

                                JSONObject data = response.optJSONObject("data");
                                JSONArray sprinklerAcre = data.optJSONArray("sprinkler_acre");
                                JSONArray sprinklerZone = data.optJSONArray("sprinkler_zone");

                                if (sprinklerAcre.length() > 0) {
                                    binding.rbAcerage.setChecked(true);

                                    String firstPrice = null;
                                    if (sprinklerAcre.length() > 0) {
                                        JSONObject sprinklerAcreObject = sprinklerAcre.optJSONObject(0);
                                        firstPrice = sprinklerAcreObject.optString("service_field_price");
                                        binding.etFirstPrice.setText(firstPrice);
                                    }

                                    if (sprinklerAcre.length() > 1) {
                                        JSONObject sprinklerAcreObject1 = sprinklerAcre.optJSONObject(1);
                                        String nextPrice = sprinklerAcreObject1.optString("service_field_price");
                                        binding.etNextPrice.setText(String.valueOf(Double.parseDouble(nextPrice) - Double.parseDouble(firstPrice)));
                                    }

                                    String upperLimit = String.valueOf(sprinklerAcre.length() / 4);
                                    binding.etUpperLimit.setText(upperLimit);
                                }
                                if (sprinklerZone.length() > 0) {
                                    binding.rbZones.setChecked(true);

                                    String firstZonePrice = null;
                                    if (sprinklerZone.length() > 0) {
                                        JSONObject sprinklerZoneObject = sprinklerZone.optJSONObject(0);
                                        firstZonePrice = sprinklerZoneObject.optString("service_field_price");
                                        binding.etFirstZone.setText(firstZonePrice);
                                    }
                                    if (sprinklerZone.length() > 1) {
                                        JSONObject sprinklerZoneObject1 = sprinklerZone.optJSONObject(1);
                                        String secondZonePrice = sprinklerZoneObject1.optString("service_field_price");
                                        binding.etNextZone.setText("" + (Double.parseDouble(secondZonePrice) - Double.parseDouble(firstZonePrice)));

                                        binding.etZoneLimit.setText("" + (sprinklerZone.length() * 3));
                                    }
                                }

                                //show recurring service
                                boolean recurringFlag = false;
                                JSONArray servicePrices = data.optJSONArray("service_prices");
                                for (int i = 0; i < servicePrices.length(); i++) {
                                    JSONObject jsonObject = servicePrices.optJSONObject(i);
                                    String price = jsonObject.optString("discount_price");
                                    if (!jsonObject.optString("service_frequency").equalsIgnoreCase("just once"))
                                        if (!price.equals("")
                                                && !price.equals("0")
                                                && !price.equals("0.0")
                                                && !price.equals("0.00")
                                                ) {
                                            recurringFlag = true;
                                        }
                                    switch (i) {
                                        case 0:
                                            binding.etSevenDays.setText(jsonObject.optString("discount_price"));
                                            break;
                                        case 1:
                                            binding.etTenDays.setText(jsonObject.optString("discount_price"));
                                            break;
                                        case 2:
                                            binding.etFourteenDays.setText(jsonObject.optString("discount_price"));
                                            break;

                                    }
                                }
                                if (recurringFlag) {
                                    binding.cbCheckRecurring.setChecked(true);
                                } else {
                                    binding.cbCheckRecurring.setChecked(false);
                                }


                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
        binding.tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*recurring services*/
                /*if (binding.cbCheckRecurring.isChecked()) {

                    mSevenDaysRS = binding.etSevenDays.getText().toString();
                    if (TextUtils.isEmpty(mSevenDaysRS))
                        mSevenDaysRS = "0";

                    mTenDaysRS = binding.etTenDays.getText().toString();
                    if (TextUtils.isEmpty(mTenDaysRS))
                        mTenDaysRS = "0";

                    mFourteenDaysRS = binding.etFourteenDays.getText().toString();
                    if (TextUtils.isEmpty(mFourteenDaysRS))
                        mFourteenDaysRS = "0";
                } else {*/
                mSevenDaysRS = "0";
                mTenDaysRS = "0";
                mFourteenDaysRS = "0";
                //}
                JSONArray jsonArrayRecService = new JSONArray();

                try {

                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("rs", mSevenDaysRS);

                    JSONObject jsonObject2 = new JSONObject();
                    jsonObject2.put("rs", mTenDaysRS);

                    JSONObject jsonObject3 = new JSONObject();
                    jsonObject3.put("rs", mFourteenDaysRS);

                    jsonArrayRecService.put(jsonObject1);
                    jsonArrayRecService.put(jsonObject2);
                    jsonArrayRecService.put(jsonObject3);
                } catch (Exception e) {
                    e.printStackTrace();
                }

/*end recurring service*/
                String firstPrice = binding.etFirstPrice.getText().toString();
                String nextPrice = binding.etNextPrice.getText().toString();
                String upperLimit = binding.etUpperLimit.getText().toString();

                String firstZone = binding.etFirstZone.getText().toString();
                String nextZone = binding.etNextZone.getText().toString();
                String zoneLimit = binding.etZoneLimit.getText().toString();

                HashMap<String, String> params = new HashMap<>();
                params.put("service_id", serviceId);

                if (binding.rbAcerage.isChecked()) {

                    if (TextUtils.isEmpty(firstPrice) ||
                            TextUtils.isEmpty(nextPrice) ||
                            TextUtils.isEmpty(upperLimit)
                            ) {
                        Toast.makeText(AddServiceActivity.this, "Please fill up all  fields.", Toast.LENGTH_SHORT).show();
                        return;

                    }
                    params.put("winter_first_acre", firstPrice);
                    params.put("winter_next_acre", nextPrice);
                    params.put("winter_acre_limit", upperLimit);

                    params.put("winter_first_zone", "");
                    params.put("winter_next_zone", "");
                    params.put("winter_zone_limit", "");


                } else if (binding.rbZones.isChecked()) {
                    if (TextUtils.isEmpty(firstZone) ||
                            TextUtils.isEmpty(nextZone) ||
                            TextUtils.isEmpty(zoneLimit)
                            ) {

                        Toast.makeText(AddServiceActivity.this, "Please fill up all fields.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    params.put("winter_first_acre", "");
                    params.put("winter_next_acre", "");
                    params.put("winter_acre_limit", "");


                    params.put("winter_first_zone", firstZone);
                    params.put("winter_next_zone", nextZone);
                    params.put("winter_zone_limit", zoneLimit);

                }


                params.put("recurring_services", jsonArrayRecService.toString());


                if (mode.equalsIgnoreCase("add")) {
                    params.put("user_id", AppData.sUserId);
                    saveService(Api.sAddService, params, binding.tvSave);
                } else if (mode.equalsIgnoreCase("edit")) {
                    params.put("added_service_id", getIntent().getExtras().getString("addedServiceId"));
                    saveService(Api.sEditService, params, binding.tvSave);
                }
            }
        });
        binding.tvPricingPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*showAndCloseTextview(binding.tvPricingPrev, binding.lnPrice);
                binding.lnPrice.removeAllViews();
                LeafRemovalRowBinding leafBindingFirstRow = DataBindingUtil.inflate(getLayoutInflater(), R.layout.leaf_removal_row, binding.lnPrice, true);
                leafBindingFirstRow.tvTitle1.setText("Acreage");
                leafBindingFirstRow.tvTitle1.setTextColor(getResources().getColor(R.color.colorText));
                leafBindingFirstRow.tvTitle2.setText("Zone");
                leafBindingFirstRow.tvTitle2.setTextColor(getResources().getColor(R.color.colorText));
                leafBindingFirstRow.tvTitle3.setText("Price");
                leafBindingFirstRow.tvTitle3.setTextColor(getResources().getColor(R.color.colorText));
                try {
                    double firstPrice = Double.parseDouble(binding.etFirstPrice.getText().toString());
                    double nextPrice = Double.parseDouble(binding.etNextPrice.getText().toString());
                    double upperLimit = Double.parseDouble(binding.etUpperLimit.getText().toString());

                    double firstZone = Double.parseDouble(binding.etFirstZone.getText().toString());
                    double nextZone = Double.parseDouble(binding.etNextZone.getText().toString());
                    double zoneLimit = Double.parseDouble(binding.etZoneLimit.getText().toString());
                    double acreagePrice = 0;
                    for (double i = 0; i < upperLimit; i = i + 0.25) {
                        if (i < 0.25)
                            acreagePrice = firstPrice;
                        else
                            acreagePrice = acreagePrice + nextPrice;
                        double zonesPrice = 0;
                        for (int j = 0; j < zoneLimit; j = j + 3) {
                            LeafRemovalRowBinding leafBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.leaf_removal_row, binding.lnPrice, true);
                            leafBinding.tvTitle1.setText(i + " - " + (i + 0.25) + " acre");
                            leafBinding.tvTitle2.setText(j + " - " + (j + 3) + " Zones");
                            if (j < 3)
                                zonesPrice = firstZone;
                            else
                                zonesPrice = zonesPrice + nextZone;
                            leafBinding.tvTitle3.setText("$ " + (acreagePrice + zonesPrice));

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }*/


                showAndCloseTextview(binding.tvPricingPrev, binding.lnPrice);
                binding.lnPrice.removeAllViews();
                LeafRemovalRowBinding leafBindingFirstRow = DataBindingUtil.inflate(getLayoutInflater(), R.layout.leaf_removal_row, binding.lnPrice, true);

                if (priceFlagSprinkler == 0) {

                    //for Acreage

                    leafBindingFirstRow.tvTitle1.setText("Acreage");
                    leafBindingFirstRow.tvTitle1.setTextColor(getResources().getColor(R.color.colorText));
                    leafBindingFirstRow.tvTitle2.setVisibility(View.GONE);
                    leafBindingFirstRow.tvTitle3.setText("Price");
                    leafBindingFirstRow.tvTitle3.setTextColor(getResources().getColor(R.color.colorText));
                    try {
                        double firstPrice = Double.parseDouble(binding.etFirstPrice.getText().toString());
                        double nextPrice = Double.parseDouble(binding.etNextPrice.getText().toString());
                        double upperLimit = Double.parseDouble(binding.etUpperLimit.getText().toString());
                        double acreagePrice = 0;
                        for (double i = 0; i < upperLimit; i = i + 0.25) {
                            if (i < 0.25)
                                acreagePrice = firstPrice;
                            else
                                acreagePrice = acreagePrice + nextPrice;


                            LeafRemovalRowBinding leafBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.leaf_removal_row, binding.lnPrice, true);
                            leafBinding.tvTitle1.setText(i + " - " + (i + 0.25) + " acre");

                            leafBinding.tvTitle2.setVisibility(View.GONE);
                            leafBinding.tvTitle3.setText("$ " + Util.getDecimalTwoPoint(acreagePrice));


                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                    //for zones

                    leafBindingFirstRow.tvTitle1.setText("Zones");
                    leafBindingFirstRow.tvTitle1.setTextColor(getResources().getColor(R.color.colorText));
                    leafBindingFirstRow.tvTitle2.setVisibility(View.GONE);
                    leafBindingFirstRow.tvTitle3.setText("Price");
                    leafBindingFirstRow.tvTitle3.setTextColor(getResources().getColor(R.color.colorText));

                    double firstZone = Double.parseDouble(binding.etFirstZone.getText().toString());
                    double nextZone = Double.parseDouble(binding.etNextZone.getText().toString());
                    double zoneLimit = Double.parseDouble(binding.etZoneLimit.getText().toString());
                    double zonesPrice = 0;
                    for (int j = 0; j < zoneLimit; j = j + 3) {
                        LeafRemovalRowBinding leafBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.leaf_removal_row, binding.lnPrice, true);
                        leafBinding.tvTitle1.setText(j + " - " + (j + 3) + " Zones");
                        if (j < 3)
                            zonesPrice = firstZone;
                        else
                            zonesPrice = zonesPrice + nextZone;
                        leafBinding.tvTitle2.setVisibility(View.GONE);
                        leafBinding.tvTitle3.setText("$ " + Util.getDecimalTwoPoint(zonesPrice));

                    }

                }

            }
        });

    }

    private void setAeration() {
        final ActivityAerationBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_aeration, mBinding.lnContainer, true);
        binding.cbCheckRecurring.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                /*if (b) {
                    binding.lnRecurring.setVisibility(View.VISIBLE);
                } else
                    binding.lnRecurring.setVisibility(View.GONE);*/
            }
        });
        if (mode.equalsIgnoreCase("edit")) {
            binding.tvSave.setText("Update");
            HashMap<String, String> params = new HashMap<>();
            params.put("added_service_id", getIntent().getExtras().getString("addedServiceId"));
            params.put("service_id", serviceId);

            new PostDataParser(AddServiceActivity.this, Api.sViewServiceDetails, params, true, true, new PostDataParser.OnGetResponseListner() {
                public String success;

                @Override
                public void onGetResponse(JSONObject response) {

                    if (response != null) {
                        try {
                            success = response.optString("success");
                            if (success.equalsIgnoreCase("1")) {
                                JSONObject data = response.optJSONObject("data");
                                JSONArray aerationAcre = data.optJSONArray("aeration_acre");
                                String firstPrice = null;
                                if (aerationAcre.length() > 0) {
                                    JSONObject aerationAcreObject = aerationAcre.optJSONObject(0);
                                    firstPrice = aerationAcreObject.optString("service_field_price");
                                    binding.etFirstPrice.setText(firstPrice);
                                }

                                if (aerationAcre.length() > 1) {
                                    JSONObject aerationAcreObject1 = aerationAcre.optJSONObject(1);
                                    String nextPrice = aerationAcreObject1.optString("service_field_price");
                                    binding.etNextPrice.setText(String.valueOf(Double.parseDouble(nextPrice) - Double.parseDouble(firstPrice)));
                                }

                                String upperLimit = String.valueOf(aerationAcre.length() / 4);
                                binding.etUpperLimit.setText(upperLimit);
                                //show recurring service
                                boolean recurringFlag = false;
                                JSONArray servicePrices = data.optJSONArray("service_prices");
                                for (int i = 0; i < servicePrices.length(); i++) {
                                    JSONObject jsonObject = servicePrices.optJSONObject(i);
                                    String price = jsonObject.optString("discount_price");
                                    if (!jsonObject.optString("service_frequency").equalsIgnoreCase("just once"))
                                        if (!price.equals("")
                                                && !price.equals("0")
                                                && !price.equals("0.0")
                                                && !price.equals("0.00")
                                                ) {
                                            recurringFlag = true;
                                        }
                                    switch (i) {
                                        case 0:
                                            binding.etSevenDays.setText(jsonObject.optString("discount_price"));
                                            break;
                                        case 1:
                                            binding.etTenDays.setText(jsonObject.optString("discount_price"));
                                            break;
                                        case 2:
                                            binding.etFourteenDays.setText(jsonObject.optString("discount_price"));
                                            break;

                                    }
                                }
                                if (recurringFlag) {
                                    binding.cbCheckRecurring.setChecked(true);
                                } else {
                                    binding.cbCheckRecurring.setChecked(false);
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }


        binding.tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*recurring services*/
                /*if (binding.cbCheckRecurring.isChecked()) {

                    mSevenDaysRS = binding.etSevenDays.getText().toString();
                    if (TextUtils.isEmpty(mSevenDaysRS))
                        mSevenDaysRS = "0";

                    mTenDaysRS = binding.etTenDays.getText().toString();
                    if (TextUtils.isEmpty(mTenDaysRS))
                        mTenDaysRS = "0";

                    mFourteenDaysRS = binding.etFourteenDays.getText().toString();
                    if (TextUtils.isEmpty(mFourteenDaysRS))
                        mFourteenDaysRS = "0";
                } else {*/
                mSevenDaysRS = "0";
                mTenDaysRS = "0";
                mFourteenDaysRS = "0";
                //}
                JSONArray jsonArrayRecService = new JSONArray();

                try {

                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("rs", mSevenDaysRS);

                    JSONObject jsonObject2 = new JSONObject();
                    jsonObject2.put("rs", mTenDaysRS);

                    JSONObject jsonObject3 = new JSONObject();
                    jsonObject3.put("rs", mFourteenDaysRS);

                    jsonArrayRecService.put(jsonObject1);
                    jsonArrayRecService.put(jsonObject2);
                    jsonArrayRecService.put(jsonObject3);
                } catch (Exception e) {
                    e.printStackTrace();
                }

/*end recurring service*/
                String firstPrice = binding.etFirstPrice.getText().toString();
                String nextPrice = binding.etNextPrice.getText().toString();
                String upperLimit = binding.etUpperLimit.getText().toString();
                if (TextUtils.isEmpty(firstPrice) ||
                        TextUtils.isEmpty(nextPrice) ||
                        TextUtils.isEmpty(upperLimit)) {
                    Toast.makeText(AddServiceActivity.this, "Please fill up all fields.", Toast.LENGTH_SHORT).show();
                    return;
                }
                HashMap<String, String> params = new HashMap<>();
                params.put("service_id", serviceId);
                params.put("area_first_acre", firstPrice);
                params.put("area_next_acre", nextPrice);
                params.put("area_acre_limit", upperLimit);
                params.put("recurring_services", jsonArrayRecService.toString());


                if (mode.equalsIgnoreCase("add")) {
                    params.put("user_id", AppData.sUserId);
                    saveService(Api.sAddService, params, binding.tvSave);
                } else if (mode.equalsIgnoreCase("edit")) {
                    params.put("added_service_id", getIntent().getExtras().getString("addedServiceId"));
                    saveService(Api.sEditService, params, binding.tvSave);
                }
            }
        });
        binding.tvPricingPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAndCloseTextview(binding.tvPricingPrev, binding.lnPrice);
                binding.lnPrice.removeAllViews();
                LeafRemovalRowBinding leafBindingFirstRow = DataBindingUtil.inflate(getLayoutInflater(), R.layout.leaf_removal_row, binding.lnPrice, true);
                leafBindingFirstRow.tvTitle1.setText("Acreage");
                leafBindingFirstRow.tvTitle1.setTextColor(getResources().getColor(R.color.colorText));
                leafBindingFirstRow.tvTitle3.setVisibility(View.GONE);
                leafBindingFirstRow.tvTitle2.setText("Price");
                leafBindingFirstRow.tvTitle2.setTextColor(getResources().getColor(R.color.colorText));
                try {
                    double firstPrice = Double.parseDouble(binding.etFirstPrice.getText().toString());
                    double nextPrice = Double.parseDouble(binding.etNextPrice.getText().toString());
                    double upperLimit = Double.parseDouble(binding.etUpperLimit.getText().toString());
                    double acreagePrice = 0;
                    for (double i = 0; i < upperLimit; i = i + 0.25) {
                        if (i < 0.25)
                            acreagePrice = firstPrice;
                        else
                            acreagePrice = acreagePrice + nextPrice;


                        LeafRemovalRowBinding leafBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.leaf_removal_row, binding.lnPrice, true);
                        leafBinding.tvTitle1.setText(i + " - " + (i + 0.25) + " acre");

                        leafBinding.tvTitle3.setVisibility(View.GONE);
                        leafBinding.tvTitle2.setText("$ " + Util.getDecimalTwoPoint(acreagePrice));


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setLawnTreatment() {
        final ActivityLawnTreatmentBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_lawn_treatment, mBinding.lnContainer, true);
        binding.cbCheckRecurring.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                /*if (b) {
                    binding.lnRecurring.setVisibility(View.VISIBLE);
                } else
                    binding.lnRecurring.setVisibility(View.GONE);*/
            }
        });
        if (mode.equalsIgnoreCase("edit")) {
            binding.tvSave.setText("Update");
            HashMap<String, String> params = new HashMap<>();
            params.put("added_service_id", getIntent().getExtras().getString("addedServiceId"));
            params.put("service_id", serviceId);

            new PostDataParser(AddServiceActivity.this, Api.sViewServiceDetails, params, true, true, new PostDataParser.OnGetResponseListner() {
                public String success;

                @Override
                public void onGetResponse(JSONObject response) {

                    if (response != null) {
                        try {
                            success = response.optString("success");
                            if (success.equalsIgnoreCase("1")) {
                                JSONObject data = response.optJSONObject("data");
                                JSONArray lawnAcre = data.optJSONArray("lawn_acre");
                                String firstPrice = null;
                                if (lawnAcre.length() > 0) {
                                    JSONObject lawnAcreObject = lawnAcre.optJSONObject(0);
                                    firstPrice = lawnAcreObject.optString("service_field_price");
                                    binding.etFirstPrice.setText(firstPrice);
                                }

                                if (lawnAcre.length() > 1) {
                                    JSONObject lawnAcreObject1 = lawnAcre.optJSONObject(1);
                                    String nextPrice = lawnAcreObject1.optString("service_field_price");
                                    binding.etNextPrice.setText(String.valueOf(Double.parseDouble(nextPrice) - Double.parseDouble(firstPrice)));
                                }

                                String upperLimit = String.valueOf(lawnAcre.length() / 4);
                                binding.etUpperLimit.setText(upperLimit);
                                //show recurring service
                                boolean recurringFlag = false;
                                JSONArray servicePrices = data.optJSONArray("service_prices");
                                for (int i = 0; i < servicePrices.length(); i++) {
                                    JSONObject jsonObject = servicePrices.optJSONObject(i);
                                    String price = jsonObject.optString("discount_price");
                                    if (!jsonObject.optString("service_frequency").equalsIgnoreCase("just once"))
                                        if (!price.equals("")
                                                && !price.equals("0")
                                                && !price.equals("0.0")
                                                && !price.equals("0.00")
                                                ) {
                                            recurringFlag = true;
                                        }
                                    switch (i) {
                                        case 0:
                                            binding.etSevenDays.setText(jsonObject.optString("discount_price"));
                                            break;
                                        case 1:
                                            binding.etTenDays.setText(jsonObject.optString("discount_price"));
                                            break;
                                        case 2:
                                            binding.etFourteenDays.setText(jsonObject.optString("discount_price"));
                                            break;

                                    }
                                }
                                if (recurringFlag) {
                                    binding.cbCheckRecurring.setChecked(true);
                                } else {
                                    binding.cbCheckRecurring.setChecked(false);
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
        binding.tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*recurring services*/
                /*if (binding.cbCheckRecurring.isChecked()) {

                    mSevenDaysRS = binding.etSevenDays.getText().toString();
                    if (TextUtils.isEmpty(mSevenDaysRS))
                        mSevenDaysRS = "0";

                    mTenDaysRS = binding.etTenDays.getText().toString();
                    if (TextUtils.isEmpty(mTenDaysRS))
                        mTenDaysRS = "0";

                    mFourteenDaysRS = binding.etFourteenDays.getText().toString();
                    if (TextUtils.isEmpty(mFourteenDaysRS))
                        mFourteenDaysRS = "0";
                } else {*/
                mSevenDaysRS = "0";
                mTenDaysRS = "0";
                mFourteenDaysRS = "0";
                //}
                JSONArray jsonArrayRecService = new JSONArray();

                try {

                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("rs", mSevenDaysRS);

                    JSONObject jsonObject2 = new JSONObject();
                    jsonObject2.put("rs", mTenDaysRS);

                    JSONObject jsonObject3 = new JSONObject();
                    jsonObject3.put("rs", mFourteenDaysRS);

                    jsonArrayRecService.put(jsonObject1);
                    jsonArrayRecService.put(jsonObject2);
                    jsonArrayRecService.put(jsonObject3);
                } catch (Exception e) {
                    e.printStackTrace();
                }

/*end recurring service*/
                String firstPrice = binding.etFirstPrice.getText().toString();
                String nextPrice = binding.etNextPrice.getText().toString();
                String upperLimit = binding.etUpperLimit.getText().toString();
                if (TextUtils.isEmpty(firstPrice) ||
                        TextUtils.isEmpty(nextPrice) ||
                        TextUtils.isEmpty(upperLimit)) {
                    Toast.makeText(AddServiceActivity.this, "Please fill up all fields.", Toast.LENGTH_SHORT).show();
                    return;
                }
                HashMap<String, String> params = new HashMap<>();
                params.put("service_id", serviceId);
                params.put("lawntreat_first_acre", firstPrice);
                params.put("lawntreat_next_acre", nextPrice);
                params.put("lawntreat_acre_limit", upperLimit);
                params.put("recurring_services", jsonArrayRecService.toString());


                if (mode.equalsIgnoreCase("add")) {
                    params.put("user_id", AppData.sUserId);
                    saveService(Api.sAddService, params, binding.tvSave);
                } else if (mode.equalsIgnoreCase("edit")) {
                    params.put("added_service_id", getIntent().getExtras().getString("addedServiceId"));
                    saveService(Api.sEditService, params, binding.tvSave);
                }
            }
        });
        binding.tvPricingPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAndCloseTextview(binding.tvPricingPrev, binding.lnPrice);
                binding.lnPrice.removeAllViews();
                LeafRemovalRowBinding leafBindingFirstRow = DataBindingUtil.inflate(getLayoutInflater(), R.layout.leaf_removal_row, binding.lnPrice, true);
                leafBindingFirstRow.tvTitle1.setText("Acreage");
                leafBindingFirstRow.tvTitle1.setTextColor(getResources().getColor(R.color.colorText));
                leafBindingFirstRow.tvTitle2.setVisibility(View.GONE);
                leafBindingFirstRow.tvTitle3.setText("Price");
                leafBindingFirstRow.tvTitle3.setTextColor(getResources().getColor(R.color.colorText));
                try {
                    double firstPrice = Double.parseDouble(binding.etFirstPrice.getText().toString());
                    double nextPrice = Double.parseDouble(binding.etNextPrice.getText().toString());
                    double upperLimit = Double.parseDouble(binding.etUpperLimit.getText().toString());
                    double acreagePrice = 0;
                    for (double i = 0; i < upperLimit; i = i + 0.25) {
                        if (i < 0.25)
                            acreagePrice = firstPrice;
                        else
                            acreagePrice = acreagePrice + nextPrice;


                        LeafRemovalRowBinding leafBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.leaf_removal_row, binding.lnPrice, true);
                        leafBinding.tvTitle1.setText(i + " - " + (i + 0.25) + " acre");

                        leafBinding.tvTitle2.setVisibility(View.GONE);
                        leafBinding.tvTitle3.setText("$ " + Util.getDecimalTwoPoint(acreagePrice));


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setLeafRemoval() {
        final ActivityLeafRemovalBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_leaf_removal, mBinding.lnContainer, true);
        binding.cbCheckRecurring.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                /*if (b) {
                    binding.lnRecurring.setVisibility(View.VISIBLE);
                } else
                    binding.lnRecurring.setVisibility(View.GONE);*/
            }
        });
        if (mode.equalsIgnoreCase("edit")) {
            binding.tvSave.setText("Update");
            HashMap<String, String> params = new HashMap<>();
            params.put("added_service_id", getIntent().getExtras().getString("addedServiceId"));
            params.put("service_id", serviceId);

            new PostDataParser(AddServiceActivity.this, Api.sViewServiceDetails, params, true, true, new PostDataParser.OnGetResponseListner() {
                public String success;

                @Override
                public void onGetResponse(JSONObject response) {

                    if (response != null) {
                        try {
                            success = response.optString("success");
                            if (success.equalsIgnoreCase("1")) {
                                JSONObject data = response.optJSONObject("data");

                                JSONArray leafAcre = data.optJSONArray("leaf_acre");
                                String firstPrice = null;
                                if (leafAcre.length() > 0) {
                                    JSONObject leafAcreObject = leafAcre.optJSONObject(0);
                                    firstPrice = leafAcreObject.optString("service_field_price");
                                    binding.etFirstPrice.setText(firstPrice);
                                }

                                if (leafAcre.length() > 1) {
                                    JSONObject leafAcreObject1 = leafAcre.optJSONObject(1);
                                    String nextPrice = leafAcreObject1.optString("service_field_price");
                                    binding.etNextPrice.setText(String.valueOf(Double.parseDouble(nextPrice) - Double.parseDouble(firstPrice)));
                                }

                                String upperLimit = String.valueOf(leafAcre.length() / 4);
                                binding.etUpperLimit.setText(upperLimit);

                                JSONArray leafAccumulation = data.optJSONArray("leaf_accumulation");
                                if (leafAccumulation.length() > 0) {
                                    JSONObject jsonObject = leafAccumulation.optJSONObject(0);
                                    binding.etLight.setText(jsonObject.optString("service_field_price"));
                                }
                                if (leafAccumulation.length() > 1) {
                                    JSONObject jsonObject = leafAccumulation.optJSONObject(1);
                                    binding.etMedium.setText(jsonObject.optString("service_field_price"));
                                }
                                if (leafAccumulation.length() > 2) {
                                    JSONObject jsonObject = leafAccumulation.optJSONObject(2);
                                    binding.etHeavy.setText(jsonObject.optString("service_field_price"));
                                }
                                if (leafAccumulation.length() > 3) {
                                    JSONObject jsonObject = leafAccumulation.optJSONObject(3);
                                    binding.etOverTheTop.setText(jsonObject.optString("service_field_price"));
                                }
                                //show recurring service
                                boolean recurringFlag = false;
                                JSONArray servicePrices = data.optJSONArray("service_prices");
                                for (int i = 0; i < servicePrices.length(); i++) {
                                    JSONObject jsonObject = servicePrices.optJSONObject(i);
                                    String price = jsonObject.optString("discount_price");
                                    if (!jsonObject.optString("service_frequency").equalsIgnoreCase("just once"))
                                        if (!price.equals("")
                                                && !price.equals("0")
                                                && !price.equals("0.0")
                                                && !price.equals("0.00")
                                                ) {
                                            recurringFlag = true;
                                        }
                                    switch (i) {
                                        case 0:
                                            binding.etSevenDays.setText(jsonObject.optString("discount_price"));
                                            break;
                                        case 1:
                                            binding.etTenDays.setText(jsonObject.optString("discount_price"));
                                            break;
                                        case 2:
                                            binding.etFourteenDays.setText(jsonObject.optString("discount_price"));
                                            break;

                                    }
                                }
                                if (recurringFlag) {
                                    binding.cbCheckRecurring.setChecked(true);
                                } else {
                                    binding.cbCheckRecurring.setChecked(false);
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }


        binding.tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*recurring services*/
                /*if (binding.cbCheckRecurring.isChecked()) {

                    mSevenDaysRS = binding.etSevenDays.getText().toString();
                    if (TextUtils.isEmpty(mSevenDaysRS))
                        mSevenDaysRS = "0";

                    mTenDaysRS = binding.etTenDays.getText().toString();
                    if (TextUtils.isEmpty(mTenDaysRS))
                        mTenDaysRS = "0";

                    mFourteenDaysRS = binding.etFourteenDays.getText().toString();
                    if (TextUtils.isEmpty(mFourteenDaysRS))
                        mFourteenDaysRS = "0";
                } else {*/
                mSevenDaysRS = "0";
                mTenDaysRS = "0";
                mFourteenDaysRS = "0";
                //}
                JSONArray jsonArrayRecService = new JSONArray();

                try {

                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("rs", mSevenDaysRS);

                    JSONObject jsonObject2 = new JSONObject();
                    jsonObject2.put("rs", mTenDaysRS);

                    JSONObject jsonObject3 = new JSONObject();
                    jsonObject3.put("rs", mFourteenDaysRS);

                    jsonArrayRecService.put(jsonObject1);
                    jsonArrayRecService.put(jsonObject2);
                    jsonArrayRecService.put(jsonObject3);
                } catch (Exception e) {
                    e.printStackTrace();
                }

/*end recurring service*/
                String firstPrice = binding.etFirstPrice.getText().toString();
                String nextPrice = binding.etNextPrice.getText().toString();
                String light = binding.etLight.getText().toString();
                String medium = binding.etMedium.getText().toString();
                String heavy = binding.etHeavy.getText().toString();
                String overTheTop = binding.etOverTheTop.getText().toString();
                String upperLimit = binding.etUpperLimit.getText().toString();
                if (TextUtils.isEmpty(firstPrice) ||
                        TextUtils.isEmpty(nextPrice) ||
                        TextUtils.isEmpty(light) ||
                        TextUtils.isEmpty(medium) ||
                        TextUtils.isEmpty(heavy) ||
                        TextUtils.isEmpty(overTheTop) ||
                        TextUtils.isEmpty(upperLimit)
                        ) {
                    Toast.makeText(AddServiceActivity.this, "Please fill up all fields.", Toast.LENGTH_SHORT).show();
                    return;
                }
                HashMap<String, String> params = new HashMap<>();
                params.put("service_id", serviceId);
                params.put("leaf_first_acre", firstPrice);
                params.put("leaf_next_acre", nextPrice);
                params.put("leaf_acre_limit", upperLimit);
                params.put("light", light);
                params.put("medium", medium);
                params.put("heavy", heavy);
                params.put("over_top", overTheTop);
                params.put("recurring_services", jsonArrayRecService.toString());


                if (mode.equalsIgnoreCase("add")) {
                    params.put("user_id", AppData.sUserId);
                    saveService(Api.sAddService, params, binding.tvSave);
                } else if (mode.equalsIgnoreCase("edit")) {
                    params.put("added_service_id", getIntent().getExtras().getString("addedServiceId"));
                    saveService(Api.sEditService, params, binding.tvSave);
                }
            }
        });
        binding.tvPricingPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAndCloseTextview(binding.tvPricingPrev, binding.lnPrice);
                binding.lnPrice.removeAllViews();
                LeafRemovalRowBinding leafBindingFirstRow = DataBindingUtil.inflate(getLayoutInflater(), R.layout.leaf_removal_row, binding.lnPrice, true);
                leafBindingFirstRow.tvTitle1.setText("Acreage");
                leafBindingFirstRow.tvTitle1.setTextColor(getResources().getColor(R.color.colorText));
                leafBindingFirstRow.tvTitle2.setText("Accumulation");
                leafBindingFirstRow.tvTitle2.setTextColor(getResources().getColor(R.color.colorText));
                leafBindingFirstRow.tvTitle3.setText("Price");
                leafBindingFirstRow.tvTitle3.setTextColor(getResources().getColor(R.color.colorText));
                try {
                    double firstPrice = Double.parseDouble(binding.etFirstPrice.getText().toString());
                    double nextPrice = Double.parseDouble(binding.etNextPrice.getText().toString());
                    double light = Double.parseDouble(binding.etLight.getText().toString());
                    double medium = Double.parseDouble(binding.etMedium.getText().toString());
                    double heavy = Double.parseDouble(binding.etHeavy.getText().toString());
                    double overTheTop = Double.parseDouble(binding.etOverTheTop.getText().toString());
                    double upperLimit = Double.parseDouble(binding.etUpperLimit.getText().toString());
                    double price = 0;
                    for (double i = 0; i < upperLimit; i = i + 0.25) {
                        if (i < 0.25)
                            price = firstPrice;
                        else
                            price = price + nextPrice;
                        for (int j = 1; j <= 4; j++) {

                            LeafRemovalRowBinding leafBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.leaf_removal_row, binding.lnPrice, true);
                            leafBinding.tvTitle1.setText(i + " - " + (i + 0.25) + " acre");
                            if (j == 1) {
                                leafBinding.tvTitle2.setText("Light");
                                leafBinding.tvTitle3.setText("$ " + Util.getDecimalTwoPoint(price + light));
                            } else if (j == 2) {
                                leafBinding.tvTitle2.setText("Medium");
                                leafBinding.tvTitle3.setText("$ " + Util.getDecimalTwoPoint(price + medium));
                            } else if (j == 3) {
                                leafBinding.tvTitle2.setText("Heavy");
                                leafBinding.tvTitle3.setText("$ " + Util.getDecimalTwoPoint(price + heavy));
                            } else if (j == 4) {
                                leafBinding.tvTitle2.setText("Over the top");
                                leafBinding.tvTitle3.setText("$ " + Util.getDecimalTwoPoint(price + overTheTop));
                            }


                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void setPoolCleaning() {
        final ActivityPoolCleaningBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_pool_cleaning, mBinding.lnContainer, true);
        binding.cbCheckRecurring.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    binding.lnRecurring.setVisibility(View.VISIBLE);
                } else
                    binding.lnRecurring.setVisibility(View.GONE);
            }
        });
        if (mode.equalsIgnoreCase("edit")) {
            binding.tvSave.setText("Update");
            HashMap<String, String> params = new HashMap<>();
            params.put("added_service_id", getIntent().getExtras().getString("addedServiceId"));
            params.put("service_id", serviceId);

            new PostDataParser(AddServiceActivity.this, Api.sViewServiceDetails, params, true, true, new PostDataParser.OnGetResponseListner() {
                public String success;

                @Override
                public void onGetResponse(JSONObject response) {

                    if (response != null) {
                        try {
                            success = response.optString("success");
                            if (success.equalsIgnoreCase("1")) {
                                JSONObject data = response.optJSONObject("data");

                                JSONArray poolWaterType = data.optJSONArray("pool_water_type");
                                if (poolWaterType.length() > 0) {
                                    JSONObject jsonObject = poolWaterType.optJSONObject(0);
                                    binding.etChlorine.setText(jsonObject.optString("service_field_price"));
                                }
                                if (poolWaterType.length() > 1) {
                                    JSONObject jsonObject = poolWaterType.optJSONObject(1);
                                    binding.etSaline.setText(jsonObject.optString("service_field_price"));
                                }

                                JSONArray poolSpa = data.optJSONArray("pool_spa");
                                if (poolSpa.length() > 0) {
                                    JSONObject jsonObject = poolSpa.optJSONObject(0);
                                    binding.etHotTub.setText(jsonObject.optString("service_field_price"));
                                }

                                JSONArray poolType = data.optJSONArray("pool_type");
                                if (poolType.length() > 0) {
                                    JSONObject jsonObject = poolType.optJSONObject(0);
                                    binding.etInground.setText(jsonObject.optString("service_field_price"));
                                }
                                if (poolType.length() > 1) {
                                    JSONObject jsonObject = poolType.optJSONObject(1);
                                    binding.etAboveGround.setText(jsonObject.optString("service_field_price"));
                                }

                                JSONArray poolState = data.optJSONArray("pool_state");
                                if (poolState.length() > 0) {
                                    JSONObject jsonObject = poolState.optJSONObject(0);
                                    binding.etClear.setText(jsonObject.optString("service_field_price"));
                                }
                                if (poolState.length() > 1) {
                                    JSONObject jsonObject = poolState.optJSONObject(1);
                                    binding.etCloudy.setText(jsonObject.optString("service_field_price"));
                                }
                                if (poolState.length() > 2) {
                                    JSONObject jsonObject = poolState.optJSONObject(2);
                                    binding.etHeavy.setText(jsonObject.optString("service_field_price"));
                                }
                                //show recurring service
                                boolean recurringFlag = false;
                                JSONArray servicePrices = data.optJSONArray("service_prices");
                                for (int i = 0; i < servicePrices.length(); i++) {
                                    JSONObject jsonObject = servicePrices.optJSONObject(i);
                                    String price = jsonObject.optString("discount_price");
                                    if (!jsonObject.optString("service_frequency").equalsIgnoreCase("just once"))
                                        if (!price.equals("")
                                                && !price.equals("0")
                                                && !price.equals("0.0")
                                                && !price.equals("0.00")
                                                ) {
                                            recurringFlag = true;
                                        }
                                    switch (i) {
                                        case 0:
                                            binding.etSevenDays.setText(jsonObject.optString("discount_price"));
                                            break;
                                        case 1:
                                            binding.etTenDays.setText(jsonObject.optString("discount_price"));
                                            break;
                                        case 2:
                                            binding.etFourteenDays.setText(jsonObject.optString("discount_price"));
                                            break;

                                    }
                                }
                                if (recurringFlag) {
                                    binding.cbCheckRecurring.setChecked(true);
                                } else {
                                    binding.cbCheckRecurring.setChecked(false);
                                }


                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
        binding.tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*recurring services*/
                if (binding.cbCheckRecurring.isChecked()) {

                    mSevenDaysRS = binding.etSevenDays.getText().toString();
                    if (TextUtils.isEmpty(mSevenDaysRS))
                        mSevenDaysRS = "0";

                    mTenDaysRS = binding.etTenDays.getText().toString();
                    if (TextUtils.isEmpty(mTenDaysRS))
                        mTenDaysRS = "0";

                    mFourteenDaysRS = binding.etFourteenDays.getText().toString();
                    if (TextUtils.isEmpty(mFourteenDaysRS))
                        mFourteenDaysRS = "0";
                } else {
                    mSevenDaysRS = "0";
                    mTenDaysRS = "0";
                    mFourteenDaysRS = "0";
                }
                JSONArray jsonArrayRecService = new JSONArray();

                try {

                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("rs", mSevenDaysRS);

                    JSONObject jsonObject2 = new JSONObject();
                    jsonObject2.put("rs", mTenDaysRS);

                    JSONObject jsonObject3 = new JSONObject();
                    jsonObject3.put("rs", mFourteenDaysRS);

                    jsonArrayRecService.put(jsonObject1);
                    jsonArrayRecService.put(jsonObject2);
                    jsonArrayRecService.put(jsonObject3);
                } catch (Exception e) {
                    e.printStackTrace();
                }

/*end recurring service*/
                String chlorine = binding.etChlorine.getText().toString();
                String saline = binding.etSaline.getText().toString();
                String hotTub = binding.etHotTub.getText().toString();
                String inground = binding.etInground.getText().toString();
                String aboveGround = binding.etAboveGround.getText().toString();
                String clear = binding.etClear.getText().toString();
                String cloudy = binding.etCloudy.getText().toString();
                String heavy = binding.etHeavy.getText().toString();
                if (TextUtils.isEmpty(chlorine) ||
                        TextUtils.isEmpty(saline) ||
                        TextUtils.isEmpty(hotTub) ||
                        TextUtils.isEmpty(inground) ||
                        TextUtils.isEmpty(aboveGround) ||
                        TextUtils.isEmpty(clear) ||
                        TextUtils.isEmpty(cloudy) ||
                        TextUtils.isEmpty(heavy)
                        ) {
                    Toast.makeText(AddServiceActivity.this, "Please fill up all fields.", Toast.LENGTH_SHORT).show();
                    return;
                }
                HashMap<String, String> params = new HashMap<>();
                params.put("service_id", serviceId);
                params.put("pool_chlorine", chlorine);
                params.put("pool_saline", saline);
                params.put("pool_spa_hot_tub", hotTub);
                params.put("pool_inground", inground);
                params.put("pool_above_ground", aboveGround);
                params.put("pool_clear", clear);
                params.put("pool_cloudy", cloudy);
                params.put("pool_heavy", heavy);
                params.put("recurring_services", jsonArrayRecService.toString());


                if (mode.equalsIgnoreCase("add")) {
                    params.put("user_id", AppData.sUserId);
                    saveService(Api.sAddService, params, binding.tvSave);
                } else if (mode.equalsIgnoreCase("edit")) {
                    params.put("added_service_id", getIntent().getExtras().getString("addedServiceId"));
                    saveService(Api.sEditService, params, binding.tvSave);
                }
            }
        });
        binding.tvPricingPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAndCloseTextview(binding.tvPricingPrev, binding.lnPrice);
                binding.lnPrice.removeAllViews();
                PoolCleaningRowBinding poolBindingFirstRow = DataBindingUtil.inflate(getLayoutInflater(), R.layout.pool_cleaning_row, binding.lnPrice, true);
                poolBindingFirstRow.tvTitle1.setText("Pool Water Type");
                poolBindingFirstRow.tvTitle1.setTextColor(getResources().getColor(R.color.colorText));
                poolBindingFirstRow.tvTitle2.setText("Include a Spa/Hot Tub");
                poolBindingFirstRow.tvTitle2.setTextColor(getResources().getColor(R.color.colorText));
                poolBindingFirstRow.tvTitle3.setText("Pool Type");
                poolBindingFirstRow.tvTitle3.setTextColor(getResources().getColor(R.color.colorText));
                poolBindingFirstRow.tvTitle4.setText("State of Pool");
                poolBindingFirstRow.tvTitle4.setTextColor(getResources().getColor(R.color.colorText));
                poolBindingFirstRow.tvTitle5.setText("Price");
                poolBindingFirstRow.tvTitle5.setTextColor(getResources().getColor(R.color.colorText));
                try {
                    double chlorine = Double.parseDouble(binding.etChlorine.getText().toString());
                    double saline = Double.parseDouble(binding.etSaline.getText().toString());
                    double hotTub = Double.parseDouble(binding.etHotTub.getText().toString());
                    double inground = Double.parseDouble(binding.etInground.getText().toString());
                    double aboveGround = Double.parseDouble(binding.etAboveGround.getText().toString());
                    double clear = Double.parseDouble(binding.etClear.getText().toString());
                    double cloudy = Double.parseDouble(binding.etCloudy.getText().toString());
                    double heavy = Double.parseDouble(binding.etHeavy.getText().toString());

                    for (int i = 1; i <= 2; i++) {//water pool

                        for (int j = 1; j <= 2; j++) {//pool type

                            for (int k = 1; k <= 3; k++) {//state of pool
                                PoolCleaningRowBinding poolBindingRow = DataBindingUtil.inflate(getLayoutInflater(), R.layout.pool_cleaning_row, binding.lnPrice, true);
                                double waterPollPrice = 0;
                                if (i == 1) {
                                    poolBindingRow.tvTitle1.setText("Chlorine");
                                    waterPollPrice = chlorine;
                                } else if (i == 2) {
                                    poolBindingRow.tvTitle1.setText("Saline");
                                    waterPollPrice = saline;
                                }

                                poolBindingRow.tvTitle2.setText("Yes");
                                double poolTypePrice = 0;

                                if (j == 1) {
                                    poolBindingRow.tvTitle3.setText("Inground");
                                    poolTypePrice = inground;
                                } else if (j == 2) {
                                    poolBindingRow.tvTitle3.setText("Above Ground");
                                    poolTypePrice = aboveGround;
                                }
                                if (k == 1) {
                                    poolBindingRow.tvTitle4.setText("Relatively Clear");
                                    poolBindingRow.tvTitle5.setText("$ " + Util.getDecimalTwoPoint(waterPollPrice + hotTub + poolTypePrice + clear));
                                } else if (k == 2) {
                                    poolBindingRow.tvTitle4.setText("Moderately Cloudy");
                                    poolBindingRow.tvTitle5.setText("$ " + Util.getDecimalTwoPoint(waterPollPrice + hotTub + poolTypePrice + cloudy));
                                } else if (k == 3) {
                                    poolBindingRow.tvTitle4.setText("Heavy Algae Present");
                                    poolBindingRow.tvTitle5.setText("$ " + Util.getDecimalTwoPoint(waterPollPrice + hotTub + poolTypePrice + heavy));
                                }
                            }

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setMowingAndEdging() {
        final ActivityMowingEdgingBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_mowing_edging, mBinding.lnContainer, true);
        binding.cbCheckRecurring.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    binding.lnRecurring.setVisibility(View.VISIBLE);
                } else
                    binding.lnRecurring.setVisibility(View.GONE);
            }
        });
        if (mode.equalsIgnoreCase("edit")) {
            binding.tvSave.setText("Update");
            HashMap<String, String> params = new HashMap<>();
            params.put("added_service_id", getIntent().getExtras().getString("addedServiceId"));
            params.put("service_id", serviceId);

            new PostDataParser(AddServiceActivity.this, Api.sViewServiceDetails, params, true, true, new PostDataParser.OnGetResponseListner() {
                public String success;

                @Override
                public void onGetResponse(JSONObject response) {

                    if (response != null) {
                        try {
                            success = response.optString("success");
                            if (success.equalsIgnoreCase("1")) {
                                JSONObject data = response.optJSONObject("data");
                                JSONArray mowingAcre = data.optJSONArray("mowing_acre");
                                String firstPrice = null;
                                if (mowingAcre.length() > 0) {
                                    JSONObject mowingAcreObject = mowingAcre.optJSONObject(0);
                                    firstPrice = mowingAcreObject.optString("service_field_price");
                                    binding.etFirstPrice.setText(firstPrice);
                                }

                                if (mowingAcre.length() > 1) {
                                    JSONObject mowingAcreObject1 = mowingAcre.optJSONObject(1);
                                    String nextPrice = mowingAcreObject1.optString("service_field_price");
                                    binding.etNextPrice.setText(String.valueOf(Double.parseDouble(nextPrice) - Double.parseDouble(firstPrice)));
                                }

                                String upperLimit = String.valueOf(mowingAcre.length() / 4);
                                binding.etUpperLimit.setText(upperLimit);

                                JSONArray mowingGrass = data.optJSONArray("mowing_grass");

                                if (mowingGrass.length() > 0) {
                                    JSONObject mowingGrassObject = mowingGrass.optJSONObject(0);
                                    String firstGrassPrice = mowingGrassObject.optString("service_field_price");
                                    binding.etFirstSixInch.setText(firstGrassPrice);
                                }

                                if (mowingAcre.length() > 1) {
                                    JSONObject mowingGrassObject1 = mowingGrass.optJSONObject(1);
                                    String nextGrassPrice = mowingGrassObject1.optString("service_field_price");
                                    binding.etNextSixInch.setText(nextGrassPrice);
                                }

                                //show recurring service
                                boolean recurringFlag = false;
                                JSONArray servicePrices = data.optJSONArray("service_prices");
                                for (int i = 0; i < servicePrices.length(); i++) {
                                    JSONObject jsonObject = servicePrices.optJSONObject(i);
                                    String price = jsonObject.optString("discount_price");
                                    if (!jsonObject.optString("service_frequency").equalsIgnoreCase("just once"))
                                        if (!price.equals("")
                                                && !price.equals("0")
                                                && !price.equals("0.0")
                                                && !price.equals("0.00")
                                                ) {
                                            recurringFlag = true;
                                        }
                                    switch (i) {
                                        case 0:
                                            binding.etSevenDays.setText(jsonObject.optString("discount_price"));
                                            break;
                                        case 1:
                                            binding.etTenDays.setText(jsonObject.optString("discount_price"));
                                            break;
                                        case 2:
                                            binding.etFourteenDays.setText(jsonObject.optString("discount_price"));
                                            break;

                                    }
                                }
                                if (recurringFlag) {
                                    binding.cbCheckRecurring.setChecked(true);
                                } else {
                                    binding.cbCheckRecurring.setChecked(false);
                                }


                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }

        binding.tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*recurring services*/
                if (binding.cbCheckRecurring.isChecked()) {

                    mSevenDaysRS = binding.etSevenDays.getText().toString();
                    if (TextUtils.isEmpty(mSevenDaysRS))
                        mSevenDaysRS = "0";

                    mTenDaysRS = binding.etTenDays.getText().toString();
                    if (TextUtils.isEmpty(mTenDaysRS))
                        mTenDaysRS = "0";

                    mFourteenDaysRS = binding.etFourteenDays.getText().toString();
                    if (TextUtils.isEmpty(mFourteenDaysRS))
                        mFourteenDaysRS = "0";
                } else {
                    mSevenDaysRS = "0";
                    mTenDaysRS = "0";
                    mFourteenDaysRS = "0";
                }
                JSONArray jsonArrayRecService = new JSONArray();

                try {

                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("rs", mSevenDaysRS);

                    JSONObject jsonObject2 = new JSONObject();
                    jsonObject2.put("rs", mTenDaysRS);

                    JSONObject jsonObject3 = new JSONObject();
                    jsonObject3.put("rs", mFourteenDaysRS);

                    jsonArrayRecService.put(jsonObject1);
                    jsonArrayRecService.put(jsonObject2);
                    jsonArrayRecService.put(jsonObject3);
                } catch (Exception e) {
                    e.printStackTrace();
                }

/*end recurring service*/
                final String firstPrice = binding.etFirstPrice.getText().toString();
                final String nextPrice = binding.etNextPrice.getText().toString();
                final String firstSixInch = binding.etFirstSixInch.getText().toString();
                final String nextSixInch = binding.etNextSixInch.getText().toString();
                final String upperLimit = binding.etUpperLimit.getText().toString();

                if (TextUtils.isEmpty(firstPrice) ||
                        TextUtils.isEmpty(nextPrice) ||
                        TextUtils.isEmpty(firstSixInch) ||
                        TextUtils.isEmpty(nextSixInch) ||
                        TextUtils.isEmpty(upperLimit)) {
                    Toast.makeText(AddServiceActivity.this, "Please fill up all fields.", Toast.LENGTH_SHORT).show();
                    return;
                }

                HashMap<String, String> params = new HashMap<>();
                params.put("service_id", serviceId);
                params.put("lawn_first_acre", firstPrice);
                params.put("lawn_first_grass", firstSixInch);
                params.put("lawn_next_acre", nextPrice);
                params.put("lawn_next_grass", nextSixInch);
                params.put("lawn_acre_limit", upperLimit);
                params.put("recurring_services", jsonArrayRecService.toString());

                if (mode.equalsIgnoreCase("add")) {
                    params.put("user_id", AppData.sUserId);
                    saveService(Api.sAddService, params, binding.tvSave);
                } else if (mode.equalsIgnoreCase("edit")) {
                    params.put("added_service_id", getIntent().getExtras().getString("addedServiceId"));
                    saveService(Api.sEditService, params, binding.tvSave);
                }
            }
        });

        binding.tvPricingPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAndCloseTextview(binding.tvPricingPrev, binding.lnPrice);
                binding.lnPrice.removeAllViews();
                LeafRemovalRowBinding leafBindingFirstRow = DataBindingUtil.inflate(getLayoutInflater(), R.layout.leaf_removal_row, binding.lnPrice, true);
                leafBindingFirstRow.tvTitle1.setText("Acreage");
                leafBindingFirstRow.tvTitle1.setTextColor(getResources().getColor(R.color.colorText));
                leafBindingFirstRow.tvTitle2.setText("Grass Size");
                leafBindingFirstRow.tvTitle2.setTextColor(getResources().getColor(R.color.colorText));
                leafBindingFirstRow.tvTitle3.setText("Price");
                leafBindingFirstRow.tvTitle3.setTextColor(getResources().getColor(R.color.colorText));
                try {
                    final double firstPrice = Double.parseDouble(binding.etFirstPrice.getText().toString());
                    final double nextPrice = Double.parseDouble(binding.etNextPrice.getText().toString());
                    final double firstSixInch = Double.parseDouble(binding.etFirstSixInch.getText().toString());
                    final double nextSixInch = Double.parseDouble(binding.etNextSixInch.getText().toString());

                    final double upperLimit = Double.parseDouble(binding.etUpperLimit.getText().toString());
                    double acreagePrice = 0;
                    for (double i = 0; i < upperLimit; i = i + 0.25) {
                        if (i < 0.25)
                            acreagePrice = firstPrice;
                        else
                            acreagePrice = acreagePrice + nextPrice;
                        for (int j = 1; j <= 2; j++) {

                            LeafRemovalRowBinding leafBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.leaf_removal_row, binding.lnPrice, true);
                            leafBinding.tvTitle1.setText(i + " - " + (i + 0.25) + " acre");
                            if (j == 1) {
                                leafBinding.tvTitle2.setText("0 - 6 inch");
                                leafBinding.tvTitle3.setText("$ " + Util.getDecimalTwoPoint(acreagePrice + firstSixInch));
                            } else if (j == 2) {
                                leafBinding.tvTitle2.setText(">6 inch");
                                leafBinding.tvTitle3.setText("$ " + Util.getDecimalTwoPoint(acreagePrice + firstSixInch + nextSixInch));
                            }


                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void saveService(String url, final HashMap<String, String> params, final TextView tvSave) {
        if (!Util.isConnected(AddServiceActivity.this)) {
            Util.showSnakBar(AddServiceActivity.this, getResources().getString(R.string.internectconnectionerror), tvSave);
            return;
        }
        dialog = MyCustomProgressDialog.ctor(AddServiceActivity.this);
        dialog.setCancelable(false);
        dialog.setMessage("Please wait...");
        showpDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            public String msg;
            public String success;

            @Override
            public void onResponse(String data) {
                try {
                    JSONObject response = Util.getjsonobject(data);
                    success = response.optString("success");
                    msg = response.optString("msg");
                    Toast.makeText(AddServiceActivity.this, msg, Toast.LENGTH_SHORT).show();
                    if (success.equalsIgnoreCase("1")) {
                        mBinding.lnBack.performClick();
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
                Util.showSnakBar(AddServiceActivity.this, getResources().getString(R.string.networkerror), tvSave);
                VolleyLog.d("Error: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(AddServiceActivity.this).add(stringRequest);


    }

    private void showAndCloseTextview(TextView tvPricingPrev, LinearLayout lnPrice) {
        if (tvPricingPrev.getText().toString().equalsIgnoreCase("Pricing Preview")) {
            lnPrice.setVisibility(View.VISIBLE);
            tvPricingPrev.setText(Html.fromHtml("<u>Hide Preview</u>"));
        } else {
            lnPrice.setVisibility(View.GONE);
            tvPricingPrev.setText(Html.fromHtml("<u>Pricing Preview</u>"));
        }
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        mBinding.lnBack.performClick();
    }

    @Override
    public void onClick(View view) {
        if (view == mBinding.lnBack) {
            if (page.equalsIgnoreCase("registration")) {
                finish();
                //startActivity(new Intent(AddServiceActivity.this, RegistrationStep2Activity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            } else if (page.equalsIgnoreCase("serviceSettings")) {
                finish();
                startActivity(new Intent(AddServiceActivity.this, ServiceSettings.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        }
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
