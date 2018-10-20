package com.taiqudong.android.enayeh.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import com.taiqudong.android.enayeh.R;
import com.taiqudong.android.enayeh.application.AppLogic;
import com.taiqudong.android.enayeh.application.SysApplication;

import java.util.Calendar;

/**
 * Created by taiqudong on 2017/8/4.
 */

public class ReminderUtil {
    public static final int ALARM_BEGINNING_REQUEST_CODE = 10001;
    public static final int ALARM_END_REQUEST_CODE = 10002;
    public static final int ALARM_MEDICATION_REQUEST_CODE = 10003;
    public static final int ALARM_DRINK_REQUEST_CODE1 = 10004;
    public static final int ALARM_DRINK_REQUEST_CODE2 = 10005;
    public static final int ALARM_DRINK_REQUEST_CODE3 = 10006;
    public static final int ALARM_DRINK_REQUEST_CODE4 = 10007;
    public static final int ALARM_DRINK_REQUEST_CODE5 = 10008;
    public static final int ALARM_DRINK_REQUEST_CODE6 = 10009;
    public static final int ALARM_DRINK_REQUEST_CODE7 = 10010;
    public static final int ALARM_DRINK_REQUEST_CODE8 = 10011;
    public static final String ALARM_BEGINNING_ACTION = "com.taiqudong.android.enayeh.service.beginning";
    public static final String ALARM_END_ACTION = "com.taiqudong.android.enayeh.service.end";
    public static final String ALARM_MEDICATION_ACTION = "com.taiqudong.android.enayeh.service.medication";
    public static final String ALARM_DRINK_ACTION1 = "com.taiqudong.android.enayeh.service.drink1";
    public static final String ALARM_DRINK_ACTION2 = "com.taiqudong.android.enayeh.service.drink2";
    public static final String ALARM_DRINK_ACTION3 = "com.taiqudong.android.enayeh.service.drink3";
    public static final String ALARM_DRINK_ACTION4 = "com.taiqudong.android.enayeh.service.drink4";
    public static final String ALARM_DRINK_ACTION5 = "com.taiqudong.android.enayeh.service.drink5";
    public static final String ALARM_DRINK_ACTION6 = "com.taiqudong.android.enayeh.service.drink6";
    public static final String ALARM_DRINK_ACTION7 = "com.taiqudong.android.enayeh.service.drink7";
    public static final String ALARM_DRINK_ACTION8 = "com.taiqudong.android.enayeh.service.drink8";

