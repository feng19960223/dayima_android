package com.taiqudong.android.enayeh.activity;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.taiqudong.android.enayeh.R;
import com.taiqudong.android.enayeh.application.AppLogic;
import com.taiqudong.android.enayeh.application.SysApplication;
import com.taiqudong.android.enayeh.utils.ApiUtil;
import com.taiqudong.android.enayeh.utils.DialogUtil;
import com.taiqudong.android.enayeh.utils.StatusBarUtils;
import com.taiqudong.android.enayeh.view.NumberPickerView;

/**
 * 设置周期
 * Created by taiqudong on 2017/8/15.
 */

public class SetCycleActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_duration_day;//月经持续时间
    private TextView tv_cycle;//月经周期
    private Button btn_finish;//下一步

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setcycle);
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
        tv_duration_day = (TextView) findViewById(R.id.tv_duration_day);
        tv_cycle = (TextView) findViewById(R.id.tv_cycle);
        btn_finish = (Button) findViewById(R.id.btn_finish);
    }

    public void initListener() {
        findViewById(R.id.iv_return).setOnClickListener(this);
        findViewById(R.id.ll_duration_day).setOnClickListener(this);
        findViewById(R.id.ll_cycle).setOnClickListener(this);
        btn_finish.setOnClickListener(this);
    }

    public void initDate() {
        if (AppLogic.getInstance().getMentrualCicle() > 0) {//月经周期
            tv_cycle.setText(AppLogic.getInstance().getMentrualCicle() + getString(R.string.Days));
            nextBtnUI();//btn变红
        } else {
            tv_cycle.setText("");
        }

        if (AppLogic.getInstance().getMenstrualTime() > 0) {//月经天数
            tv_duration_day.setText(AppLogic.getInstance().getMenstrualTime() + getString(R.string.Days));
            nextBtnUI();//btn变红
        } else {
            tv_duration_day.setText("");
        }
    }

    private void nextBtnUI() {
        btn_finish.setEnabled(true);
        if (Build.VERSION.SDK_INT >= 16) {
            btn_finish.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_sbw));
        } else {
            btn_finish.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.btn_sbw));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return:
                this.finish();
                break;
            case R.id.ll_duration_day://月经天数
                showDurationDayDialog();
                break;
            case R.id.ll_cycle://月经周期
                showCycleDialog();
                break;
            case R.id.btn_finish:
                if (AppLogic.getInstance().isInitialized()) {
                    //用户信息上传
                    ApiUtil.postUserinfo(this);
                    //经期数据上传
                    ApiUtil.postMenstrualLog(this);
                }
                SysApplication.removeAllActivitiesLogin();
                break;
            default:
        }
    }

    final String[] DurationDays = {"02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14"};
    private Dialog mDurationDayDialog;
    private NumberPickerView npv_durationdays;

    private void showDurationDayDialog() {
        mDurationDayDialog = new Dialog(this, R.style.MyDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.dialog_duration_days, null);

        npv_durationdays = (NumberPickerView) root.findViewById(R.id.npv_durationdays);
        npv_durationdays.setDisplayedValues(DurationDays);
        npv_durationdays.setMinValue(0);
        npv_durationdays.setMaxValue(DurationDays.length - 1);
        npv_durationdays.setWrapSelectorWheel(false);
        npv_durationdays.setValue(5);

        root.findViewById(R.id.iv_dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDurationDayDialog.cancel();
            }
        });
        root.findViewById(R.id.tv_dialog_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_duration_day.setText(DurationDays[npv_durationdays.getValue()] + getString(R.string.Days));
                AppLogic.getInstance().setMenstrualTime(Integer.valueOf(DurationDays[npv_durationdays.getValue()]));
                nextBtnUI();
                mDurationDayDialog.cancel();
            }
        });

        mDurationDayDialog.setContentView(root);
        Window dialogWindow = DialogUtil.getDialogWindow(this, mDurationDayDialog);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();
        //        lp.alpha = 0.7f; // 透明度
        dialogWindow.setAttributes(lp);
        mDurationDayDialog.show();
    }

    final String[] Cycledays = {"15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34",
            "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56"};
    private Dialog mCycleDialog;
    private NumberPickerView npv_cycle;

    private void showCycleDialog() {
        mCycleDialog = new Dialog(this, R.style.MyDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.dialog_cycle, null);

        npv_cycle = (NumberPickerView) root.findViewById(R.id.npv_cycle);
        npv_cycle.setDisplayedValues(Cycledays);
        npv_cycle.setMinValue(0);
        npv_cycle.setMaxValue(Cycledays.length - 1);
        npv_cycle.setWrapSelectorWheel(false);
        npv_cycle.setValue(13);

        root.findViewById(R.id.iv_dialog_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCycleDialog.cancel();
            }
        });
        root.findViewById(R.id.tv_dialog_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_cycle.setText(Cycledays[npv_cycle.getValue()] + getString(R.string.Days));
                AppLogic.getInstance().setMentrualCicle(Integer.valueOf(Cycledays[npv_cycle.getValue()]));
                nextBtnUI();
                mCycleDialog.cancel();
            }
        });

        mCycleDialog.setContentView(root);
        Window dialogWindow = DialogUtil.getDialogWindow(this, mCycleDialog);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();
        //        lp.alpha = 0.7f; // 透明度
        dialogWindow.setAttributes(lp);
        mCycleDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SysApplication.getInstance().removeActivity(this);
    }
}
