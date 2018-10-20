package com.taiqudong.android.ad;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.telecom.Call;
import android.util.Log;

import com.taiqudong.android.ad.cache.AdContentHolder;
import com.taiqudong.android.ad.cache.GlobalCache;
import com.taiqudong.android.ad.loader.FbAdLoaderFactory;
import com.taiqudong.android.ad.loader.GgAdLoaderFactory;
import com.taiqudong.android.ad.loader.SingleAdLoader;
import com.taiqudong.android.ad.loader.SingleAdLoaderFactory;
import com.taiqudong.android.ad.loader.SingleAdLoaderListener;
import com.taiqudong.android.ad.stats.EventConsts;
import com.taiqudong.android.ad.stats.EventLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AdParallelLoader#loadAd 加载广告，
 * <p>
 * 并发请求加载多个广告，并将未展示的广告写入缓存
 * Created by zhangxiang on 2017/9/5.
 */
public class AdParallelLoader {
    private AdLoadListener adLoadListener;
    private AdConfig adConfig;
    private static final String TAG = "AdParallelLoader";
    private FbAdLoaderFactory fbAdLoaderFactory;
    private GgAdLoaderFactory ggAdLoaderFactory;

    //超时后执行
    private Handler timeoutHandler;

    //是否超时
    // adLoadListener.onError 已经调用
    private boolean isTimeout;

    //最高优先级广告是否已经完成
    // adLoadListener.onAdLoaded 已经调用
    private boolean isFinished;

    //超时方法
    private Runnable timeoutRunnable;

    //是否被中断
    private boolean isAborted;

    //是否已经发送了请求
    private boolean isStarted;
    //请求是否返回
    Map<AdConfig.Source, Boolean> requestFinished = new HashMap<>();
    //请求返回的结果
    Map<AdConfig.Source, AdContentHolder> responses = new HashMap<>();

    //已经绑定的listener
    private List<QSQListener> boundedListeners;


    //fb广告是否获取为null(全局情况)
    private boolean isFBEmpty;

