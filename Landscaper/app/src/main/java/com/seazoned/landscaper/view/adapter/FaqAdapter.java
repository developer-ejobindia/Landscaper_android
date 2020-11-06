package com.seazoned.landscaper.view.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.seazoned.landscaper.R;
import com.seazoned.landscaper.databinding.FaqRowBinding;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by root on 2/5/18.
 */

public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.BindingHolder> {
    Context context;
    ArrayList<HashMap<String, String>> arrayList;

    public FaqAdapter(Context context, ArrayList<HashMap<String, String>> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FaqRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.faq_row, parent, false);
        return new BindingHolder(binding);
    }

    @Override
    public void onBindViewHolder(BindingHolder holder, final int position) {
        holder.binding.tvQuestions.setText(arrayList.get(position).get("questions"));
        holder.binding.tvAnswer.setText(arrayList.get(position).get("answers"));
        final HashMap<String,String> hashMap=arrayList.get(position);
        if (hashMap.get("visibility").equalsIgnoreCase("0")){
            holder.binding.tvAnswer.setVisibility(View.GONE);
        }
        else {
            holder.binding.tvAnswer.setVisibility(View.VISIBLE);
        }
        holder.binding.lnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hashMap.get("visibility").equalsIgnoreCase("0")){
                    hashMap.put("visibility","1");
                    arrayList.remove(position);
                    arrayList.add(position,hashMap);
                    notifyDataSetChanged();
                }
                else {
                    hashMap.put("visibility","0");
                    arrayList.remove(position);
                    arrayList.add(position,hashMap);
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class BindingHolder extends RecyclerView.ViewHolder {
        private FaqRowBinding binding;

        public BindingHolder(FaqRowBinding binding) {
            super(binding.lnMain);
            this.binding = binding;
        }
    }
}
