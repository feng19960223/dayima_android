package com.taiqudong.android.enayeh.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.taiqudong.android.enayeh.activity.MainActivity;
import com.taiqudong.android.enayeh.fragment.MainFragment;

/**
 * Created by tangxianming on 2017/8/17.
 */

public class NestedViewPager extends ViewPager {
    public NestedViewPager(Context context) {
        super(context);
    }

    public NestedViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("MyViewPager", "dispatchTouchEvent: " + "down");
                break;

            case MotionEvent.ACTION_MOVE:
                Log.d("MyViewPager", "dispatchTouchEvent: " + "move");
                break;

            case MotionEvent.ACTION_UP:
                Log.d("MyViewPager", "dispatchTouchEvent: " + "up");
                break;
        }
        if (MainFragment.mViewPagerState == MainFragment.ViewState.CONSUME) {
            return super.onInterceptTouchEvent(ev);
        } else {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("MyViewPager", "dispatchTouchEvent: " + "down");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("MyViewPager", "dispatchTouchEvent: " + "move");
                break;

            case MotionEvent.ACTION_UP:
                Log.d("MyViewPager", "dispatchTouchEvent: " + "up");
                break;
        }
        if (MainFragment.mViewPagerState == MainFragment.ViewState.CONSUME) {
            return super.onTouchEvent(ev);
        } else {
            return false;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("MyViewPager", "dispatchTouchEvent: " + "down");
                break;

            case MotionEvent.ACTION_MOVE:
                Log.d("MyViewPager", "dispatchTouchEvent: " + "move");
                break;

            case MotionEvent.ACTION_UP:
                Log.d("MyViewPager", "dispatchTouchEvent: " + "up");
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
