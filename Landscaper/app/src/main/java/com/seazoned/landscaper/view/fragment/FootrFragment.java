package com.seazoned.landscaper.view.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.seazoned.landscaper.R;
import com.seazoned.landscaper.app.AppData;
import com.seazoned.landscaper.databinding.FooterFragmentBinding;
import com.seazoned.landscaper.service.RedDotService;
import com.seazoned.landscaper.view.activity.ChatListActivity;
import com.seazoned.landscaper.view.activity.ContactUs;
import com.seazoned.landscaper.view.activity.DashBoardActivity;
import com.seazoned.landscaper.view.activity.FaqActivity;
import com.seazoned.landscaper.view.activity.ProfileActivity;


/**
 * Created by root on 31/1/18.
 */

public class FootrFragment extends Fragment implements View.OnClickListener {
    FooterFragmentBinding mBinding;
    private FragmentActivity mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.footer_fragment, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        mBinding.lnProfile.setOnClickListener(this);
        mBinding.lnHome.setOnClickListener(this);
        mBinding.lnMessage.setOnClickListener(this);
        mBinding.lnContactUs.setOnClickListener(this);
        mBinding.lnFaq.setOnClickListener(this);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                mMessageReceiver, new IntentFilter("RedDotStatus"));
        getActivity().startService(new Intent(getActivity(), RedDotService.class));
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().stopService(new Intent(getActivity(), RedDotService.class));
    }
    public void showRedDot(boolean status){
        if (status)
            getActivity().findViewById(R.id.ivMsgDot).setVisibility(View.VISIBLE);
        else
            getActivity().findViewById(R.id.ivMsgDot).setVisibility(View.GONE);

    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            boolean status = intent.getExtras().getBoolean("status");
            if (status){
                if (mContext.getClass().getSimpleName().equalsIgnoreCase("ChatListActivity")) {
                    if (((RecyclerView) ((Activity) mContext).findViewById(R.id.rcvChatList)).getAdapter() != null) {
                        ((RecyclerView) ((Activity) mContext).findViewById(R.id.rcvChatList)).getAdapter().notifyDataSetChanged();
                    }
                }
                mBinding.ivMsgDot.setVisibility(View.VISIBLE);
            }
            else {
                mBinding.ivMsgDot.setVisibility(View.GONE);
            }

            //tvStatus.setText(message);
            // Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    };
    @Override
    public void onResume() {
        super.onResume();
        int myActiveVectorColor = ContextCompat.getColor(getActivity(), R.color.colorActiveNavText);
        int myInActiveVectorColor = ContextCompat.getColor(getActivity(), R.color.colorNavText);

        if (getActivity().getClass().getSimpleName().equalsIgnoreCase("ProfileActivity") ||
                getActivity().getClass().getSimpleName().equalsIgnoreCase("ViewProfileActivity") ||
                getActivity().getClass().getSimpleName().equalsIgnoreCase("EditProfileActivity") ||
                getActivity().getClass().getSimpleName().equalsIgnoreCase("BookingHistoryActivity") ||
                getActivity().getClass().getSimpleName().equalsIgnoreCase("PaymentInfoActivity") ||
                getActivity().getClass().getSimpleName().equalsIgnoreCase("ServiceSettings")
                ) {
            //active
            mBinding.ivProfile.getDrawable().setColorFilter(myActiveVectorColor, PorterDuff.Mode.SRC_IN);
            mBinding.tvProfile.setTextColor(myActiveVectorColor);

            //inactive
            mBinding.ivMessage.getDrawable().setColorFilter(myInActiveVectorColor, PorterDuff.Mode.SRC_IN);
            mBinding.tvMessage.setTextColor(myInActiveVectorColor);

            mBinding.ivContact.getDrawable().setColorFilter(myInActiveVectorColor, PorterDuff.Mode.SRC_IN);
            mBinding.tvContact.setTextColor(myInActiveVectorColor);

            mBinding.ivFaq.getDrawable().setColorFilter(myInActiveVectorColor, PorterDuff.Mode.SRC_IN);
            mBinding.tvFaq.setTextColor(myInActiveVectorColor);

            if (getActivity().getClass().getSimpleName().equalsIgnoreCase("ProfileActivity"))
                mBinding.lnProfile.setOnClickListener(null);

        } else if (getActivity().getClass().getSimpleName().equalsIgnoreCase("ServiceHistoryDetailsActivity")) {
            //inactive
            mBinding.ivMessage.getDrawable().setColorFilter(myInActiveVectorColor, PorterDuff.Mode.SRC_IN);
            mBinding.tvMessage.setTextColor(myInActiveVectorColor);

            mBinding.ivContact.getDrawable().setColorFilter(myInActiveVectorColor, PorterDuff.Mode.SRC_IN);
            mBinding.tvContact.setTextColor(myInActiveVectorColor);

            mBinding.ivFaq.getDrawable().setColorFilter(myInActiveVectorColor, PorterDuff.Mode.SRC_IN);
            mBinding.tvFaq.setTextColor(myInActiveVectorColor);
            if (AppData.sServiceHistoryFlag.equalsIgnoreCase("BookingHistory")) {
                //active
                mBinding.ivProfile.getDrawable().setColorFilter(myActiveVectorColor, PorterDuff.Mode.SRC_IN);
                mBinding.tvProfile.setTextColor(myActiveVectorColor);


            } else {
                mBinding.ivProfile.getDrawable().setColorFilter(myInActiveVectorColor, PorterDuff.Mode.SRC_IN);
                mBinding.tvProfile.setTextColor(myInActiveVectorColor);
            }
        } else if (getActivity().getClass().getSimpleName().equalsIgnoreCase("DashBoardActivity")) {
            //inactive
            mBinding.ivProfile.getDrawable().setColorFilter(myInActiveVectorColor, PorterDuff.Mode.SRC_IN);
            mBinding.tvProfile.setTextColor(myInActiveVectorColor);

            mBinding.ivMessage.getDrawable().setColorFilter(myInActiveVectorColor, PorterDuff.Mode.SRC_IN);
            mBinding.tvMessage.setTextColor(myInActiveVectorColor);

            mBinding.ivContact.getDrawable().setColorFilter(myInActiveVectorColor, PorterDuff.Mode.SRC_IN);
            mBinding.tvContact.setTextColor(myInActiveVectorColor);

            mBinding.ivFaq.getDrawable().setColorFilter(myInActiveVectorColor, PorterDuff.Mode.SRC_IN);
            mBinding.tvFaq.setTextColor(myInActiveVectorColor);

            mBinding.lnHome.setOnClickListener(null);
        } else if (getActivity().getClass().getSimpleName().equalsIgnoreCase("ChatListActivity")) {

            //active
            mBinding.ivMessage.getDrawable().setColorFilter(myActiveVectorColor, PorterDuff.Mode.SRC_IN);
            mBinding.tvMessage.setTextColor(myActiveVectorColor);

            //inactive
            mBinding.ivProfile.getDrawable().setColorFilter(myInActiveVectorColor, PorterDuff.Mode.SRC_IN);
            mBinding.tvProfile.setTextColor(myInActiveVectorColor);

            mBinding.ivContact.getDrawable().setColorFilter(myInActiveVectorColor, PorterDuff.Mode.SRC_IN);
            mBinding.tvContact.setTextColor(myInActiveVectorColor);

            mBinding.ivFaq.getDrawable().setColorFilter(myInActiveVectorColor, PorterDuff.Mode.SRC_IN);
            mBinding.tvFaq.setTextColor(myInActiveVectorColor);

            mBinding.lnMessage.setOnClickListener(null);
        } else if (getActivity().getClass().getSimpleName().equalsIgnoreCase("ContactUs")) {

            //active
            mBinding.ivContact.getDrawable().setColorFilter(myActiveVectorColor, PorterDuff.Mode.SRC_IN);
            mBinding.tvContact.setTextColor(myActiveVectorColor);

            //inactive
            mBinding.ivMessage.getDrawable().setColorFilter(myInActiveVectorColor, PorterDuff.Mode.SRC_IN);
            mBinding.tvMessage.setTextColor(myInActiveVectorColor);

            mBinding.ivProfile.getDrawable().setColorFilter(myInActiveVectorColor, PorterDuff.Mode.SRC_IN);
            mBinding.tvProfile.setTextColor(myInActiveVectorColor);

            mBinding.ivFaq.getDrawable().setColorFilter(myInActiveVectorColor, PorterDuff.Mode.SRC_IN);
            mBinding.tvFaq.setTextColor(myInActiveVectorColor);



            mBinding.lnContactUs.setOnClickListener(null);
        }
        else if (getActivity().getClass().getSimpleName().equalsIgnoreCase("FaqActivity")) {

            //active
            mBinding.ivFaq.getDrawable().setColorFilter(myActiveVectorColor, PorterDuff.Mode.SRC_IN);
            mBinding.tvFaq.setTextColor(myActiveVectorColor);

            //inactive
            mBinding.ivContact.getDrawable().setColorFilter(myInActiveVectorColor, PorterDuff.Mode.SRC_IN);
            mBinding.tvContact.setTextColor(myInActiveVectorColor);

            mBinding.ivMessage.getDrawable().setColorFilter(myInActiveVectorColor, PorterDuff.Mode.SRC_IN);
            mBinding.tvMessage.setTextColor(myInActiveVectorColor);

            mBinding.ivProfile.getDrawable().setColorFilter(myInActiveVectorColor, PorterDuff.Mode.SRC_IN);
            mBinding.tvProfile.setTextColor(myInActiveVectorColor);

            mBinding.lnFaq.setOnClickListener(null);
        }


    }

    @Override
    public void onClick(View view) {
        if (view == mBinding.lnProfile) {
            getActivity().finish();
            startActivity(new Intent(getActivity(), ProfileActivity.class));
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (view == mBinding.lnHome) {
            getActivity().finish();
            startActivity(new Intent(getActivity(), DashBoardActivity.class));
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (view == mBinding.lnMessage) {
            getActivity().finish();
            startActivity(new Intent(getActivity(), ChatListActivity.class));
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (view == mBinding.lnContactUs) {
            getActivity().finish();
            startActivity(new Intent(getActivity(), ContactUs.class));
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        else if (view == mBinding.lnFaq) {
            getActivity().finish();
            startActivity(new Intent(getActivity(), FaqActivity.class));
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }
}
