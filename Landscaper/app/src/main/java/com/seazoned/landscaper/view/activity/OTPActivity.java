package com.seazoned.landscaper.view.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.seazoned.landscaper.R;
import com.seazoned.landscaper.databinding.ActivityOtpBinding;
import com.seazoned.landscaper.service.api.Api;
import com.seazoned.landscaper.service.parser.PostDataParser;

import org.json.JSONObject;

import java.util.HashMap;

public class OTPActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityOtpBinding mBinding;
    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_otp);
        mBinding.tvSubmit.setOnClickListener(this);
        mBinding.tvCancel.setOnClickListener(this);
        mBinding.lnBack.setOnClickListener(this);
        email=getIntent().getStringExtra("email");
    }

    @Override
    public void onClick(View v) {
        if (v == mBinding.tvSubmit) {
            String otp = mBinding.etOtp.getText().toString();
            if(TextUtils.isEmpty(otp)){

                Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show();
                return;
            }
            generateOtp(otp);

        } else if (v == mBinding.tvCancel) {

            finish();
        }
        else if(v==mBinding.lnBack){
            finish();
        }
    }

    private void generateOtp(String otp) {

        HashMap<String,String> params=new HashMap<>();
        params.put("email",email);
        params.put("otp",otp);

        new PostDataParser(OTPActivity.this, Api.sOTP, params, true, new PostDataParser.OnGetResponseListner() {
            @Override
            public void onGetResponse(JSONObject response) {

                if (response!=null){

                    try {

                        String success=response.optString("success");
                        String msg=response.optString("msg");

                        if (success.equalsIgnoreCase("1")){

                            finish();
                            startActivity(new Intent(OTPActivity.this, ChangePasswordActivity.class).putExtra("email",email));
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                        Toast.makeText(OTPActivity.this, ""+msg, Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }
        });
    }
}
