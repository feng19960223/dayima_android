package com.taiqudong.android.enayeh.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by tangxianming on 2017/8/26.
 */

public class TimeUtil {

    public static String getDate(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String date = simpleDateFormat.format(time);
        return date;
    }
}
