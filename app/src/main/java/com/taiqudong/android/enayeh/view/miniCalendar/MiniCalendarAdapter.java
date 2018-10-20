package com.taiqudong.android.enayeh.view.miniCalendar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.taiqudong.android.enayeh.R;
import com.taiqudong.android.enayeh.application.AppDay;
import com.taiqudong.android.enayeh.application.AppLogic;
import com.taiqudong.android.enayeh.bean.Basic;
import com.taiqudong.android.enayeh.utils.DateUtil;
import com.taiqudong.android.enayeh.utils.LanguageUtil;
import com.taiqudong.android.enayeh.utils.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by zhangxiang on 2017/7/7.
 */


public class MiniCalendarAdapter
        extends RecyclerView.Adapter<MiniCalendarAdapter.ViewHolder> {

    private final String TAG = "miniCalAdapter";
    private Context mContext = null;
    private AppLogic mAppLogic = null;

    private List<AppDay> mAppDays = null;

    private SimpleDateFormat sdf = new SimpleDateFormat("MMM ");

    private List<ViewHolder> mViewHolderList;

    public MiniCalendarAdapter(Context context, AppLogic appLogic) {
        mContext = context;
        mAppLogic = appLogic;
        mAppDays = appLogic.getList(30, 30);

        mViewHolderList = new ArrayList<>();
    }

    //刷新数据
    public void refreshData() {
        mAppDays = mAppLogic.getList(30, 30);

        notifyDataSetChanged();

        for (ViewHolder vh : mViewHolderList) {
            //getLayoutPosition()返回的适配器位置ViewHolder传递最新的布局。
            if (vh.getLayoutPosition() != RecyclerView.NO_POSITION) {
                bindViewHolder(vh, vh.getLayoutPosition());
            }
        }
    }

    public void refreshData(int type) {
        mAppDays = mAppLogic.getList(30, 30);
        mAppDays.get(30).setDayType(type);//设置当天的类型
        notifyDataSetChanged();
        for (ViewHolder vh : mViewHolderList) {
            if (vh.getLayoutPosition() != RecyclerView.NO_POSITION) {
                bindViewHolder(vh, vh.getLayoutPosition());
            }
        }
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {//回收
        mViewHolderList.remove(holder);
    }

    @Override
    public MiniCalendarAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();

        RelativeLayout holdView = new RelativeLayout(context);
        holdView.setGravity(Gravity.CENTER);

        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout

        View contactView = inflater.inflate(R.layout.item_mini_calendar, parent, false);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        contactView.setLayoutParams(layoutParams);

        //        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        holdView.addView(contactView);

        // Return a new holder instance
        MiniCalendarAdapter.ViewHolder viewHolder = new MiniCalendarAdapter.ViewHolder(holdView);//第一次创建5个，但只要滑动就会创建剩余的65个
        mViewHolderList.add(viewHolder);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MiniCalendarAdapter.ViewHolder holder, int position) {
        //        Log.i("+++++**","onBindViewHolder"+holder);
        // Get the data model based on position
        AppDay appDay = mAppDays.get(position);

        // Set item views based on your views and data model
        TextView textView = holder.nameTextView;
        textView.setText(String.valueOf(appDay.getDay().get(Calendar.DATE)));

        if (DateUtil.dayEqual(Calendar.getInstance(), appDay.getDay())) {
            if (!AppLogic.getInstance().isInitialized()) {
                holder.tv_edit.setVisibility(View.VISIBLE);
                holder.smallView.setVisibility(View.INVISIBLE);
                holder.bigView.setVisibility(View.INVISIBLE);
            }
        }

        //
        switch (appDay.getDayType()) {
            case AppDay.DAY_TYPE_MENSTRUAL:
                //                holder.periodNameText.setText("Menstrual\nperiod");
                if (AppLogic.getInstance().queryBasic(appDay).isComing()) {
                    holder.periodNameText.setText(mContext.getString(R.string.Menstrualperiodn));
                } else {
                    holder.periodNameText.setText(mContext.getString(R.string.Expectedmenstrualperiodn));
                    if (!LanguageUtil.isALB()) {
                        holder.periodNameText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                    } else {
                        holder.periodNameText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                    }
                }
                setColor(holder, Color.WHITE);
                break;
            case AppDay.DAY_TYPE_OVULATION:
                //                holder.periodNameText.setText("Ovulation");
                holder.periodNameText.setText(mContext.getString(R.string.Ovulation));
                setColor(holder, Color.WHITE);
                break;
            case AppDay.DAY_TYPE_OVULATION_DAY:
                //                holder.periodNameText.setText("Ovulation\nDay");
                holder.periodNameText.setText(mContext.getString(R.string.OvulationDayn));
                setColor(holder, Color.WHITE);
                break;
            case AppDay.DAY_TYPE_SECURITY:
                //                holder.periodNameText.setText("Security\nperiod");
                holder.periodNameText.setText(mContext.getString(R.string.Securityperiodn));
                holder.probabilityLl.setVisibility(View.GONE);
                setColor(holder, Color.BLACK);
                break;
            default:
                setColor(holder, Color.WHITE);
                break;
        }
        holder.probabilityTv.setText(String.valueOf(appDay.getProbability()) + "%");
        holder.dateText.setText(sdf.format(appDay.getDay().getTime()) + appDay.getDay().get(Calendar.DAY_OF_MONTH));
        holder.dayCountText.setText(String.valueOf(appDay.getDayCount()));

        String language = Locale.getDefault().getLanguage();
        if (language.equals("ar") | language.equals("fa")) {
            holder.dayCountTailText.setText(mContext.getString(R.string.Day));
            if(appDay.getDayType()!=AppDay.DAY_TYPE_MENSTRUAL){
                holder.dayCountTailText.setText(mContext.getString(R.string.Dayss));
            }

        } else {//非阿拉伯
            holder.dayCountTailText.setText(AppDay.getOrdingalNumberSuffix(appDay.getDayCount()) + " " + mContext.getString(R.string.Day));
        }

        int color = AppDay.getColor(mContext, appDay.getDayType());
        android.util.Log.d(TAG, "onBindViewHolder1: "+color);
        if (appDay.isPassed() || appDay.isToday()) {
            Basic basic = AppLogic.getInstance().queryBasic(appDay);
            if (appDay.getDayType() == AppDay.DAY_TYPE_MENSTRUAL && basic.isComing()) {
                color = Color.parseColor("#FF189F");
                setColor(holder, Color.WHITE);
            }
        }
        android.util.Log.d(TAG, "onBindViewHolder2: "+color);
        OvalShape a = new OvalShape();
        ShapeDrawable drawable = new ShapeDrawable();
        drawable.setShape(a);
        drawable.getPaint().setStyle(Paint.Style.FILL);
        drawable.getPaint().setColor(color);
        holder.itemView.setBackground(drawable);
        //Log.d("bind", " " + position);
        if (appDay.getDayCount() == 0) {//未知的日期
            holder.nameTextView.setTextColor(Color.BLACK);
        }
    }

    private void setColor(MiniCalendarAdapter.ViewHolder holder, int color) {
        holder.nameTextView.setTextColor(color);
        holder.dayCountText.setTextColor(color);
        holder.dayCountTailText.setTextColor(color);
        holder.dateText.setTextColor(color);
        holder.periodNameText.setTextColor(color);
    }

    @Override
    public int getItemCount() {
        return mAppDays.size();
    }

    public AppDay getItemByPos(int pos) {
        Log.d(TAG, "getItemByPos： " + pos);
        if (pos >= 0 && pos < mAppDays.size()) {
            return mAppDays.get(pos);
        }
        return null;
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;

        public TextView dayCountText; //第几天 1st Day, 中的"1"
        public TextView dayCountTailText; //第几天  1st Day 中的 st Day
        public TextView dateText; // 日期 Jul, 06
        public TextView periodNameText; //名称 Ovulation

        public View smallView; //尚未放大时的状态
        public View bigView; //放大后的状态
        public TextView probabilityTv;//怀孕几率
        public LinearLayout probabilityLl;//怀孕几率框架
        public TextView tv_edit;//如果用户没有初始化数据，显示


        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            RelativeLayout layout = (RelativeLayout) itemView;
            nameTextView = (TextView) layout.findViewById(R.id.textViewDateNum);
            dayCountTailText = (TextView) layout.findViewById(R.id.textDayCountTail);
            probabilityTv = (TextView) layout.findViewById(R.id.tv_conceive_probability);
            dayCountText = (TextView) layout.findViewById(R.id.textDayCount);
            dateText = (TextView) layout.findViewById(R.id.textDayDate);
            periodNameText = (TextView) layout.findViewById(R.id.textDayPeriodStr);
            probabilityLl = (LinearLayout) layout.findViewById(R.id.ll_probability);
            smallView = (View) layout.findViewById(R.id.smallDayInfo);
            bigView = (View) layout.findViewById(R.id.bigDayInfo);

            smallView.setVisibility(View.VISIBLE);
            bigView.setVisibility(View.INVISIBLE);

            tv_edit = (TextView) layout.findViewById(R.id.tv_edit);
            tv_edit.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemViewType(int pos) {
        AppDay appDay = mAppDays.get(pos);
        return appDay.getDayType();
    }
}