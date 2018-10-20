package com.taiqudong.android.enayeh.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.taiqudong.android.enayeh.activity.MainActivity;

/**
 * Created by tangxianming on 2017/8/17.
 */

public class NestedRecycleView extends XRecyclerView {
    float startY = 0;
    float endY = 0;
    float dy = 0;

    public NestedRecycleView(Context context) {
        super(context);
    }

    public NestedRecycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NestedRecycleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = ev.getY();
                Log.d("MyRecycleView", "onTouchEvent: " + "down");
                break;
            case MotionEvent.ACTION_MOVE:
                endY = ev.getY();
                dy = endY - startY;
                if (Math.abs(dy) > 0.001) {
                }
                Log.d("MyRecycleView", "onTouchEvent: " + "move:" + dy);
                break;

            case MotionEvent.ACTION_UP:
                Log.d("MyRecycleView", "onTouchEvent: " + "up");
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("MyRecycleView", "onInterceptTouchEvent: " + "down");
                break;

            case MotionEvent.ACTION_MOVE:
                Log.d("MyRecycleView", "onInterceptTouchEvent: " + "move");
                break;

            case MotionEvent.ACTION_UP:
                Log.d("MyRecycleView", "onInterceptTouchEvent: " + "up");
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("MyRecycleView", "dispatchTouchEvent: " + "down");
                break;

            case MotionEvent.ACTION_MOVE:
                Log.d("MyRecycleView", "dispatchTouchEvent: " + "move");
                break;

            case MotionEvent.ACTION_UP:
                Log.d("MyRecycleView", "dispatchTouchEvent: " + "up");
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        return super.fling((int) velocityX, (int) velocityY/2);
    }
}
