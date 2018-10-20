package com.taiqudong.android.ad;

import android.util.Log;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

/**
 * 这里是程序的入口
 * 缓存机制并未实现
 * Created by zhangxiang on 2017/9/4.
 */
public class AdManager {

    private static boolean mIsInit = false;

    private static final String TAG = "AdMng";

    private static GlobalConfig globalConfig = null;

    /**
     * 初始化的方法
     */
    public static void initialize() {
        if (mIsInit) {
            Log.w(TAG, "AdManager#initialize 只需要调用一次");
            return;
        }
        if (globalConfig == null) {
            Log.w(TAG, "AdManager#adConfig = null");
            return;
        }
    }

    /**
     * 获得广告
     *
     * @param placeId
     * @param parent
     * @return
     */
    public static TNativeAd getNativeAd(String placeId, ViewGroup parent) {
        AdConfig config = GlobalConfig.getInstance().getAdConfig(placeId);
        if (config == null) {
            throw new RuntimeException("config is null.");
        }
        TNativeAd nativeAd = new TNativeAd(config, parent);
        return nativeAd;
    }

    /**
     * 获得广告
     *
     * @param placeId
     * @param parent
     * @return
     */
    public static TNativeAd getNativeAd4Splash(String placeId, ViewGroup parent) {
        Map<String, AdConfig> map = (Map<String, AdConfig>) SPUtil.getHashMapData(GlobalConfig.getInstance().getContext(), com.taiqudong.android.ad.Constants.SP_AD_SPLASH_CONFIG, AdConfig.class);
        AdConfig config = map.get(placeId);
        if (config == null) {
            throw new RuntimeException("config is null.");
        }
        TNativeAd nativeAd = new TNativeAd(config, parent);
        return nativeAd;
    }

    /**
     * 获得广告
     *
     * @param config
     * @param parent
     * @return
     */
    public static TNativeAd getNativeAd(AdConfig config, ViewGroup parent) {
        if (config == null) {
            throw new RuntimeException("config is null.");
        }
        TNativeAd nativeAd = new TNativeAd(config, parent);
        return nativeAd;
    }

    public static void setConfig(GlobalConfig config) {
        globalConfig = config;
    }
}