package com.seazoned.landscaper.model;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by root on 24/2/18.
 */

public class DayModel {
    private CheckBox checkBox;
    private TextView tvStartTime;
    private TextView tvEndTime;
    private TextView tvHideStartTime;
    private TextView tvHideEndTime;

    public DayModel(CheckBox checkBox, TextView tvStartTime, TextView tvEndTime, TextView tvHideStartTime, TextView tvHideEndTime) {
        this.checkBox = checkBox;
        this.tvStartTime = tvStartTime;
        this.tvEndTime = tvEndTime;
        this.tvHideStartTime = tvHideStartTime;
        this.tvHideEndTime = tvHideEndTime;
    }

    public TextView getTvHideStartTime() {
        return tvHideStartTime;
    }

    public void setTvHideStartTime(TextView tvHideStartTime) {
        this.tvHideStartTime = tvHideStartTime;
    }

    public TextView getTvHideEndTime() {
        return tvHideEndTime;
    }

    public void setTvHideEndTime(TextView tvHideEndTime) {
        this.tvHideEndTime = tvHideEndTime;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    public TextView getTvStartTime() {
        return tvStartTime;
    }

    public void setTvStartTime(TextView tvStartTime) {
        this.tvStartTime = tvStartTime;
    }

    public TextView getTvEndTime() {
        return tvEndTime;
    }

    public void setTvEndTime(TextView tvEndTime) {
        this.tvEndTime = tvEndTime;
    }
}
