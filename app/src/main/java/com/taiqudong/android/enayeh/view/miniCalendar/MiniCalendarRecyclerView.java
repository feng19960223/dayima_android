package com.taiqudong.android.enayeh.view.miniCalendar;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.taiqudong.android.enayeh.utils.Log;

/**
 * Created by zhangxiang on 2017/7/28.
 */

public class MiniCalendarRecyclerView extends RecyclerView {

    final static double PI_1_2 = Math.PI / 2;

    public MiniCalendarRecyclerView(Context context) {
        super(context);
    }

    public MiniCalendarRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MiniCalendarRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        double vX = getWidth() * Math.atan(velocityX) / PI_1_2;
        Log.d("fling", " velocityX " + velocityX + " vX " + vX);
        return super.fling((int) vX, velocityY);
    }

    //    public void setCurrentPos(int currentPos) {
    //        MiniCalendarLayoutManager miniCalendarLayoutManager = (MiniCalendarLayoutManager) getLayoutManager();
    //        miniCalendarLayoutManager.setCurrentPos(currentPos);
    //    }

}
