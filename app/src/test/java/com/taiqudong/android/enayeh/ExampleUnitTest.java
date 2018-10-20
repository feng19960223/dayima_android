package com.taiqudong.android.enayeh;

import com.taiqudong.android.enayeh.application.AppDay;
import com.taiqudong.android.enayeh.application.AppLogic;

import org.junit.Test;

import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void appMonth() throws Exception{
        List<AppDay> list = AppLogic.getInstance().getMonth(2017, 0);

        for (AppDay appDay : list){
            Calendar c = appDay.getTheDay();
            System.out.println(
                    c.get(Calendar.MONTH) + ":" +
                            c.get(Calendar.DATE) + ":" +
                            c.get(Calendar.DAY_OF_WEEK));
        }
        assertEquals(true, list.size() >= 35);
        assertEquals(true, list.size() <= 42);
    }

    @Test
    public void appDay() throws Exception {
        Calendar a = Calendar.getInstance();
        Calendar b = Calendar.getInstance();
        b.add(Calendar.DATE, 1);
        AppDay appDay = new AppDay(a, b, AppDay.DAY_TYPE_MENSTRUAL);

        assertEquals(true, appDay.isCurrentMonth());
        assertEquals(true, appDay.isFuture());


        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -1);
        appDay = new AppDay(a, c, AppDay.DAY_TYPE_MENSTRUAL);

        assertEquals(true, appDay.isCurrentMonth());
        assertEquals(true, appDay.isPassed());
    }
}