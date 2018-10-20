package com.taiqudong.android.enayeh.view.miniCalendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PointF;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.taiqudong.android.enayeh.R;
import com.taiqudong.android.enayeh.application.AppDay;
import com.taiqudong.android.enayeh.application.AppLogic;
import com.taiqudong.android.enayeh.utils.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhangxiang on 2017/7/7.
 */

public class MiniCalendarViewHelper {

    private final String TAG = "MiniCalVH";

    private MiniCalendarViewHelper(Context context, RecyclerView miniCalendar, AppLogic appLogic) {

    }

    public static MiniCalendarViewWrapper build(Context context,
                                                MiniCalendarRecyclerView theView,
                                                AppLogic appLogic,
                                                BlurCircleView blurCircleView) {
        MiniCalendarViewWrapper wrapper = new MiniCalendarViewHelper.MiniCalendarViewWrapper(context, theView, appLogic);
        wrapper.setAnimateView(blurCircleView);
        return wrapper;
    }

    public static void centerAnimate(Context context, RecyclerView recyclerView, BlurCircleView animateView) {
        int ww = recyclerView.getWidth();
        int hh = recyclerView.getHeight();
        Path path = new Path();
        path.addRect(0, 0, ww, hh, Path.Direction.CCW);
        //path.addRect(ww/4, hh/2 - ww/4, ww/3, hh/2 + ww/4, Path.Direction.CCW);
        MiniCalendarLayoutManager layoutManager = (MiniCalendarLayoutManager) recyclerView.getLayoutManager();
        if (animateView != null) {
            int pos = layoutManager.getCurrentPos();
            int viewType = recyclerView.getAdapter().getItemViewType(pos);
            int color = AppDay.getColor(context, viewType);

            color = Color.argb(Math.round(.2f * 255), Color.red(color), Color.green(color), Color.blue(color));
            Bitmap bitmap = BlurBuilder.getScreenshot(recyclerView, path, color);
            animateView.getDrawableState();
            animateView.setBitmap(bitmap);
            if (!animateView.getAnimator().isRunning()) {
                animateView.startAnimation();
            }
        }
    }

    //日期变更时的事件
    public interface DayChangeListener {
        void onDayChange(Calendar cal);
    }

    //中间日期点击事件
    public interface CenterDayClickListener {
        void onDayClick(AppDay cal);
    }

    public static class MiniCalendarViewWrapper {


        private BlurCircleView mAnimateView;

        protected Context mContext;

        private DayChangeListener mDayChangeListener;
        private CenterDayClickListener mCenterDayClickListener;


        public void setAnimateView(BlurCircleView view) {
            mAnimateView = view;
        }

        public void setDayChangeListener(DayChangeListener listener) {
            mDayChangeListener = listener;
        }

        public void setCenterDayClickListener(CenterDayClickListener listener) {
            mCenterDayClickListener = listener;
        }

        public Context getContext() {
            return mContext;
        }

