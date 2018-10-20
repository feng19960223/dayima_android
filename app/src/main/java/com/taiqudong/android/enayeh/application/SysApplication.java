package com.taiqudong.android.enayeh.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.MobileAds;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.taiqudong.android.ad.SPUtil;
import com.taiqudong.android.ad.stats.EventLogger;
import com.taiqudong.android.enayeh.BuildConfig;
import com.taiqudong.android.enayeh.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by taiqudong on 2017/7/6.
 */

public class SysApplication extends Application {
    private static Context context;
    private int titleHeight;
    private int stateHeight;
    static SysApplication app;
    private static final String TAG = "SysApplication";
    /**
     * activity实例保存
     */
    private CopyOnWriteArrayList<Activity> mList = new CopyOnWriteArrayList<>();

    YouTubeInitializationResult youTubeInitializationResult;

    public synchronized static SysApplication getInstance() {
        if (app == null) {
            app = new SysApplication();
        }
        return app;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        AppLogic.init(this);
        EventLogger.start(this);
        /**
         * TODO 初始化谷歌的广告
         */
        MobileAds.initialize(this, "ca-app-pub-9477775406918793~7966840072");
        //        FacebookSdk.setIsDebugEnabled(true);
        //        FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS);
        //配置appflyer.
        AppsFlyerConversionListener conversionDataListener = new AppsFlyerConversionListener() {
            @Override
            public void onInstallConversionDataLoaded(Map<String, String> map) {
                for (String attrName : map.keySet()) {
                    Log.d(AppsFlyerLib.LOG_TAG, "attribute: " + attrName + " = " +
                            map.get(attrName));
                }
                //SCREEN VALUES//
                final String install_type = "Install Type: " + map.get("af_status");
                final String media_source = "Media Source: " + map.get("media_source");
                final String install_time = "Install Time(GMT): " + map.get("install_time");
                final String click_time = "Click Time(GMT): " + map.get("click_time");
                Log.d(AppsFlyerLib.LOG_TAG, "Install_Type:" + install_type);
                Log.d(AppsFlyerLib.LOG_TAG, "media_source:" + media_source);
                Log.d(AppsFlyerLib.LOG_TAG, "install_time:" + install_time);
                Log.d(AppsFlyerLib.LOG_TAG, "click_time:" + click_time);

            }

            @Override
            public void onInstallConversionFailure(String s) {
                Log.d(AppsFlyerLib.LOG_TAG, "error getting conversion data: " + s);
            }

            @Override
            public void onAppOpenAttribution(Map<String, String> map) {

            }

            @Override
            public void onAttributionFailure(String s) {
                Log.d(AppsFlyerLib.LOG_TAG, "error onAttributionFailure : " + s);
            }
        };
        AppsFlyerLib.getInstance().setAndroidIdData(AppLogic.getInstance().getDeviceId());
        AppsFlyerLib.getInstance().init(BuildConfig.AF_KEY, conversionDataListener);
        AppsFlyerLib.getInstance().startTracking(this);
    }

    private static List<Activity> activities = new ArrayList<>();//初始化数据

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeAllActivity() {
        for (Activity activity : activities) {
            activity.finish();
        }
    }

    public static Context getContext() {
        return context;
    }

    private static List<Activity> activitiesLogin = new ArrayList<>();//用户通过个人中心修改数据

    public static void addActivitiesLogin(Activity activity) {
        activitiesLogin.add(activity);
    }

    public static void removeAllActivitiesLogin() {
        for (Activity activity : activitiesLogin) {
            activity.finish();
        }
    }

    public YouTubeInitializationResult getYouTubeInitializationResult() {
        return youTubeInitializationResult;
    }

    public void setYouTubeInitializationResult(YouTubeInitializationResult youTubeInitializationResult) {
        this.youTubeInitializationResult = youTubeInitializationResult;
    }

    public int getTitleHeight() {
        return titleHeight;
    }

    public void setTitleHeight(int titleHeight) {
        this.titleHeight = titleHeight;
    }

    public int getStateHeight() {
        return stateHeight;
    }

    public void setStateHeight(int stateHeight) {
        this.stateHeight = stateHeight;
    }

    public static boolean isNoWiFiVedioPlay = false;

    /**
     * 退出整个程序
     */
    public void exit(Context context) {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    /**
     * TODO 释放之前的activity
     * txm 20170908
     *
     * @param context
     */
    public void finishBeforeActivity(Context context) {
        try {
            for (Activity activity : mList) {
                if (activity != null) {
                    Log.d(TAG, activity.getLocalClassName());
                    activity.finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }


    /**
     * 添加Activity
     */
    public void addActivit(Activity activity) {
        mList.add(activity);
    }

    /**
     * 出栈
     *
     * @param activity
     */
    public void removeActivity(Activity activity) {
        for (Activity activity1 : mList) {
            if (activity1 == activity) {
                mList.remove(activity1);
            }
        }
    }
}