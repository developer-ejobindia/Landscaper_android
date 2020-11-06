package com.seazoned.landscaper.view.fragment;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.seazoned.landscaper.R;
import com.seazoned.landscaper.databinding.FragmentAccountInfoBinding;
import com.seazoned.landscaper.service.api.Api;
import com.seazoned.landscaper.service.parser.GetDataParser;
import com.seazoned.landscaper.service.parser.PostDataParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountInfoFragment extends Fragment implements View.OnClickListener {
    FragmentAccountInfoBinding accountInfoBinding;
    private String paypalId;

    public AccountInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        accountInfoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_account_info, container, false);
        return accountInfoBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        accountInfoBinding.tvSubmit.setOnClickListener(this);
        accountInfoBinding.tvCreatePaypal.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getAccountInfo();
    }

    private void getAccountInfo() {
        new GetDataParser(getActivity(), Api.sViewPaypalAccount, true, new GetDataParser.OnGetResponseListner() {
            @Override
            public void onGetResponse(JSONObject response) {
                if (response != null) {
                    try {
                        String msg, success;
                        success = response.optString("success");
                        msg = response.optString("msg");
                        if (success.equalsIgnoreCase("1")) {
                            accountInfoBinding.tvSubmit.setText("Remove Account");
                            accountInfoBinding.lnViewAccount.setVisibility(View.VISIBLE);
                            accountInfoBinding.tvMessage.setVisibility(View.GONE);
                            accountInfoBinding.lnAddAccount.setVisibility(View.GONE);

                            JSONObject data = response.optJSONObject("data");
                            JSONArray payment_accounts = data.optJSONArray("payment_accounts");
                            if (payment_accounts.length() > 0) {
                                JSONObject jsonObject = payment_accounts.optJSONObject(0);

                                paypalId = jsonObject.optString("id");
                                String account_password = jsonObject.optString("account_password");
                                String account_signature = jsonObject.optString("account_signature");
                                String account_details = jsonObject.optString("account_details");
                                String account_email = jsonObject.optString("account_email");
                                String name = jsonObject.optString("name");
                                accountInfoBinding.tvAccountHolderName.setText(name);
                                accountInfoBinding.tvAccountId.setText(account_email);
                            }
                        } else if (success.equalsIgnoreCase("0")) {
                            accountInfoBinding.tvSubmit.setText("submit");
                            accountInfoBinding.lnAddAccount.setVisibility(View.VISIBLE);
                            accountInfoBinding.lnViewAccount.setVisibility(View.GONE);
                            accountInfoBinding.tvMessage.setVisibility(View.VISIBLE);


                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == accountInfoBinding.tvSubmit) {
            if (accountInfoBinding.tvSubmit.getText().toString().equalsIgnoreCase("submit")) {


                String accountHolderName = accountInfoBinding.etAccountHolderName.getText().toString();
                String paypalAccountId = accountInfoBinding.etPaypalAccountId.getText().toString();
                String paypalAccountUserName = accountInfoBinding.etPaypalUserName.getText().toString();
                String paypalPassword = accountInfoBinding.etPaypalPassword.getText().toString();
                String paypalSignature = accountInfoBinding.etPaypalSignature.getText().toString();

                if (TextUtils.isEmpty(accountHolderName)) {
                    Toast.makeText(getActivity(), "Input account holder name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(paypalAccountId)) {
                    Toast.makeText(getActivity(), "Input account holder id", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(paypalPassword)) {
                    Toast.makeText(getActivity(), "Input password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(paypalSignature)) {
                    Toast.makeText(getActivity(), "Input signature", Toast.LENGTH_SHORT).show();
                    return;
                }

                HashMap<String, String> params = new HashMap<>();
                params.put("paypal_name", accountHolderName);
                params.put("paypal_account_email", paypalAccountId);
                params.put("paypal_api_username", paypalAccountUserName);
                params.put("paypal_api_password", paypalPassword);
                params.put("paypal_api_signature", paypalSignature);

                addRemovePaypalAccount(Api.sAddPaypalAccount,params);
            } else if (accountInfoBinding.tvSubmit.getText().toString().equalsIgnoreCase("Remove Account")) {
                //
                HashMap<String, String> params = new HashMap<>();
                params.put("paypal_id", paypalId);
                addRemovePaypalAccount(Api.sRemovePaypalAccount,params);
            }
        }
        else if (view==accountInfoBinding.tvCreatePaypal){
            String url="http://www.paypal.com";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }
    }

    private void addRemovePaypalAccount(String url, HashMap<String, String> params) {
        new PostDataParser(getActivity(), url, params, true, new PostDataParser.OnGetResponseListner() {
            @Override
            public void onGetResponse(JSONObject response) {
                if (response != null) {
                    try {
                        String msg, success;
                        msg = response.optString("msg");
                        success = response.optString("success");
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                        if (success.equalsIgnoreCase("1")) {
                            getAccountInfo();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }
}
