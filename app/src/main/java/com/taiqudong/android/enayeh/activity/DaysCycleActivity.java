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
 * 月经周期
 */

public class DaysCycleActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_dayscycle;
    private NumberPickerView npv_daysetting_day;
    final String[] days = {"15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34",
            "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_days_cycle);
        SysApplication.getInstance().addActivit(this);
        StatusBarUtils.statusbar(this);
        SysApplication.addActivity(this);
        initView();
        initListener();
        initData();
    }

    private void initView() {
        btn_dayscycle = (Button) findViewById(R.id.btn_dayscycle);
        npv_daysetting_day = (NumberPickerView) findViewById(R.id.npv_daysetting_day);
    }

    private void initListener() {
        findViewById(R.id.iv_return).setOnClickListener(this);
        btn_dayscycle.setOnClickListener(this);
    }

    private void initData() {
        npv_daysetting_day.setDisplayedValues(days);
        npv_daysetting_day.setMinValue(0);
        npv_daysetting_day.setMaxValue(days.length - 1);
        npv_daysetting_day.setWrapSelectorWheel(false);
        npv_daysetting_day.setValue(13);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return:
                this.finish();
                break;
            case R.id.btn_dayscycle:
                next();
                break;
            default:
        }
    }

    private void next() {
        //月经周期
        AppLogic.getInstance().setMentrualCicle(Integer.valueOf(days[npv_daysetting_day.getValue()]));
        startActivity(BasicInformationActivity.class);
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
