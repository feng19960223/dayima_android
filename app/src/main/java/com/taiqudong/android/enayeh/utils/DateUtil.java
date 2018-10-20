package com.taiqudong.android.enayeh.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by taiqudong on 2017/7/8.
 */

public class DateUtil {
    /**
     * @return 01-31的字符串数组
     */
    public static String[] getDays() {//1-31
        String days[] = new String[31];
        for (int i = 0; i < 31; i++) {
            if (i < 9) {
                days[i] = "0" + (i + 1);
            } else {
                days[i] = "" + (i + 1);
            }
        }
        return days;
    }

    /**
     * yyyy-MM
     * 得到某年某月的天数
     *
     * @param date yyyy-MM
     * @return yyyy年MM月的天数
     */
    public static int getDaysOfMonth(String date) {//得到某年某月的天数
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Calendar calendar = GregorianCalendar.getInstance();
        try {
            calendar.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static String[] getMonth() {//英文简体
        SimpleDateFormat sdf = new SimpleDateFormat("MMM");
        String[] months = new String[12];
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        for (int i = 0; i < 12; i++) {
            cal.set(Calendar.MONTH, i);
            months[i] = sdf.format(cal.getTime());
        }
        return months;
    }


    /**
     * int型的日期转换
     * @param yyyyMMdd
     * @return
     */
    public static Calendar int2date(int yyyyMMdd){

        int yyyy = yyyyMMdd / 10000;
        int MM = yyyyMMdd % 10000 / 100 - 1;
        int dd = yyyyMMdd % 100;

        Calendar cal = Calendar.getInstance();

        cal.set(yyyy, MM, dd, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal;
    }

    /**
     * 日期转成int 形如 yyyyMMdd
     * @param cal
     * @return
     */
    public static int date2int(Calendar cal){
        int ret = cal.get(Calendar.YEAR) * 10000 +
                (cal.get(Calendar.MONTH) + 1) * 100 +
                cal.get(Calendar.DATE);
        return ret;
    }

    /**
     * 日期转成int 形如 yyyyMMdd
     * @param cal
     * @return
     */
    public static  String date2str(Calendar cal){
        return String.valueOf(date2int(cal));
    }

    /**
     * 年月是否相同
     */
    public static boolean monthEqual(Calendar c1, Calendar c2){
        if(c1 == null || c2 == null){
            return false;
        }
        return (
                c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                        c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
        );
    }


    /**
     * 年月日是否相同
     */
    public static boolean dayEqual(Calendar c1, Calendar c2){
        if(c1 == null || c2 == null){
            return false;
        }
        return (
                c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                        c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) &&
                        c1.get(Calendar.DATE) == c2.get(Calendar.DATE)
                );
    }

    /**
     * 拷贝
     */
    public static Calendar copy(Calendar c){
        if(c == null){
            return c;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(c.getTimeInMillis());
        return cal;
    }

    /**
     * 年月日的比较方法
     * @param c1
     * @param c2
     * @return
     */
    public  static  int dayCmp(Calendar c1, Calendar c2){
        int d1 = date2int(c1);
        int d2 = date2int(c2);

        return d1 - d2;
    }
}
