package com.taiqudong.android.enayeh.activity;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.taiqudong.android.ad.stats.EventConsts;
import com.taiqudong.android.ad.stats.EventLogger;
import com.taiqudong.android.enayeh.R;
import com.taiqudong.android.enayeh.application.AppDay;
import com.taiqudong.android.enayeh.application.AppLogic;
import com.taiqudong.android.enayeh.application.SysApplication;
import com.taiqudong.android.enayeh.bean.Basic;
import com.taiqudong.android.enayeh.utils.DateUtil;
import com.taiqudong.android.enayeh.utils.Log;
import com.taiqudong.android.enayeh.utils.StatusBarUtils;
import com.taiqudong.android.enayeh.view.calendar.CalendarMonthView;
import com.taiqudong.android.enayeh.view.calendar.UserCalendarView;
import com.taiqudong.android.enayeh.view.calendar.adapter.CalendarEditAdapter;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by taiqudong on 2017/7/31.
 */

public class EditActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv;
    private UserCalendarView userCalendarView;
    private TextView iv_gotoday;

    long scroll = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        SysApplication.getInstance().addActivit(this);
        StatusBarUtils.statusbar(this);
        scroll = getIntent().getLongExtra("scroll", 0);
        initView();
        initListener();
        initViewPager();
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        date.setTime(scroll);
        calendar.setTime(date);
        setToolbarString(calendar);

        if (DateUtil.monthEqual(Calendar.getInstance(), calendar)) {
            iv_gotoday.setVisibility(View.INVISIBLE);
        } else {
            iv_gotoday.setVisibility(View.VISIBLE);
        }

        userCalendarView.setCurrentItem(calendar);
    }

    private void initView() {
        tv = (TextView) findViewById(R.id.tv);
        userCalendarView = (UserCalendarView) findViewById(R.id.vp_edit);
        iv_gotoday = (TextView) findViewById(R.id.iv_gotoday);
    }

    private boolean isMORE = true;

    private void initViewPager() {
        userCalendarView.init(new CalendarEditAdapter(this));
        userCalendarView.setOnCalendarClickListener(new CalendarMonthView.OnCalendarClickListener() {
            @Override
            public void onClick(View view, int position, AppDay appDay) {
                Basic basic = AppLogic.getInstance().queryBasic(appDay);
                if (basic.isComing()) {
                    userCalendarView.selectMenstrual(appDay.getDay());
                    isMORE = false;
                    return;
                }
                if (isMORE) {//多个
                    // 第一次点用户记录的多个个点
                    Calendar today = Calendar.getInstance();
                    for (int i = 0; i < AppLogic.getInstance().getMenstrualTime(); i++) {
                        Calendar cal = DateUtil.copy(appDay.getDay());
                        cal.add(Calendar.DATE, i);
                        if (DateUtil.dayCmp(cal, today) > 0) {
                            break;
                        }
                        if (AppLogic.getInstance().queryBasic(AppLogic.getInstance().getAppDay(cal)).isComing()) {//不要反选，如果来了就是来了
                            continue;
                        }
                        userCalendarView.selectMenstrual(cal);
                    }
                    isMORE = false;
                } else {
                    userCalendarView.selectMenstrual(appDay.getDay());
                }
            }
        });
        userCalendarView.setOnCalendarChangeListener(new UserCalendarView.OnCalendarChangeListener() {
            @Override
            public void OnSelectedChange(Calendar cal) {
                Calendar today = Calendar.getInstance();
                setToolbarString(cal);
                if (DateUtil.monthEqual(today, cal)) {
                    iv_gotoday.setVisibility(View.INVISIBLE);
                } else {
                    iv_gotoday.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    private void initListener() {
        findViewById(R.id.iv_return).setOnClickListener(this);
        findViewById(R.id.tv_edit_cancel).setOnClickListener(this);
        findViewById(R.id.tv_edit_ok).setOnClickListener(this);
        iv_gotoday.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return:
                userCalendarView.totalRefresh();
                this.finish();
                break;
            case R.id.tv_edit_cancel:
                userCalendarView.totalRefresh();
                this.finish();
                break;
            case R.id.tv_edit_ok:
                if (userCalendarView.getSelectResult() != null) {
                    for (Map.Entry<String, Boolean> entry : userCalendarView.getSelectResult().entrySet()) {
                        Log.i("getKey" + entry.getKey(), "getValue" + entry.getValue());
                        menstrualationChange(entry.getKey(), entry.getValue());
                    }
                }
                userCalendarView.totalRefresh();
                this.finish();
                EventLogger.logEvent(EventConsts.e_JingQiBianJi, EventConsts.p_BianJiJingQi);
                break;
            case R.id.iv_gotoday:
                userCalendarView.setCurrentItem(AppLogic.getInstance().getTodayAppDay().getTheDay());
                Calendar calendar = Calendar.getInstance();
                setToolbarString(calendar);
                iv_gotoday.setVisibility(View.INVISIBLE);
                break;
            default:
        }
    }

    //月经记录
    private void menstrualationChange(Calendar cal, boolean isInMenstrualation) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("coming", isInMenstrualation);
        AppLogic.getInstance().updateBasic(AppLogic.getInstance().getSdf().format(cal.getTime()),
                contentValues);
    }

    private void menstrualationChange(String string, boolean isInMenstrualation) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("coming", isInMenstrualation);
        if (!isInMenstrualation) {//false就清空月经数据
            contentValues.put("menstruation", 0);
            contentValues.put("dysmenorrhea", 0);
        }
        AppLogic.getInstance().updateBasic(string, contentValues);
    }

    private void setToolbarString(Calendar cal) {
        int y = Calendar.getInstance().get(Calendar.YEAR);
        int yy = cal.get(Calendar.YEAR);
        if (y == yy) {
            int m = cal.get(Calendar.MONTH) + 1;
            String M = "" + m;
            if (m < 10) {
                M = "0" + m;
            }
            tv.setText(M);
        } else {
            int m = cal.get(Calendar.MONTH) + 1;
            String M = "" + m;
            if (m < 10) {
                M = "0" + m;
            }
            tv.setText("" + yy + "" + M);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SysApplication.getInstance().removeActivity(this);
    }
}
