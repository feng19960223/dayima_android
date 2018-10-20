package com.taiqudong.android.enayeh.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.jcodecraeer.xrecyclerview.XRecyclerView;

/**
 * TODO 慢速recycleview
 */
public class SlowRecycleView extends XRecyclerView {
    private static final String TAG = "SlowRecycleView";

    public SlowRecycleView(Context context) {
        super(context);
    }

    public SlowRecycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SlowRecycleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public boolean fling(int velocityX, int velocityY) {
        return super.fling((int) velocityX, (int) velocityY/2);
    }
}
