package com.taiqudong.android.enayeh.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;


/**
 * Created by taiqudong on 2017/7/6.
 * 自定义控件，让文字和图片一起居中
 */

public class DrawableHorizontalButton extends AppCompatButton {

    public DrawableHorizontalButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas = getTopCanvas(canvas);
        super.onDraw(canvas);
    }

    private Canvas getTopCanvas(Canvas canvas) {
        Drawable[] drawables = getCompoundDrawables();
        if (drawables == null) {
            return canvas;
        }
        Drawable drawable = drawables[0];// 左面的drawable
        if (drawable == null) {
            drawable = drawables[2];// 右面的drawable
        }
        // float textSize = getPaint().getTextSize(); // 使用这个会导致文字竖向排下来
        float textSize = getPaint().measureText(getText().toString());
        int drawWidth = drawable.getIntrinsicWidth();
        int drawPadding = getCompoundDrawablePadding();
        float contentWidth = textSize + drawWidth + drawPadding;
        int leftPadding = (int) (getWidth() - contentWidth);
        setPadding(0, 0, leftPadding, 0); // 直接贴到左边
        float dx = (getWidth() - contentWidth) / 2;
        canvas.translate(dx, 0);// 往右移动
        return canvas;
    }
}