        protected MiniCalendarViewWrapper(final Context context, final RecyclerView recyclerView, AppLogic appLogic) {

            mContext = context;

            final MiniCalendarAdapter adapter = new MiniCalendarAdapter(context, AppLogic.getInstance());
//            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
//            layoutManager.setOrientation(OrientationHelper.HORIZONTAL);

            final MiniCalendarLayoutManager layoutManager = new MiniCalendarLayoutManager(context);
            //GridLayoutManager layoutManager = new GridLayoutManager(context, 5);
            recyclerView.setLayoutManager(layoutManager);

            layoutManager.setRecyclerViewOnItemClickListener(new RecyclerViewOnItemClickListener() {
                @Override
                public void onClick(View view, int magOffset) {
                    if (magOffset != 0) {
                        recyclerView.smoothScrollBy(-magOffset, 0);
                    } else if (mCenterDayClickListener != null) {
                        AppDay appDay = adapter.getItemByPos(layoutManager.getCurrentPos());
                        mCenterDayClickListener.onDayClick(appDay);
                    }
                }
            });

            //载入的动画
            recyclerView.setAdapter(adapter);
            /** 动画
             recyclerView.setAlpha(0.8f);
             recyclerView.animate().alpha(1).setDuration(200).setListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation) {

            }

            @Override public void onAnimationEnd(Animator animation) {
            mAnimateView.setVisibility(View.VISIBLE);
            MiniCalendarViewHelper.centerAnimate(context, recyclerView, mAnimateView);
            }

            @Override public void onAnimationCancel(Animator animation) {
            mAnimateView.setVisibility(View.VISIBLE);
            MiniCalendarViewHelper.centerAnimate(context, recyclerView, mAnimateView);
            }

            @Override public void onAnimationRepeat(Animator animation) {

            }
            });
             **/
            //layout
            recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    MiniCalendarLayoutManager layoutManager = (MiniCalendarLayoutManager) recyclerView.getLayoutManager();
                    MiniCalendarAdapter.ViewHolder vh =
                            (MiniCalendarAdapter.ViewHolder) recyclerView
                                    .getChildViewHolder(layoutManager.findViewByPosition(layoutManager.getCurrentPos()));
                    TextView t = (TextView) vh.bigView.findViewById(R.id.textDayCount);
                    if (!"0".equals(t.getText().toString()) &&
                            !TextUtils.isEmpty(t.getText().toString())) {
                        //有内容才进行展示
                        if (!AppLogic.getInstance().isInitialized()) {
                            vh.tv_edit.setVisibility(View.VISIBLE);

                            vh.bigView.setVisibility(View.INVISIBLE);
                            vh.smallView.setVisibility(View.INVISIBLE);
                        } else {
                            vh.tv_edit.setVisibility(View.INVISIBLE);
                            vh.bigView.setVisibility(View.VISIBLE);
                            vh.smallView.setVisibility(View.INVISIBLE);
                        }
                    }
                    //mAnimateView.setVisibility(View.VISIBLE);
                    //MiniCalendarViewHelper.centerAnimate(context, recyclerView, mAnimateView);
                }
            });

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    MiniCalendarLayoutManager layoutManager = (MiniCalendarLayoutManager) recyclerView.getLayoutManager();
                    View view = layoutManager.findViewByPosition(layoutManager.getCurrentPos());
                    if (view == null) {
                        return;
                    }
                    MiniCalendarAdapter.ViewHolder vh =
                            (MiniCalendarAdapter.ViewHolder) recyclerView.getChildViewHolder(view);
                    vh.bigView.setVisibility(View.INVISIBLE);
                    vh.smallView.setVisibility(View.VISIBLE);

                    vh.tv_edit.setVisibility(View.INVISIBLE);

                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        Log.d("magOffset", "IDLE");
                        int dx = layoutManager.getMagOffset();
                        if (Math.abs(dx) >= 2) {
                            Log.d("magOffset", "dx: " + dx);
                            recyclerView.smoothScrollBy(-dx, 0);
                        } else {
                            //mAnimateView.setVisibility(View.VISIBLE);
                            //MiniCalendarViewHelper.centerAnimate(context, recyclerView, mAnimateView);\
                            //                            vh.bigView.setVisibility(View.VISIBLE);
                            //                            vh.smallView.setVisibility(View.INVISIBLE);

                            if (!AppLogic.getInstance().isInitialized()) {
                                vh.tv_edit.setVisibility(View.VISIBLE);
                                vh.bigView.setVisibility(View.INVISIBLE);
                                vh.smallView.setVisibility(View.INVISIBLE);
                            } else {
                                vh.tv_edit.setVisibility(View.INVISIBLE);
                                vh.bigView.setVisibility(View.VISIBLE);
                                vh.smallView.setVisibility(View.INVISIBLE);
                            }

                            TextView t = (TextView) vh.bigView.findViewById(R.id.textDayCount);
                            if ("0".equals(t.getText().toString()) || TextUtils.isEmpty(t.getText().toString())) {
                                if (!AppLogic.getInstance().isInitialized()) {
                                    vh.tv_edit.setVisibility(View.VISIBLE);
                                    vh.bigView.setVisibility(View.INVISIBLE);
                                    vh.smallView.setVisibility(View.INVISIBLE);
                                } else {
                                    vh.tv_edit.setVisibility(View.INVISIBLE);
                                    vh.bigView.setVisibility(View.INVISIBLE);
                                    vh.smallView.setVisibility(View.VISIBLE);
                                }
                            }
                            if (mDayChangeListener != null) {
                                AppDay appDay = adapter.getItemByPos(layoutManager.getCurrentPos());
                                mDayChangeListener.onDayChange(appDay.getDay());
                            }
                        }
                    } else {
                        //mAnimateView.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                }

            });
            //RecyclerView.ItemDecoration itemDecoration = new
            //        DividerItemDecoration(context, DividerItemDecoration.VERTICAL);

            //recyclerView.setItemAnimator(new SlideInUpAnimator());
        }
    }
}


