package com.seazoned.landscaper.view.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.seazoned.landscaper.R;
import com.seazoned.landscaper.databinding.ServiceDetailsImageRowBinding;
import com.seazoned.landscaper.databinding.ServiceHistoryDetailsRowBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by root on 19/7/18.
 */

public class RcvImageOneAdapter extends RecyclerView.Adapter<RcvImageOneAdapter.BindingHolder> {
    Context context;
    ArrayList<String> imageList;
    OnItemClickListner listner;


    public RcvImageOneAdapter(Context context, ArrayList<String> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @Override
    public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ServiceDetailsImageRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.service_details_image_row, parent, false);
        return new BindingHolder(binding);
    }

    @Override
    public void onBindViewHolder(BindingHolder holder, final int position) {
        if ((imageList.get(position) != null) || (imageList.get(position) != "null"))
            Picasso.with(context).load(imageList.get(position)).placeholder(R.mipmap.feature_image).error(R.mipmap.feature_image).into(holder.binding.ivRcvImage);
        else {
            Picasso.with(context).load(R.mipmap.feature_image).placeholder(R.mipmap.feature_image).error(R.mipmap.feature_image).into(holder.binding.ivRcvImage);

        }
        holder.binding.llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listner.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }


    public class BindingHolder extends RecyclerView.ViewHolder {
        final ServiceDetailsImageRowBinding binding;


        public BindingHolder(ServiceDetailsImageRowBinding itemView) {
            super(itemView.llMain);
            this.binding = itemView;


        }
    }

    public interface OnItemClickListner {
        public void onItemClick(int position);
    }

    public void setOnItemClickListner(OnItemClickListner listner) {
        this.listner = listner;
    }
}
