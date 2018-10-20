package com.taiqudong.android.enayeh.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.taiqudong.android.enayeh.R;
import com.taiqudong.android.enayeh.application.AppLogic;
import com.taiqudong.android.enayeh.application.SysApplication;
import com.taiqudong.android.enayeh.utils.StatusBarUtils;
import com.taiqudong.android.enayeh.view.NumberPickerView;

/**
 * Created by taiqudong on 2017/7/7.
 * 月经天数
 */

public class DaysSettingActivity extends AppCompatActivity implements View.OnClickListener {
    private NumberPickerView npv_daysetting_day;
    private Button btn_dayssetting;
    final String[] days = {"02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_days_setting);
        SysApplication.getInstance().addActivit(this);
        StatusBarUtils.statusbar(this);
        SysApplication.addActivity(this);
        initView();
        initListener();
        initData();
    }

    private void initView() {
        npv_daysetting_day = (NumberPickerView) findViewById(R.id.npv_daysetting_day);
        btn_dayssetting = (Button) findViewById(R.id.btn_dayssetting);
    }

    private void initListener() {
        findViewById(R.id.iv_return).setOnClickListener(this);
        btn_dayssetting.setOnClickListener(this);
    }

    private void initData() {
        npv_daysetting_day.setDisplayedValues(days);
        npv_daysetting_day.setMinValue(0);
        npv_daysetting_day.setMaxValue(days.length - 1);
        npv_daysetting_day.setWrapSelectorWheel(false);
        npv_daysetting_day.setValue(5);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return:
                this.finish();
                break;
            case R.id.btn_dayssetting:
                next();
                break;
            default:
        }
    }

    private void next() {
        AppLogic.getInstance().setMenstrualTime(Integer.valueOf(days[npv_daysetting_day.getValue()]).intValue());
        startActivity(DaysCycleActivity.class);
    }

    private void startActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SysApplication.getInstance().removeActivity(this);
    }
}
