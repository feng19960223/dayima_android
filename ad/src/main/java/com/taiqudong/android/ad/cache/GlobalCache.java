package com.taiqudong.android.ad.cache;

import com.taiqudong.android.ad.AdConfig;
import com.taiqudong.android.ad.AdContent;
import com.taiqudong.android.ad.GlobalConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by zhangxiang on 2017/9/4.
 */

public class GlobalCache {

    private Map<String, Queue<AdContentHolder>> mCache;

    private GlobalCache() {
        mCache = new HashMap<>();
    }

    private static GlobalCache sInstance = null;


    public static GlobalCache getInstance() {
        if (sInstance == null) {
            sInstance = new GlobalCache();
        }
        return sInstance;
    }

    /**
     * 将广告插入缓存
     *
     * @param placeId
     * @param source
     * @param ad
     */
    public void pushAd(String placeId, AdConfig.Source source, AdContent ad, int timeout) {
        AdContentHolder holder = new AdContentHolder();
        holder.setContent(ad);
        holder.setTimeoutTs(System.currentTimeMillis() + timeout);
        pushAdHolder(placeId, source, holder);
    }

    public void pushAdHolder(String placeId, AdConfig.Source source, AdContentHolder holder) {
        String key = getKey(placeId, source);
        synchronized (this) {
            if (!mCache.containsKey(key)) {
                mCache.put(key, new LinkedBlockingQueue<AdContentHolder>(GlobalConfig.MAX_AD_CACHE_SIZE));
            }
            Queue<AdContentHolder> queue = mCache.get(key);
            while (!queue.offer(holder)) {
                queue.poll();
            }
        }
    }

    private String getKey(String placeId, AdConfig.Source source) {
        return String.format("%s_%s_%s", placeId, source.getAdKey(), source.getAdType());
    }

    /**
     * 获得广告并从缓存中移除
     *
     * @param placeId
     * @return
     */
    public AdContent popAd(String placeId, AdConfig.Source source) {

        AdContent ad = null;
        AdContentHolder holder = popAdHolder(placeId, source);
        if (holder != null) {
            ad = holder.getContent();
        }
        return ad;
    }

    public AdContentHolder popAdHolder(String placeId, AdConfig.Source source) {
        String key = getKey(placeId, source);
        synchronized (this) {
            if (mCache.containsKey(key)) {
                Queue<AdContentHolder> queue = mCache.get(key);
                AdContentHolder holder = null;
                long ts = System.currentTimeMillis();
                do {
                    holder = queue.poll();
                } while (holder != null && holder.isTimeout(ts));
                //如果取到了未超时的广告
                return holder;
            }
        }
        return null;
    }

    /**
     * 该广告位是否有特定的广告
     *
     * @param placeId
     * @param source
     * @return
     */
    public boolean hasAd(String placeId, AdConfig.Source source) {
        String key = getKey(placeId, source);
        if (mCache.containsKey(key)) {
            synchronized (this) {
                Queue<AdContentHolder> queue = mCache.get(key);
                AdContentHolder holder = queue.peek();
                long ts = System.currentTimeMillis();
                while (holder != null && holder.isTimeout(ts)) {
                    queue.poll();
                    holder = queue.peek();
                }
                return holder != null;
            }
        }
        return false;
    }
}