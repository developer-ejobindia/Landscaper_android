package com.seazoned.landscaper.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


import com.seazoned.landscaper.R;
import com.seazoned.landscaper.databinding.ActivityContactUsBinding;
import com.seazoned.landscaper.service.api.Api;
import com.seazoned.landscaper.service.parser.GetDataParser;
import com.seazoned.landscaper.service.parser.PostDataParser;
import com.seazoned.landscaper.service.util.Util;

import org.json.JSONObject;

import java.util.HashMap;

public class ContactUs extends AppCompatActivity implements View.OnClickListener {
    ActivityContactUsBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_contact_us);
        mBinding.tvSend.setOnClickListener(this);
        //mBinding.etMessage.setMovementMethod(new ScrollingMovementMethod());
        mBinding.etMessage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.etMessage) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });
        mBinding.lnNotificationBell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ContactUs.this, Notification.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        });
        mBinding.lnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBinding.drawer.openDrawer(Gravity.LEFT);
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == mBinding.tvSend) {
            String firstName = mBinding.etFirstName.getText().toString();
            String lastName = mBinding.etLastName.getText().toString();
            String email = mBinding.etEmail.getText().toString();
            String message = mBinding.etMessage.getText().toString();
            if (TextUtils.isEmpty(firstName)) {
                Toast.makeText(this, "Enter first name.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(lastName)) {
                Toast.makeText(this, "Enter last name.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Enter email.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Util.isValidEmail(email)) {
                Toast.makeText(this, "Enter valid email id.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(message)) {
                Toast.makeText(this, "Write something..", Toast.LENGTH_SHORT).show();
                return;
            }
            HashMap<String, String> params = new HashMap<>();
            params.put("name", firstName + " " + lastName);
            params.put("email", email);
            params.put("desc", message);
            saveData(params);
        }
    }

    private void saveData(HashMap<String, String> params) {
        new PostDataParser(ContactUs.this, Api.sContactUs, params, true, true, new PostDataParser.OnGetResponseListner() {
            public String msg;
            public String success;

            @Override
            public void onGetResponse(JSONObject response) {
                try {
                    if (response != null) {
                        success = response.optString("success");
                        msg = response.optString("msg");
                        //Toast.makeText(ContactUs.this, msg, Toast.LENGTH_SHORT).show();

                        AlertDialog.Builder builder=new AlertDialog.Builder(ContactUs.this);
                        builder.setMessage(msg);
                        builder.setCancelable(false);
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (success.equalsIgnoreCase("1")) {
                                    onBackPressed();
                                }
                            }
                        });
                        builder.show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(ContactUs.this, DashBoardActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getNotiStatus();
    }
    private void getNotiStatus() {

        new GetDataParser(ContactUs.this, Api.sGetNotificationStatus, true, new GetDataParser.OnGetResponseListner() {
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
