package com.taiqudong.android.enayeh.application;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;

import com.taiqudong.android.enayeh.R;

import java.util.Calendar;

/**
 * 用来代表日历上的日期
 * Created by zhangxiang on 2017/7/6.
 */
public class AppDay {
    public final static int DAY_TYPE_UNKNOWN = -1;//无法计算
    public final static int DAY_TYPE_NORMAL = 0; //正常的日子
    public final static int DAY_TYPE_MENSTRUAL = 1; //月经期
    public final static int DAY_TYPE_SECURITY = 2; //安全期
    public final static int DAY_TYPE_OVULATION = 3; //排卵期
    public final static int DAY_TYPE_OVULATION_DAY = 4; //排卵日


    private boolean isToday = false; //是否是今天
    private boolean isPassed = false;
    private boolean isFuture = false;

    private boolean isPredicted = false; //是否是预测值， 否的话即是用户设置的

    private Calendar today = null; //当前日期
    private Calendar theDay = null; //实际日期
    private int mDayType = 0; //type

    private int mDayCount = 0;// 这是第几天

    private float mWeight = 0; //体重，单位，千克

    private int mMonth = 0;

    public AppDay(Calendar today, Calendar theDay, int dayType) {
        strip(today);
        strip(theDay);

        this.today = today;
        this.theDay = theDay;

        //日期判断
        int todayNum = getDayNum(today);
        int theDayNum = getDayNum(theDay);
        if (todayNum == theDayNum) {
            this.isToday = true;
        } else if (todayNum < theDayNum) {
            this.isFuture = true;
        } else {
            this.isPassed = true;
        }

        //日期类型
        this.mDayType = dayType;
    }


    //0..11;
    public void setCurrentMonth(int month){
        mMonth = month;
    }



    private void strip(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    public Calendar getTheDay() {
        return theDay;
    }

    private int getDayNum(Calendar cal) {
        return cal.get(Calendar.YEAR) * 10000 + cal.get(Calendar.MONTH) * 100 + cal.get(Calendar.DATE);
    }

    public void setDayType(int t) {
        mDayType = t;
    }

    public Calendar getDay() {
        return theDay;
    }

    public int getDayType() {
        return mDayType;
    }


    public boolean isPredicted() {
        return isPredicted;
    }

    public void setPredicted(boolean predicted) {
        isPredicted = predicted;
    }

    //是否过去；
    public boolean isPassed() {
        return isPassed;
    }

    //是否是今天
    public boolean isToday() {
        return isToday;
    }

    //是否是以后
    public boolean isFuture() {
        return isFuture;
    }


    //这是第几天
    public int getDayCount() {
        return mDayCount;
    }

    public void setDayCount(int c) {
        mDayCount = c;
    }

    //上个月
    public boolean isPrevMonth() {
        return theDay.get(Calendar.MONTH) < mMonth;
    }

    //这个月
    public boolean isCurrentMonth() {
        return theDay.get(Calendar.MONTH) == mMonth;
    }

    //下个月
    public boolean isNextMonth() {
        return theDay.get(Calendar.MONTH) > mMonth;
    }


    //获得体重
    public float getWeight() {
        return mWeight;
    }

    //设置体重
    public void setWeight(float mWeight) {
        this.mWeight = mWeight;
    }


    //是否正常的经期
    public static boolean isSafeMenstualLength(int n) {
        return n >= 3 && n <= 5;
    }


    //是否正常的经期间隔
    public static boolean isSafeNormalLength(int n) {
        return n >= 25 && n <= 35;
    }

    //返回数字的后缀
    public static String getOrdingalNumberSuffix(int n) {
        String ret = null;
        switch (n % 10) {
            case 1:
                ret = "st";
                break;
            case 2:
                ret = "nd";
                break;
            case 3:
                ret = "rd";
                break;
            default:
                ret = "th";
                break;
        }
        return ret;
    }

    public static int getColor(Context context, int viewType) {
        Resources res = context.getResources();
        int color = Color.WHITE;
        switch (viewType) {
            case AppDay.DAY_TYPE_MENSTRUAL:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    color = res.getColor(R.color.colorMenstrual, context.getTheme());
                    color = Color.parseColor("#FFC6E8");
                } else {
//                    color = res.getColor(R.color.colorMenstrual);
                    color = Color.parseColor("#FFC6E8");
                }
                break;
            case AppDay.DAY_TYPE_OVULATION:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    color = res.getColor(R.color.colorDayOvulation2, context.getTheme());
                } else {
                    color = res.getColor(R.color.colorDayOvulation2);
                }
                break;
            case AppDay.DAY_TYPE_SECURITY:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    color = res.getColor(R.color.colorDayOther, context.getTheme());
                } else {
                    color = res.getColor(R.color.colorDayOther);
                }
                break;
            case AppDay.DAY_TYPE_OVULATION_DAY:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    color = context.getResources().getColor(R.color.colorDayOvulationDay2, context.getTheme());
                } else {
                    color = res.getColor(R.color.colorDayOvulationDay2);
                }
                break;
            case AppDay.DAY_TYPE_NORMAL:
            default:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    color = context.getResources().getColor(R.color.colorDayOther, context.getTheme());
                } else {
                    color = res.getColor(R.color.colorDayOther);
                }
                break;
        }
        return color;
    }

    //安全期返回为0，要特殊修改（判断）
    public int getProbability( ){//怀孕概率
        int probability = 0;
        if(getDayType()==AppDay.DAY_TYPE_OVULATION_DAY){//排卵日
            probability = 90;
        }else if(getDayType()==AppDay.DAY_TYPE_MENSTRUAL){//月经期
            probability = 5;
        }else if(getDayType()==AppDay.DAY_TYPE_OVULATION){//排卵期
            switch (getDayCount()){
                case 1:probability = 35;break;
                case 2:probability = 40;break;
                case 3:probability = 55;break;
                case 4:probability = 65;break;
                case 5:probability = 80;break;
                case 6:probability = 90;break;
                case 7:probability = 80;break;
                case 8:probability = 75;break;
                case 9:probability = 65;break;
                case 10:probability = 60;break;
                default:
                    probability=35;
            }
        }
        return probability;
    }
}
