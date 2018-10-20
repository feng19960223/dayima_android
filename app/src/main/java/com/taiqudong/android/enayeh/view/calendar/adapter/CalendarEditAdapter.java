package com.taiqudong.android.enayeh.view.calendar.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.taiqudong.android.enayeh.R;
import com.taiqudong.android.enayeh.view.calendar.CalendarMonthEditView;
import com.taiqudong.android.enayeh.view.calendar.CalendarMonthView;

/**
 * Created by zhangxiang on 2017/8/4.
 */

public class CalendarEditAdapter extends CalendarAdapter {


    public CalendarEditAdapter(Context context) {
        super(context);
    }

    @Override
    protected CalendarMonthView initMonthView() {
        return (CalendarMonthEditView) View.inflate(getContext(), R.layout.item_calendar_edit, null);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        mCurrentViewHolder = (ViewHolder) object;
    }

    /**
     @Override protected void setMonthList() {

     List<CalendarMonthView> monthList = new ArrayList<CalendarMonthView>();
     for (int i = 0; i < PRELOAD_VIEWS; ++i) {
     CalendarMonthView monthView = (CalendarMonthEditView) View.inflate(getContext(), R.layout.item_calendar_edit, null);
     monthList.add(monthView);
     }

     setMonthList(monthList);
     }
     **/
}
