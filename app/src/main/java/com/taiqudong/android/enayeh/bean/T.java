package com.taiqudong.android.enayeh.bean;

import java.util.Calendar;

/**
 * Created by taiqudong on 2017/8/2.
 */
//用户当前选中的时间
public class T {
    public static int YY = Calendar.getInstance().get(Calendar.YEAR);
    public static int MM = Calendar.getInstance().get(Calendar.MONTH) + 1;
    public static int DD = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

    public static String getYY() {
        return "" + YY;
    }

    public static void setYY(int Y) {
        YY = Y;
    }

    public static String getMM() {
        if (MM < 10) {
            return "0" + MM;
        } else {
            return "" + MM;
        }
    }

    public static void setMM(int M) {
        MM = M;
    }

    public static String getDD() {
        if (DD < 10) {
            return "0" + DD;
        } else {
            return "" + DD;
        }
    }

    public static void setDD(int D) {
        DD = D;
    }

    public static String getString() {
        return getYY() + getMM() + getDD();
    }
}
