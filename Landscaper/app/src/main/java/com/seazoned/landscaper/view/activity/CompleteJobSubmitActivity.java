package com.seazoned.landscaper.view.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.seazoned.landscaper.R;
import com.seazoned.landscaper.app.AppData;
import com.seazoned.landscaper.databinding.ActivityCompleteJobSubmitBinding;
import com.seazoned.landscaper.service.api.Api;
import com.seazoned.landscaper.service.parser.PostDataParser;
import com.seazoned.landscaper.service.util.Util;

import org.json.JSONObject;

import java.util.HashMap;

public class CompleteJobSubmitActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityCompleteJobSubmitBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding= DataBindingUtil.setContentView(this,R.layout.activity_complete_job_submit);
        Util.setPadding(this, mBinding.mainLayout);
        mBinding.tvCompleteJob.setOnClickListener(this);
        mBinding.lnCompleteImage.setOnClickListener(this);
        AppData.sCompletejobFlag=0;
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppData.sCompleteImage!=null){
            mBinding.ivComplete.setImageBitmap(AppData.sCompleteImage);
        }
    }

    @Override
    public void onClick(View view) {
        if (view==mBinding.lnCompleteImage){
            //AppData.sCompletejobFlag=0;
            startActivity(new Intent(CompleteJobSubmitActivity.this, ImageUploadActivity.class)
                    .putExtra("fromLocation", "CompleteJobSubmitActivity")
            );
        }
        else if (view==mBinding.tvCompleteJob){
            if (AppData.sCompletejobFlag==1) {

                final HashMap<String,String> params=new HashMap<>();
                params.put("order_id",AppData.sBookingId);
                params.put("completion_time",Util.getSystemDateTime());
                new PostDataParser(CompleteJobSubmitActivity.this, Api.sEndJobLandscaper, params, true, new PostDataParser.OnGetResponseListner() {
                    String success;

                    @Override

                    public void onGetResponse(JSONObject response) {
                        if(response!=null){
                            try{
                                success=response.optString("success");
                                if(success.equalsIgnoreCase("1")){
                                    finish();
                                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                                }
                            }
                            catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                });


            }
            else {
                Toast.makeText(this, "Please upload at least 1 photo", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
