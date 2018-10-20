package com.taiqudong.android.enayeh.view.calendar.adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.taiqudong.android.enayeh.R;
import com.taiqudong.android.enayeh.application.AppDay;
import com.taiqudong.android.enayeh.utils.DateUtil;
import com.taiqudong.android.enayeh.utils.Log;
import com.taiqudong.android.enayeh.view.calendar.CalendarMonthView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangxiang on 2017/8/3.
 */

public class CalendarAdapter extends PagerAdapter {

    public static final int MAX_MONTH = 200; //最大
    public static final int PRELOAD_VIEWS = 5;

    private int mMiddlePos;

    private Calendar mMiddleMonth;

    private List<CalendarMonthView> mMonthList;
    private List<CalendarMonthView> mMonthListExtra;

    private int[] mViewUsed;

    private Context mContext;

    private final static String TAG = "CalAdt";


    private Calendar mHighlight;
    private Calendar mNow;

    protected ViewHolder mCurrentViewHolder;

    private int mDirection;

    //临时
    private Map<String, Boolean> mTmpSelect;

    private int mGeneration = 0;

    public CalendarAdapter(Context context) {
        mMiddlePos = MAX_MONTH / 2;
        mMiddleMonth = Calendar.getInstance();
        mMiddleMonth.set(Calendar.DATE, 1);
        mMiddleMonth.set(Calendar.HOUR_OF_DAY, 0);
        mMiddleMonth.set(Calendar.MINUTE, 0);
        mMiddleMonth.set(Calendar.SECOND, 0);
        mMiddleMonth.set(Calendar.MILLISECOND, 0);

        mContext = context;
        mViewUsed = new int[]{0, 0, 0, 0, 0};

        mTmpSelect = new HashMap<>();
        mMonthListExtra = new ArrayList<>();

        Configuration config = context.getResources().getConfiguration();
        mDirection = config.getLayoutDirection();
        init();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        mCurrentViewHolder = (ViewHolder) object;

        CalendarMonthAdapter adapter = (CalendarMonthAdapter)mCurrentViewHolder.getMonthView().getAdapter();
        Calendar cal = mCurrentViewHolder.getMonthView().getHightlight();
        for(int i=0; i < adapter.getCount(); ++i){
            //查找应该高亮的view， 出发点击
            AppDay appDay = (AppDay) adapter.getItem(i);
            if(DateUtil.dayEqual(cal, appDay.getDay())){
                View v = mCurrentViewHolder.getMonthView().getChildAt(i);
                if(v != null){
                    mCurrentViewHolder.getMonthView().performItemClick(v, i, i%7);
                }
                break;
            }
        }
    }

    public CalendarMonthView getPrimaryView() {
        if (mCurrentViewHolder == null) {
            return null;
        } else {
            return mCurrentViewHolder.getMonthView();
        }
    }

    public void setTmpSelect(Map<String, Boolean> tmpSelect) {
        mTmpSelect = tmpSelect;
    }

    public Map<String, Boolean> getTmpSelect() {
        return mTmpSelect;
    }

    private void init() {
        this.setMonthList();
    }

    //给继承的方法来调用
    protected void setMonthList(List<CalendarMonthView> monthList) {
        mMonthList = monthList;
    }


    protected CalendarMonthView initMonthView() {
        return (CalendarMonthView) View.inflate(mContext, R.layout.item_gridview, null);
    }

    protected void setMonthList() {
        mMonthList = new ArrayList<CalendarMonthView>();

        for (int i = 0; i < PRELOAD_VIEWS; ++i) {
            CalendarMonthView monthView = (CalendarMonthView) initMonthView();
            mMonthList.add(monthView);
        }
    }

    public void setOnCalendarClickListener(CalendarMonthView.OnCalendarClickListener listener) {
        for (CalendarMonthView monthView : mMonthList) {
            monthView.setOnCalendarClickListener(listener);
        }
    }

    public void setHighlight(Calendar mHighlight) {
        this.mHighlight = mHighlight;
        for (CalendarMonthView monthView : mMonthList) {
            Calendar hi = DateUtil.copy(monthView.getCurrentMonth());
            if (hi == null) {
                monthView.setHighlight(mHighlight);
            } else {
                hi.set(Calendar.DATE, mHighlight.get(Calendar.DATE));
                monthView.setHighlight(hi);
            }

        }
    }

    public void setNow(Calendar mNow) {
        this.mNow = mNow;
    }


