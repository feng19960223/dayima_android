package com.taiqudong.android.enayeh.view.calendar.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.taiqudong.android.enayeh.R;
import com.taiqudong.android.enayeh.application.AppDay;

import java.util.Calendar;

/**
 * Created by zhangxiang on 2017/8/4.
 */

public class CalendarMonthEditAdapter extends CalendarMonthAdapter {

    private Context mContext;

    public CalendarMonthEditAdapter(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected View resetView(int position, View targetView) {
        TextView tv = (TextView) targetView.findViewById(R.id.tv);
        AppDay appDay = (AppDay) getItem(position);
        if (appDay == null) {
            return targetView;
        }
        tv.setText(String.valueOf(appDay.getDay().get(Calendar.DATE)));
        if (appDay.isToday() || appDay.isPassed()) {
            tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextTime3));
            if (appDay.isToday()) {
                tv.setText(mContext.getString(R.string.now));
                tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            }
            //            Drawable drawable3 = ContextCompat.getDrawable(mContext, R.drawable.cal_ring);
            Drawable drawable3 = ContextCompat.getDrawable(mContext, R.drawable.ic_ring_15dp);
            drawable3.setBounds(0, 0, drawable3.getMinimumWidth(), drawable3.getMinimumHeight());
            tv.setCompoundDrawables(null, null, null, drawable3);
            if (appDay.getDayType() == AppDay.DAY_TYPE_MENSTRUAL && !appDay.isPredicted()) {
                tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                Drawable drawable4 = ContextCompat.getDrawable(mContext, R.drawable.ic_check_circle_15dp);
                drawable4.setBounds(0, 0, drawable4.getMinimumWidth(), drawable4.getMinimumHeight());
                tv.setCompoundDrawables(null, null, null, drawable4);
            }
        }
        if (appDay.isFuture()) {
            tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextTime1));
            tv.setCompoundDrawables(null, null, null, null);
        }
        return targetView;
    }

}
