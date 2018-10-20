package com.taiqudong.android.ad.stats;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.facebook.appevents.AppEventsLogger;

/**
 * Created by zhangxiang on 2017/8/7.
 */

public class EventLogger {


    private static AppEventsLogger logger;
    private static boolean isInit = false;
    private final static String TAG = "EvtLog";

    //写在 Application 的onCreate 方法内
    public static void start(Application app) {
        AppEventsLogger.activateApp(app);
        logger = AppEventsLogger.newLogger(app);
        isInit = true;
    }

    //
    public static void logEvent(String evt) {
        if (!isInit) {
            Log.w(TAG, "logEvent without call start()");
            return;
        }
        logger.logEvent(evt);

    }

    //
    public static void logEvent(String evt, Bundle params) {
        if (!isInit) {
            Log.w(TAG, "logEvent without call start()");
            return;
        }
        logger.logEvent(evt, params);
    }

    //表示事件发生
    public static void logEvent(String evt, String from) {
        logEvent(evt, EventConsts.Cong, from);
    }

    //Feed流用了2次，name id，val id值,参考adapter/FeedAdapter.FeedViewHoloder点击事件
    public static void logEvent(String evt, String name, String val) {
        Bundle bundle = new Bundle();
        bundle.putString(name, val);
        logEvent(evt, bundle);
    }

}
