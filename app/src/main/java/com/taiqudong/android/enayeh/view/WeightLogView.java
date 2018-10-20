package com.taiqudong.android.enayeh.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.taiqudong.android.enayeh.BuildConfig;
import com.taiqudong.android.enayeh.application.AppDay;

import java.util.Calendar;
import java.util.List;

/**
 * Created by zhangxiang on 2017/7/13.
 */

public class WeightLogView extends View {

    private final static String TAG = "WeightLogView";

    private List<AppDay> mDays = null;

    private int mDefaultMax = 70; //最大纵坐标
    private int mDefaultMin = 35; //最小纵坐标
    private boolean mIsMeasure = false; //是否measure 过了
    private boolean mIsCalculate = false; //是否已经计算过了

    private float mGridTop = 10f / 518;//距离上的距离
    private float mGridBottom = (518 - 20f) / 518;//距离下面的距离，改小会超过x轴字
    private float mGridLeft = 50f / 360;//网格距离左面的距离，不会影响高亮的粉红色大小
    private float mGridRight = 360 / 360;//网格右
    private float mAxisTick = 7f / 360;//y轴刻度的长度7

    private float mFontSize = 12f / 360;//x轴，y轴的字体大小12
    private float mYAxisTextLeft = 19f / 360;//y轴文字的距离19
    private float mXAxisTextTop = (518 + 11f - 20) / 518;//x轴文字的距离-20
    private float mDashSpan = 2f / 360;//指示y轴值的虚线
    private float mHighlightTextRectHeight = 18f / 518;

    private Paint mGridPaint;//网格
    private Paint mAxisPaint;//左测刻度的线
    private Paint mAxisTextPaint;//文字
    private Paint mTodayAxisTextPaint;//今天文字红色

    private Paint mLinePaint;//今天的值，到y轴的红色连线
    private Paint mPointPaint;
    private float mPointRadius = 6 / 360f;//沒有被选中的点
    private float mPointStrokeWidth = 5f / 360;//被选中的点的宽
    private Paint mPointHightlightPaint;//空心圆
    private Paint mHighlightTextPaint;//被选中的点，y轴的值，白色
    private Paint mHighlightTextRectPaint;//被选中的点，y轴的值的背景色，粉红色

    private Paint mHighlightPaint;//选中的值，整列的背景色

    private int[][] mGridLines;
    private int[][] mAxisLines;
    private int[][] mYAxisNums;
    private String[] mYAxisStr;
    private int[][] mXAxisNums;
    private String[] mXAxisStr;

    private float[] mLine;
    private float[][] mPoints;
    private Rect mHighlightRect;//选中的值，整列的背景色高亮背景的大小
    private float[] mHighlightDash;
    private int[] mHilightNums;
    private String mHilightStr;
    private Rect mHighlightTextRect;//选中的值，y轴的背景的的大小

    private Paint mBgPaint;//大背景
    private boolean mIsHighLight;

    public void setIsHighLight(boolean isHighLight) {
        mIsHighLight = isHighLight;
    }

    private Context mContext;