class MiniCalendarLayoutManager extends RecyclerView.LayoutManager
        implements RecyclerView.SmoothScroller.ScrollVectorProvider {

    private Context mContext;

    private static final String TAG = "MiniCalLM";

    static final boolean DEBUG = false;

    private int mFistVisiblePosition = 0;
    private int mLastVisiblePosition = 6;


    private View.OnClickListener mChildOnClickListener;

    private RecyclerViewOnItemClickListener mRecyclerViewOnItemClickListener;

    //两个点直接的间距
    private float mPivotMargin = 0f;

    private int mCurrentPos = 0;

    //对应的比例, 停靠的位置
    private float[] mPercents = null;// {-9f / 360, 38f / 360, 85f / 360, 0.5f, 275f / 360, 322f / 360, 369f / 360};

    private int mCenterPercentIndex = 3;

    private List<Integer> passed = new LinkedList<>();
    private List<Integer> appended = new LinkedList<>();

    private int magOffset = 0; //将item回弹到固定的位置

    private static final float BUBBLE_RADIUS_RADIO = .86f;
    private static final float BUBBLE_BASE_SCALE = 40f / 140;

    private int mOffset; //记录移动的距离

    public MiniCalendarLayoutManager(Context context) {
        //super();
        mContext = context;

    }

    //滚动到中间需要多少offset
    public int getMagOffset() {
        return magOffset;
    }

    public void setRecyclerViewOnItemClickListener(RecyclerViewOnItemClickListener listener) {
        mRecyclerViewOnItemClickListener = listener;

        if (mChildOnClickListener != null) {
            //TODO 移除旧的
        }

        //监听点击事件
        mChildOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSmoothScrolling()) {
                    return;
                }
                double w = getWidth();
                double leftLimit = w * mPercents[mCenterPercentIndex - 1];
                double rightLimit = w * mPercents[mCenterPercentIndex + 1];
                double ratio = (mPercents[1] - mPercents[0]) /
                        (mPercents[mCenterPercentIndex] - mPercents[mCenterPercentIndex - 1]);

                double viewMid = (v.getLeft() + v.getRight()) / 2f;
                double parentMid = w * mPercents[mCenterPercentIndex];

                double vViewMid = RatioUtil.ratioConvert(leftLimit, rightLimit, viewMid, ratio);
                double vParentMid = RatioUtil.ratioConvert(leftLimit, rightLimit, parentMid, ratio);

                mRecyclerViewOnItemClickListener.onClick(v, (int) (vParentMid - vViewMid));

            }
        };
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        int height = getHeight();
        return new RecyclerView.LayoutParams(
                Math.round(height * BUBBLE_RADIUS_RADIO),
                Math.round(height * BUBBLE_RADIUS_RADIO)
        );
    }

    //
    public int getCurrentPos() {
        return mCurrentPos;
    }

    public void setCurrentPos(int pos) {
        //int middle = getItemCount()/2;
        //int delta = pos - middle;
        mCurrentPos = pos;
        //mFistVisiblePosition = middle - 2 + delta;//
        //mLastVisiblePosition = middle + 2 + delta;//一共展示五个
    }

    private void initPercents() {

        int height = getHeight();
        int width = getWidth();

        int span = 10;

        float bigRadius = height * BUBBLE_RADIUS_RADIO;
        float smallRadius = height * BUBBLE_RADIUS_RADIO * BUBBLE_BASE_SCALE;

        float ww = (width - bigRadius) / 2;
        int small_1_2 = 0;
        while (ww > 0) {
            ww = ww - smallRadius - span;
            small_1_2++;
        }

        int max = small_1_2 + 1 + small_1_2;
        mPercents = new float[max];
        for (int i = 0; i < max; ++i) {
            if (i == small_1_2) {
                mPercents[i] = 0.5f;
            } else if (i < small_1_2) {
                mPercents[i] = (width / 2 - bigRadius / 2 - (small_1_2 - i)
                        * (span + smallRadius) + smallRadius / 2) / width;
            } else {
                mPercents[i] = (width / 2 + bigRadius / 2 + (i - small_1_2)
                        * (span + smallRadius) - smallRadius / 2) / width;
            }
        }

        mPivotMargin = mPercents[1] - mPercents[0];
        //这里写死

        //TODO 这边可以生成可配置的
        mFistVisiblePosition = 30 - small_1_2;
        mLastVisiblePosition = 30 + small_1_2;
        mCurrentPos = 30;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {


        mOffset = 0;
        int width = getWidth();
        int height = getHeight();
        Log.d(TAG, "onLayoutCdn width:" + width + " height:" + height +
                " fPos:" + mFistVisiblePosition + " lPos:" + mLastVisiblePosition);
        removeAndRecycleAllViews(recycler);
        initPercents();
        for (int i = mFistVisiblePosition, count = 0; i <= mLastVisiblePosition; ++i, ++count) {

            View scrap = findViewByPosition(i);
            if (scrap == null) {
                scrap = recycler.getViewForPosition(i);
                scrap.setOnClickListener(mChildOnClickListener);
            }
            addView(scrap);
            measureChildWithMargins(scrap, 0, 0);

            int viewWidth = getDecoratedMeasuredWidth(scrap);
            int viewHeight = getDecoratedMeasuredHeight(scrap);
            int percentIdx = count;
            if (getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
                percentIdx = mPercents.length - 1 - count;
            }

            int left = makeVal(width, mPercents[percentIdx], viewWidth);
            int top = makeVal(height, 0.5f, viewHeight);
            if (DEBUG)
                Log.d(TAG, "percents[count]:" + mPercents[percentIdx] + " left:" + left + " top:" + top + " width:" + viewWidth + " height:" + viewHeight);
            layoutDecorated(scrap, left, top, left + viewWidth, top + viewHeight);
            setScale(scrap);
        }
    }

    //计算边缘
    private int makeVal(int width, float percent, int viewWidth) {
        return Math.round(width * percent - viewWidth / 2);
    }


    @Override
    public boolean canScrollVertically() {
        return false;
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    public int isHitBound() {
        View firstView = getChildAt(0);
        View lastView = getChildAt(getChildCount() - 1);

        if (0 == getPosition(firstView)) {
            return -1;
        } else if (getItemCount() - 1 == getPosition(lastView)) {
            Log.d(TAG, "bound " + getItemCount() + " " + getPosition(lastView));
            return 1;
        }
        return 0;
    }

    //计算最近的回弹点
    public void calculateMag() {
        int recycleViewWidth = getWidth();

        int start, end, step;
        if (getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            start = getChildCount() - 1;
            end = -1;
            step = -1;
        } else {
            start = 0;
            end = getChildCount();
            step = 1;
        }

        float minDist = recycleViewWidth;
        float targetAchor = 0;
        float achor = 0;
        View achorView = null;
        int left = 0;
        int right = 0;
        for (int i = start; i != end; i += step) {
            achorView = getChildAt(i);
            left = achorView.getLeft();
            right = achorView.getRight();
            achor = mPercents[0] * recycleViewWidth - (right + left) / 2;
            if (Math.abs(achor) < minDist) {
                minDist = achor;
                targetAchor = achor;
                Log.d(TAG, "magOffset " + magOffset + " achor:" + achor + " left:" + left + " right:" + right);
            }
        }
        magOffset = Math.round(targetAchor);
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {

        int childCount = getChildCount();
        if (childCount == 0) {
            // we cannot scroll when there is no views
            return 0;
        }
        mOffset += dx;

        float base = 0;
        int parentViewWidth = getWidth();
        int parentViewHeight = getHeight();

        float recycleLeftPos = -mPivotMargin * 2 * parentViewWidth;
        float recycleRightPos = parentViewWidth * (1 + mPivotMargin * 2);

        float disappearLeftPos = 0;
        float disappearRightPos = parentViewWidth - mPivotMargin * parentViewWidth;
        int disappearingLeftCount = 0;
        int disappearingRightCount = 0;
        List<View> toRemove = new ArrayList<>(10);

        int bound = isHitBound(); //if hit bound

        if (getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            Log.d(TAG, String.format("bound: %d, dx: %d", bound, dx));
            if (bound == -1) {
                View view = getChildAt(0);
                if ((view.getRight() + view.getLeft()) / 2 < parentViewWidth &&
                        dx > 0) {
                    return 0;
                }
            } else if (bound == 1) {
                View view = getChildAt(getChildCount() - 1);
                if ((view.getLeft() + view.getRight()) / 2 > 0 && dx < 0) {
                    return 0;
                }
            }
        } else {
            Log.d(TAG, String.format("bound: %d, dx: %d", bound, dx));
            if (bound == -1) {
                View view = getChildAt(0);
                if ((view.getLeft() + view.getRight()) / 2 > 0 && dx < 0) {
                    return 0;
                }
            } else if (bound == 1) {
                View view = getChildAt(getChildCount() - 1);
                if ((view.getRight() + view.getLeft()) / 2 < parentViewWidth &&
                        dx > 0) {
                    return 0;
                }
            }
        }

        int start, end, step;
        if (getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            start = childCount - 1;
            end = -1;
            step = -1;
        } else {
            start = 0;
            end = childCount;
            step = 1;
        }
        for (int i = start; i != end; i += step) {
            View view = getChildAt(i);
            if (view == null) {
                if (DEBUG) Log.d(TAG, "child " + i + " not found");
                continue;
            }

            float scrollX = scrollViewBy(dx, view, base);//left position
            base = scrollX;

            if (scrollX < recycleLeftPos) {
                toRemove.add(view);
            } else if (scrollX > recycleRightPos) {
                toRemove.add(view);
            }

            if (scrollX < mPercents.length / 2) {
                ++disappearingLeftCount;
            } else if (scrollX > disappearRightPos) {
                ++disappearingRightCount;
            }
        }

        for (View view : toRemove) { //remove them
            //
            detachAndScrapView(view, recycler);
            removeView(view);
            Log.d("bind", "remove View");
        }

        //计算回弹
        calculateMag();

        if (disappearingLeftCount > 0) { //追加view
            View rightView = null;
            if (getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
                rightView = getChildAt(0);
            } else {
                rightView = getChildAt(getChildCount() - 1);
            }
            int rightPos = getPosition(rightView);
            for (int i = 1; i <= disappearingLeftCount; ++i) {
                int pos = i + rightPos;
                if (getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
                    pos = rightPos - i;
                }
                if (pos >= getItemCount() || pos < 0) {
                    break;
                }
                View view = findViewByPosition(pos);
                if (view == null) {
                    view = recycler.getViewForPosition(pos);
                    view.setOnClickListener(mChildOnClickListener);
                    if (getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
                        addView(view, 0);
                    } else {
                        addView(view);
                    }
                } else {
                    //should not happen;
                    continue;
                }
                measureChildWithMargins(view, 0, 0);

                int viewWidth = getDecoratedMeasuredWidth(view);
                int viewHeight = getDecoratedMeasuredHeight(view);
                int left = rightView.getLeft() + Math.round(i * mPivotMargin * parentViewHeight);
                int top = makeVal(parentViewHeight, 0.5f, viewHeight);
                layoutDecorated(view, left, top, left + viewWidth, top + viewHeight);
            }

        }

        if (disappearingRightCount > 0) { //左边插入view
            View leftView = null;
            if (getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
                leftView = getChildAt(getChildCount() - 1);
            } else {
                leftView = getChildAt(0);
            }
            int leftPos = getPosition(leftView);
            for (int i = 1; i <= disappearingRightCount; ++i) {
                int pos = leftPos - i;
                if (getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
                    pos = leftPos + i;
                }
                if (pos < 0 || pos >= getItemCount()) {
                    break;
                }
                View view = findViewByPosition(pos);
                if (view == null) {
                    view = recycler.getViewForPosition(pos);
                    view.setOnClickListener(mChildOnClickListener);
                    if (getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
                        addView(view);
                    } else {
                        addView(view, 0);
                    }
                } else {
                    continue;
                }
                measureChildWithMargins(view, 0, 0);

                int viewWidth = getDecoratedMeasuredWidth(view);
                int viewHeight = getDecoratedMeasuredHeight(view);
                int left = leftView.getLeft() - Math.round(i * mPivotMargin * parentViewWidth);
                int top = makeVal(parentViewHeight, 0.5f, viewHeight);
                layoutDecorated(view, left, top, left + viewWidth, top + viewHeight);
            }
        }


        return dx;
    }

    //
    public float scrollViewBy(int dx, View view, float base) {

        double w = getWidth();

        double leftLimit = w * mPercents[mCenterPercentIndex - 1];
        double rightLimit = w * mPercents[mCenterPercentIndex + 1];
        //距离的比例
        double ratio = (mPercents[mCenterPercentIndex - 1] - mPercents[mCenterPercentIndex - 2])
                / (mPercents[mCenterPercentIndex] - mPercents[mCenterPercentIndex - 1]);


        double vMiddle = 0;
        double newVMiddle = 0;
        double middle = (view.getRight() + view.getLeft()) / 2;
        if (Math.abs(base) <= 0.01f) {
            vMiddle = RatioUtil.ratioConvert(leftLimit, rightLimit, middle, ratio);
            newVMiddle = vMiddle - dx;
        } else {
            vMiddle = RatioUtil.ratioConvert(leftLimit, rightLimit, base, ratio);
            newVMiddle = vMiddle + w * mPivotMargin;
        }

        double newMiddle = RatioUtil.ratioConvert(leftLimit, leftLimit + (rightLimit - leftLimit) * ratio, newVMiddle, 1 / ratio);
        if (DEBUG)
            Log.d(TAG, "vmove base:" + base + " newVMiddle: " + newVMiddle + " vMiddle:" + vMiddle + " ratio:" + ratio + " viewWidth:" + w);
        int delta = (int) Math.round(newMiddle - middle);

        view.offsetLeftAndRight(delta);
        double scale = setScale(view);
        if (DEBUG)
            Log.d(TAG, "scale_delta base:" + base + " delta: " + delta + " scale:" + scale + " middle:" + middle + " newMiddle:" + newMiddle);

        return (float) newMiddle;
    }


    private float setScale(View view) {

        float scale = 1;
        float baseScale = BUBBLE_BASE_SCALE;
        int parentViewWidth = getWidth();
        float middle = (view.getLeft() + view.getRight()) / 2.0f / parentViewWidth;
        if (middle <= mPercents[mCenterPercentIndex - 1] ||
                middle >= mPercents[mCenterPercentIndex + 1]) {
            view.setScaleX(baseScale);
            view.setScaleY(baseScale);
        } else {
            float span = Math.abs(middle - mPercents[mCenterPercentIndex]);
            float maxDelta = mPercents[mCenterPercentIndex] - mPercents[mCenterPercentIndex - 1];
            scale = baseScale + (1 - baseScale) * (1 - span / maxDelta);
            view.setScaleX(scale);
            view.setScaleY(scale);
            if (DEBUG) Log.d(TAG, "span: " + span + " maxDelta: " + maxDelta + " scale:" + scale);
            if (scale >= 0.99f) {
                int pos = getPosition(view);
                setCurrentPos(pos);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setTranslationZ(scale);
        }
        if (DEBUG) Log.d(TAG, "scale: " + scale + " middle: " + middle);
        return scale;
    }

    @Override
    public PointF computeScrollVectorForPosition(int targetPosition) {
        return null;
    }

}

interface RecyclerViewOnItemClickListener {

    void onClick(View view, int magOffset);
}