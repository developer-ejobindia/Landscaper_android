package com.seazoned.landscaper.view.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.seazoned.landscaper.R;
import com.seazoned.landscaper.databinding.ActivityForgotPassWordBinding;
import com.seazoned.landscaper.service.api.Api;
import com.seazoned.landscaper.service.parser.PostDataParser;

import org.json.JSONObject;

import java.util.HashMap;

public class ForgotPassWordActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityForgotPassWordBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_pass_word);
        mBinding.tvSubmit.setOnClickListener(this);
        mBinding.tvCancel.setOnClickListener(this);
        mBinding.lnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v == mBinding.tvSubmit) {
            String mail = mBinding.etEmail.getText().toString();
            if(TextUtils.isEmpty(mail)){

                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show();
                return;
            }
            forgotPass(mail);

        } else if (v == mBinding.tvCancel) {

            finish();
        }
        else if(v==mBinding.lnBack){
            finish();
        }

    }

    private void forgotPass(final String mail) {

        HashMap<String, String> params = new HashMap<>();
        params.put("email", mail);
        params.put("profile_id", "3");
        new PostDataParser(ForgotPassWordActivity.this, Api.sForgotPass, params, true, new PostDataParser.OnGetResponseListner() {
            @Override
            public void onGetResponse(JSONObject response) {

                if (response != null) {


                    try {

                        String success, msg;
                        success = response.optString("success");
                        msg = response.optString("msg");
                        if (success.equalsIgnoreCase("1")) {

                            finish();
                            startActivity(new Intent(ForgotPassWordActivity.this, OTPActivity.class).putExtra("email",mail));
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                        Toast.makeText(ForgotPassWordActivity.this, "" + msg, Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                }

            }
        });

    }
}
