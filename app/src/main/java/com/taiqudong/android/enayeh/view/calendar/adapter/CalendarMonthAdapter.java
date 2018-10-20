package com.taiqudong.android.enayeh.view.calendar.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.taiqudong.android.enayeh.R;
import com.taiqudong.android.enayeh.application.AppDay;
import com.taiqudong.android.enayeh.application.AppLogic;
import com.taiqudong.android.enayeh.utils.DateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangxiang on 2017/8/3.
 */

public class CalendarMonthAdapter extends BaseAdapter {


    private Calendar mMonth;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<AppDay> mDays;
    private Calendar mNowDay;
    private Calendar mHighlightDay;

    private Map<String, Boolean> mTmpSelect;

    private final static String TAG = "CalMAdt";

    public CalendarMonthAdapter(Context context) {

        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mDays = new ArrayList<>();
    }

    /**
     * 设置当前的月份, 设置完成后需要 call notifyDataSetChanged
     */
    public void setMonth(Calendar cal) {
        mMonth = cal;
        mDays = AppLogic.getInstance()
                .getMonth(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));
        //notifyDataSetChanged();
    }

    //, 设置完成后需要 call notifyDataSetChanged
    public void setHighLight(Calendar cal) {
        mHighlightDay = cal;
    }

    //, 设置完成后需要 call notifyDataSetChanged
    public void setNowDay(Calendar cal) {
        mNowDay = cal;
        //notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDays.size();
    }

    @Override
    public Object getItem(int position) {
        if (mDays.size() > position &&
                position >= 0) {
            return mDays.get(position);
        } else {
            Log.e(TAG, String.format("position %d not found", position));
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position % 7;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View targetView = null;
        if (convertView == null) {
            targetView = mLayoutInflater.inflate(R.layout.item_time, parent, false);
        } else {
            targetView = convertView;
        }

        return resetView(position, targetView);
    }

    protected View resetView(int position, View targetView) {
        TextView tv = (TextView) targetView.findViewById(R.id.tv);
        AppDay appDay = (AppDay) getItem(position);
        if (appDay == null) {
            return targetView;
        }
        //初始化
        tv.setText(String.valueOf(appDay.getDay().get(Calendar.DATE)));
        tv.setTextColor(Color.parseColor("#DDDDDD"));
        //设置为白色
        setViewBackground(tv, R.drawable.bg_dot7);
        if (!appDay.isFuture()) {//当前日期
            if (appDay.getDayType() == AppDay.DAY_TYPE_MENSTRUAL) {//月经期
                setViewBackground(tv, R.drawable.bg_dot1);
                tv.setTextColor(Color.parseColor("#333333"));
            } else if (appDay.getDayType() == AppDay.DAY_TYPE_OVULATION) {//排卵期
                setViewBackground(tv, R.drawable.bg_dot5);
                tv.setTextColor(Color.parseColor("#FFFFFF"));
            } else if (appDay.getDayType() == AppDay.DAY_TYPE_OVULATION_DAY) {//排卵日
                setViewBackground(tv, R.drawable.bg_dot3);
                tv.setTextColor(Color.parseColor("#FFFFFF"));
            } else {//安全期 和 其他
                if (appDay.isCurrentMonth()) {
                    tv.setTextColor(Color.parseColor("#333333"));
                }
            }
            if (AppLogic.getInstance().queryBasic(appDay).isComing()) {
                setViewBackground(tv, R.drawable.bg_dot6);
                tv.setTextColor(Color.parseColor("#FFFFFF"));
            }
        } else {//推测
            if (appDay.getDayType() == AppDay.DAY_TYPE_MENSTRUAL) {
                tv.setTextColor(Color.parseColor("#333333"));
                setViewBackground(tv, R.drawable.bg_dot1);
            } else if (appDay.getDayType() == AppDay.DAY_TYPE_OVULATION) {//排卵期
                tv.setTextColor(Color.parseColor("#C77EFF"));
            } else if (appDay.getDayType() == AppDay.DAY_TYPE_OVULATION_DAY) {//排卵日
                tv.setTextColor(Color.parseColor("#FF9F00"));
            } else if (appDay.getDayType() == AppDay.DAY_TYPE_SECURITY) {//安全期
                tv.setTextColor(Color.parseColor("#333333"));
            }
        }

        if (!appDay.isCurrentMonth()) {
            tv.setTextColor(Color.parseColor("#DDDDDD"));
            setViewBackground(tv, R.drawable.bg_dot7);
        }

        int today = DateUtil.date2int(appDay.getDay());
        //如果是今天
        if (DateUtil.date2int(mNowDay) == today) {
            tv.setText(mContext.getString(R.string.now));
        }

        if (DateUtil.date2int(mHighlightDay) == today) {
            if (appDay.isPassed() || appDay.isToday()) {//已经过去
                tv.setTextColor(Color.WHITE);
                if (appDay.getDayType() == AppDay.DAY_TYPE_MENSTRUAL) {//月经期
                    setViewBackground(tv, R.drawable.cal_selected1);
                    tv.setTextColor(Color.parseColor("#333333"));
                } else if (appDay.getDayType() == AppDay.DAY_TYPE_OVULATION) {//排卵期
                    setViewBackground(tv, R.drawable.cal_selected5);
                } else if (appDay.getDayType() == AppDay.DAY_TYPE_OVULATION_DAY) {//排卵日
                    setViewBackground(tv, R.drawable.cal_selected3);
                } else {//安全期 和 其他情况
                    tv.setTextColor(Color.parseColor("#333333"));
                    setViewBackground(tv, R.drawable.cal_selected7);
                }
                if (AppLogic.getInstance().queryBasic(appDay).isComing()) {
                    tv.setTextColor(Color.WHITE);
                    setViewBackground(tv, R.drawable.cal_selected6);
                }
            }

            if (appDay.isFuture()) {//将来
                setViewBackground(tv, R.drawable.cal_selected7);
                if (appDay.getDayType() == AppDay.DAY_TYPE_MENSTRUAL) {//月经期
                    setViewBackground(tv, R.drawable.cal_selected1);
                    tv.setTextColor(Color.parseColor("#333333"));
                } else if (appDay.getDayType() == AppDay.DAY_TYPE_OVULATION) {//排卵期
                    tv.setTextColor(Color.parseColor("#C77EFF"));
                } else if (appDay.getDayType() == AppDay.DAY_TYPE_OVULATION_DAY) {//排卵日
                    tv.setTextColor(Color.parseColor("#FF9F00"));
                } else if (appDay.getDayType() == AppDay.DAY_TYPE_SECURITY) {//安全期
                    tv.setTextColor(Color.parseColor("#333333"));
                }
            }
        }

        return targetView;
    }

    protected void setViewBackground(TextView tv, int resId) {
        if (Build.VERSION.SDK_INT >= 16) {
            tv.setBackground(ContextCompat.getDrawable(mContext, resId));
        } else {
            tv.setBackgroundDrawable(ContextCompat.getDrawable(mContext, resId));
        }
    }
}

