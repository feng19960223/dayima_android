package com.taiqudong.android.enayeh.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.taiqudong.android.enayeh.R;
import com.taiqudong.android.enayeh.application.AppDay;
import com.taiqudong.android.enayeh.application.AppLogic;
import com.taiqudong.android.enayeh.utils.NotificationUtil;
import com.taiqudong.android.enayeh.utils.ReminderUtil;

import java.util.Calendar;

/**
 * 健康提醒页面，定时发送notification
 */
public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = AppLogic.getInstance().getPrefs();
        String action = intent.getAction();
        Log.i("********", "广播" + action);
        ReminderUtil.setReminder(context);

        if (action.equals(ReminderUtil.ALARM_BEGINNING_ACTION)) {
            if (AppLogic.getInstance().getReminderBEGINNING()) {
                if (AppLogic.getInstance().getTodayAppDay().getDayType() != AppDay.DAY_TYPE_MENSTRUAL) {//今天不是月经期
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DATE, 1);
                    if (AppLogic.getInstance().getAppDay(calendar).getDayType() != AppDay.DAY_TYPE_MENSTRUAL) {//明天也不是月经期
                        Calendar calendar2 = Calendar.getInstance();
                        calendar2.add(Calendar.DATE, 2);
                        if (AppLogic.getInstance().getAppDay(calendar2).getDayType() == AppDay.DAY_TYPE_MENSTRUAL) {//2天后是月经期
                            //月经2天后要来
                            NotificationUtil.showNotification(context, context.getString(R.string.BeginningTitle), context.getString(R.string.BeginningContent));
                        }
                    }
                }
            }
        } else if (action.equals(ReminderUtil.ALARM_END_ACTION)) {
            if (AppLogic.getInstance().getReminderEND()) {
                if (AppLogic.getInstance().getTodayAppDay().getDayType() == AppDay.DAY_TYPE_MENSTRUAL) {//今天是月经期
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DATE, 1);
                    if (AppLogic.getInstance().getAppDay(calendar).getDayType() == AppDay.DAY_TYPE_MENSTRUAL) {//明天也是月经期
                        Calendar calendar2 = Calendar.getInstance();
                        calendar2.add(Calendar.DATE, 2);
                        if (AppLogic.getInstance().getAppDay(calendar2).getDayType() != AppDay.DAY_TYPE_MENSTRUAL) {//2天后不是月经期
                            //月经2天后要走
                            NotificationUtil.showNotification(context, context.getString(R.string.EndTitle), context.getString(R.string.EndContent));
                        }
                    }
                }
            }
        } else if (action.equals(ReminderUtil.ALARM_MEDICATION_ACTION)) {
            if (AppLogic.getInstance().getReminderMEDICATION()) {
                AppDay appDay = AppLogic.getInstance().getTodayAppDay();
                if (appDay.getDayType() != AppDay.DAY_TYPE_MENSTRUAL) {//不是月经期
                    NotificationUtil.showNotification(context, context.getString(R.string.MedicationTitle), context.getString(R.string.MedicationContent));
                }
            }
        } else if (action.equals(ReminderUtil.ALARM_DRINK_ACTION1)) {
            if ( AppLogic.getInstance().getReminderDRINK()) {
                NotificationUtil.showNotification(context, context.getString(R.string.DrinkTitle), context.getString(R.string.DrinkContent1));
            }
        } else if (action.equals(ReminderUtil.ALARM_DRINK_ACTION2)) {
            if ( AppLogic.getInstance().getReminderDRINK()) {
                NotificationUtil.showNotification(context, context.getString(R.string.DrinkTitle), context.getString(R.string.DrinkContent2));
            }
        } else if (action.equals(ReminderUtil.ALARM_DRINK_ACTION3)) {
            if ( AppLogic.getInstance().getReminderDRINK()) {
                NotificationUtil.showNotification(context, context.getString(R.string.DrinkTitle), context.getString(R.string.DrinkContent3));
            }
        } else if (action.equals(ReminderUtil.ALARM_DRINK_ACTION4)) {
            if ( AppLogic.getInstance().getReminderDRINK()) {
                NotificationUtil.showNotification(context, context.getString(R.string.DrinkTitle), context.getString(R.string.DrinkContent4));
            }
        } else if (action.equals(ReminderUtil.ALARM_DRINK_ACTION5)) {
            if ( AppLogic.getInstance().getReminderDRINK()) {
                NotificationUtil.showNotification(context, context.getString(R.string.DrinkTitle), context.getString(R.string.DrinkContent5));
            }
        } else if (action.equals(ReminderUtil.ALARM_DRINK_ACTION6)) {
            if ( AppLogic.getInstance().getReminderDRINK()) {
                NotificationUtil.showNotification(context, context.getString(R.string.DrinkTitle), context.getString(R.string.DrinkContent6));
            }
        } else if (action.equals(ReminderUtil.ALARM_DRINK_ACTION7)) {
            if ( AppLogic.getInstance().getReminderDRINK()) {
                NotificationUtil.showNotification(context, context.getString(R.string.DrinkTitle), context.getString(R.string.DrinkContent7));
            }
        } else if (action.equals(ReminderUtil.ALARM_DRINK_ACTION8)) {
            if ( AppLogic.getInstance().getReminderDRINK()) {
                NotificationUtil.showNotification(context, context.getString(R.string.DrinkTitle), context.getString(R.string.DrinkContent8));
            }
        }
        abortBroadcast();//不再向下传递
    }
}
