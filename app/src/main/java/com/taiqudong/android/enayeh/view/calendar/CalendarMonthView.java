package com.taiqudong.android.enayeh.view.calendar;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.taiqudong.android.enayeh.application.AppDay;
import com.taiqudong.android.enayeh.utils.DateUtil;
import com.taiqudong.android.enayeh.view.calendar.adapter.CalendarMonthAdapter;

import java.util.Calendar;
import java.util.Map;

/**
 * Created by zhangxiang on 2017/8/3.
 */

public class CalendarMonthView extends GridView {

    private Calendar mCurrentMonth;
    private Calendar mNow;
    private Calendar mHightlight;

    private Context mContext;

    private OnCalendarClickListener mOnCalendarClickListener;

    //监听用户的点击
    private AdapterView.OnItemClickListener mOnItemClickListener;


    /**
     *     这个需要在 setInfo之后执行
     *     临时修改大姨妈
     */
    public void setTmpSelect(Map<String, Boolean> tmpSelect){
        if(tmpSelect == null){
            return;
        }
        CalendarMonthAdapter adapter = getCalendarMonthAdapter();
        for(int i=0; i<adapter.getCount(); ++i){
            AppDay appDay = (AppDay)adapter.getItem(i);
            String d = DateUtil.date2str(appDay.getDay());
            if(tmpSelect.containsKey(d)){
                boolean isComing = tmpSelect.get(d);
                if(isComing){
                    appDay.setDayType(AppDay.DAY_TYPE_MENSTRUAL);
                    appDay.setPredicted(false);
                }else{
                    appDay.setPredicted(true);
                    appDay.setDayType(AppDay.DAY_TYPE_NORMAL);
                }
            }
        }
    }

    public CalendarMonthView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public CalendarMonthView(Context context, AttributeSet attrs) {

        super(context, attrs);
        mContext = context;
        init();
    }

    public CalendarMonthView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CalendarMonthView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        init();
    }

    private void init(){

        mOnItemClickListener = new AdapterView.OnItemClickListener() {
            //item点击事件
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CalendarMonthView monthView = (CalendarMonthView) parent;
                CalendarMonthAdapter adapter = (CalendarMonthAdapter)monthView.getAdapter();
                if(monthView.getOnCalendarClickListener() != null){
                    AppDay appDay = (AppDay) adapter.getItem(position);
                    monthView.getOnCalendarClickListener().onClick(view, position, appDay);
                }
            }
        };
        this.setOnItemClickListener(mOnItemClickListener);
    }

    public Calendar getCurrentMonth() {
        return mCurrentMonth;
    }

    public void setInfo(Calendar currentMonth, Calendar now, Calendar highlight) {
        this.mCurrentMonth = currentMonth;
        this.mHightlight = highlight;
        this.mNow = now;

        CalendarMonthAdapter adapter = getCalendarMonthAdapter();

        if(adapter == null){
            adapter = initAdapter();
        }
        adapter.setHighLight(highlight);
        adapter.setNowDay(now);
        adapter.setMonth(currentMonth);
        adapter.notifyDataSetChanged();
    }


    protected CalendarMonthAdapter initAdapter(){

        CalendarMonthAdapter adapter = new CalendarMonthAdapter(getContext());
        setAdapter(adapter);
        return adapter;
    }

    public OnCalendarClickListener getOnCalendarClickListener() {
        return this.mOnCalendarClickListener;
    }

    public void setOnCalendarClickListener(OnCalendarClickListener mOnCalendarClickListener) {
        this.mOnCalendarClickListener = mOnCalendarClickListener;
    }

    public void setHighlight(Calendar cal){
        boolean update = DateUtil.dayEqual(mHightlight, cal);
        mHightlight = cal;
        if(getCalendarMonthAdapter() != null){
            getCalendarMonthAdapter().setHighLight(cal);
            getCalendarMonthAdapter().notifyDataSetChanged();
        }
    }

    public void setNow(Calendar cal){
        boolean update = DateUtil.dayEqual(mNow, cal);
        mNow = cal;
        if(getCalendarMonthAdapter() != null){
            getCalendarMonthAdapter().setNowDay(cal);
            getCalendarMonthAdapter().notifyDataSetChanged();
        }
    }

    public Calendar getHightlight(){
        return mHightlight;
    }

    public Calendar getNow(){
        return mNow;
    }

    protected CalendarMonthAdapter getCalendarMonthAdapter(){
        return  (CalendarMonthAdapter) getAdapter();
    }

    /**
     *
     * 日历的点击的事件
     */
    public static interface OnCalendarClickListener {
        public void onClick(View view, int position, AppDay appDay);
    }
}
