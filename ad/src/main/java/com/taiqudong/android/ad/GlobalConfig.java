package com.taiqudong.android.ad;

import android.content.Context;

import java.util.Map;

/**
 * 全局广告设置设置
 * Created by zhangxiang on 2017/9/4.
 */
public class GlobalConfig {
    private Map<String, AdConfig> allAdConfigs; //
    private int requestTimeoutMs = 8000; //请求超时时间，单位：毫秒
    private int cacheTimeoutMs = 60000 * 30;
    private int launchAdTimeout = 5000;//启动app的获取广告时的超时时间
    private int backhomeAdInterval = 14400000;//间隔4个小时

    private int choosedAdTimeout = 5000;//只会和业务逻辑有关系，这里预设5000ms

    public final static int MAX_AD_CACHE_SIZE = 15;

    private static GlobalConfig sInstance = null;


    /**
     * TODO 上下文
     * txm 20170906
     */
    private Context context;


    private static GlobalConfig instance;


    private AdContent globalAdContent;//全局缓存的广告内容
    private long globalSaveTime;//全局缓存中的广告被保存时候的时间戳
    private AdConfig globalAdConfig;//全局的广告配置


    private GlobalConfig() {
    }

    /**
     * 单例
     *
     * @return
     */
    public static GlobalConfig getInstance() {
        if (sInstance == null) {
            sInstance = new GlobalConfig();
        }
        return sInstance;
    }

    public int getRequestTimeoutMs() {
        return requestTimeoutMs;
    }

    public void setRequestTimeoutMs(int requestTimeoutMs) {
        this.requestTimeoutMs = requestTimeoutMs;
    }


    public int getCacheTimeoutMs() {
        return cacheTimeoutMs;
    }

    public void setCacheTimeoutMs(int cacheTimeoutMs) {
        this.cacheTimeoutMs = cacheTimeoutMs;

    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setAllAdConfigs(Map<String, AdConfig> allAdConfigs) {
        this.allAdConfigs = allAdConfigs;
    }

    public Map<String, AdConfig> getAllAdConfigs() {
        return allAdConfigs;
    }

    public AdConfig getAdConfig(String placeId) {
        return allAdConfigs.get(placeId);
    }


    public int getLaunchAdTimeout() {
        return launchAdTimeout;
    }

    public void setLaunchAdTimeout(int launchAdTimeout) {
        this.launchAdTimeout = launchAdTimeout;
    }

    public int getBackhomeAdInterval() {
        return backhomeAdInterval;
    }

    public void setBackhomeAdInterval(int backhomeAdInterval) {
        this.backhomeAdInterval = backhomeAdInterval;
    }


    public int getChoosedAdTimeout() {
        return choosedAdTimeout;
    }

    public void setChoosedAdTimeout(int choosedAdTimeout) {
        this.choosedAdTimeout = choosedAdTimeout;
    }

    public AdContent getGlobalAdContent() {
        return globalAdContent;
    }

    public void setGlobalAdContent(AdContent globalAdContent) {
        this.globalAdContent = globalAdContent;
    }

    public void setGlobalSaveTime(long globalSaveTime) {
        this.globalSaveTime = globalSaveTime;
    }

    public long getGlobalSaveTime() {
        return globalSaveTime;
    }

    public AdConfig getGlobalAdConfig() {
        return globalAdConfig;
    }

    public void setGlobalAdConfig(AdConfig globalAdConfig) {
        this.globalAdConfig = globalAdConfig;
    }
}