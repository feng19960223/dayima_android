package com.taiqudong.android.enayeh.utils;

import android.text.TextUtils;

/**
 * Created by taiqudong on 2017/8/30.
 */

public class NumberUtil {
    public static String getChangeCount(long count) {
        String changeStr = "" + count;
        if (count < 1000) {
            changeStr = "" + count;
        } else if (count < 1000000) {
            changeStr = "" + (count / 1000) + "K";
        } else if (count < 1000000000) {
            changeStr = "" + (count / 1000 / 1000) + "M";
        } else {
            changeStr = "" + (count / 1000 / 1000 / 1000) + "B";
        }
        return changeStr;
    }

    public static String getChangeCount(String count) {
        long c = 0;
        if (!TextUtils.isEmpty(count)) {//如果为空
            try {
                c = Long.parseLong(count);
            } catch (Exception e) {
            }
        }
        return getChangeCount(c);
    }
}
