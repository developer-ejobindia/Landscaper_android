package com.seazoned.landscaper.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.seazoned.landscaper.R;
import com.seazoned.landscaper.app.AppData;
import com.seazoned.landscaper.databinding.ActivityProfileBinding;
import com.seazoned.landscaper.model.User;
import com.seazoned.landscaper.service.api.Api;
import com.seazoned.landscaper.service.parser.GetDataParser;
import com.seazoned.landscaper.service.preference.SharedPreferenceHelper;
import com.seazoned.landscaper.service.util.Util;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;


public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityProfileBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        mBinding.lnViewProfile.setOnClickListener(this);
        mBinding.lnBookingHistory.setOnClickListener(this);
        mBinding.lnPayment.setOnClickListener(this);
        mBinding.lnLogout.setOnClickListener(this);
        mBinding.lnFeatureImage.setOnClickListener(this);
        mBinding.lnServiceSettings.setOnClickListener(this);
        mBinding.lnNotificationBell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, Notification.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });
        mBinding.lnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.drawer.openDrawer(Gravity.LEFT);
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBinding.ivFeatureImage.setClipToOutline(true);
        }
        Util.setPadding(this, mBinding.mainLayout);
        SharedPreferenceHelper helper = SharedPreferenceHelper.getInstance(ProfileActivity.this);
        mBinding.tvName.setText(helper.getUserFastName() + " " + helper.getUserLastName());

    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserInfo();
        getNotiStatus();
    }


    @Override
    public void onClick(View view) {
        if (view == mBinding.lnViewProfile) {
            finish();
            startActivity(new Intent(ProfileActivity.this, ViewProfileActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (view == mBinding.lnServiceSettings) {
            finish();
            startActivity(new Intent(ProfileActivity.this, ServiceSettings.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (view == mBinding.lnBookingHistory) {
            finish();
            startActivity(new Intent(ProfileActivity.this, BookingHistoryActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (view == mBinding.lnPayment) {
            finish();
            startActivity(new Intent(ProfileActivity.this, PaymentInfoActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (view == mBinding.lnFeatureImage) {
            //finish();
            startActivity(new Intent(ProfileActivity.this, ImageUploadActivity.class)
                    .putExtra("fromLocation", "ProfileActivity")
            );
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (view == mBinding.lnLogout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
            builder.setCancelable(true);
            builder.setMessage(getResources().getString(R.string.logout));
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferenceHelper helper = SharedPreferenceHelper.getInstance(ProfileActivity.this);
                    if (helper.clearData()) {
                        try {
                            Profile p = Profile.getCurrentProfile();
                            if (p != null) {
                                LoginManager.getInstance().logOut();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        startActivity(new Intent(ProfileActivity.this, LoginActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK)
                        );
                        finish();
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finish();
        startActivity(new Intent(ProfileActivity.this, DashBoardActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void getUserInfo() {
        new GetDataParser(ProfileActivity.this, Api.sUserInfo, true, new GetDataParser.OnGetResponseListner() {
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

                            final String profileImage = data.optString("profile_image");
                            final String featureImage = data.optString("featured_image");

                            if (!profileImage.equalsIgnoreCase("") && !profileImage.equalsIgnoreCase("null") && profileImage != null) {
                                mBinding.ivProfilePic.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Util.getPopUpWindow(ProfileActivity.this, profileImage);
                                    }
                                });
                                Picasso.with(ProfileActivity.this).load(profileImage).resize(200, 200).error(R.mipmap.temp).into(mBinding.ivProfilePic);
                            } else {
                                Picasso.with(ProfileActivity.this).load(R.mipmap.temp).into(mBinding.ivProfilePic);
                            }
                            //feature image
                            if (!featureImage.equalsIgnoreCase("") && !featureImage.equalsIgnoreCase("null") && featureImage != null) {
                                mBinding.ivFeatureImage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Util.getPopUpWindow(ProfileActivity.this, featureImage);
                                    }
                                });
                                Picasso.with(ProfileActivity.this).load(featureImage).error(R.mipmap.featurepic).into(mBinding.ivFeatureImage);
                            } else {
                                Picasso.with(ProfileActivity.this).load(R.mipmap.featurepic).into(mBinding.ivFeatureImage);
                            }

                        } else {
                            Toast.makeText(ProfileActivity.this, "" + mStatus, Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private void getNotiStatus() {

        new GetDataParser(ProfileActivity.this, Api.sGetNotificationStatus, true, new GetDataParser.OnGetResponseListner() {
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
