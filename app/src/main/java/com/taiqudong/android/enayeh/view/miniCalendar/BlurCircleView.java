package com.taiqudong.android.enayeh.view.miniCalendar;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.taiqudong.android.enayeh.BuildConfig;

/**
 * Created by zhangxiang on 2017/7/10.
 */
public class BlurCircleView extends View{

    private Path mCirclePath;
    private Path mSubCirclePath;

    private Paint mBlurPaint;
    private Paint mBgPaint;

    private Bitmap mBitmap;

    private Animator mAnimator;

    public BlurCircleView(Context context) {
        super(context);
        init();
    }

    public BlurCircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BlurCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BlurCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        mCirclePath = new Path();
        mBlurPaint = new Paint(Paint.ANTI_ALIAS_FLAG );
        mBlurPaint.setColor(Color.RED);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }
        mSubCirclePath = new Path();

        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setStyle(Paint.Style.FILL);
        mBgPaint.setColor(Color.WHITE);

        ValueAnimator animator = new ValueAnimator();
        animator.setDuration(200);
        //animator.setRepeatCount(1);
        animator.setInterpolator(new CubicTimeInterpolator(0, .4f, 1.29f, 1f));
        animator.setFloatValues(0, 1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                float val = (Float)animation.getAnimatedValue();
                if(BuildConfig.DEBUG)Log.d("animate", "a: " + val);
                mCirclePath.reset();
                int w = getWidth();
                mCirclePath.addCircle(w/2, getHeight()/2,
                        w/6 + (w/4 - w/6) * val, Path.Direction.CW);
                BlurCircleView.this.invalidate();
            }
        });

        mAnimator = animator;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec ){

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        mCirclePath.addCircle(width/2, height/2, width/4, Path.Direction.CW);

        mSubCirclePath.addCircle(width/2, height/2, width/6 - 2, Path.Direction.CW);
    }

    public void setBitmap(Bitmap bitmap){
        mBitmap = BlurBuilder.fastblur(bitmap, 1/2f, Math.round(getWidth() * 10f/360/2));
    }

    public  void setBitmap(Bitmap bitmap, int color) {
        setBitmap(bitmap);
        mBgPaint.setColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        if(mBitmap == null){
            //canvas.drawPath(mCirclePath, mBlurPaint);
        }else{
            Log.d("onDraw", "bitmap");
            canvas.clipPath(mCirclePath);
            canvas.clipPath(mSubCirclePath, Region.Op.DIFFERENCE);
            canvas.drawBitmap(mBitmap, new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight()),
                    new Rect(0, 0, getWidth(), getHeight()), mBlurPaint);
        }
        Log.d("onDraw", "DDDDDD");
    }

    public void startAnimation(){
        mAnimator.start();
    }

    public Animator getAnimator(){
        return mAnimator;
    }
}

class CubicTimeInterpolator implements TimeInterpolator{

    private float mA0;
    private float mA1;
    private float mA2;
    private float mA3;

    public CubicTimeInterpolator(float p0, float p1, float p2, float p3){
        mA0 =                              (    p0);
        mA1 =                    (3.0f*p1)-(3.0f*p0);
        mA2 =          (3.0f*p2)-(6.0f*p1)+(3.0f*p0);
        mA3 =(    p3)-(3.0f*p2)+(3.0f*p1)-(    p0);

    }

    @Override
    public float getInterpolation(float input) {
        float t = input;
        return mA0 + mA1*t + mA2*t*t + mA3*t*t*t;
    }
}