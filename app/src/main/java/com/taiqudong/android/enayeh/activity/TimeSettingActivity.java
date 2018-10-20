package com.taiqudong.android.enayeh.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.taiqudong.android.enayeh.R;
import com.taiqudong.android.enayeh.application.AppLogic;
import com.taiqudong.android.enayeh.application.SysApplication;
import com.taiqudong.android.enayeh.utils.DateUtil;
import com.taiqudong.android.enayeh.utils.StatusBarUtils;
import com.taiqudong.android.enayeh.view.NumberPickerView;

import java.util.GregorianCalendar;

/**
 * Created by taiqudong on 2017/7/7.
 * 上一次月经日期
 */

public class TimeSettingActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_timesetting;
    private NumberPickerView npv_timesetting_month;
    private NumberPickerView npv_timesetting_day;

    private int mLastTimeDate;
    private int mLastTimeMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_setting);
        SysApplication.getInstance().addActivit(this);
        StatusBarUtils.statusbar(this);
        SysApplication.addActivity(this);
        initView();
        initListener();
        initData();
        mLastTimeDate = 0;
        mLastTimeMonth = 0;
    }

    private void initView() {
        npv_timesetting_month = (NumberPickerView) findViewById(R.id.npv_timesetting_month);
        npv_timesetting_day = (NumberPickerView) findViewById(R.id.npv_timesetting_day);
        btn_timesetting = (Button) findViewById(R.id.btn_timesetting);
    }

    private void initListener() {
        findViewById(R.id.iv_return).setOnClickListener(this);
        btn_timesetting.setOnClickListener(this);
        npv_timesetting_month.setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                //让二级联动的值（日）定死不动
                int value = npv_timesetting_day.getValue()
                        >
                        DateUtil.getDaysOfMonth("" + year + "-" + (newVal + 1))
                        ?
                        DateUtil.getDaysOfMonth("" + year + "-" + (newVal + 1)) - 1
                        :
                        npv_timesetting_day.getValue();
                npv_timesetting_day.setMaxValue(DateUtil.getDaysOfMonth("" + year + "-" + (newVal + 1)) - 1);
                npv_timesetting_day.setValue(value);
            }
        });
    }

    private void initData() {
        npv_timesetting_month.setDisplayedValues(months);
        npv_timesetting_month.setMinValue(0);
        npv_timesetting_month.setMaxValue(months.length - 1);
        npv_timesetting_month.setWrapSelectorWheel(false);
        npv_timesetting_month.setValue(month);

        npv_timesetting_day.setDisplayedValues(DateUtil.getDays());
        npv_timesetting_day.setMinValue(0);
        npv_timesetting_day.setMaxValue(DateUtil.getDaysOfMonth("" + year + "-" + (month + 1)) - 1);
        npv_timesetting_day.setWrapSelectorWheel(false);
        npv_timesetting_day.setValue(day - 1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return:
                this.finish();
                break;
            case R.id.btn_timesetting:
                next();
                break;
            default:
        }
    }

    final String[] months = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
    GregorianCalendar gregorianCalendar = new GregorianCalendar();
    int month = gregorianCalendar.get(GregorianCalendar.MONTH);
    int year = gregorianCalendar.get(GregorianCalendar.YEAR);
    int day = gregorianCalendar.get(GregorianCalendar.DAY_OF_MONTH);

    private void next() {
        mLastTimeMonth = npv_timesetting_month.getValue() + 1;
        mLastTimeDate = npv_timesetting_day.getValue() + 1;
        //保存上次大姨妈日期
        AppLogic.getInstance().setLastMenstrualDate(year, mLastTimeMonth, mLastTimeDate);
        startActivity(DaysSettingActivity.class);
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
