package com.taiqudong.android.enayeh;

import android.support.test.runner.AndroidJUnit4;

import com.taiqudong.android.enayeh.application.AppLogic;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;

/**
 * Created by taiqudong on 2017/7/24.
 */
@RunWith(AndroidJUnit4.class)
public class AppLogicTest {
    AppLogic appLogic = AppLogic.getInstance();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    @Test
    public void text1() throws Exception {
        appLogic.setWeight(10.0f);
        assertEquals("", 10.0, appLogic.getWeight(), 0);
    }

    @Test
    public void text2() throws Exception {
        appLogic.setMentrualCicle(28);
        assertEquals(28, appLogic.getMentrualCicle());
    }

    @Test
    public void text3() throws Exception {
        appLogic.setMenstrualTime(5);
        assertEquals(5, appLogic.getMenstrualTime());
    }

    @Test
    public void text4() throws Exception {
        appLogic.setBirthday(2015, 6, 22);
        Calendar birthday = appLogic.getBirthday();
        assertEquals(2015,birthday.get(Calendar.YEAR));
        assertEquals(5,birthday.get(Calendar.MONTH));//值-1
        assertEquals(22, birthday.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void text5() throws Exception {
        appLogic.setLastMenstrualDate(2017, 5, 22);
        Calendar lastMenstrualDate = appLogic.getLastMenstrualDate();
        assertEquals(2017, lastMenstrualDate.get(Calendar.YEAR));
        assertEquals(4, lastMenstrualDate.get(Calendar.MONTH));//值-1
        assertEquals(22, lastMenstrualDate.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void text6() throws Exception {
        appLogic.setMenstrualTime(10);
        assertEquals(10,appLogic.getMenstrualTime());
    }

    @Test
    public void text7() throws Exception {
        appLogic.setStatus(3);
        assertEquals(3, appLogic.getStatus());
    }

    @Test
    public void text8() throws Exception {
        assertEquals(simpleDateFormat.format(Calendar.getInstance().getTime()), appLogic.getTodayStr());
    }

    @Test
    public void text9() throws Exception {
        appLogic.setMentrualCicle(29);//15-56//只有27是对的
        appLogic.setMenstrualTime(2);//2-14
        appLogic.setLastMenstrualDate(2017,6,25);//2017-1-1----2017-12-32

//        List<AppDay> month = appLogic.getMonth(2017, 6);//+1,7月
        //第一天20170625
        //28   20170724
//        AppDay appDay = month.get(28);
//        Calendar day = appDay.getDay();
//        String format = simpleDateFormat.format(day.getTime());
//        assertEquals("20170723",format);
//        int dayType = appDay.getDayType();
//        assertEquals(AppDay.DAY_TYPE_MENSTRUAL,dayType);//1

//        for(int i =0;i<6;i++){
//
//            AppDay appDay = month.get(i);
//            int dayType = appDay.getDayType();
//            assertEquals(AppDay.DAY_TYPE_MENSTRUAL,dayType);
//
//        }


    }

    @Test
    public void testGetAppDay() throws Exception {
    }

}
