package com.taiqudong.android.enayeh.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ScrollView;

import com.taiqudong.android.enayeh.fragment.MainFragment;
import com.taiqudong.android.enayeh.listener.PostionChange;

/**
 * Created by taiqudong on 2017/7/20.
 * 可以嵌套RecyclerView的ScrollView
 */

public class NestedScrollview extends ScrollView {
    private int downY;
    private int mTouchSlop;
    PostionChange postionChange;

    public void setPostionChange(PostionChange postionChange) {
        this.postionChange = postionChange;
    }

    public NestedScrollview(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("MySrollView", "onInterceptTouchEvent: " + "down");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("MySrollView", "onInterceptTouchEvent: " + "move");
                break;
            case MotionEvent.ACTION_UP:
                Log.d("MySrollView", "onInterceptTouchEvent: " + "up");
                break;
        }
        if (MainFragment.mScrollViewState == MainFragment.ViewState.UNCONSUME) {
            return false;
        }
        int action = e.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downY = (int) e.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveY = (int) e.getRawY();
                if (Math.abs(moveY - downY) > mTouchSlop) {
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(e);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("MySrollView", "onInterceptTouchEvent: " + "down");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("MySrollView", "onInterceptTouchEvent: " + "move");
                break;
            case MotionEvent.ACTION_UP:
                Log.d("MySrollView", "onInterceptTouchEvent: " + "up");
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("MySrollView", "onTouchEvent: " + "down");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("MySrollView", "onTouchEvent: " + "move");
                break;
            case MotionEvent.ACTION_UP:
                Log.d("MySrollView", "onTouchEvent: " + "up");
                break;
        }

        if (MainFragment.mScrollViewState == MainFragment.ViewState.UNCONSUME) {

            return false;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX,
                                  boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        if (scrollY > 0 && null != onScrollToBottom) {
            onScrollToBottom.onScrollBottomListener(clampedY);
        }
    }

    private OnScrollToBottomListener onScrollToBottom;

    public void setOnScrollToBottomLintener(OnScrollToBottomListener listener) {
        onScrollToBottom = listener;
    }

    public interface OnScrollToBottomListener {
        void onScrollBottomListener(boolean isBottom);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (postionChange != null) {
            postionChange.change();
        }
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return false;
    }
}
