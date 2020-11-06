package com.seazoned.landscaper.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.seazoned.landscaper.R;
import com.seazoned.landscaper.app.AppData;
import com.seazoned.landscaper.databinding.ActivityEditProfileBinding;
import com.seazoned.landscaper.service.api.Api;
import com.seazoned.landscaper.service.parser.PostDataParser;
import com.seazoned.landscaper.service.preference.SharedPreferenceHelper;
import com.seazoned.landscaper.service.util.Util;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    ActivityEditProfileBinding mBinding;
    private String mAddress;
    private double lat = 0.0;
    private double lang = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);
        //Util.setPadding(this,mBinding.mainLayout);
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            Window window = getWindow(); // in Activity's onCreate() for instance
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);

            tintManager.setTintColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        SharedPreferenceHelper helper = SharedPreferenceHelper.getInstance(getApplicationContext());
        String loginType = helper.getUserLoginType();
        if (loginType.equalsIgnoreCase("appLogin")) {
            mBinding.lnChangePwd.setVisibility(View.VISIBLE);
        } else {
            mBinding.lnChangePwd.setVisibility(View.GONE);
        }

        mBinding.lnBack.setOnClickListener(this);
        mBinding.tvSave.setOnClickListener(this);
        mBinding.tvAddress.setOnClickListener(this);
        mBinding.tvChangePwd.setOnClickListener(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String fastName = bundle.getString("fname");
            String lastName = bundle.getString("lname");
            String phonenumber = bundle.getString("phonenumber");
            String ssn = bundle.getString("ssn");
            String address = bundle.getString("address");
            String latitude = bundle.getString("latitude");
            String longitude = bundle.getString("longitude");
            try {
                if (!latitude.equalsIgnoreCase("")) {
                    lat = AppData.sSearchLatitiude=Double.parseDouble(latitude);
                }
                if (!longitude.equalsIgnoreCase("")) {
                    lang =AppData.sSearchLongitiude= Double.parseDouble(longitude);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            mBinding.etFirstName.setText(fastName);
            mBinding.etLastName.setText(lastName);
            mBinding.etPhoneNumber.setText(phonenumber);
            mBinding.etSocial.setText(ssn);
            mBinding.tvAddress.setText(address);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == mBinding.lnBack) {
            finish();
            startActivity(new Intent(EditProfileActivity.this, ViewProfileActivity.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else if (view == mBinding.tvAddress) {

            try {
            /* Intent intent =
                     new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                             .build(this);
             startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);*/

                /*Intent intents = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                        .build(EditProfileActivity.this);
                startActivityForResult(intents, PLACE_AUTOCOMPLETE_REQUEST_CODE);*/
                Intent intent=new Intent(EditProfileActivity.this,LocationSearchActivity.class);
                startActivityForResult(intent,PLACE_AUTOCOMPLETE_REQUEST_CODE);
            } catch (Exception e) {
                // TODO: Handle the error.
            }

        } else if (view == mBinding.tvSave) {
            String mFirstname = mBinding.etFirstName.getText().toString();
            String mLastName = mBinding.etLastName.getText().toString();
            String mPhoneNo = mBinding.etPhoneNumber.getText().toString();
            String social = mBinding.etSocial.getText().toString();
            String mAddress = mBinding.tvAddress.getText().toString();
            if (TextUtils.isEmpty(mFirstname)) {
                Toast.makeText(this, "Enter first name.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(mLastName)) {
                Toast.makeText(this, "Enter last name.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(mPhoneNo)) {
                Toast.makeText(this, "Enter phone number.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(social)) {
                Toast.makeText(this, "Enter social security  number.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Util.isValidPhoneNumber(mPhoneNo)) {
                Toast.makeText(this, "Enter valid phone number.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(mAddress)) {
                Toast.makeText(this, "Enter address.", Toast.LENGTH_SHORT).show();
                return;
            }

            HashMap<String, String> params = new HashMap<>();
            params.put("first_name", mFirstname);
            params.put("last_name", mLastName);
            params.put("tel", mPhoneNo);
            params.put("ssn_no", social);
            params.put("address", mAddress);
            params.put("latitude", "" + lat);
            params.put("longitude", "" + lang);
            getUserRegisterInfo(params);
        } else if (view == mBinding.tvChangePwd) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
            builder.setCancelable(false);
            final View v = getLayoutInflater().inflate(R.layout.change_password, null);
            final EditText etOldPwd = (EditText) v.findViewById(R.id.etOldPassword);
            final EditText etNewPwd = (EditText) v.findViewById(R.id.etNewPassword);
            final EditText etCnfPwd = (EditText) v.findViewById(R.id.etCnfPassword);
            final TextView tvCancel = (TextView) v.findViewById(R.id.tvCancel);
            final TextView tvsave = (TextView) v.findViewById(R.id.tvUpdate);


            builder.setView(v);
            builder.setCancelable(false);
            final AlertDialog mAlert = builder.create();
            mAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAlert.cancel();
                }
            });
            tvsave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String mOldPwd = etOldPwd.getText().toString();
                    String mNewPwd = etNewPwd.getText().toString();
                    String mConfPwd = etCnfPwd.getText().toString();

                    if (TextUtils.isEmpty(mOldPwd)) {
                        Toast.makeText(EditProfileActivity.this, "Input old password.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(mNewPwd)) {
                        Toast.makeText(EditProfileActivity.this, "Input new password.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(mConfPwd)) {
                        Toast.makeText(EditProfileActivity.this, "Input confirm password.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!mNewPwd.equals(mConfPwd)) {
                        Toast.makeText(EditProfileActivity.this, "Password does not match.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    HashMap<String, String> params = new HashMap<>();
                    params.put("old_pw", mOldPwd);
                    params.put("new_pw", mNewPwd);
                    params.put("conf_pw", mConfPwd);

                    new PostDataParser(EditProfileActivity.this, Api.sChangePassword, params, true, new PostDataParser.OnGetResponseListner() {
                        public String msg;
                        public String success;

                        @Override
                        public void onGetResponse(JSONObject response) {

                            if (response != null) {
                                try {
                                    success = response.optString("success");
                                    msg = response.optString("msg");
                                    if (success.equalsIgnoreCase("1")) {
                                        mAlert.cancel();
                                    }
                                    Toast.makeText(EditProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
            });
            mAlert.show();

        }
    }


    private void getUserRegisterInfo(final HashMap<String, String> params) {
        new PostDataParser(EditProfileActivity.this, Api.sUserInfoEdit, params, true, mBinding.tvSave, new PostDataParser.OnGetResponseListner() {
            public String mStatus;
            public String mSuccess;

            @Override
            public void onGetResponse(JSONObject response) {
                if (response != null) {
                    try {
                        mSuccess = response.optString("success");
                        mStatus = response.optString("msg");
                        if (mSuccess.equalsIgnoreCase("1")) {
                            mBinding.lnBack.performClick();
                        } else {
                            Toast.makeText(EditProfileActivity.this, "" + mStatus, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        mBinding.lnBack.performClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {

            //if (resultCode==RESULT_OK){

            mAddress = AppData.sAddress;

            try {
                lat=AppData.sSearchLatitiude;
                lang=AppData.sSearchLongitiude;
                Geocoder geocoder = new Geocoder(this);
                // String locationName = loc.getText().toString();
                List<Address> addresses = geocoder.getFromLocationName(
                        mAddress, 5);
                mAddress = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()


                Log.i("address", mAddress);

                mBinding.tvAddress.setText(mAddress);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        /*if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i("Place:", String.valueOf(place.getLatLng()));
                Log.i("data", place.toString());
                /*//********Lat lan wise address
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(this, Locale.getDefault());
                LatLng location = place.getLatLng();
                try {
                    lat = location.latitude;
                    lang = location.longitude;
                    addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    mAddress = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    *//*mCity = addresses.get(0).getLocality();
                    mState = addresses.get(0).getAdminArea();
                    mCountry = addresses.get(0).getCountryName();
                    mPostalCode = addresses.get(0).getPostalCode();*//*

                    Log.i("address", mAddress);

                    mBinding.tvAddress.setText(mAddress);


                } catch (IOException e) {
                    e.printStackTrace();
                }


                /*//************

                //finish();
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("Placeerr:", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }*/
        }
    }

