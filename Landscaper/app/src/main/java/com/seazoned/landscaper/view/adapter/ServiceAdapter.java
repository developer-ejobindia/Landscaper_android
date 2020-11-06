package com.seazoned.landscaper.view.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.seazoned.landscaper.R;
import com.seazoned.landscaper.service.util.PixelUtil;

import java.util.ArrayList;
import java.util.HashMap;


public class ServiceAdapter extends ArrayAdapter<HashMap<String, String>> {

    private Context context;
    private ArrayList<HashMap<String, String>> mServiceList;

    public ServiceAdapter(Context context, int resource,
                          ArrayList<HashMap<String, String>> mServiceList) {
        super(context, resource, mServiceList);
        this.context = context;
        this.mServiceList = mServiceList;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // return super.getView(position, convertView, parent);

        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.spinner_item, parent, false);
        }
        TextView mTvItem = (TextView) row.findViewById(R.id.tv_main);
        mTvItem.setTextColor(ContextCompat.getColor(context, R.color.colorText));
        mTvItem.setText(mServiceList.get(position).get("serviceName"));


        return row;

    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.spinner_item, parent, false);
        }
        TextView mTvItem = (TextView) row.findViewById(R.id.tv_main);
        /*if (position<= mServiceList.size()-2)
        mTvItem.setBackgroundResource(R.drawable.spinner_item_divider);
        else*/
        mTvItem.setBackgroundColor(Color.parseColor("#FFFFFF"));        //mTvItem.setBackgroundColor(Color.parseColor("#1e2028"));
        //mTvItem.setTextColor(Color.parseColor("#FFFFFF"));
        mTvItem.setPadding(PixelUtil.dpToPx(context, 10), PixelUtil.dpToPx(context, 15), PixelUtil.dpToPx(context, 10), PixelUtil.dpToPx(context, 15));
        mTvItem.setTextColor(ContextCompat.getColor(context, R.color.colorText));
        mTvItem.setText(mServiceList.get(position).get("serviceName"));
        return row;
    }

}
