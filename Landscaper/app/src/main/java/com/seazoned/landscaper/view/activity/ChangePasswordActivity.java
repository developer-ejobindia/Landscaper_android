package com.seazoned.landscaper.view.activity;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.seazoned.landscaper.R;
import com.seazoned.landscaper.databinding.ActivityChangePasswordBinding;
import com.seazoned.landscaper.service.api.Api;
import com.seazoned.landscaper.service.parser.PostDataParser;

import org.json.JSONObject;

import java.util.HashMap;

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityChangePasswordBinding mBinding;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding= DataBindingUtil.setContentView(this,R.layout.activity_change_password);
        mBinding.tvSubmit.setOnClickListener(this);
        mBinding.tvCancel.setOnClickListener(this);
        mBinding.lnBack.setOnClickListener(this);
        mBinding.ivHideNewPass.setOnClickListener(this);
        mBinding.ivShowNewPass.setOnClickListener(this);
        mBinding.ivHideCnfPass.setOnClickListener(this);
        mBinding.ivShowCnfPass.setOnClickListener(this);
        email = getIntent().getStringExtra("email");
    }

    @Override
    public void onClick(View v) {
        if (v == mBinding.tvSubmit) {

            String pass = mBinding.etNewPass.getText().toString();
            String CnfPass = mBinding.etCnfPass.getText().toString();

            if(TextUtils.isEmpty(pass)){

                Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(CnfPass)){
                Toast.makeText(this, "Enter confirm password", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!pass.equals(CnfPass)){

                Toast.makeText(this, "Password doesnot match", Toast.LENGTH_SHORT).show();
                return;
            }
            changePass(pass);

        } else if (v == mBinding.tvCancel) {

            finish();
        }
        else if(v==mBinding.lnBack){
            finish();
        }
        else if (v == mBinding.ivHideNewPass) {
            mBinding.ivHideNewPass.setVisibility(View.GONE);
            mBinding.ivShowNewPass.setVisibility(View.VISIBLE);
            mBinding.etNewPass.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            mBinding.etNewPass.setSelection(mBinding.etNewPass.getText().length());
        } else if (v == mBinding.ivShowNewPass) {
            mBinding.ivShowNewPass.setVisibility(View.GONE);
            mBinding.ivHideNewPass.setVisibility(View.VISIBLE);
            mBinding.etNewPass.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mBinding.etNewPass.setSelection(mBinding.etNewPass.getText().length());
        } else if (v == mBinding.ivHideCnfPass) {
            mBinding.ivHideCnfPass.setVisibility(View.GONE);
            mBinding.ivShowCnfPass.setVisibility(View.VISIBLE);
            mBinding.etCnfPass.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            mBinding.etCnfPass.setSelection(mBinding.etCnfPass.getText().length());
        } else if (v == mBinding.ivShowCnfPass) {
            mBinding.ivShowCnfPass.setVisibility(View.GONE);
            mBinding.ivHideCnfPass.setVisibility(View.VISIBLE);
            mBinding.etCnfPass.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mBinding.etCnfPass.setSelection(mBinding.etCnfPass.getText().length());
        }
    }

    private void changePass(String pass) {
        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("new_password", pass);

        new PostDataParser(ChangePasswordActivity.this, Api.sNewPass, params, true, new PostDataParser.OnGetResponseListner() {
            @Override
            public void onGetResponse(JSONObject response) {

                if (response!=null){


                    try {

                        String success,msg;
                        success=response.optString("success");
                        msg=response.optString("msg");

                        if(success.equalsIgnoreCase("1")){

                            finish();
                        }
                        Toast.makeText(ChangePasswordActivity.this, ""+msg, Toast.LENGTH_SHORT).show();

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }
        });
    }
}