    public static void setReminder(Context mContext) {
        SharedPreferences sharedPreferences = AppLogic.getInstance().getPrefs();
        //提醒时间
        long h = 60 * 60 * 1000;//hour
        long m = 60 * 1000;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        //周期
        long intervalMillis = 24 * 60 * 60 * 1000;//1天
        long T1970_now = cal.getTimeInMillis();//到今天的0点的时间

        long triggerAtMillis1 = T1970_now;
        String isBeginningHour = sharedPreferences.getString("isBeginningHour", "10");
        String isBeginningMinute = sharedPreferences.getString("isBeginningMinute", "30");
        String isBeginningAMPM = sharedPreferences.getString("isBeginningAMPM", SysApplication.getContext().getString(R.string.am));
        int BeginningHour = Integer.parseInt(isBeginningHour);
        int BeginningMinute = Integer.parseInt(isBeginningMinute);
        triggerAtMillis1 += BeginningHour * h;
        triggerAtMillis1 += BeginningMinute * m;
        if (SysApplication.getContext().getString(R.string.pm).equals(isBeginningAMPM)) {
            triggerAtMillis1 += 12 * h;
        }

        long triggerAtMillis2 = T1970_now;
        String isEndHour = sharedPreferences.getString("isEndHour", "10");
        String isEndMinute = sharedPreferences.getString("isEndMinute", "30");
        String isEndAMPM = sharedPreferences.getString("isEndAMPM", SysApplication.getContext().getString(R.string.am));
        int EndHour = Integer.parseInt(isEndHour);
        int EndMinute = Integer.parseInt(isEndMinute);
        triggerAtMillis2 += EndHour * h;
        triggerAtMillis2 += EndMinute * m;
        if (SysApplication.getContext().getString(R.string.pm).equals(isEndAMPM)) {
            triggerAtMillis2 += 12 * h;
        }
        long triggerAtMillis3 = T1970_now;
        String isMedicationHour = sharedPreferences.getString("isMedicationHour", "10");
        String isMedicationMinute = sharedPreferences.getString("isMedicationMinute", "30");
        String isMedicationAMPM = sharedPreferences.getString("isMedicationAMPM", SysApplication.getContext().getString(R.string.am));
        int MedicationHour = Integer.parseInt(isMedicationHour);
        int MedicationMinute = Integer.parseInt(isMedicationMinute);
        triggerAtMillis3 += MedicationHour * h;
        triggerAtMillis3 += MedicationMinute * m;
        if (SysApplication.getContext().getString(R.string.pm).equals(isMedicationAMPM)) {
            triggerAtMillis3 += 12 * h;
        }

        setReminder(mContext, ALARM_BEGINNING_ACTION, ALARM_BEGINNING_REQUEST_CODE, triggerAtMillis1, intervalMillis);//周期月经的周期
        setReminder(mContext, ALARM_END_ACTION, ALARM_END_REQUEST_CODE, triggerAtMillis2, intervalMillis);
        setReminder(mContext, ALARM_MEDICATION_ACTION, ALARM_MEDICATION_REQUEST_CODE, triggerAtMillis3, intervalMillis);//周期1天

        setReminder(mContext, ALARM_DRINK_ACTION1, ALARM_DRINK_REQUEST_CODE1, T1970_now + 8 * h, intervalMillis);
        setReminder(mContext, ALARM_DRINK_ACTION2, ALARM_DRINK_REQUEST_CODE2, T1970_now + 9 * h, intervalMillis);
        setReminder(mContext, ALARM_DRINK_ACTION3, ALARM_DRINK_REQUEST_CODE3, T1970_now + 11 * h + h / 2, intervalMillis);
        setReminder(mContext, ALARM_DRINK_ACTION4, ALARM_DRINK_REQUEST_CODE4, T1970_now + 13 * h + h / 2, intervalMillis);
        setReminder(mContext, ALARM_DRINK_ACTION5, ALARM_DRINK_REQUEST_CODE5, T1970_now + 15 * h + h / 2, intervalMillis);
        setReminder(mContext, ALARM_DRINK_ACTION6, ALARM_DRINK_REQUEST_CODE6, T1970_now + 17 * h + h / 2, intervalMillis);
        setReminder(mContext, ALARM_DRINK_ACTION7, ALARM_DRINK_REQUEST_CODE7, T1970_now + 19 * h, intervalMillis);
        setReminder(mContext, ALARM_DRINK_ACTION8, ALARM_DRINK_REQUEST_CODE8, T1970_now + 20 * h + h / 4, intervalMillis);
        //        long t = System.currentTimeMillis();
        //        setReminder(mContext, ALARM_DRINK_ACTION1, ALARM_DRINK_REQUEST_CODE1, t + m * 1, intervalMillis);
        //        setReminder(mContext, ALARM_DRINK_ACTION2, ALARM_DRINK_REQUEST_CODE2, t + m * 6, intervalMillis);
        //        setReminder(mContext, ALARM_DRINK_ACTION3, ALARM_DRINK_REQUEST_CODE3, t + m * 11, intervalMillis);
        //        setReminder(mContext, ALARM_DRINK_ACTION4, ALARM_DRINK_REQUEST_CODE4, t + m * 16, intervalMillis);
        //        setReminder(mContext, ALARM_DRINK_ACTION5, ALARM_DRINK_REQUEST_CODE5, t + m * 21, intervalMillis);
        //        setReminder(mContext, ALARM_DRINK_ACTION6, ALARM_DRINK_REQUEST_CODE6, t + m * 26, intervalMillis);
        //        setReminder(mContext, ALARM_DRINK_ACTION7, ALARM_DRINK_REQUEST_CODE7, t + m * 31, intervalMillis);
        //        setReminder(mContext, ALARM_DRINK_ACTION8, ALARM_DRINK_REQUEST_CODE8, t + m * 36, intervalMillis);
    }

    public static void setReminder(Context context, String action, int requestCode,
                                   long triggerAtMillis, long intervalMillis) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(action);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);//表示包含未启动的App
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0);
        alarmManager.cancel(pendingIntent);//设置前取消取消
        //设置
        long day = 24 * 60 * 60 * 1000;//24hour
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//19
            if (triggerAtMillis > System.currentTimeMillis()) {//如果大于当前时间
                alarmManager.setWindow(AlarmManager.RTC_WAKEUP, triggerAtMillis, intervalMillis, pendingIntent);
            } else {//如果小于当前时间，往后推1天
                alarmManager.setWindow(AlarmManager.RTC_WAKEUP, triggerAtMillis + day, intervalMillis, pendingIntent);
            }
        } else {
            if (triggerAtMillis > System.currentTimeMillis()) {//如果大于当前时间
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, intervalMillis, pendingIntent);
            } else {//如果小于当前时间，往后推1天
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis + day, intervalMillis, pendingIntent);
            }
        }
    }

}
