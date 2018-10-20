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
import com.taiqudong.android.enayeh.utils.DialogUtil;
import com.taiqudong.android.enayeh.utils.StatusBarUtils;

/**
 * 设置状态
 * Created by taiqudong on 2017/8/15.
 */

public class ConditionActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_state;
    private TextView tv_pregnant;//怀孕
    private TextView tv_track;//追踪
    private Button btn_next;//下一步

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_condition);
        SysApplication.getInstance().addActivit(this);
        StatusBarUtils.statusbar(this);
        SysApplication.addActivitiesLogin(this);
        initView();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initDate();
    }

    public void initView() {
        tv_state = (TextView) findViewById(R.id.tv_state);

        tv_pregnant = (TextView) findViewById(R.id.tv_pregnant);
        tv_track = (TextView) findViewById(R.id.tv_track);

        btn_next = (Button) findViewById(R.id.btn_next);
    }

    public void initListener() {
        findViewById(R.id.iv_return).setOnClickListener(this);

        tv_pregnant.setOnClickListener(this);
        tv_track.setOnClickListener(this);

        btn_next.setOnClickListener(this);
    }

    public void initDate() {

        if (AppLogic.getInstance().getStatus() == AppLogic.USER_STATUS_TO_PREGNANT) {//怀孕
            pregnantUI();
        } else if (AppLogic.getInstance().getStatus() == AppLogic.USER_STATUS_TO_TRACK) {//追踪
            trackUI();
        } else {//用户还没有记录
            tv_state.setText(getString(R.string.state));
        }
    }

    private void pregnantUI() {
        tv_state.setText(getString(R.string.state) + ":" + getString(R.string.state1));

        tv_pregnant.setTextColor(ContextCompat.getColor(this, R.color.colorTextWhite));
        tv_pregnant.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_tv_select));

        tv_track.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        tv_track.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_tv_defult));

        nextBtnUI();
    }

    private void trackUI() {
        tv_state.setText(getString(R.string.state) + ":" + getString(R.string.state2));

        tv_track.setTextColor(ContextCompat.getColor(this, R.color.colorTextWhite));
        tv_track.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_tv_select));

        tv_pregnant.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        tv_pregnant.setBackground(ContextCompat.getDrawable(this, R.drawable.shape_tv_defult));

        nextBtnUI();
    }

    private void nextBtnUI() {
        btn_next.setEnabled(true);
        if (Build.VERSION.SDK_INT >= 16) {
            btn_next.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_sbw));
        } else {
            btn_next.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.btn_sbw));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return:
                this.finish();
                break;
            case R.id.tv_pregnant:
                if (AppLogic.getInstance().getStatus() == AppLogic.USER_STATUS_TO_TRACK) {//现在的状态如果是追踪
                    DialogUtil.showDialog(this, getString(R.string.message1), new DialogUtil.OnDialogUtilListener() {
                        @Override
                        public void onDialogUtil_YES() {
                            pregnantUI();//打开怀孕ui
                            AppLogic.getInstance().setStatus(AppLogic.USER_STATUS_TO_PREGNANT);
                        }

                        @Override
                        public void onDialogUtil_CANCEL() {
                        }
                    });
                } else {//用户没有数据的时候点击，或者本来就是坏用，直接点击，不谈对话框
                    pregnantUI();
                    AppLogic.getInstance().setStatus(AppLogic.USER_STATUS_TO_PREGNANT);
                }
                break;
            case R.id.tv_track:
                if (AppLogic.getInstance().getStatus() == AppLogic.USER_STATUS_TO_PREGNANT) {//现在的状态如果是怀孕
                    DialogUtil.showDialog(this, getString(R.string.message2), new DialogUtil.OnDialogUtilListener() {
                        @Override
                        public void onDialogUtil_YES() {
                            trackUI();//打开追踪ui
                            AppLogic.getInstance().setStatus(AppLogic.USER_STATUS_TO_TRACK);
                        }

                        @Override
                        public void onDialogUtil_CANCEL() {
                        }
                    });
                    return;
                } else {//用户没有数据的时候点击
                    trackUI();
                    AppLogic.getInstance().setStatus(AppLogic.USER_STATUS_TO_TRACK);
                }
                break;
            case R.id.btn_next:
                startActivity(new Intent(this, SetCycleActivity.class));
                break;
            default:
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SysApplication.getInstance().removeActivity(this);
    }
}
