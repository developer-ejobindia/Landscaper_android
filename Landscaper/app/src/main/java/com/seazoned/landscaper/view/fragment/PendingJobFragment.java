package com.seazoned.landscaper.view.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seazoned.landscaper.R;
import com.seazoned.landscaper.databinding.PendingJobsFragmentBinding;
import com.seazoned.landscaper.databinding.ServiceRequestFragmentBinding;
import com.seazoned.landscaper.service.api.Api;
import com.seazoned.landscaper.service.parser.GetDataParser;
import com.seazoned.landscaper.view.adapter.ServiceRequestListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by root on 8/2/18.
 */

public class PendingJobFragment extends Fragment {
    private PendingJobsFragmentBinding mBinding;
    private ArrayList<HashMap<String, String>> serviceList = null;
    private LinearLayoutManager mLayoutManager;
    private TextView tvCount1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.pending_jobs_fragment, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLayoutManager = new LinearLayoutManager(getActivity());
        //mLayoutManager.setAutoMeasureEnabled(false);
        mBinding.rcvPendingJob.setLayoutManager(mLayoutManager);
        mBinding.rcvPendingJob.setNestedScrollingEnabled(false);
        //getServiceRequest();
        TabLayout tab = (TabLayout) getActivity().findViewById(R.id.tabLayout);
        View view = tab.getTabAt(1).getCustomView();
        tvCount1 = (TextView) view.findViewById(R.id.tvCount);

    }

    @Override
    public void onResume() {
        super.onResume();
        getServiceRequest();
    }

    private void getServiceRequest() {
        new GetDataParser(getActivity(), Api.sServicePending, false, new GetDataParser.OnGetResponseListner() {
            public String mSuccess;

            @Override
            public void onGetResponse(JSONObject response) {
                if (response != null) {
                    try {
                        mSuccess = response.optString("success");
                        if (mSuccess.equalsIgnoreCase("1")) {
                            serviceList = new ArrayList<>();
                            JSONArray data = response.getJSONArray("data");
                            if (data.length() > 0) {
                                tvCount1.setVisibility(View.VISIBLE);
                                tvCount1.setText("" + data.length());
                            } else {
                                tvCount1.setVisibility(View.GONE);
                            }
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject jobjData = data.getJSONObject(i);

                                HashMap<String, String> hashMap = new HashMap<>();

                                //book service data
                                JSONObject bookService = jobjData.getJSONObject("book_service");

                                hashMap.put("serviceId", bookService.optString("id"));
                                hashMap.put("serviceDate", bookService.optString("service_date"));
                                hashMap.put("serviceTime", bookService.optString("service_time"));
                                hashMap.put("servicePrice", bookService.optString("service_price"));

                                //book address data
                                JSONObject bookAddress = jobjData.getJSONObject("book_address");

                                hashMap.put("name", bookAddress.optString("name"));
                                hashMap.put("address", bookAddress.optString("address"));

                                //service data
                                JSONObject name = jobjData.getJSONObject("name");

                                hashMap.put("serviceName", name.optString("service_name"));
                                //hashMap.put("serviceName", bookService.optString("service_name"));
                                serviceList.add(hashMap);

                            }
                            //set adapter to recyclerview
                            mBinding.rcvPendingJob.setVisibility(View.VISIBLE);
                            mBinding.tvJobAlert.setVisibility(View.GONE);
                            mBinding.rcvPendingJob.setAdapter(new ServiceRequestListAdapter(getActivity(), serviceList, false));
                        } else {
                            mBinding.rcvPendingJob.setVisibility(View.GONE);
                            mBinding.tvJobAlert.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }
}
