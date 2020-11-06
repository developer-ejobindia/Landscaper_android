package com.seazoned.landscaper.view.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.seazoned.landscaper.R;
import com.seazoned.landscaper.databinding.ActivityPaymentInfoBinding;
import com.seazoned.landscaper.databinding.AddPaymentViewBinding;
import com.seazoned.landscaper.databinding.ChangePaymentViewBinding;
import com.seazoned.landscaper.service.util.Util;
import com.seazoned.landscaper.view.adapter.ViewPagerAdapter;
import com.seazoned.landscaper.view.fragment.AccountInfoFragment;
import com.seazoned.landscaper.view.fragment.TransactionHistoryFragment;

public class PaymentInfoActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityPaymentInfoBinding mBinding;
    ViewPagerAdapter mAdapter=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_payment_info);
        Util.setPadding(this, mBinding.mainLayout);
        mBinding.lnBack.setOnClickListener(this);
        mAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        mAdapter.addFragment(new AccountInfoFragment(),"Account");
        mAdapter.addFragment(new TransactionHistoryFragment(),"Transaction History");
        mBinding.viewPager.setAdapter(mAdapter);
        mBinding.tabLayout.setupWithViewPager(mBinding.viewPager);
    }


    @Override
    public void onClick(View view) {

        if (view == mBinding.lnBack) {
            finish();
            startActivity(new Intent(PaymentInfoActivity.this, ProfileActivity.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        mBinding.lnBack.performClick();
    }
}
