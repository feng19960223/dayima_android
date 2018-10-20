package com.taiqudong.android.enayeh.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by taiqudong on 2017/8/21.
 */

public class ForceScrollView extends ScrollView {
    public ForceScrollView(Context context) {
        super(context);
    }

    public ForceScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ForceScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }
}
