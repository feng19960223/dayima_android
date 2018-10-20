package com.taiqudong.android.enayeh.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.taiqudong.android.enayeh.R;
import com.taiqudong.android.enayeh.application.AppLogic;
import com.taiqudong.android.enayeh.application.SysApplication;
import com.taiqudong.android.enayeh.utils.ApiUtil;
import com.taiqudong.android.enayeh.utils.DialogUtil;
import com.taiqudong.android.enayeh.utils.ReminderUtil;
import com.taiqudong.android.enayeh.utils.StatusBarUtils;
import com.taiqudong.android.enayeh.view.NumberPickerView;

/**
 * 健康提醒
 * Created by taiqudong on 2017/7/10.
 */

public class HealthReminderActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView iv_healthremnider_beginning;
    private ImageView iv_healthremnider_end;
    private ImageView iv_healthremnider_medication;
    private ImageView iv_healthremnider_drink;
    private TextView tv_healthremnider_beginning;
    private TextView tv_healthremnider_end;
    private TextView tv_healthremnider_medication;
    private TextView tv_healthremnider_drink;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_reminder);
        SysApplication.getInstance().addActivit(this);
        StatusBarUtils.statusbar(this);
        sharedPreferences = AppLogic.getInstance().getPrefs();
        editor = sharedPreferences.edit();
        initView();
        initListener();
        initData();
    }

    private void initView() {
        iv_healthremnider_beginning = (ImageView) findViewById(R.id.iv_healthremnider_beginning);
        iv_healthremnider_end = (ImageView) findViewById(R.id.iv_healthremnider_end);
        iv_healthremnider_medication = (ImageView) findViewById(R.id.iv_healthremnider_medication);
        iv_healthremnider_drink = (ImageView) findViewById(R.id.iv_healthremnider_drink);
        tv_healthremnider_beginning = (TextView) findViewById(R.id.tv_healthremnider_beginning);
        tv_healthremnider_end = (TextView) findViewById(R.id.tv_healthremnider_end);
        tv_healthremnider_medication = (TextView) findViewById(R.id.tv_healthremnider_medication);
        tv_healthremnider_drink = (TextView) findViewById(R.id.tv_healthremnider_drink);
    }

    private void initListener() {
        findViewById(R.id.iv_return).setOnClickListener(this);
        iv_healthremnider_beginning.setOnClickListener(this);
        iv_healthremnider_end.setOnClickListener(this);
        iv_healthremnider_medication.setOnClickListener(this);
        iv_healthremnider_drink.setOnClickListener(this);
    }

    private void initData() {
        //初始iv和tv和isBeginning的内容
        isBeginning = AppLogic.getInstance().getReminderBEGINNING();
        isEnd = AppLogic.getInstance().getReminderEND();
        isMedication = AppLogic.getInstance().getReminderMEDICATION();
        isDrink = AppLogic.getInstance().getReminderDRINK();

        if (isBeginning) {
            iv_healthremnider_beginning.setImageResource(R.mipmap.ic_switch_s);
            tv_healthremnider_beginning.setVisibility(View.VISIBLE);
            String isBeginningHour = sharedPreferences.getString("isBeginningHour", "");
            String isBeginningMinute = sharedPreferences.getString("isBeginningMinute", "");
            String isBeginningAMPM = sharedPreferences.getString("isBeginningAMPM", "");
            tv_healthremnider_beginning.setText(getString(R.string.Remindtime) + isBeginningHour + ":" + isBeginningMinute + isBeginningAMPM);
        }
        if (isEnd) {
            iv_healthremnider_end.setImageResource(R.mipmap.ic_switch_s);
            tv_healthremnider_end.setVisibility(View.VISIBLE);
            String isEndHour = sharedPreferences.getString("isEndHour", "");
            String isEndMinute = sharedPreferences.getString("isEndMinute", "");
            String isEndAMPM = sharedPreferences.getString("isEndAMPM", "");
            tv_healthremnider_end.setText(getString(R.string.Remindtime) + isEndHour + ":" + isEndMinute + isEndAMPM);
        }
        if (isMedication) {
            iv_healthremnider_medication.setImageResource(R.mipmap.ic_switch_s);
            tv_healthremnider_medication.setVisibility(View.VISIBLE);
            String isMedicationHour = sharedPreferences.getString("isMedicationHour", "");
            String isMedicationMinute = sharedPreferences.getString("isMedicationMinute", "");
            String isMedicationAMPM = sharedPreferences.getString("isMedicationAMPM", "");
            tv_healthremnider_medication.setText(getString(R.string.Remindtime) + isMedicationHour + ":" + isMedicationMinute + isMedicationAMPM);
        }

        if (isDrink) {
            tv_healthremnider_drink.setVisibility(View.VISIBLE);
            iv_healthremnider_drink.setImageResource(R.mipmap.ic_switch_s);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return:
                this.finish();
                break;
            case R.id.iv_healthremnider_beginning:
                beginning();
                break;
            case R.id.iv_healthremnider_end:
                end();
                break;
            case R.id.iv_healthremnider_medication:
                medication();
                break;
            case R.id.iv_healthremnider_drink:
                drink();
                break;
            default:
        }
    }

    final String[] hours = {
            "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
            "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11"};
    final String[] minutes = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15",
            "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35",
            "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55",
            "56", "57", "58", "59"};

    private boolean isBeginning = true;
    private NumberPickerView npv_startreminding_hour;
    private NumberPickerView npv_startreminding_minute;
    private Dialog mStartDialog;

    private void beginning() {
        if (isBeginning) {//取消
            tv_healthremnider_beginning.setVisibility(View.INVISIBLE);
            iv_healthremnider_beginning.setImageResource(R.mipmap.ic_switch_d);//灰色
            isBeginning = false;
            // 取消提醒
            AppLogic.getInstance().setReminderBEGINNING(false);
        } else {//没有勾选
            if (!AppLogic.getInstance().isInitialized()) {//没有初始化
                showInitializedDialog();
                return;
            }
            iv_healthremnider_beginning.setImageResource(R.mipmap.ic_switch_s);
            //弹对话框
            mStartDialog = new Dialog(this, R.style.MyDialog);
            LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(
                    R.layout.dialog_reminding, null);
            ((TextView) root.findViewById(R.id.tv_dialog_reminding_hint)).setText(getString(R.string.StartReminding));
            root.findViewById(R.id.iv_dialog_reminding_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isBeginning) {
                        iv_healthremnider_beginning.setImageResource(R.mipmap.ic_switch_d);//灰色
                    }
                    mStartDialog.cancel();
                }
            });
            root.findViewById(R.id.tv_dialog_reminding_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isBeginning = true;
                    tv_healthremnider_beginning.setVisibility(View.VISIBLE);
                    tv_healthremnider_beginning.setText(getString(R.string.StartReminding) + hours[npv_startreminding_hour.getValue()] + ":" + minutes[npv_startreminding_minute.getValue()] + (((npv_startreminding_hour.getValue()) < 12) ? getString(R.string.am) : getString(R.string.pm)));
                    AppLogic.getInstance().setReminderBEGINNING(true);
                    editor.putString("isBeginningHour", "" + hours[npv_startreminding_hour.getValue()]);
                    editor.putString("isBeginningMinute", "" + minutes[npv_startreminding_minute.getValue()]);
                    editor.putString("isBeginningAMPM", "" + (((npv_startreminding_hour.getValue()) < 12) ? getString(R.string.am) : getString(R.string.pm)));
                    editor.apply();
                    ReminderUtil.setReminder(HealthReminderActivity.this);
                    mStartDialog.cancel();
                }
            });
            npv_startreminding_hour = (NumberPickerView) root.findViewById(R.id.npv_reminding_hour);
            npv_startreminding_hour.setDisplayedValues(hours);
            npv_startreminding_hour.setMinValue(0);
            npv_startreminding_hour.setMaxValue(hours.length - 1);
            npv_startreminding_hour.setWrapSelectorWheel(false);
            npv_startreminding_hour.setValue(10);
            npv_startreminding_hour.setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                    if (newVal < 12) {
                        npv_startreminding_hour.setHintText(getString(R.string.am));
                    } else {
                        npv_startreminding_hour.setHintText(getString(R.string.pm));
                    }
                }
            });
            npv_startreminding_minute = (NumberPickerView) root.findViewById(R.id.npv_reminding_minute);
            npv_startreminding_minute.setDisplayedValues(minutes);
            npv_startreminding_minute.setMinValue(0);
            npv_startreminding_minute.setMaxValue(minutes.length - 1);
            npv_startreminding_minute.setValue(0);

            mStartDialog.setContentView(root);
            Window dialogWindow = DialogUtil.getDialogWindow(this, mStartDialog);
            WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            lp.x = 0; // 新位置X坐标
            lp.y = 0; // 新位置Y坐标
            lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
            root.measure(0, 0);
            lp.height = root.getMeasuredHeight();
            //        lp.alpha = 0.7f; // 透明度
            dialogWindow.setAttributes(lp);
            mStartDialog.show();
        }

    }

    private boolean isEnd = true;
    private NumberPickerView npv_endreminding_hour;
    private NumberPickerView npv_endreminding_minute;
    private Dialog mEndDialog;

    private void end() {
        if (isEnd) {//取消
            tv_healthremnider_end.setVisibility(View.INVISIBLE);
            iv_healthremnider_end.setImageResource(R.mipmap.ic_switch_d);//灰色
            isEnd = false;
            AppLogic.getInstance().setReminderEND(false);
        } else {//没有勾选
            if (!AppLogic.getInstance().isInitialized()) {//没有初始化
                showInitializedDialog();
                return;
            }
            iv_healthremnider_end.setImageResource(R.mipmap.ic_switch_s);
            //弹对话框
            mEndDialog = new Dialog(this, R.style.MyDialog);
            LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(
                    R.layout.dialog_reminding, null);
            ((TextView) root.findViewById(R.id.tv_dialog_reminding_hint)).setText(getString(R.string.Endreminding));
            root.findViewById(R.id.iv_dialog_reminding_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isEnd) {
                        iv_healthremnider_end.setImageResource(R.mipmap.ic_switch_d);//灰色
                    }
                    mEndDialog.cancel();
                }
            });
            root.findViewById(R.id.tv_dialog_reminding_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isEnd = true;
                    tv_healthremnider_end.setVisibility(View.VISIBLE);
                    tv_healthremnider_end.setText(getString(R.string.Remindtime) + hours[npv_endreminding_hour.getValue()] + ":" + minutes[npv_endreminding_minute.getValue()] + (((npv_endreminding_hour.getValue()) < 12) ? getString(R.string.am) : getString(R.string.pm)));
                    AppLogic.getInstance().setReminderEND(true);
                    editor.putString("isEndHour", "" + hours[npv_endreminding_hour.getValue()]);
                    editor.putString("isEndMinute", "" + minutes[npv_endreminding_minute.getValue()]);
                    editor.putString("isEndAMPM", "" + (((npv_endreminding_minute.getValue()) < 12) ? getString(R.string.am) : getString(R.string.pm)));
                    editor.apply();
                    ReminderUtil.setReminder(HealthReminderActivity.this);
                    mEndDialog.cancel();
                }
            });
            npv_endreminding_hour = (NumberPickerView) root.findViewById(R.id.npv_reminding_hour);
            npv_endreminding_hour.setDisplayedValues(hours);
            npv_endreminding_hour.setMinValue(0);
            npv_endreminding_hour.setMaxValue(hours.length - 1);
            npv_endreminding_hour.setWrapSelectorWheel(false);
            npv_endreminding_hour.setValue(20);
            npv_endreminding_hour.setHintText(getString(R.string.pm));
            npv_endreminding_hour.setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                    if (newVal < 12) {
                        npv_endreminding_hour.setHintText(getString(R.string.am));
                    } else {
                        npv_endreminding_hour.setHintText(getString(R.string.pm));
                    }
                }
            });
            npv_endreminding_minute = (NumberPickerView) root.findViewById(R.id.npv_reminding_minute);
            npv_endreminding_minute.setDisplayedValues(minutes);
            npv_endreminding_minute.setMinValue(0);
            npv_endreminding_minute.setMaxValue(minutes.length - 1);
            npv_endreminding_minute.setValue(0);

            mEndDialog.setContentView(root);
            Window dialogWindow = DialogUtil.getDialogWindow(this, mEndDialog);
            WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            lp.x = 0; // 新位置X坐标
            lp.y = 0; // 新位置Y坐标
            lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
            root.measure(0, 0);
            lp.height = root.getMeasuredHeight();
            //        lp.alpha = 0.7f; // 透明度
            dialogWindow.setAttributes(lp);
            mEndDialog.show();
        }

    }

    private boolean isMedication = true;
    private NumberPickerView npv_medicationreminding_hour;
    private NumberPickerView npv_medicationreminding_minute;
    private Dialog mMedicationDialog;

    private void medication() {
        if (isMedication) {//取消
            tv_healthremnider_medication.setVisibility(View.INVISIBLE);
            iv_healthremnider_medication.setImageResource(R.mipmap.ic_switch_d);//灰色
            isMedication = false;
            AppLogic.getInstance().setReminderMEDICATION(false);
        } else {//没有勾选
            if (!AppLogic.getInstance().isInitialized()) {//没有初始化
                showInitializedDialog();
                return;
            }
            iv_healthremnider_medication.setImageResource(R.mipmap.ic_switch_s);
            //弹对话框
            mMedicationDialog = new Dialog(this, R.style.MyDialog);
            LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(
                    R.layout.dialog_reminding, null);
            ((TextView) root.findViewById(R.id.tv_dialog_reminding_hint)).setText(getString(R.string.Endreminding));
            root.findViewById(R.id.iv_dialog_reminding_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isMedication) {
                        iv_healthremnider_medication.setImageResource(R.mipmap.ic_switch_d);//灰色
                    }
                    mMedicationDialog.cancel();
                }
            });
            root.findViewById(R.id.tv_dialog_reminding_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isMedication = true;
                    tv_healthremnider_medication.setVisibility(View.VISIBLE);
                    tv_healthremnider_medication.setText(getString(R.string.Remindtime) + hours[npv_medicationreminding_hour.getValue()] + ":" + minutes[npv_medicationreminding_minute.getValue()] + (((npv_medicationreminding_hour.getValue()) < 12) ? getString(R.string.am) : getString(R.string.pm)));
                    AppLogic.getInstance().setReminderMEDICATION(true);
                    editor.putString("isMedicationHour", "" + hours[npv_medicationreminding_hour.getValue()]);
                    editor.putString("isMedicationMinute", "" + minutes[npv_medicationreminding_minute.getValue()]);
                    editor.putString("isMedicationAMPM", "" + (((npv_medicationreminding_hour.getValue()) < 12) ? getString(R.string.am) : getString(R.string.pm)));
                    editor.apply();
                    ReminderUtil.setReminder(HealthReminderActivity.this);
                    mMedicationDialog.cancel();
                }
            });
            npv_medicationreminding_hour = (NumberPickerView) root.findViewById(R.id.npv_reminding_hour);
            npv_medicationreminding_hour.setDisplayedValues(hours);
            npv_medicationreminding_hour.setMinValue(0);
            npv_medicationreminding_hour.setMaxValue(hours.length - 1);
            npv_medicationreminding_hour.setWrapSelectorWheel(false);
            npv_medicationreminding_hour.setValue(8);
            npv_medicationreminding_hour.setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPickerView picker, int oldVal, int newVal) {
                    if (newVal < 12) {
                        npv_medicationreminding_hour.setHintText(getString(R.string.am));
                    } else {
                        npv_medicationreminding_hour.setHintText(getString(R.string.pm));
                    }
                }
            });
            npv_medicationreminding_minute = (NumberPickerView) root.findViewById(R.id.npv_reminding_minute);
            npv_medicationreminding_minute.setDisplayedValues(minutes);
            npv_medicationreminding_minute.setMinValue(0);
            npv_medicationreminding_minute.setMaxValue(minutes.length - 1);
            npv_medicationreminding_minute.setValue(0);

            mMedicationDialog.setContentView(root);
            Window dialogWindow = DialogUtil.getDialogWindow(this, mMedicationDialog);
            WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            lp.x = 0; // 新位置X坐标
            lp.y = 0; // 新位置Y坐标
            lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
            root.measure(0, 0);
            lp.height = root.getMeasuredHeight();
            //        lp.alpha = 0.7f; // 透明度
            dialogWindow.setAttributes(lp);
            mMedicationDialog.show();
        }
    }

    private boolean isDrink = true;

    private void drink() {
        if (isDrink) {
            tv_healthremnider_drink.setVisibility(View.INVISIBLE);
            iv_healthremnider_drink.setImageResource(R.mipmap.ic_switch_d);//灰色
            isDrink = false;
            AppLogic.getInstance().setReminderDRINK(false);
        } else {
            tv_healthremnider_drink.setVisibility(View.VISIBLE);
            iv_healthremnider_drink.setImageResource(R.mipmap.ic_switch_s);
            isDrink = true;
            AppLogic.getInstance().setReminderDRINK(true);
            ReminderUtil.setReminder(HealthReminderActivity.this);
        }
    }

    private void showInitializedDialog() {
        DialogUtil.showDialog(this, getString(R.string.messageBianji), new DialogUtil.OnDialogUtilListener() {
            @Override
            public void onDialogUtil_YES() {
                startActivity(new Intent(HealthReminderActivity.this, SelectStatusActivity.class));
            }

            @Override
            public void onDialogUtil_CANCEL() {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (AppLogic.getInstance().isInitialized()) {
            //用户信息上传
            ApiUtil.postUserinfo(this);
            //经期数据上传
            ApiUtil.postMenstrualLog(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SysApplication.getInstance().removeActivity(this);
    }
}
