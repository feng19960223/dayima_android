package com.taiqudong.android.enayeh.view.miniCalendar;

/**
 * Created by zhangxiang on 2017/7/8.
 */

public class RatioUtil {
    //比例转换
    public static double ratioConvert(double leftLimit, double rightLimit, double m, double ratio){
        if(m < leftLimit){
            return m;
        }else if(m <= rightLimit){
            return leftLimit + (m -  leftLimit)*ratio;
        }else {
            return leftLimit + (rightLimit - leftLimit) * ratio + (m - rightLimit);
        }
    }
}
