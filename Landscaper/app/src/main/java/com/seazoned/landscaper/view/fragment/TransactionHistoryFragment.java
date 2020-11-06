package com.seazoned.landscaper.view.fragment;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.seazoned.landscaper.R;
import com.seazoned.landscaper.databinding.FragmentTransactionHistoryBinding;
import com.seazoned.landscaper.service.api.Api;
import com.seazoned.landscaper.service.parser.GetDataParser;
import com.seazoned.landscaper.service.util.Util;
import com.seazoned.landscaper.view.adapter.TransactionHistoryListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class TransactionHistoryFragment extends Fragment {

    FragmentTransactionHistoryBinding transactionHistoryBinding;
    private ArrayList<HashMap<String,String>> mTransactionHistoryList=null;
    private LinearLayoutManager mLayoutManager;

    public TransactionHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
     transactionHistoryBinding= DataBindingUtil.inflate(inflater,R.layout.fragment_transaction_history, container, false);
     return transactionHistoryBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLayoutManager = new LinearLayoutManager(getActivity());
        transactionHistoryBinding.rcvTransactionList.setLayoutManager(mLayoutManager);
       transactionHistoryBinding.rcvTransactionList.setNestedScrollingEnabled(false);

    }

    @Override
    public void onResume() {
        super.onResume();
        getTransactionHistory();
    }
    private void getTransactionHistory(){
        new GetDataParser(getActivity(), Api.sViewTransactionHistory, true, new GetDataParser.OnGetResponseListner() {
            @Override
            public void onGetResponse(JSONObject response) {
            if(response!=null){
                try{
                    String msg,success;
                    success = response.optString("success");
                    msg = response.optString("msg");
                    if(success.equalsIgnoreCase("1")) {
                        mTransactionHistoryList=new ArrayList<>();

                        JSONObject jobj = response.optJSONObject("data");
                        String total_amount=jobj.optString("total_amount");
                        if (!total_amount.equalsIgnoreCase(""))
                        transactionHistoryBinding.tvPaymentReceive.setText("$ "+ Util.getDecimalTwoPoint(Double.parseDouble(total_amount)));
                        JSONArray data=jobj.optJSONArray("transaction_list");

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject jsonObject=data.optJSONObject(i);
                            String first_name = jsonObject.optString("first_name");
                            String last_name = jsonObject.optString("last_name");
                            String profile_image = jsonObject.optString("profile_image");
                            String order_no = jsonObject.optString("order_no");
                            String landscaper_payment = jsonObject.optString("landscaper_payment");
                            String status = jsonObject.optString("status");
                            String full_name = jsonObject.optString("full_name");
                            String status_name = jsonObject.optString("status_name");
                            String payment_date = jsonObject.optString("payment_date");
                            String transaction_id = jsonObject.optString("transaction_id");

                            HashMap<String, String> hashMap = new HashMap<>();

                            hashMap.put("full_name", full_name);
                            hashMap.put("order_no", order_no);
                            hashMap.put("profile_image", profile_image);
                            hashMap.put("status",status);
                            hashMap.put("status_name",status_name);
                            hashMap.put("transaction_id",transaction_id);
                            hashMap.put("payment_date",payment_date);
                            hashMap.put("landscaper_payment",landscaper_payment);

                            mTransactionHistoryList.add(hashMap);
                        }
                        transactionHistoryBinding.rcvTransactionList.setAdapter(new TransactionHistoryListAdapter(getActivity(),mTransactionHistoryList));
                    }

                }
                catch(Exception e){
                    e.printStackTrace();
                }

            }
            }
        });
    }


}
