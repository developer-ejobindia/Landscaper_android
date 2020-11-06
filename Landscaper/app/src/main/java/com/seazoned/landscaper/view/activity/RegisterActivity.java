package com.seazoned.landscaper.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.seazoned.landscaper.R;
import com.seazoned.landscaper.app.AppData;
import com.seazoned.landscaper.databinding.ActivityRegisterBinding;
import com.seazoned.landscaper.other.FilePath;
import com.seazoned.landscaper.other.MyCustomProgressDialog;
import com.seazoned.landscaper.service.api.Api;
import com.seazoned.landscaper.service.parser.DataPart;
import com.seazoned.landscaper.service.parser.VolleyMultipartParser;
import com.seazoned.landscaper.service.util.Util;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityRegisterBinding mBinding;
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private String mAddress;
    private String mCity;
    private String mState;
    private String mCountry;
    private String mPostalCode;
    private ProgressDialog dialog;
    private double lat = 0.0;
    private double lang = 0.0;

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
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        //Util.setPadding(this, mBinding.mainLayout);
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            Window window = getWindow(); // in Activity's onCreate() for instance
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);

            tintManager.setTintColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        mBinding.tvClickhere.setOnClickListener(this);
        mBinding.lnAddress.setOnClickListener(this);
        mBinding.tvSignUp.setOnClickListener(this);
        mBinding.ivHidePwd.setOnClickListener(this);
        mBinding.ivHideCnfPwd.setOnClickListener(this);
        mBinding.ivShowCnfPwd.setOnClickListener(this);
        mBinding.ivShowPwd.setOnClickListener(this);
        mBinding.tvTerms.setOnClickListener(this);
        mBinding.tvUpload.setOnClickListener(this);
        AppData.selectedFileUri=null;
    }

    @Override
    public void onClick(View view) {
        if (view == mBinding.tvClickhere) {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else if (view == mBinding.lnAddress) {

            try {
            /* Intent intent =
                     new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                             .build(this);
             startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);*/

                /*Intent intents = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                        .build(RegisterActivity.this);
                startActivityForResult(intents, PLACE_AUTOCOMPLETE_REQUEST_CODE);*/
                Intent intent = new Intent(RegisterActivity.this, LocationSearchActivity.class);
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
            } catch (Exception e) {
                // TODO: Handle the error.
            }

        }
        else if (view==mBinding.tvUpload){
            Intent intent=new Intent(RegisterActivity.this,SelectFileActivity.class);
            intent.putExtra("type", "image");
            startActivityForResult(intent,100);
        }

        else if (view == mBinding.tvSignUp) {
            String mFirstname = mBinding.etFirstName.getText().toString();
            String mLastName = mBinding.etLastName.getText().toString();
            String mEmail = mBinding.etEmail.getText().toString();
            String mPhoneNo = mBinding.etPhoneNumber.getText().toString();
            String ssn = mBinding.etSocial.getText().toString();
            String mLocation = mBinding.tvAddress.getText().toString();

            String mPassword = mBinding.etPassword.getText().toString();
            String mCnfPassword = mBinding.etConfirmPassword.getText().toString();
            if (TextUtils.isEmpty(mFirstname)) {
                Toast.makeText(this, "Enter first name.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(mLastName)) {
                Toast.makeText(this, "Enter last name.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(mEmail)) {
                Toast.makeText(this, "Enter email.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Util.isValidEmail(mEmail)) {
                Toast.makeText(this, "Enter valid.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(mPhoneNo)) {
                Toast.makeText(this, "Enter phone number.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Util.isValidPhoneNumber(mPhoneNo)) {
                Toast.makeText(this, "Enter valid phone number.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(ssn)) {
                Toast.makeText(this, "Enter social security number.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (AppData.selectedFileUri==null) {
                Toast.makeText(this, "Choose driving license image", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(mLocation)) {
                Toast.makeText(this, "Enter street/location name.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(mPassword)) {
                Toast.makeText(this, "Enter password.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(mCnfPassword)) {
                Toast.makeText(this, "Enter confirm password.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!mPassword.equals(mCnfPassword)) {
                Toast.makeText(this, "Password does not match.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!mBinding.cbTerms.isChecked()){
                Toast.makeText(this, "Sorry, you must accept the Terms and Conditions", Toast.LENGTH_SHORT).show();
                return;
            }

            HashMap<String, String> params = new HashMap<>();
            params.put("first_name", mFirstname);
            params.put("last_name", mLastName);
            params.put("email", mEmail);
            params.put("tel", mPhoneNo);
            params.put("ssn_no", ssn);
            params.put("street", mLocation);
            params.put("city", mCity);
            params.put("state", mState);
            params.put("country", "193");
            params.put("password", mCnfPassword);
            params.put("latitude", "" + lat);
            params.put("longitude", "" + lang);
            params.put("source", "android");
            getUserRegisterInfo(params, mFirstname, mLastName);

        }
        else if (view == mBinding.ivHidePwd) {
            mBinding.ivHidePwd.setVisibility(View.GONE);
            mBinding.ivShowPwd.setVisibility(View.VISIBLE);
            mBinding.etPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            mBinding.etPassword.setSelection(mBinding.etPassword.getText().length());
        }
        else if (view == mBinding.ivShowPwd) {
            mBinding.ivShowPwd.setVisibility(View.GONE);
            mBinding.ivHidePwd.setVisibility(View.VISIBLE);
            mBinding.etPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mBinding.etPassword.setSelection(mBinding.etPassword.getText().length());
        }
        else if (view == mBinding.ivHideCnfPwd) {
            mBinding.ivHideCnfPwd.setVisibility(View.GONE);
            mBinding.ivShowCnfPwd.setVisibility(View.VISIBLE);
            mBinding.etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            mBinding.etConfirmPassword.setSelection(mBinding.etConfirmPassword.getText().length());
        }
        else if (view == mBinding.ivShowCnfPwd) {
            mBinding.ivShowCnfPwd.setVisibility(View.GONE);
            mBinding.ivHideCnfPwd.setVisibility(View.VISIBLE);
            mBinding.etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mBinding.etConfirmPassword.setSelection(mBinding.etConfirmPassword.getText().length());
        }
        else if (view==mBinding.tvTerms){
            Intent intent=new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://www.seazoned.com/provider-terms-conditions"));
            startActivity(intent);
        }
    }


    private void getUserRegisterInfo(final HashMap<String, String> params, final String fname, final String lname) {
        Map<String, DataPart> files = new HashMap<>();
        if (AppData.selectedFileUri != null) {
            try {
                InputStream iStream = RegisterActivity.this.getContentResolver().openInputStream(AppData.selectedFileUri);
                byte[] inputData = Util.getBytes(iStream);
                String path = FilePath.getPath(RegisterActivity.this, AppData.selectedFileUri);
                final String ext = path.substring(path.lastIndexOf(".") + 1, path.length());
                DataPart dataPart = new DataPart("Landscaper" + "." + ext, inputData);
                files.put("drivers_license", dataPart);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            new VolleyMultipartParser(RegisterActivity.this, Api.sUserRegistration, params, files, true,true, new VolleyMultipartParser.OnGetResponseListner() {
                @Override
                public void onGetResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            String mSuccess = response.optString("success");
                            String mStatus = response.optString("msg");
                            Toast.makeText(RegisterActivity.this, "" + mStatus, Toast.LENGTH_SHORT).show();
                            if (mSuccess.equalsIgnoreCase("1")) {
                                AppData.sUserId = response.optJSONObject("data").optString("user_id");
                                finish();
                                AppData.lat = lat;
                                AppData.lang = lang;
                                AppData.type = "registration";
                                startActivity(new Intent(RegisterActivity.this, RegistrationStep2Activity.class)
                                        .putExtra("name", fname + " " + lname)

                                );
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            });
        }

        /*StringRequest postRequest = new StringRequest(Request.Method.POST, Api.sUserRegistration,
                new Response.Listener<String>() {
                    public String mStatus;
                    public String mSuccess;

                    @Override
                    public void onResponse(String data) {
                        try {
                            Util util = new Util();
                            JSONObject response = util.getjsonobject(data);
                            mSuccess = response.optString("success");
                            mStatus = response.optString("msg");
                            Toast.makeText(RegisterActivity.this, "" + mStatus, Toast.LENGTH_SHORT).show();
                            if (mSuccess.equalsIgnoreCase("1")) {
                                AppData.sUserId = response.optJSONObject("data").optString("user_id");
                                finish();
                                AppData.lat = lat;
                                AppData.lang = lang;
                                AppData.type = "registration";
                                startActivity(new Intent(RegisterActivity.this, RegistrationStep2Activity.class)
                                        .putExtra("name", fname + " " + lname)

                                );
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
                Util.showSnakBar(RegisterActivity.this, getResources().getString(R.string.networkerror));
                VolleyLog.d("Error: " + error.getMessage());

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(RegisterActivity.this).add(postRequest);*/


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {

            //if (resultCode==RESULT_OK){

            mAddress = AppData.sAddress;
            lat = AppData.sSearchLatitiude;
            lang = AppData.sSearchLongitiude;
            try {
                Geocoder geocoder = new Geocoder(this);
                // String locationName = loc.getText().toString();
                List<Address> addresses = geocoder.getFromLocationName(
                        mAddress, 5);
                mAddress = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                mCity = addresses.get(0).getLocality();
                mState = addresses.get(0).getAdminArea();
                mCountry = addresses.get(0).getCountryName();
                mPostalCode = addresses.get(0).getPostalCode();

                Log.i("address", mAddress);

                mBinding.tvAddress.setText(mAddress);
                mBinding.tvCity.setText(mCity);
                mBinding.tvState.setText(mState);
                mBinding.tvCountry.setText(mCountry);
            } catch (Exception e) {
                e.printStackTrace();
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
             lat=location.latitude;
             lang=location.longitude;
             addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
             mAddress = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
             mCity = addresses.get(0).getLocality();
             mState = addresses.get(0).getAdminArea();
             mCountry = addresses.get(0).getCountryName();
             mPostalCode = addresses.get(0).getPostalCode();

             Log.i("address", mAddress);

             mBinding.tvAddress.setText(mAddress);
             mBinding.tvCity.setText(mCity);
             mBinding.tvState.setText(mState);
             mBinding.tvCountry.setText(mCountry);

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
        if (requestCode==100){
            try {
                if (AppData.selectedFileUri!=null){
                    String path = FilePath.getPath(RegisterActivity.this, AppData.selectedFileUri);
                    final String ext = path.substring(path.lastIndexOf(".") + 1, path.length());
                    String fileName=Util.getFileName(path);
                    //to do
                    mBinding.tvImage.setText(fileName);

                    if(ext.equalsIgnoreCase("jpg")||
                            ext.equalsIgnoreCase("jpeg")||
                            ext.equalsIgnoreCase("png")) {
                        mBinding.ivDriving.setImageURI(AppData.selectedFileUri);
                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        mBinding.tvClickhere.performClick();
    }
}
