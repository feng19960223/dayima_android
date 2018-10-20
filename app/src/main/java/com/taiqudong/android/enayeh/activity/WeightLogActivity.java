package com.taiqudong.android.enayeh.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.taiqudong.android.ad.stats.EventConsts;
import com.taiqudong.android.ad.stats.EventLogger;
import com.taiqudong.android.enayeh.R;
import com.taiqudong.android.enayeh.application.AppDay;
import com.taiqudong.android.enayeh.application.AppLogic;
import com.taiqudong.android.enayeh.application.SysApplication;
import com.taiqudong.android.enayeh.utils.DateUtil;
import com.taiqudong.android.enayeh.utils.DialogUtil;
import com.taiqudong.android.enayeh.utils.StatusBarUtils;
import com.taiqudong.android.enayeh.view.NumberPickerView;
import com.taiqudong.android.enayeh.view.WeightLogView;

import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by zhangxiang on 2017/7/13.
 */

public class WeightLogActivity extends AppCompatActivity implements View.OnClickListener {
    int month = GregorianCalendar.getInstance().get(GregorianCalendar.MONTH);// 当前月
    private TextView tv_weightlog_month;
    private WeightLogView weightLogView;
    private LinearLayout ll_weightlog;
    private ImageView iv_weightlog;
    private TextView tv_weightlog_hint;
    private TextView tv_weightlog_weight;
    private List<AppDay> appDayList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_log);
        SysApplication.getInstance().addActivit(this);
        StatusBarUtils.statusbar(this);
        initView();
        initLinstener();
        initData();
    }

    private void initView() {
        tv_weightlog_month = (TextView) findViewById(R.id.tv_weightlog_month);
        weightLogView = (WeightLogView) findViewById(R.id.weightLogView);
        ll_weightlog = (LinearLayout) findViewById(R.id.ll_weightlog);
        iv_weightlog = (ImageView) findViewById(R.id.iv_weightlog);
        tv_weightlog_hint = (TextView) findViewById(R.id.tv_weightlog_hint);
        tv_weightlog_weight = (TextView) findViewById(R.id.tv_weightlog_weight);
    }

    private void initLinstener() {
        findViewById(R.id.iv_return).setOnClickListener(this);
        ll_weightlog.setOnClickListener(this);
    }

    private String[] Months = DateUtil.getMonth();

    private void initData() {
        tv_weightlog_month.setText(Months[month]);

        appDayList = AppLogic.getInstance().getWeightLog();
        weightLogView = (WeightLogView) findViewById(R.id.weightLogView);
        weightLogView.setDays(appDayList);
        for (AppDay appDay : appDayList) {
            if (appDay.isToday()) {
                if (appDay.getWeight() > 0) {
                    uiSub("" + appDay.getWeight());
                } else {
                    uiAdd();
                }
            }
        }

    }

    private void uiSub(String weight) {
        iv_weightlog.setImageResource(R.mipmap.ic_jian);
        tv_weightlog_hint.setText(getString(R.string.Yourweight));
        tv_weightlog_weight.setText("" + weight + getString(R.string.kg));
        isAddOrSub = false;
        AppLogic.getInstance().setWeight(Float.parseFloat(weight));
        setTodayWeight(weight);
    }

    private void setTodayWeight(String weight) {
        List<AppDay> days = weightLogView.getDays();
        if (days != null) {
            for (AppDay appDay : weightLogView.getDays()) {
                if (appDay.isToday()) {
                    float weight_float = Float.parseFloat(weight);
                    appDay.setWeight(weight_float);
                    if (weight_float > 0.1) {
                        WeightLogView.today = true;
                        weightLogView.setIsHighLight(true);
                    } else {
                        WeightLogView.today = false;
                        weightLogView.setIsHighLight(false);
                    }
                    weightLogView.setDays(AppLogic.getInstance().getWeightLog());
                    weightLogView.invalidate();
                    break;
                }
            }
        }
    }

    private void uiAdd() {
        iv_weightlog.setImageResource(R.mipmap.ic_jia);
        tv_weightlog_hint.setText(getString(R.string.Addweight));
        tv_weightlog_weight.setText("");
        isAddOrSub = true;
        AppLogic.getInstance().setWeight(0);
        setTodayWeight("0");
    }

    private boolean isAddOrSub = true;//true显示+ fase显示-

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        bundle.putString("desc", EventConsts.p_DianJiCiShu);
        bundle.putString("note", EventConsts.bz_TiZhongBiao);
        EventLogger.logEvent(EventConsts.e_TiZhongJiLu, bundle);
        switch (v.getId()) {
            case R.id.iv_return:
                this.finish();
                break;
            case R.id.ll_weightlog:
                if (isAddOrSub) {//+
                    weight();
                } else {//-
                    uiAdd();
                }
                break;
            default:
        }
    }

    private NumberPickerView npv_weight_positive;
    private NumberPickerView npv_weight_decimal;
    private Dialog mWeightDialog;
    final String[] positive = {"20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35",
            "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55",
            "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75",
            "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95",
            "96", "97", "98", "99", "100"};
    final String[] decimals = {".0", ".1", ".2", ".3", ".4", ".5", ".6", ".7", ".8", ".9"};

    private void weight() {
        mWeightDialog = new Dialog(this, R.style.MyDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.dialog_weight, null);
        root.findViewById(R.id.iv_dialog_weight_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWeightDialog.cancel();
            }
        });
        root.findViewById(R.id.tv_dialog_weight_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWeightDialog.cancel();
                uiSub("" + positive[npv_weight_positive.getValue()] + decimals[npv_weight_decimal.getValue()]);
                EventLogger.logEvent(EventConsts.e_TiZhongJiLu, EventConsts.p_JiLuChengGongCiShu);
            }
        });
        npv_weight_positive = (NumberPickerView) root.findViewById(R.id.npv_weight_positive);
        npv_weight_positive.setDisplayedValues(positive);
        npv_weight_positive.setMinValue(0);
        npv_weight_positive.setMaxValue(positive.length - 1);
        npv_weight_positive.setWrapSelectorWheel(false);
        npv_weight_positive.setValue(30);

        npv_weight_decimal = (NumberPickerView) root.findViewById(R.id.npv_weight_decimal);
        npv_weight_decimal.setDisplayedValues(decimals);
        npv_weight_decimal.setMinValue(0);
        npv_weight_decimal.setMaxValue(decimals.length - 1);
        npv_weight_decimal.setWrapSelectorWheel(false);
        npv_weight_decimal.setValue(5);

        mWeightDialog.setContentView(root);
        Window dialogWindow = DialogUtil.getDialogWindow(this, mWeightDialog);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();
        //        lp.alpha = 0.7f; // 透明度
        dialogWindow.setAttributes(lp);
        mWeightDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SysApplication.getInstance().removeActivity(this);
    }
}
