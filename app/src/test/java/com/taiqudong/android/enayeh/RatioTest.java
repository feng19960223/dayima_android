package com.taiqudong.android.enayeh;

import com.taiqudong.android.enayeh.view.miniCalendar.RatioUtil;

import org.junit.Test;

/**
 * Created by zhangxiang on 2017/7/8.
 */
import static org.junit.Assert.*;

public class RatioTest {

    /***
    //精度会丢失的问题
    @Test
    public void testRatio(){

        float ratio = 1.3f;
        float left = 10;
        float right = 40;


        for(int i=0; i<10000; ++i){
            int n = -100 + (int)(Math.random() * 200);
            double offset = -100f + Math.random() * 200;
            double vN = RatioUtil.ratioConvert(left, right, n, ratio);
            double vN2 = vN + offset;
            int newN = Math.round((float)(RatioUtil.ratioConvert(left, left + (right - left)*ratio, vN2, 1/ratio)));
            vN2 = RatioUtil.ratioConvert(left, right, newN, ratio);
            vN = vN2 - offset;
            newN = Math.round((float)(RatioUtil.ratioConvert(left, left + (right - left)*ratio, vN, 1/ratio)));
            assertEquals("i: " + i + " newN: " + newN + " n:" + n + " vN:" + vN, true, Math.abs(newN - n) < 0.00001);
        }
    }
     **/

    @Test
    public void testRatio2(){
        double ratio = 2.742f;
        double left = 252;
        double right = 828;
        double n = 1038f;
        double vN = RatioUtil.ratioConvert(left, right, n, 1/ratio);
        double newN = RatioUtil.ratioConvert(left, left + (right - left)/ratio, vN, ratio);

        assertEquals( " newN: " + newN + " n:" + n + " vN:" + vN, true, Math.abs(newN - n) < 0.00001);

    }
}
