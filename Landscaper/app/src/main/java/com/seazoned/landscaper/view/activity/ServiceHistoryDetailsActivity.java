package com.seazoned.landscaper.view.activity;

import android.content.Intent;
import android.database.DatabaseUtils;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.AccordionTransformer;
import com.seazoned.landscaper.R;
import com.seazoned.landscaper.app.AppData;
import com.seazoned.landscaper.databinding.ActivityServiceHistoryDetailsBinding;
import com.seazoned.landscaper.databinding.ServiceHistoryDetailsRowBinding;
import com.seazoned.landscaper.service.api.Api;
import com.seazoned.landscaper.service.parser.PostDataParser;
import com.seazoned.landscaper.service.util.Util;
import com.seazoned.landscaper.view.adapter.RcvImageOneAdapter;
import com.seazoned.landscaper.view.adapter.SlidingImage_Adapter;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;



public class ServiceHistoryDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityServiceHistoryDetailsBinding mBinding;
    private ArrayList<HashMap<String, String>> mCardList;
    private String landscaper_id;
    int currentPage;
    int NUM_PAGES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_service_history_details);
        Util.setPadding(this, mBinding.mainLayout);
        mBinding.lnBack.setOnClickListener(this);
        mBinding.tvCompleteJob.setOnClickListener(this);
        String bookingId = getIntent().getExtras().getString("bookingId");
        landscaper_id = getIntent().getExtras().getString("landscaper_id");
        HashMap<String, String> params = new HashMap<>();
        params.put("order_id", bookingId);
        getBookingHistoryDetails(params);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onClick(View view) {
        if (view == mBinding.lnBack) {
            finish();
            //startActivity(new Intent(ServiceHistoryDetailsActivity.this, BookingHistoryActivity.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
        else if(view==mBinding.tvCompleteJob){

            String bookingId = AppData.sBookingId= getIntent().getExtras().getString("bookingId");
            AppData.sCompleteImage=null;
            finish();
            startActivity(new Intent(ServiceHistoryDetailsActivity.this, CompleteJobSubmitActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            /*final HashMap<String,String> params=new HashMap<>();
            String bookingId = AppData.sBookingId= getIntent().getExtras().getString("bookingId");
            params.put("order_id",bookingId);
            new PostDataParser(ServiceHistoryDetailsActivity.this, Api.sEndJobLandscaper, params, true, new PostDataParser.OnGetResponseListner() {
                String success;

                @Override

                public void onGetResponse(JSONObject response) {
                if(response!=null){
                    try{
                        success=response.optString("success");
                        if(success.equalsIgnoreCase("1")){
                            AppData.sCompleteImage=null;
                            finish();

                            startActivity(new Intent(ServiceHistoryDetailsActivity.this, CompleteJobSubmitActivity.class));
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
                }
            });*/
        }
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        mBinding.lnBack.performClick();
    }

    private void getBookingHistoryDetails(Map<String, String> params) {
        new PostDataParser(ServiceHistoryDetailsActivity.this, Api.sBookingHistoryDetails, params, true, new PostDataParser.OnGetResponseListner() {
            String success;
            String msg;

            @Override
            public void onGetResponse(JSONObject response) {
                if (response != null) {
                    try {
                        success = response.optString("success");
                        msg = response.optString("msg");
                        if (success.equalsIgnoreCase("1")) {
                            AppData.sTaxRate=response.optString("tax_rate");
                            JSONObject data = response.optJSONObject("data");

                            JSONObject order_details = data.optJSONObject("order_details");

                            String service_booked_price = order_details.optString("service_booked_price").trim();
                            String workingStatus = order_details.optString("status").trim();
                            String is_completed=order_details.optString("is_completed");
                            if (workingStatus.equalsIgnoreCase("2")&&is_completed.equalsIgnoreCase("1")){
                                mBinding.tvCompleteJob.setVisibility(View.VISIBLE);
                            }
                            else {
                                mBinding.tvCompleteJob.setVisibility(View.GONE);
                            }
                           /* if (workingStatus.equalsIgnoreCase("1")) {
                                mBinding.tvCompleteJob.setVisibility(View.VISIBLE);
                            } else {
                                mBinding.tvCompleteJob.setVisibility(View.GONE);
                            } */

                            /*if (!workingStatus.equalsIgnoreCase("3")&&!transaction_id.equalsIgnoreCase("")&&!transaction_id.equalsIgnoreCase("null")&&transaction_id!=null) {
                                mBinding.tvCompleteJob.setVisibility(View.VISIBLE);
                            } else {
                                mBinding.tvCompleteJob.setVisibility(View.GONE);
                            }*/
                            /*if (workingStatus.equalsIgnoreCase("2")) {
                                //payment success
                                mBinding.tvCompleteJob.setVisibility(View.VISIBLE);
                            } else {
                                mBinding.tvCompleteJob.setVisibility(View.GONE);
                            }*/

                            String orderNo = order_details.optString("order_no").trim();
                            String landscaper_name = order_details.optString("lanscaper_name").trim();

                            String service_id = order_details.optString("service_id").trim();
                            mBinding.tvFrequency.setText(order_details.optString("service_frequency"));

                            mBinding.lnServiceDetails.removeAllViews();
                            ServiceHistoryDetailsRowBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.service_history_details_row, mBinding.lnServiceDetails, true);
                            if (service_id.equalsIgnoreCase("1")) {
                                String lawn_area = order_details.optString("lawn_area").trim();
                                String grass_length = order_details.optString("grass_length").trim();
                                binding.tvTitle1.setText("Lawn Size");
                                binding.tvValue1.setText(lawn_area);
                                binding.tvTitle2.setText("Grass Length");
                                binding.tvValue2.setText(grass_length);
                            } else if (service_id.equalsIgnoreCase("2")) {
                                String lawn_area = order_details.optString("lawn_area").trim();
                                String leaf_accumulation = order_details.optString("leaf_accumulation").trim();
                                binding.tvTitle1.setText("Lawn Size");
                                binding.tvValue1.setText(lawn_area);
                                binding.tvTitle2.setText("Leaf Accumulation");
                                binding.tvValue2.setText(leaf_accumulation);
                            } else if (service_id.equalsIgnoreCase("3") || service_id.equalsIgnoreCase("4")) {
                                String lawn_area = order_details.optString("lawn_area").trim();
                                binding.tvTitle1.setText("Lawn Size");
                                binding.tvValue1.setText(lawn_area);
                            } else if (service_id.equalsIgnoreCase("5")) {
                                String lawn_area = order_details.optString("lawn_area").trim();
                                String no_of_zones = order_details.optString("no_of_zones").trim();
                                binding.tvTitle1.setText("Lawn Size");
                                binding.tvValue1.setText(lawn_area);
                                binding.tvTitle2.setText("No of Zones");
                                binding.tvValue2.setText(no_of_zones);
                            } else if (service_id.equalsIgnoreCase("6")) {
                                String water_type = order_details.optString("water_type").trim();
                                String include_spa = order_details.optString("include_spa").trim();
                                String pool_type = order_details.optString("pool_type").trim();
                                String pool_state = order_details.optString("pool_state").trim();
                                binding.tvTitle1.setText("Water Type");
                                binding.tvValue1.setText(water_type);
                                binding.tvTitle2.setText("Include Spa/Hot tub");
                                binding.tvValue2.setText(include_spa);
                                ServiceHistoryDetailsRowBinding binding1 = DataBindingUtil.inflate(getLayoutInflater(), R.layout.service_history_details_row, mBinding.lnServiceDetails, true);
                                binding1.tvTitle1.setText("Pool Type");
                                binding1.tvValue1.setText(pool_type);
                                binding1.tvTitle2.setText("Pool State");
                                binding1.tvValue2.setText(pool_state);
                            } else if (service_id.equalsIgnoreCase("7")) {
                                String no_of_cars = order_details.optString("no_of_cars").trim();
                                String driveway_type = order_details.optString("driveway_type").trim();
                                String service_type = order_details.optString("service_type").trim();
                                binding.tvTitle1.setText("No of Cars");
                                binding.tvValue1.setText(no_of_cars);
                                binding.tvTitle2.setText("Driveway Type");
                                binding.tvValue2.setText(driveway_type);
                                ServiceHistoryDetailsRowBinding binding1 = DataBindingUtil.inflate(getLayoutInflater(), R.layout.service_history_details_row, mBinding.lnServiceDetails, true);
                                binding1.tvTitle1.setText("Service Type");
                                if (service_type.contains(",")){
                                    service_type=service_type.replace(",","\n");
                                }
                                binding1.tvValue1.setText(service_type);
                            }


                            String additional_note = order_details.optString("additional_note").trim();
                            String request_date = order_details.optString("service_date").trim();
                            String request_time = order_details.optString("service_time").trim();
                            String completion_date = order_details.optString("completion_date").trim();
                            final String profile_image = order_details.optString("profile_image").trim();
                            String grandtotal = order_details.optString("service_price").trim();
                            String serviceName = order_details.optString("service_name").trim();

                            JSONObject service_address = data.optJSONObject("service_address");

                            String name = service_address.optString("name").trim();
                            String address = service_address.optString("address").trim();
                            String contact_number = service_address.optString("contact_number").trim();
                            String email_address = service_address.optString("email_address").trim();

                            JSONArray service_images = data.optJSONArray("service_images");


                            if (service_images.length() > 0) {
                                /*ArrayList<String> imageList = new ArrayList<>();
                                mBinding.imgDetails.setVisibility(View.GONE);
                                mBinding.vpImage.setVisibility(View.VISIBLE);
                                for (int j = 0; j < service_images.length(); j++) {
                                    imageList.add(service_images.optJSONObject(j).getString("service_image"));
                                }
                                setImage(imageList);*/

                                final ArrayList<String> imageList=new ArrayList<>();
                                for(int i=0;i<service_images.length();i++){
                                    imageList.add(service_images.optJSONObject(i).getString("service_image"));
                                }
                                RcvImageOneAdapter image1Adapter;

                                mBinding.rcvImageOne.setVisibility(View.VISIBLE);
                                mBinding.rcvImageOne.setLayoutManager(new GridLayoutManager(ServiceHistoryDetailsActivity.this, 2));
                                mBinding.rcvImageOne.setNestedScrollingEnabled(false);
                                mBinding.rcvImageOne.setAdapter(image1Adapter=new RcvImageOneAdapter(ServiceHistoryDetailsActivity.this,imageList));
                                mBinding.tvNoDataFound.setVisibility(View.GONE);
                                image1Adapter.setOnItemClickListner(new RcvImageOneAdapter.OnItemClickListner() {
                                    @Override
                                    public void onItemClick(int position) {
                                        Util.getPopUpWindow(ServiceHistoryDetailsActivity.this,imageList.get(position));

                                    }
                                });






                            }

                            //add image to

                            mBinding.tvServiceName.setText(serviceName);
                            mBinding.tvName.setText(name);
                            mBinding.tvOrderNo.setText(orderNo);
                            mBinding.tvServiceProviderName.setText(landscaper_name);
                            mBinding.tvServiceRequestDate.setText(Util.changeAnyDateFormat(request_date,"yyyy-MM-dd","MMM-dd-yyyy")+" "+
                                    Util.changeAnyDateFormat(request_time,"hh:mm:ss","hh:mm a"));
                            if (completion_date.equalsIgnoreCase("") ||
                                    completion_date.equalsIgnoreCase("null") ||
                                    completion_date.equalsIgnoreCase(null))
                                mBinding.tvCompleteDate.setText("---");
                            else
                                mBinding.tvCompleteDate.setText(Util.changeAnyDateFormat(completion_date,"yyyy-MM-dd","MMM-dd-yyyy"));
                            mBinding.tvAdditionalNote.setText(additional_note);
                            if (profile_image.equalsIgnoreCase("") || profile_image.equalsIgnoreCase("null") ||
                                    profile_image.equalsIgnoreCase(null))
                                Picasso.with(ServiceHistoryDetailsActivity.this).load(R.drawable.ic_user).placeholder(R.drawable.ic_user).into(mBinding.ivProfile);
                            else
                            {
                                mBinding.ivProfile.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Util.getPopUpWindow(ServiceHistoryDetailsActivity.this,profile_image);
                                    }
                                });
                                Picasso.with(ServiceHistoryDetailsActivity.this).load(profile_image).placeholder(R.drawable.ic_user).error(R.drawable.ic_user).resize(150, 150).into(mBinding.ivProfile);
                            }
                            mBinding.tvTax.setText(Util.getDecimalTwoPoint(Double.parseDouble(AppData.sTaxRate))+" %");
                            mBinding.tvGrandToatl.setText("$ " + grandtotal);
                            mBinding.tvTotalPrice.setText("$ " + grandtotal);

                            mBinding.tvAddress.setText(address);
                            mBinding.tvPhone.setText(contact_number);
                            mBinding.tvEmail.setText(email_address);
                            mBinding.tvServicePrice.setText("$ " + Util.getDecimalTwoPoint(Double.parseDouble(service_booked_price)));


                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    //commented out by kamalika
   /* private void setImage(ArrayList<String> images) {

        mBinding.vpImage.setAdapter(new SlidingImage_Adapter(ServiceHistoryDetailsActivity.this, images));
        mBinding.vpImage.setPageTransformer(true, new AccordionTransformer());

        NUM_PAGES = images.size();
        mBinding.vpImage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mBinding.vpImage.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 2000, 2000);


    }*/


}