    @Override
    public int getCount() {
        return MAX_MONTH;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {

        if (view == null || object == null) {
            return false;
        }

        CalendarMonthView monthView = (CalendarMonthView) view;
        ViewHolder viewHolder = (ViewHolder) object;
        return viewHolder.getMonthView() == monthView;

    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        mDirection = container.getLayoutDirection();
        //Log.d(TAG, "mDirection " + (mDirection == View.LAYOUT_DIRECTION_RTL ? "rtl":"ltr"));

        Calendar cal = pos2Calendar(position);
        CalendarMonthView view = findAvailableView();
        if (view == null) {
            Log.w(TAG, "no view get");
            return null;
        }
        Calendar hi = DateUtil.copy(cal);
        hi.set(Calendar.DATE, mHighlight.get(Calendar.DATE));
        view.setInfo(cal, mNow, hi);
        view.setTmpSelect(getTmpSelect());
        container.addView(view);
        ViewHolder viewHolder = new ViewHolder(cal, view);
        viewHolder.setCurrentGeneration(mGeneration);
        return viewHolder;
    }

    @Override
    public int getItemPosition(Object object) {
        ViewHolder viewHolder = (ViewHolder) object;
        if (mGeneration == viewHolder.getCurrentGeneration()) {
            return POSITION_UNCHANGED;
        } else {
            return POSITION_NONE;
        }
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewHolder viewHolder = (ViewHolder) object;
        if (viewHolder == null) {
            return;
        }
        container.removeView(viewHolder.getMonthView());
        int idx = mMonthList.indexOf(viewHolder.getMonthView());
        if (idx >= 0 && idx < mViewUsed.length) {
            mViewUsed[idx] = 0;
        } else {
            mMonthListExtra.remove(viewHolder.getMonthView());
        }
    }

    protected CalendarMonthView findAvailableView() {
        for (int i = 0; i < PRELOAD_VIEWS; ++i) {
            if (mViewUsed[i] == 0) {
                mViewUsed[i] = 1;
                return mMonthList.get(i);
            }
        }
        Log.d(TAG, "no available view");
        CalendarMonthView view = initMonthView();
        mMonthListExtra.add(view);
        return view;
    }

    public int getMiddlePos() {
        return mMiddlePos;
    }


    public int calendar2Pos(Calendar calendar) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(calendar.getTimeInMillis());
        int pos = 0;
        int step = 0;
        if (cal.getTimeInMillis() > mMiddleMonth.getTimeInMillis()) {
            step = 1;
        } else {
            step = -1;
        }
        while (cal.get(Calendar.YEAR) != mMiddleMonth.get(Calendar.YEAR) ||
                cal.get(Calendar.MONTH) != mMiddleMonth.get(Calendar.MONTH)) {
            pos += step;
            cal.add(Calendar.MONTH, -step);
        }

        int retPos = mMiddlePos + pos;
        if (mDirection == View.LAYOUT_DIRECTION_RTL) {
            retPos = MAX_MONTH - retPos;
        }
        return retPos;
    }

    public Calendar pos2Calendar(int pos) {
        if (mDirection == View.LAYOUT_DIRECTION_RTL) {
            pos = MAX_MONTH - pos;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(mMiddleMonth.getTimeInMillis());
        cal.add(Calendar.MONTH, pos - mMiddlePos);
        return cal;
    }

    /**
     * @Deprecated
     * 忘记这个方法有什么作用
     * 知道的请标注
     * @param pos
     * @return
     */
    public int getPYL(int pos) {//第几页
        if (mDirection == View.LAYOUT_DIRECTION_RTL) {
            pos = MAX_MONTH - pos;
        }
        return mMiddlePos;
    }

    public static class Month {
        public int yyyy;
        public int MM;

        public Month(int yyyy, int MM) {
            this.yyyy = yyyy;
            this.MM = MM;
        }
    }

    public void notifyDataSetChanged() {
        mGeneration++;
        super.notifyDataSetChanged();
    }

    public static class ViewHolder {

        private Calendar month;
        private CalendarMonthView monthView;
        private int mCurrentGeneration;

        public ViewHolder(Calendar month, CalendarMonthView monthView) {
            this.month = month;
            this.monthView = monthView;
            mCurrentGeneration = 0;
        }

        public void setCurrentGeneration(int currentGeneration) {
            mCurrentGeneration = currentGeneration;
        }

        public int getCurrentGeneration() {
            return mCurrentGeneration;
        }

        public Calendar getMonth() {
            return month;
        }

        public CalendarMonthView getMonthView() {
            return monthView;
        }
    }

    protected Context getContext() {
        return mContext;
    }

}
