package com.taiqudong.android.enayeh.view.calendar;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;

import com.taiqudong.android.enayeh.view.calendar.adapter.CalendarMonthAdapter;
import com.taiqudong.android.enayeh.view.calendar.adapter.CalendarMonthEditAdapter;

import java.util.Calendar;

/**
 * 用于编辑界面的view
 * Created by zhangxiang on 2017/8/4.
 */
public class CalendarMonthEditView extends CalendarMonthView {
    public CalendarMonthEditView(Context context) {
        super(context);
    }

    public CalendarMonthEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CalendarMonthEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CalendarMonthEditView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * 通过重载这个方法 使用不同的adatper
     * @return
     */
    @Override
    protected CalendarMonthAdapter initAdapter(){
        CalendarMonthAdapter adapter = new CalendarMonthEditAdapter(getContext());
        setAdapter(adapter);
        return adapter;
    }

    public void setHighlight(Calendar cal){
    }
}