    public AdParallelLoader(final AdConfig adConfig) {
        this.adConfig = adConfig;
        ggAdLoaderFactory = new GgAdLoaderFactory();
        fbAdLoaderFactory = new FbAdLoaderFactory();
        boundedListeners = new ArrayList<>(adConfig.getSources().size());
        timeoutHandler = new Handler();
        timeoutRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isFinished) {
                    Log.d(TAG, "timeout");
                    isTimeout = true;
                    //广告超时
                    Bundle bundle = new Bundle();
                    bundle.putString(EventConsts.AD_ACTION, EventConsts.AD_TIMEOUT);
                    //分类别：fb/gg
                    if (GgAdLoaderFactory.GGSingleAdLoader.status == 0) {
                        bundle.putString(EventConsts.AD_SDK, EventConsts.GG);
                        GgAdLoaderFactory.GGSingleAdLoader.status = 1;
                    }
                    if (FbAdLoaderFactory.FBSingleAdLoader.status == 0) {
                        bundle.putString(EventConsts.AD_SDK, EventConsts.FB);
                        FbAdLoaderFactory.FBSingleAdLoader.status = 1;
                    }
                    String ctg = (String) SPUtil.get(GlobalConfig.getInstance().getContext(), Constants.CTG, "");
                    if (!ctg.equals("")) {
                        bundle.putString(EventConsts.CTG, ctg);
                    }
                    EventLogger.logEvent(adConfig.getPlaceId(), bundle);
                    sweep();
                }
                resetState();
            }
        };
        resetState();
    }

    /**
     * 重载各种状态
     */
    private void resetState() {
        this.isFinished = false;
        this.isTimeout = false;
        this.isAborted = false;
        this.isStarted = false;
        requestFinished.clear();

        if (boundedListeners.size() > 0) {
            //如果已经绑定的listener
            for (QSQListener listener : boundedListeners) {
                listener.invalidate();
            }
            boundedListeners.clear();
        }
    }

    /**
     * TODO 同步发出所有请求，最后加载成功后调用 AdloadListener
     * 发出广告请求
     */
    public void loadAd() {
        if (adLoadListener == null) {
            Log.e(TAG, "adLoadListener 不等为 null");
            return;
        }
        loadAdParallel();
    }

    /**
     * 判断是否所有都完成了
     *
     * @param map
     * @return
     */
    private boolean isRequestFinished(Map<AdConfig.Source, Boolean> map) {
        boolean ret = true;
        for (Map.Entry<AdConfig.Source, Boolean> entry : map.entrySet()) {
            if (entry.getValue().booleanValue() == false) {
                return false;
            }
        }
        return ret;
    }

    /**
     * 并发加载各个广告
     */
    private void loadAdParallel() {
        timeoutHandler.postDelayed(timeoutRunnable, GlobalConfig.getInstance().getRequestTimeoutMs());
        for (AdConfig.Source source : adConfig.getSources()) {
            SingleAdLoaderFactory adLoaderFactory = null;
            //查找对应的AdLoaderFactory
            if (Constants.AD_TYPE_FB.equals(source.getAdType())) {
                adLoaderFactory = fbAdLoaderFactory;
            } else if (Constants.AD_TYPE_GG.equals(source.getAdType())) {
                adLoaderFactory = ggAdLoaderFactory;
            }
            //如果找到了对应的loaderFactory
            if (adLoaderFactory != null) {
                isStarted = true;
                requestFinished.put(source, false);
                QSQListener listener = new QSQListener(source);
                boundedListeners.add(listener);
                AdContentHolder holder = GlobalCache.getInstance().popAdHolder(adConfig.getPlaceId(), source);
                if (holder != null) {
                    //如果缓存内有广告
                    responses.put(source, holder);
                    requestFinished.put(source, true);

                    //加入缓存
                    Bundle bundle = new Bundle();
                    bundle.putString(EventConsts.AD_ACTION, EventConsts.READ_SUCCESSFULLY);
                    EventLogger.logEvent(EventConsts.AD_CHACHE, bundle);

                    sweep();
                } else {
                    //没有广告则请求
                    SingleAdLoader singleAdLoader = adLoaderFactory.getSingleAdLoader(adConfig, source, listener);
                    singleAdLoader.loadAd();
                    requestFinished.put(source, false);
                }
            }
        }
        if (!isStarted) {
            timeoutHandler.removeCallbacks(timeoutRunnable);
            adLoadListener.onError(new AdError());
        }
    }

    /**
     * 单独调试时可以调用，替换 AdParallelLoader#loadAdParallel
     */
    private void loadAdSimple() {
        //以下是调用示例
        //这里并没有实现多个请求同步发出，但是确保可以调试
        List<AdConfig.Source> sourceList = adConfig.getSources();
        SingleAdLoader loader = ggAdLoaderFactory.getSingleAdLoader(adConfig, sourceList.get(0), new SingleAdLoaderListener() {
            @Override
            public void onAdLoaded(AdContent ad) {
                adLoadListener.onAdLoaded(ad);
            }

            @Override
            public void onError(AdError error) {
                adLoadListener.onError(error);
            }
        });
        loader.loadAd();
    }

    /**
     * 中断所有请求
     */
    public void abort() {
        resetState();
    }

    /**
     * 设置广告加载listener
     *
     * @param listener
     */
    public void setAdLoadListener(AdLoadListener listener) {
        this.adLoadListener = listener;
    }


    public interface AdLoadListener {

        /**
         * 广告加载成功
         *
         * @param ad
         */
        void onAdLoaded(AdContent ad);

        /**
         * 广告加载失败
         *
         * @param error
         */
        void onError(AdError error);
    }

    //清理现场
    private void sweep() {
        long ts = System.currentTimeMillis();
        if (isTimeout) {
            //超时的情况
            //将展示现有的广告
            for (AdConfig.Source source : adConfig.getSources()) {
                AdContentHolder holder = responses.get(source);
                if (holder != null) {
                    isFinished = true;
                    //加载成功广告
                    adLoadListener.onAdLoaded(holder.getContent());
                    responses.remove(source);
                    break;
                }
            }
            if (!isFinished) {
                isFinished = true;
                //TODO 这里加载全局广告
                long duration = System.currentTimeMillis() - GlobalConfig.getInstance().getGlobalSaveTime();
                if (GlobalConfig.getInstance().getGlobalAdContent() != null && duration <= 1.8e6) {
                    adLoadListener.onAdLoaded(GlobalConfig.getInstance().getGlobalAdContent());
                    //TODO 使用后，全局广告要置空。
                    GlobalConfig.getInstance().setGlobalAdContent(null);
                } else {
                    //如果一条广告都没有加载成功,报错.
                    adLoadListener.onError(new AdError());
                }
                //TODO 重新获取全局广告
                obtainGlobalAdContent();
            }
            //状态被重置, 之后返回的结果都被放弃
            resetState();
            return;
        } else if (!isFinished) {//正常的请求返回, 查找优先级最高，并且正确返回的结果
            for (AdConfig.Source source : adConfig.getSources()) {
                Boolean fin = requestFinished.get(source);
                if (fin == null || fin.booleanValue() == false) {
                    //当前请求未返回结果
                    break;
                } else {
                    AdContentHolder holder = responses.get(source);
                    if (holder == null) {
                        continue;
                    } else { //
                        responses.remove(source);
                        adLoadListener.onAdLoaded(holder.getContent());
                        //如果广告成功展示
                        timeoutHandler.removeCallbacks(timeoutRunnable);
                        isFinished = true;
                        break;
                    }
                }
            }
        } else { //完成了的状态, 全部数据直接入缓存
            for (AdConfig.Source source : adConfig.getSources()) {
                AdContentHolder holder = responses.get(source);
                //TODO holder可能为空
                if (holder != null && !holder.isTimeout(ts)) { //未超时，则加入缓存
                    GlobalCache.getInstance().pushAdHolder(adConfig.getPlaceId(), source, holder);
                    responses.remove(source);
                    //加入缓存
                    Bundle bundle = new Bundle();
                    bundle.putString(EventConsts.AD_ACTION, EventConsts.WRITE);
                    EventLogger.logEvent(EventConsts.AD_CHACHE, bundle);
                }
            }
        }
        if (isRequestFinished(requestFinished)) {
            //所有请求都完成了
            resetState();
            prefetch();
        }
    }

    //预加载缺失的广告
    public void prefetch() {
        for (AdConfig.Source source : adConfig.getSources()) {
            if (!GlobalCache.getInstance().hasAd(adConfig.getPlaceId(), source)) {
                //没有广告，则预加载
                SingleAdLoaderFactory adLoaderFactory = null;
                if (Constants.AD_TYPE_FB.equals(source.getAdType())) {
                    adLoaderFactory = fbAdLoaderFactory;
                } else if (Constants.AD_TYPE_GG.equals(source.getAdType())) {
                    adLoaderFactory = ggAdLoaderFactory;
                }
                //如果找到了对应的loaderFactory
                if (adLoaderFactory != null) {
                    isStarted = true;
                    requestFinished.put(source, false);
                    CacheRequestListener listener = new CacheRequestListener(source);
                    SingleAdLoader singleAdLoader = adLoaderFactory.getSingleAdLoader(adConfig, source, listener);
                    singleAdLoader.loadAd();
                }
            }
        }
    }

    class CacheRequestListener implements SingleAdLoaderListener {

        private AdConfig.Source mSource;

        public CacheRequestListener(AdConfig.Source source) {
            mSource = source;
        }

        @Override
        public void onAdLoaded(AdContent ad) {
            GlobalCache.getInstance().pushAd(adConfig.getPlaceId(), mSource, ad,
                    GlobalConfig.getInstance().getCacheTimeoutMs());
        }

        @Override
        public void onError(AdError error) {
            Log.w(TAG, "预加载广告失败" + error);
        }
    }

    /**
     * 监听请求，并且记录结果
     */
    class QSQListener implements SingleAdLoaderListener {

        private AdConfig.Source mSource;
        private boolean isValid = true;

        public QSQListener(AdConfig.Source source) {
            mSource = source;
        }

        public void invalidate() {
            isValid = false;
            mSource = null;
        }

        @Override
        public void onAdLoaded(AdContent ad) {
            if (!isValid) {
                return;
            }
            requestFinished.put(mSource, true);
            AdContentHolder holder = new AdContentHolder();
            holder.setTimeoutTs(System.currentTimeMillis() + GlobalConfig.getInstance().getRequestTimeoutMs());
            holder.setContent(ad);
            responses.put(mSource, holder);
            //按照优先级查找
            sweep();
        }

        @Override
        public void onError(AdError error) {
            if (!isValid) {
                return;
            }
            requestFinished.put(mSource, true);
            sweep();
        }
    }

    /**
     * TODO 配置全局广告，从网络获取广告当作是全局广告。
     * txm 20170911
     */
    public void obtainGlobalAdContent() {
        if (GlobalConfig.getInstance().getGlobalAdConfig() == null) {
            return;
        }
        AdConfig adConfig = GlobalConfig.getInstance().getGlobalAdConfig();
        for (AdConfig.Source source : adConfig.getSources()) {
            SingleAdLoaderFactory adLoaderFactory = null;
            //查找对应的AdLoaderFactory
            if (Constants.AD_TYPE_FB.equals(source.getAdType())) {
                adLoaderFactory = fbAdLoaderFactory;
            } else if (Constants.AD_TYPE_GG.equals(source.getAdType())) {
                adLoaderFactory = ggAdLoaderFactory;
            }
            //如果找到了对应的loaderFactory
            if (adLoaderFactory != null) {
                adLoaderFactory.getSingleAdLoader(adConfig, source, new SingleAdLoaderListener() {
                    @Override
                    public void onAdLoaded(AdContent ad) {
                        if (ad.getAdType() == AdContent.FB_AD) {
                            GlobalConfig.getInstance().setGlobalAdContent(ad);
                            GlobalConfig.getInstance().setGlobalSaveTime(System.currentTimeMillis());//保存当前插入广告的时间戳。
                            Log.d(TAG, "onAdLoaded: " + "cache fb global ad success.");

                        } else {
                            //TODO 确定了fb广告为null。
                            if (isFBEmpty) {
                                GlobalConfig.getInstance().setGlobalAdContent(ad);
                                GlobalConfig.getInstance().setGlobalSaveTime(System.currentTimeMillis());
                                Log.d(TAG, "onAdLoaded: " + "cache gg global ad success.");
                            } else {
                                if (GlobalConfig.getInstance().getGlobalAdContent() == null) {
                                    GlobalConfig.getInstance().setGlobalAdContent(ad);
                                    GlobalConfig.getInstance().setGlobalSaveTime(System.currentTimeMillis());
                                    Log.d(TAG, "onAdLoaded: " + "cache gg global ad success.");
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(AdError error) {
                        if (error.getType() == AdContent.FB_AD) {
                            isFBEmpty = true;
                            Log.d(TAG, "onAdLoaded: " + "cache fb global ad fail.");
                        }
                        if (error.getType() == AdContent.GG_AD) {
                            Log.d(TAG, "onAdLoaded: " + "cache gg global ad fail.");
                        }
                    }
                }).loadAd();
            }
        }
    }
}
