package com.taiqudong.android.enayeh.utils;

import com.taiqudong.android.enayeh.BuildConfig;

/**
 * Created by zhangxiang on 2017/7/15.
 */

public class Log {

    public static void d(String TAG, String msg){
        if(BuildConfig.DEBUG)
            android.util.Log.d(TAG, msg);
    }

    public static void i(String TAG, String msg){
        if(BuildConfig.DEBUG)
            android.util.Log.i(TAG, msg);
    }

    public static void w(String TAG, String msg){
        if(BuildConfig.DEBUG)
            android.util.Log.w(TAG, msg);
    }

    public static void e(String TAG, String msg){
        if(BuildConfig.DEBUG)
            android.util.Log.e(TAG, msg);
    }
}