    public WeightLogView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public WeightLogView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public WeightLogView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public WeightLogView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        init();
    }

    public void init() {
        mBgPaint = new Paint();
        mBgPaint.setColor(Color.WHITE);
        mBgPaint.setStyle(Paint.Style.FILL);

        mHighlightRect = new Rect();
    }

    public void setDays(List<AppDay> days) {

        mIsCalculate = false;

        mDays = days;
        mDefaultMin = 35;
        mDefaultMax = 70;
        float min = mDefaultMin;
        float max = mDefaultMax;
        for (AppDay day : days) {
            if (min > day.getWeight()) {
                float m = day.getWeight();
                if (m > 0) {
                    min = day.getWeight();
                }
            } else if (day.getWeight() > max) {
                max = day.getWeight();
            }
        }

        while (max >= mDefaultMax) {
            mDefaultMax += 5;
        }
        while (min <= mDefaultMin) {
            mDefaultMin -= 5;
        }
        //        while (max >= mDefaultMax) {
        //            mDefaultMin += 5;
        //            mDefaultMax += 5;
        //            Log.i("2****mDefaultMax",""+mDefaultMax);
        //            Log.i("2****mDefaultMin",""+mDefaultMin);
        //        }
        //        while (min <= mDefaultMin) {
        //            mDefaultMin -= 5;
        //            Log.i("3****mDefaultMax",""+mDefaultMax);
        //            Log.i("3****mDefaultMin",""+mDefaultMin);
        //        }
        if (mIsMeasure) {
            drawCalulate(getWidth(), getHeight());
        }

    }

    public List<AppDay> getDays() {
        return mDays;
    }

    @Override
    public void invalidate() {
        mIsCalculate = false;
        super.invalidate();
    }

    private void setColor(Paint paint, float a, float r, float g, float b) {
        paint.setARGB(Math.round(a * 255), Math.round(r * 255), Math.round(g * 255), Math.round(b * 255));
    }

    private int dp2px(Context context, float dpValue) {
        final float densityScale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * densityScale + 0.5f);
    }

    private void initPaints(int width, int height) {

        int highlightColor = Color.rgb(0xff, 0x18, 0x9f);

        mGridPaint = new Paint();
        mGridPaint.setStyle(Paint.Style.STROKE);
        mGridPaint.setStrokeWidth(dp2px(mContext, 1));
        setColor(mGridPaint, 0.1f, 0, 0, 0);

        mAxisPaint = new Paint();
        mAxisPaint.setStyle(Paint.Style.STROKE);
        mAxisPaint.setStrokeWidth(dp2px(mContext, 2));
        setColor(mAxisPaint, 0.54f, 0, 0, 0);

        mAxisTextPaint = new Paint();
        mAxisTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        setColor(mAxisTextPaint, 0.54f, 0, 0, 0);
        mAxisTextPaint.setTextSize(mFontSize * width);
        mAxisTextPaint.setAntiAlias(true);

        mTodayAxisTextPaint = new Paint();
        mTodayAxisTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTodayAxisTextPaint.setColor(highlightColor);
        mTodayAxisTextPaint.setTextSize(mFontSize * width);
        mTodayAxisTextPaint.setAntiAlias(true);

        mLinePaint = new Paint();
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(dp2px(mContext, 1));//今天的值，到y轴的红色连线，高1dp
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(highlightColor);

        mPointPaint = new Paint();
        mPointPaint.setStyle(Paint.Style.FILL);
        mPointPaint.setStrokeWidth(Math.round(mPointRadius * width));
        mPointPaint.setAntiAlias(true);
        mPointPaint.setColor(highlightColor);

        mPointHightlightPaint = new Paint();
        mPointHightlightPaint.setStyle(Paint.Style.STROKE);//空心圆
        mPointHightlightPaint.setStrokeWidth(Math.round(mPointStrokeWidth * width));
        mPointHightlightPaint.setAntiAlias(true);
        mPointHightlightPaint.setColor(highlightColor);


        mHighlightPaint = new Paint();
        mHighlightPaint.setColor(highlightColor);
        mHighlightPaint.setAlpha(Math.round(0xff * 0.1f));
        mHighlightPaint.setStyle(Paint.Style.FILL);

        mHighlightTextPaint = new Paint();
        mHighlightTextPaint.setColor(Color.WHITE);
        mHighlightTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mHighlightTextPaint.setTextSize(mFontSize * width);
        mHighlightTextPaint.setAntiAlias(true);

        mHighlightTextRectPaint = new Paint();
        mHighlightTextRectPaint.setStyle(Paint.Style.FILL);
        mHighlightTextRectPaint.setColor(highlightColor);
    }

    protected void drawCalulate(int width, int height) {

        int rows = (mDefaultMax - mDefaultMin) / 5;
        int cols = mDays.size();

        //计算grid
        float gridTop = mGridTop * height;
        float gridLeft = mGridLeft * width;
        float gridBottom = mGridBottom * height;
        float gridRight = mGridRight * width;

        float rowSpan = (gridBottom - gridTop) / rows;
        float colSpan = (gridRight - gridLeft) / cols;

        //网格的线
        mGridLines = new int[rows + cols + 2][4];
        int linesCount = 0;
        for (int i = 0; i <= rows; ++i) {
            mGridLines[linesCount][0] = Math.round(gridLeft);
            mGridLines[linesCount][1] = Math.round(gridTop + i * rowSpan);
            mGridLines[linesCount][2] = Math.round(gridRight);
            mGridLines[linesCount][3] = Math.round(gridTop + i * rowSpan);
            ++linesCount;
        }
        for (int i = 0; i <= cols; ++i) {
            mGridLines[linesCount][0] = Math.round(gridLeft + i * colSpan);
            mGridLines[linesCount][1] = Math.round(gridTop);
            mGridLines[linesCount][2] = Math.round(gridLeft + i * colSpan);
            mGridLines[linesCount][3] = Math.round(gridBottom);
            ++linesCount;
        }

        //坐标轴的线
        mAxisLines = new int[rows + 2][4];
        mYAxisNums = new int[rows + 1][];
        float tickLen = mAxisTick * width;
        for (int i = 0; i <= rows; ++i) {
            mAxisLines[i][0] = Math.round(gridLeft);
            mAxisLines[i][1] = Math.round(gridTop + i * rowSpan);
            mAxisLines[i][2] = Math.round(gridLeft - tickLen);
            mAxisLines[i][3] = Math.round(gridTop + i * rowSpan);
        }
        mAxisLines[rows + 1][0] = Math.round(gridLeft);
        mAxisLines[rows + 1][1] = Math.round(gridTop);
        mAxisLines[rows + 1][2] = Math.round(gridLeft);
        mAxisLines[rows + 1][3] = Math.round(gridBottom);

        mYAxisNums = new int[rows + 1][2];
        mYAxisStr = new String[rows + 1];
        int yAxisTextLeft = Math.round(mYAxisTextLeft * width);
        for (int i = 0; i <= rows; ++i) {
            Rect bounds = new Rect();
            String s = String.valueOf(mDefaultMax - i * 5);
            mAxisTextPaint.getTextBounds(s, 0, s.length(), bounds);
            mYAxisNums[i][0] = yAxisTextLeft;
            mYAxisNums[i][1] = Math.round(gridTop + i * rowSpan + bounds.height() / 2);
            mYAxisStr[i] = s;
        }

        //        mXAxisNums = new int[cols - 1][2];
        //        mXAxisStr = new String[cols - 1];
        mXAxisNums = new int[cols][2];
        mXAxisStr = new String[cols];
        float xAxisTextTop = mXAxisTextTop * height;
        //        for (int i = 0; i < cols - 1; ++i) {
        for (int i = 0; i < cols; ++i) {
            Rect bounds = new Rect();
            String s = String.valueOf(mDays.get(i).getDay().get(Calendar.DATE));
            if (mDays.get(i).isToday() && mDays.get(i).getWeight() > 0) {
                //x轴变红色
                if (mDays.get(i).getWeight() > 0) {
                    today = true;
                } else {
                    today = false;
                }
                mTodayAxisTextPaint.getTextBounds(s, 0, s.length(), bounds);
            } else {
                mAxisTextPaint.getTextBounds(s, 0, s.length(), bounds);
            }
            //            mAxisTextPaint.getTextBounds(s, 0, s.length(), bounds);
            mXAxisNums[i][0] = Math.round(gridLeft + (i + .5f) * colSpan - bounds.width() / 2);
            mXAxisNums[i][1] = Math.round(xAxisTextTop + bounds.height() / 2);
            mXAxisStr[i] = s;
        }
        //生成折线
        int[][] lines = new int[cols][4];
        int[] prev = {0, 0};
        int count = -1;//有可能只有点没有线
        float hightWeight = 0; //需要高亮的数字
        for (int i = 0, len = mDays.size(); i < len; ++i) {

            float weight = mDays.get(i).getWeight();
            //Log.d(TAG, "weight_ " + weight);
            if (weight > 1) {
                int left = Math.round(gridLeft + (i + .5f) * colSpan);
                int top = Math.round(gridTop + (gridBottom - gridTop) * (mDefaultMax - weight) / (mDefaultMax - mDefaultMin));
                hightWeight = weight;
                if (prev[0] == 0 && prev[1] == 0) {
                    prev[0] = left;
                    prev[1] = top;
                } else {
                    lines[count][0] = prev[0];
                    lines[count][1] = prev[1];
                    lines[count][2] = left;
                    lines[count][3] = top;
                    prev[0] = left;
                    prev[1] = top;
                }
                ++count;
            }
        }
        mPoints = new float[count + 1][4];
        float radius = mPointRadius * width;
        if (count == 0) {
            mLine = null;
            mPoints[0][0] = prev[0];
            mPoints[0][1] = prev[1];
            mPoints[0][2] = radius;
        } else if (count > 0) {
            mLine = new float[count * 4];
            for (int i = 0; i < count; ++i) {
                for (int j = 0; j < 4; ++j) {
                    mLine[i * 4 + j] = lines[i][j];
                }
                mPoints[i][0] = lines[i][0];
                mPoints[i][1] = lines[i][1];
                mPoints[i][2] = radius;

                if (i == count - 1) {
                    mPoints[i + 1][0] = lines[i][2];
                    mPoints[i + 1][1] = lines[i][3];
                    mPoints[i + 1][2] = radius;
                }
            }
        }

        if (count >= 0) {
            //            //高亮区域
            //            mHighlightRect.set(
            //                        Math.round(mPoints[count][0] - colSpan / 2f),
            //                    Math.round(gridTop),
            //                        Math.round(mPoints[count][0] + colSpan / 2f),
            //                    Math.round(gridBottom));
            //画虚线
            float span = mDashSpan * width;
            float twoSpan = span + span;
            int len = Math.round((mPoints[count][0] - twoSpan - gridLeft) / twoSpan);
            mHighlightDash = new float[len * 4];
            float x = gridLeft;
            float y = mPoints[count][1];
            for (int i = 0; i < len; ++i) {
                mHighlightDash[i * 4] = x;
                mHighlightDash[i * 4 + 1] = y;
                mHighlightDash[i * 4 + 2] = x + span;
                mHighlightDash[i * 4 + 3] = y;
                x += twoSpan;
            }

            //高亮的文字及其背景
            //            mHilightStr = String.format("%.2f", hightWeight);
            mHilightStr = String.format("%.1f", hightWeight);//保留1位小数
            Rect bounds = new Rect();
            mHighlightTextPaint.getTextBounds(mHilightStr, 0, mHilightStr.length(), bounds);
            mHilightNums = new int[2];
            mHilightNums[0] = Math.round((gridLeft - bounds.width()) / 2);
            mHilightNums[1] = Math.round(y + bounds.height() / 2);

            float htHeight = mHighlightTextRectHeight * height;
            mHighlightTextRect = new Rect();
            //            mHighlightTextRect.set(0, Math.round(y - htHeight / 2),
            //                    Math.round(gridLeft), Math.round(y + htHeight / 2));
            mHighlightTextRect.set(0, Math.round(y - htHeight / 2),
                    Math.round(gridLeft) - 3, Math.round(y + htHeight / 2));// left,  top,  right,  bottom
        } else {
            mIsHighLight = false;
        }
        //高亮区域,根据网格线判断
        mHighlightRect.set(
                Math.round(gridLeft + 9 * colSpan),
                Math.round(gridTop),
                Math.round(gridLeft + 10 * colSpan),
                Math.round(gridBottom));
        mIsCalculate = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mIsMeasure = true;

        int w = getMeasuredWidth();
        int h = getMeasuredHeight();
        initPaints(w, h);
        if (mDays != null) {
            drawCalulate(w, h);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!mIsCalculate && mDays != null) {
            drawCalulate(getWidth(), getHeight());
        }
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mBgPaint);
        if (mIsCalculate) {
            drawGrid(canvas);
            drawYAxis(canvas);
            drawXAxis(canvas);
            drawPoints(canvas);
            if (mIsHighLight) {
                drawHighlight(canvas);
            }
        }
    }

    private void drawGrid(Canvas canvas) {
        for (int i = 0, len = mGridLines.length; i < len; ++i) {
            int[] line = mGridLines[i];
            canvas.drawLine(line[0], line[1], line[2], line[3], mGridPaint);
            if (BuildConfig.DEBUG) Log.d(TAG,
                    String.format("draw_grid %d %d %d %d",
                            line[0], line[1], line[2], line[3]));

        }
    }

    private void drawYAxis(Canvas canvas) {
        for (int i = 0, len = mAxisLines.length; i < len; ++i) {
            int[] line = mAxisLines[i];
            canvas.drawLine(line[0], line[1], line[2], line[3], mAxisPaint);
            if (BuildConfig.DEBUG) Log.d(TAG,
                    String.format("draw_axisY %d %d %d %d",
                            line[0], line[1], line[2], line[3]));

        }
        for (int i = 0, len = mYAxisNums.length; i < len; ++i) {
            int[] start = mYAxisNums[i];
            String s = mYAxisStr[i];
            canvas.drawText(s, start[0], start[1], mAxisTextPaint);
        }
    }

    private void drawXAxis(Canvas canvas) {
        for (int i = 0, len = mXAxisNums.length; i < len; ++i) {
            int[] start = mXAxisNums[i];
            String s = mXAxisStr[i];
            if (mDays.get(i).isToday()) {
                //x轴变红色
                canvas.drawText(s, start[0], start[1], mTodayAxisTextPaint);
            } else {
                canvas.drawText(s, start[0], start[1], mAxisTextPaint);
            }
            //            canvas.drawText(s, start[0], start[1], mAxisTextPaint);
            if (BuildConfig.DEBUG) Log.d(TAG,
                    String.format("draw_axisX_txt %s %d %d", s, start[0], start[1]));
        }
        canvas.drawRect(mHighlightRect, mHighlightPaint);//整列的粉红背景
    }

    private void drawPoints(Canvas canvas) {
        if (mLine != null) {//有可能只有点，没有线
            canvas.drawLines(mLine, mLinePaint);
        }
        int len = mPoints.length;

        for (int i = 0; i < len; i += 1) {
            float[] p = mPoints[i];
            if (i == len - 1 && today) {
                canvas.drawCircle(p[0], p[1], dp2px(mContext,6f), mBgPaint);
                //                canvas.drawCircle(p[0], p[1],p[2], mPointHightlightPaint);
                canvas.drawCircle(p[0], p[1], dp2px(mContext,5f), mPointHightlightPaint);
            } else {
                canvas.drawCircle(p[0], p[1], dp2px(mContext,4f), mPointPaint);
            }
        }
    }

    public static boolean today;

    private void drawHighlight(Canvas canvas) {
        //        canvas.drawRect(mHighlightRect, mHighlightPaint);
        canvas.drawLines(mHighlightDash, mLinePaint);
        canvas.drawRect(mHighlightTextRect, mHighlightTextRectPaint);
        canvas.drawText(mHilightStr, mHilightNums[0], mHilightNums[1], mHighlightTextPaint);
    }

}
