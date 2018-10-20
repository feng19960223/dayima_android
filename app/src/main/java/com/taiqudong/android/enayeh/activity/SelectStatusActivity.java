package com.taiqudong.android.enayeh.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.taiqudong.android.enayeh.R;
import com.taiqudong.android.enayeh.application.AppLogic;
import com.taiqudong.android.enayeh.application.SysApplication;
import com.taiqudong.android.enayeh.utils.StatusBarUtils;

/**
 * Created by taiqudong on 2017/7/6.
 * 我要怀孕和我知道经期选择，俩圆
 */

public class SelectStatusActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_selectstatus_pregnant;
    private TextView tv_selectstatus_traking;
    private Button btn_selectstatus;

    private int mUserStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_status);
        SysApplication.getInstance().addActivit(this);
        StatusBarUtils.statusbar(this);
        SysApplication.addActivity(this);
        initView();
        initListener();
    }

    private void initView() {
        tv_selectstatus_pregnant = (TextView) findViewById(R.id.tv_selectstatus_pregnant);
        tv_selectstatus_traking = (TextView) findViewById(R.id.tv_selectstatus_traking);
        btn_selectstatus = (Button) findViewById(R.id.btn_selectstatus);
    }

    private void initListener() {
        findViewById(R.id.iv_return).setOnClickListener(this);
        tv_selectstatus_pregnant.setOnClickListener(this);
        tv_selectstatus_traking.setOnClickListener(this);
        btn_selectstatus.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return:
                AppLogic.getInstance().setStatus(0);
                AppLogic.getInstance().setMentrualCicle(0);
                this.finish();
                break;
            case R.id.tv_selectstatus_pregnant:
                pregnant();
                break;
            case R.id.tv_selectstatus_traking:
                traking();
                break;
            case R.id.btn_selectstatus:
                next();
                break;
            default:
        }
    }

    private boolean a = false;

    private void pregnant() {

        mUserStatus = AppLogic.USER_STATUS_TO_PREGNANT;

        tv_selectstatus_pregnant.setTextColor(ContextCompat.getColor(this, R.color.colorTextWhite));
        tv_selectstatus_pregnant.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_tv_select));
        tv_selectstatus_traking.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_tv_defult));
        tv_selectstatus_traking.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        btn_selectstatus.setEnabled(true);
        if (Build.VERSION.SDK_INT >= 16) {
            btn_selectstatus.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_sbw));
        } else {
            btn_selectstatus.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.btn_sbw));
        }
    }

    private void traking() {

        mUserStatus = AppLogic.USER_STATUS_TO_TRACK;

        tv_selectstatus_traking.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_tv_select));
        tv_selectstatus_traking.setTextColor(ContextCompat.getColor(this, R.color.colorTextWhite));
        tv_selectstatus_pregnant.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_tv_defult));
        tv_selectstatus_pregnant.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        btn_selectstatus.setEnabled(true);
        if (Build.VERSION.SDK_INT >= 16) {
            btn_selectstatus.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_sbw));
        } else {
            btn_selectstatus.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.btn_sbw));
        }
    }

    private void next() {
        AppLogic.getInstance().setStatus(mUserStatus);
        startActivity(TimeSettingActivity.class);
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
