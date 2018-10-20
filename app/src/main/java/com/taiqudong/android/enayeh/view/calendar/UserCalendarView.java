package com.taiqudong.android.enayeh.view.calendar;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import com.taiqudong.android.enayeh.application.AppDay;
import com.taiqudong.android.enayeh.utils.DateUtil;
import com.taiqudong.android.enayeh.utils.Log;
import com.taiqudong.android.enayeh.view.calendar.adapter.CalendarAdapter;
import com.taiqudong.android.enayeh.view.calendar.adapter.CalendarMonthAdapter;

import java.util.Calendar;
import java.util.Map;

/**
 * Created by zhangxiang on 2017/8/3.
 */

public class UserCalendarView extends ViewPager {


    private CalendarMonthView.OnCalendarClickListener mOnCalendarClickListener;
    private OnCalendarChangeListener mOnCalendarChangeListener;


    public UserCalendarView(Context context) {
        super(context);
    }

    public UserCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    protected CalendarAdapter createAdapter() {
        return new CalendarAdapter(getContext());
    }

    public void init() {
        init(null);
    }

    //初始化
    public void init(CalendarAdapter adapter0) {
        if (adapter0 == null) {
            adapter0 = createAdapter();
        }
        final CalendarAdapter adapter = adapter0;
        final int pos = adapter.getMiddlePos();
        adapter.setNow(Calendar.getInstance());
        adapter.setHighlight(Calendar.getInstance());
        setAdapter(adapter);
        setCurrentItem(adapter.getMiddlePos());

        addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (getOnCalendarChangeListener() != null) {
                    CalendarMonthView view = adapter.getPrimaryView();
                    Calendar cal = getCanlendarAdapter().pos2Calendar(position);//得到被选中的月
                    if (view != null && view.getHightlight() != null) {
                        int max = cal.getActualMaximum(Calendar.DAY_OF_MONTH);//当月的最大天
                        int hightlight = view.getHightlight().get(Calendar.DATE);//被选中的
                        if(hightlight>max){//如果用户选中的高亮的天，比本月最多天大，则高亮显示本月最大天
                            cal.set(Calendar.DATE, max);
                            setHightlight(cal);
                        }else {
                            cal.set(Calendar.DATE, hightlight);//得到被选中的日
                        }
                    }
                    getOnCalendarChangeListener().OnSelectedChange(cal);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //滚动到某一个月
    public void setCurrentItem(Calendar cal) {
        CalendarAdapter adapter = getCanlendarAdapter();
        int pos = adapter.calendar2Pos(cal);
        Log.d("FFFF", String.format("pos %d y %d m %d", pos, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)));
        setCurrentItem(pos);
    }

    //监听日期的点击事件


    public CalendarMonthView.OnCalendarClickListener getOnCalendarClickListener() {
        return this.mOnCalendarClickListener;
    }

    public void setOnCalendarClickListener(CalendarMonthView.OnCalendarClickListener onCalendarClickListener) {
        this.mOnCalendarClickListener = onCalendarClickListener;
        CalendarAdapter adapter = (CalendarAdapter) getAdapter();
        adapter.setOnCalendarClickListener(onCalendarClickListener);
    }

    public OnCalendarChangeListener getOnCalendarChangeListener() {
        return mOnCalendarChangeListener;
    }

    //设置重点标注的日期
    public void setHightlight(Calendar cal) {

        getCanlendarAdapter().setHighlight(cal);
    }

    //用户选择的日期改变
    public void setOnCalendarChangeListener(OnCalendarChangeListener mOnCalendarChangeListener) {
        this.mOnCalendarChangeListener = mOnCalendarChangeListener;
    }

    private CalendarAdapter getCanlendarAdapter() {
        return (CalendarAdapter) getAdapter();
    }

    public interface OnCalendarChangeListener {
        /**
         * 表示当前月份改变
         *
         * @param cal
         */
        void OnSelectedChange(Calendar cal);
    }

    /**
     * 选中这天来大姨妈
     * true 则是选中 来大姨妈， false则取消选中
     *
     * @param cal
     */
    public void selectMenstrual(Calendar cal) {
        for (int i = 0; i < getChildCount(); ++i) {
            CalendarMonthView calendarMonthView = (CalendarMonthView) getChildAt(i);
            CalendarMonthAdapter adapter = calendarMonthView.getCalendarMonthAdapter();
            String d = DateUtil.date2str(cal);
            int totalDay = adapter.getCount();
            for (int j = 0; j < totalDay; ++j) {
                AppDay appDay = (AppDay) adapter.getItem(j);
                //                Log.i(i + "*********T", DateUtil.date2str(appDay.getDay()));
                if (!DateUtil.dayEqual(cal, appDay.getDay())) {
                    continue;
                }
                //如果今天已经标记来大姨妈
                if (appDay.getDayType() == AppDay.DAY_TYPE_MENSTRUAL && !appDay.isPredicted()) {
                    appDay.setDayType(AppDay.DAY_TYPE_NORMAL);
                    getCanlendarAdapter().getTmpSelect().put(d, false);
                } else {
                    appDay.setDayType(AppDay.DAY_TYPE_MENSTRUAL);
                    appDay.setPredicted(false);
                    getCanlendarAdapter().getTmpSelect().put(d, true);
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    public Map<String, Boolean> getSelectResult() {
        return getCanlendarAdapter().getTmpSelect();
    }

    /**
     * 强制刷新所有数据
     */
    public void totalRefresh() {
        getCanlendarAdapter().getTmpSelect().clear();
        getCanlendarAdapter().notifyDataSetChanged();
    }
}
