package com.seazoned.landscaper.view.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.seazoned.landscaper.R;
import com.seazoned.landscaper.app.AppData;
import com.seazoned.landscaper.databinding.ActivityViewProfileBinding;
import com.seazoned.landscaper.model.User;
import com.seazoned.landscaper.other.FilePath;
import com.seazoned.landscaper.service.api.Api;
import com.seazoned.landscaper.service.parser.DataPart;
import com.seazoned.landscaper.service.parser.GetDataParser;
import com.seazoned.landscaper.service.parser.VolleyMultipartParser;
import com.seazoned.landscaper.service.preference.SharedPreferenceHelper;
import com.seazoned.landscaper.service.util.Util;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class ViewProfileActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityViewProfileBinding mBinding;
    private String firstName="";
    private String lastName="";
    private String phoneNumber="";
    private String email="";
    private String address="";
    private String latitude="";
    private String longitude="";
    private String drivingLicensePath="";
    private ImageView ivLicense=null;
    private String ssn="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding= DataBindingUtil.setContentView(this, R.layout.activity_view_profile);
        Util.setPadding(this,mBinding.mainLayout);
        mBinding.lnBack.setOnClickListener(this);
        mBinding.tvEditProfile.setOnClickListener(this);
        mBinding.tvPicture.setOnClickListener(this);
        mBinding.lnDriving.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserInfo();

    }

    @Override
    public void onClick(View view) {
        if (view==mBinding.lnBack){
            finish();
            startActivity(new Intent(ViewProfileActivity.this,ProfileActivity.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
        else if (view==mBinding.tvEditProfile){
            finish();
            startActivity(new Intent(ViewProfileActivity.this,EditProfileActivity.class)
                    .putExtra("fname", firstName)
                    .putExtra("lname", lastName)
                    .putExtra("address", address)
                    .putExtra("phonenumber", phoneNumber)
                    .putExtra("ssn", ssn)
                    .putExtra("latitude", latitude)
                    .putExtra("longitude", longitude));

            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        }
        else if (view==mBinding.tvPicture){
            //finish();
            startActivity(new Intent(ViewProfileActivity.this,ImageUploadActivity.class)
                    .putExtra("fromLocation","ViewProfileActivity")
            );
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        }
        else if (view==mBinding.lnDriving){
            AppData.selectedFileUri=null;
            AlertDialog.Builder builder=new AlertDialog.Builder(ViewProfileActivity.this);
            View v=getLayoutInflater().inflate(R.layout.driving_license,null);
            builder.setView(v);
            final AlertDialog dialog=builder.create();

            TextView tvUpdate=v.findViewById(R.id.tvUpdate);
            LinearLayout lnLicenseImage=v.findViewById(R.id.lnLicenseImage);
            ivLicense=v.findViewById(R.id.ivLicense);
            if (!TextUtils.isEmpty(drivingLicensePath)){
                Picasso.with(ViewProfileActivity.this).load(drivingLicensePath).error(R.mipmap.feature_image).placeholder(R.mipmap.feature_image).into(ivLicense);
            }
            lnLicenseImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(ViewProfileActivity.this,SelectFileActivity.class);
                    intent.putExtra("type", "image");
                    startActivityForResult(intent,100);
                }
            });
            tvUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Map<String, DataPart> files = new HashMap<>();
                    if (AppData.selectedFileUri != null) {
                        try {
                            InputStream iStream = ViewProfileActivity.this.getContentResolver().openInputStream(AppData.selectedFileUri);
                            byte[] inputData = Util.getBytes(iStream);
                            String path = FilePath.getPath(ViewProfileActivity.this, AppData.selectedFileUri);
                            final String ext = path.substring(path.lastIndexOf(".") + 1, path.length());
                            DataPart dataPart = new DataPart("Landscaper" + "." + ext, inputData);
                            files.put("drivers_license", dataPart);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        HashMap<String, String> params = new HashMap<>();
                        params.put("user_id",AppData.sUserId);
                        params.put("source","android");
                        new VolleyMultipartParser(ViewProfileActivity.this, Api.sUpdateLicense, params, files, true, false, new VolleyMultipartParser.OnGetResponseListner() {
                            @Override
                            public void onGetResponse(JSONObject response) {
                                if (response != null) {
                                    try {
                                        String mSuccess = response.optString("success");
                                        String mStatus = response.optString("msg");
                                        Toast.makeText(ViewProfileActivity.this, "" + mStatus, Toast.LENGTH_SHORT).show();
                                        if (mSuccess.equalsIgnoreCase("1")) {
                                            dialog.dismiss();
                                            getUserInfo();
                                        }


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                        });
                    }
                    else {
                        dialog.dismiss();
                    }
                }
            });

            dialog.show();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==100){
            try {
                if (AppData.selectedFileUri!=null){
                    String path = FilePath.getPath(ViewProfileActivity.this, AppData.selectedFileUri);
                    final String ext = path.substring(path.lastIndexOf(".") + 1, path.length());
                    String fileName=Util.getFileName(path);
                    //to do

                    if(ext.equalsIgnoreCase("jpg")||
                            ext.equalsIgnoreCase("jpeg")||
                            ext.equalsIgnoreCase("png")) {
                        if (ivLicense!=null)
                        ivLicense.setImageURI(AppData.selectedFileUri);
                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        mBinding.lnBack.performClick();
    }
    private void getUserInfo() {
        new GetDataParser(ViewProfileActivity.this, Api.sUserInfo, true, new GetDataParser.OnGetResponseListner() {
            public String mStatus;
            public String mSuccess;

            @Override
            public void onGetResponse(JSONObject response) {
                if (response != null) {
                    try {
                        mSuccess = response.optString("success");
                        mStatus = response.optString("msg");
                        if (mSuccess.equalsIgnoreCase("1")) {
                            JSONObject data = response.getJSONObject("data");

                            firstName = data.optString("first_name");
                            lastName = data.optString("last_name");

                            SharedPreferenceHelper helper=SharedPreferenceHelper.getInstance(ViewProfileActivity.this);
                            User user=new User(AppData.sUserId,AppData.sToken,firstName,lastName,helper.getUserLoginType());
                            helper.saveUserInfo(user);

                            email = data.optString("email");
                            phoneNumber = data.optString("phone_number");
                            address = data.optString("address");
                            latitude = data.optString("landscaper_latitude");
                            longitude = data.optString("landscaper_longitude");
                            final String profileImage = data.optString("profile_image");
                            String featureImage = data.optString("featured_image");

                            if (!profileImage.equalsIgnoreCase("") && !profileImage.equalsIgnoreCase("null") && profileImage != null) {
                                mBinding.ivProfilePic.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Util.getPopUpWindow(ViewProfileActivity.this,profileImage);
                                    }
                                });
                                Picasso.with(ViewProfileActivity.this).load(profileImage).error(R.mipmap.temp).into(mBinding.ivProfilePic);
                            } else {
                                Picasso.with(ViewProfileActivity.this).load(R.mipmap.temp).into(mBinding.ivProfilePic);
                            }

                            //feature image
                            if (!featureImage.equalsIgnoreCase("") && !featureImage.equalsIgnoreCase("null") && featureImage != null) {
                                Picasso.with(ViewProfileActivity.this).load(featureImage).error(R.mipmap.featurepic).into(mBinding.ivFeatureImage);
                            } else {
                                Picasso.with(ViewProfileActivity.this).load(R.mipmap.featurepic).into(mBinding.ivFeatureImage);
                            }

                            mBinding.tvName.setText(firstName + " " + lastName);
                            mBinding.tvCellPhone.setText(phoneNumber);
                            mBinding.tvEmail.setText(email);
                            mBinding.tvaddress.setText(address);
                            mBinding.tvSSN.setText(ssn=data.optString("ssn_no"));
                            drivingLicensePath=data.optString("drivers_license");
                            mBinding.tvDrivingLicense.setText(Util.getFileName(drivingLicensePath));



                        } else {
                            Toast.makeText(ViewProfileActivity.this, "" + mStatus, Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

}
