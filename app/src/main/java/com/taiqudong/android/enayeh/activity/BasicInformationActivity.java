package com.taiqudong.android.enayeh.activity;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.taiqudong.android.enayeh.R;
import com.taiqudong.android.enayeh.application.AppLogic;
import com.taiqudong.android.enayeh.application.SysApplication;
import com.taiqudong.android.enayeh.utils.ApiUtil;
import com.taiqudong.android.enayeh.utils.DateUtil;
import com.taiqudong.android.enayeh.utils.DialogUtil;
import com.taiqudong.android.enayeh.utils.StatusBarUtils;
import com.taiqudong.android.enayeh.view.NumberPickerView;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by taiqudong on 2017/7/7.
 * 基本信息
 */

public class BasicInformationActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_basicinformation_birth;
    private TextView tv_basicinformation_weight;
    private TextView tv_basicinformation_height;
    private ImageView iv_basicinformation_birth;
    private ImageView iv_basicinformation_weight;
    private ImageView iv_basicinformation_height;
    private Button btn_basicinformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_information);
        SysApplication.getInstance().addActivit(this);
        StatusBarUtils.statusbar(this);
        SysApplication.addActivity(this);
        initView();
        initListener();
    }

    private void initView() {
        tv_basicinformation_birth = (TextView) findViewById(R.id.tv_basicinformation_birth);
        tv_basicinformation_weight = (TextView) findViewById(R.id.tv_basicinformation_weight);
        tv_basicinformation_height = (TextView) findViewById(R.id.tv_basicinformation_height);
        iv_basicinformation_birth = (ImageView) findViewById(R.id.iv_basicinformation_birth);
        iv_basicinformation_weight = (ImageView) findViewById(R.id.iv_basicinformation_weight);
        iv_basicinformation_height = (ImageView) findViewById(R.id.iv_basicinformation_height);
        btn_basicinformation = (Button) findViewById(R.id.btn_basicinformation);
    }

    private void initListener() {
        findViewById(R.id.iv_return).setOnClickListener(this);
        tv_basicinformation_birth.setOnClickListener(this);
        tv_basicinformation_weight.setOnClickListener(this);
        tv_basicinformation_height.setOnClickListener(this);
        iv_basicinformation_birth.setOnClickListener(this);
        iv_basicinformation_weight.setOnClickListener(this);
        iv_basicinformation_height.setOnClickListener(this);
        btn_basicinformation.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return:
                this.finish();
                break;
            case R.id.tv_basicinformation_birth:
                birth();
                break;
            case R.id.tv_basicinformation_weight:
                weight();
                break;
            case R.id.tv_basicinformation_height:
                height();
                break;
            case R.id.iv_basicinformation_birth:
                birth();
                break;
            case R.id.iv_basicinformation_weight:
                weight();
                break;
            case R.id.iv_basicinformation_height:
                height();
                break;
            case R.id.btn_basicinformation:
                go();
                break;
            default:
        }
    }

    private boolean isBirth = false;
    private boolean isWeight = false;
    private boolean isHeight = false;

    GregorianCalendar gregorianCalendar = new GregorianCalendar();
    int year = gregorianCalendar.get(GregorianCalendar.YEAR);//当前年
    int month = gregorianCalendar.get(GregorianCalendar.MONTH);// 当前月
    int day = gregorianCalendar.get(GregorianCalendar.DAY_OF_MONTH);//当前日
    final String[] months = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
    final String[] years = getYears(year);//2007-1957
    private Dialog mBirthDialog;
    private NumberPickerView npv_birth_month;
    private NumberPickerView npv_birth_year;
    private NumberPickerView npv_birth_day;

    private void birth() {
        mBirthDialog = new Dialog(BasicInformationActivity.this, R.style.MyDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(BasicInformationActivity.this).inflate(
                R.layout.dialog_birth, null);
        root.findViewById(R.id.iv_dialog_birth_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBirthDialog.cancel();
            }
        });
        root.findViewById(R.id.tv_dialog_birth_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBirthDialog.cancel();
                isBirth = true;
                tv_basicinformation_birth.setText("" + months[npv_birth_month.getValue()]
                        + "/" + (npv_birth_day.getValue() + 1) + "/" + years[npv_birth_year.getValue()]);
                tv_basicinformation_birth.setTextColor(ContextCompat.getColor(BasicInformationActivity.this, R.color.colorPrimary));
                //保存好数据
                AppLogic.getInstance().setBirthday(npv_birth_year.getValue(),
                        npv_birth_month.getValue() + 1,
                        npv_birth_day.getValue() + 1);
                nextButtonYes();
            }
        });
        npv_birth_month = (NumberPickerView) root.findViewById(R.id.npv_birth_month);
        npv_birth_month.setDisplayedValues(months);
        npv_birth_month.setMinValue(0);
        npv_birth_month.setMaxValue(months.length - 1);
        npv_birth_month.setWrapSelectorWheel(false);
        npv_birth_month.setValue(5);

        npv_birth_day = (NumberPickerView) root.findViewById(R.id.npv_birth_day);
        npv_birth_day.setDisplayedValues(DateUtil.getDays());
        npv_birth_day.setMinValue(0);
        npv_birth_day.setMaxValue(29);//默认30
        npv_birth_day.setWrapSelectorWheel(false);
        npv_birth_day.setValue(14);

        npv_birth_year = (NumberPickerView) root.findViewById(R.id.npv_birth_year);
        npv_birth_year.setDisplayedValues(years);
        npv_birth_year.setMinValue(0);
        npv_birth_year.setMaxValue(50);
        npv_birth_year.setWrapSelectorWheel(false);
        npv_birth_year.setValue(40);

        //滑动年和月，改变日的值
        npv_birth_month.setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                //让3级联动的值（日）定死不动
                int value = npv_birth_day.getValue() > DateUtil.getDaysOfMonth("" + years[npv_birth_year.getValue()] + "-" + (newVal + 1)) ? DateUtil.getDaysOfMonth("" + years[npv_birth_year.getValue()] + "-" + (newVal + 1)) - 1 : npv_birth_day.getValue();
                npv_birth_day.setMaxValue(DateUtil.getDaysOfMonth("" + years[npv_birth_year.getValue()] + "-" + (newVal + 1)) - 1);
                npv_birth_day.setValue(value);
            }
        });
        npv_birth_year.setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                int value = npv_birth_day.getValue() > DateUtil.getDaysOfMonth("" + years[newVal] + "-" + (npv_birth_month.getValue() + 1)) ? DateUtil.getDaysOfMonth("" + years[newVal] + "-" + (npv_birth_month.getValue() + 1)) - 1 : npv_birth_day.getValue();
                npv_birth_day.setMaxValue(DateUtil.getDaysOfMonth("" + years[newVal] + "-" + (npv_birth_month.getValue() + 1)) - 1);
                npv_birth_day.setValue(value);
            }
        });
        mBirthDialog.setContentView(root);
        Window dialogWindow = DialogUtil.getDialogWindow(this, mBirthDialog);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();
        //        lp.alpha = 0.7f; // 透明度
        dialogWindow.setAttributes(lp);
        mBirthDialog.show();
    }

    private NumberPickerView npv_weight_positive;
    private NumberPickerView npv_weight_decimal;
    private Dialog mWeightDialog;
    final String[] positive = {"20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35",
            "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55",
            "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75",
            "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95",
            "96", "97", "98", "99", "100"};
    final String[] decimals = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

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
                isWeight = true;
                tv_basicinformation_weight.setText("" + positive[npv_weight_positive.getValue()] + "." + decimals[npv_weight_decimal.getValue()] + getString(R.string.kg));
                tv_basicinformation_weight.setTextColor(ContextCompat.getColor(BasicInformationActivity.this, R.color.colorPrimary));
                //设置身高
                AppLogic.getInstance().setHeight(npv_weight_positive.getHeight());
                nextButtonYes();
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
        npv_weight_decimal.setValue(0);


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

    final String[] heights = {"90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "100", "101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112", "113", "114", "115",
            "116", "117", "118", "119", "120", "121", "122", "123", "124", "125", "126", "127", "128", "129", "130", "131", "132", "133", "134", "135", "136", "137", "138", "139", "140",
            "141", "142", "143", "144", "145", "146", "147", "148", "149", "150", "151", "152", "153", "154", "155", "156", "157", "158", "159", "160", "161", "162", "163", "164", "165",
            "166", "167", "168", "169", "170", "171", "172", "173", "174", "175", "176", "177", "178", "179", "180", "181", "182", "183", "184", "185", "186", "187", "188", "189", "190",
            "191", "192", "193", "194", "195", "196", "197", "198", "199", "200", "201", "202", "203", "204", "205", "206", "207", "208", "209", "210", "211", "212", "213", "214", "215",
            "216", "217", "218", "219", "220", "221", "222", "223", "224", "225", "226", "227", "228", "229", "230", "231", "232", "233", "234", "235", "236", "237", "238", "239", "240",
            "241", "242", "243", "244", "245", "246", "247", "248", "249", "250",
    };
    private NumberPickerView npv_height;
    private Dialog mHeightDialog;

    private void height() {
        mHeightDialog = new Dialog(this, R.style.MyDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.dialog_height, null);

        root.findViewById(R.id.iv_dialog_height_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHeightDialog.cancel();
            }
        });
        root.findViewById(R.id.tv_dialog_height_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHeightDialog.cancel();
                isHeight = true;
                tv_basicinformation_height.setText("" + heights[npv_height.getValue()] + getString(R.string.cm));
                tv_basicinformation_height.setTextColor(ContextCompat.getColor(BasicInformationActivity.this, R.color.colorPrimary));
                //保存身高
                AppLogic.getInstance().setHeight(npv_height.getValue());
                nextButtonYes();
            }
        });
        npv_height = (NumberPickerView) root.findViewById(R.id.npv_height);
        npv_height.setDisplayedValues(heights);
        npv_height.setMinValue(0);
        npv_height.setMaxValue(heights.length - 1);
        npv_height.setWrapSelectorWheel(false);
        npv_height.setValue(73);
        mHeightDialog.setContentView(root);
        Window dialogWindow = DialogUtil.getDialogWindow(this, mHeightDialog);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();
        //        lp.alpha = 0.7f; // 透明度
        dialogWindow.setAttributes(lp);
        mHeightDialog.show();
    }

    private void go() {

        Calendar lastMenstrualDate = AppLogic.getInstance().getLastMenstrualDate();
        ContentValues contentValues = new ContentValues();
        contentValues.put("coming", true);
        Calendar today = Calendar.getInstance();
        for (int i = 0; i < AppLogic.getInstance().getMenstrualTime(); ++i) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(lastMenstrualDate.getTimeInMillis());
            cal.add(Calendar.DATE, i);
            if (cal.after(today)) {
                break;
            }
            String d = AppLogic.getInstance().getSdf().format(cal.getTime());
            AppLogic.getInstance().updateBasic(d, contentValues);
        }

        AppLogic.getInstance().afterInit(true);
        SysApplication.removeAllActivity();
        if (AppLogic.getInstance().isInitialized()) {
            ApiUtil.postUserinfo(this);
            //用户信息上传
            ApiUtil.postUserinfo(this);
        }
    }


    private void nextButtonYes() {//红色，可以点击
        if (isBirth && isHeight && isWeight) {
            btn_basicinformation.setEnabled(true);
            if (Build.VERSION.SDK_INT >= 16) {
                btn_basicinformation.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_sbw));
            } else {
                btn_basicinformation.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.btn_sbw));
            }
        }
    }

    private String[] getYears(int year) {//-10~~~-60
        String years[] = new String[51];
        for (int i = 0; i < 51; i++) {
            years[i] = "" + (year - 60 + i);
        }
        return years;
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
