package com.taiqudong.android.ad.loader;

import android.os.Bundle;

import com.facebook.ads.Ad;
import com.facebook.ads.AdSettings;
import com.facebook.ads.NativeAd;
import com.taiqudong.android.ad.AdConfig;
import com.taiqudong.android.ad.AdContent;
import com.taiqudong.android.ad.AdError;
import com.taiqudong.android.ad.Constants;
import com.taiqudong.android.ad.GlobalConfig;
import com.taiqudong.android.ad.SPUtil;
import com.taiqudong.android.ad.stats.EventConsts;
import com.taiqudong.android.ad.stats.EventLogger;

/**
 * Created by zhangxiang on 2017/9/5.
 */

/***
 * TODO
 * xxx 有问题，与xxx冲突
 * 张翔 20170905
 */
public class FbAdLoaderFactory extends SingleAdLoaderFactory {
    private static final String TAG = "FbAdLoaderFactory";

    @Override
    public SingleAdLoader getSingleAdLoader(AdConfig config, AdConfig.Source source, SingleAdLoaderListener listener) {
        FBSingleAdLoader fbSingleAdLoader = new FBSingleAdLoader();
        fbSingleAdLoader.setSingleAdLoaderListener(listener);
        fbSingleAdLoader.setAdConfig(config, source);
        return fbSingleAdLoader;
    }

    public static class FBSingleAdLoader implements SingleAdLoader {
        AdConfig config;
        AdConfig.Source source;
        SingleAdLoaderListener listener;
        public static int status = 0;//判断广告是否请求过的状态值，0表示没有请求，1表示请求。

        public FBSingleAdLoader() {

        }


        @Override
        public void setSingleAdLoaderListener(SingleAdLoaderListener listener) {
            this.listener = listener;
        }

        @Override
        public void setAdConfig(AdConfig config, AdConfig.Source source) {
            this.config = config;
            this.source = source;
        }

        @Override
        public void loadAd() {
            status = 0;
            //开始请求FB广告
            Bundle bundle = new Bundle();
            bundle.putString(EventConsts.AD_ACTION, EventConsts.AD_REQUEST);
            bundle.putString(EventConsts.AD_SDK, EventConsts.FB);
            String ctg = (String) SPUtil.get(GlobalConfig.getInstance().getContext(), Constants.CTG, "");
            if (!ctg.equals("")) {
                bundle.putString(EventConsts.CTG, ctg);
            }
            EventLogger.logEvent(config.getPlaceId(), bundle);
            /**
             * TODO
             * facebook广告加载的代码
             * txm 20170905
             */
            String FB_AD_UNIT_ID = source.getAdKey();
            final NativeAd nativeAd = new NativeAd(GlobalConfig.getInstance().getContext(), FB_AD_UNIT_ID);
            nativeAd.setAdListener(new com.facebook.ads.AdListener() {
                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    status = 1;
                    AdError error = new AdError();
                    error.setMessage(AdError.FB_CODE_PREFIX + adError.getErrorCode());
                    error.setType(AdContent.FB_AD);
                    listener.onError(error);
                    //请求广告失败
                    Bundle bundle = new Bundle();
                    bundle.putString(EventConsts.AD_ACTION, EventConsts.AD_REQUEST_FAILED);
                    bundle.putString(EventConsts.AD_SDK, EventConsts.FB);
                    String ctg = (String) SPUtil.get(GlobalConfig.getInstance().getContext(), Constants.CTG, "");
                    if (!ctg.equals("")) {
                        bundle.putString(EventConsts.CTG, ctg);
                    }
                    EventLogger.logEvent(config.getPlaceId(), bundle);
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    status = 1;
                    if (nativeAd != null) {
                        nativeAd.unregisterView();
                    }
                    AdContent adContent = new AdContent();
                    adContent.setFbAd(nativeAd);
                    adContent.setAdType(AdContent.FB_AD);
                    listener.onAdLoaded(adContent);
                }

                @Override
                public void onAdClicked(Ad ad) {
                    //点击广告
                    Bundle bundle = new Bundle();
                    bundle.putString(EventConsts.AD_ACTION, EventConsts.AD_CLICK);
                    bundle.putString(EventConsts.AD_SDK, EventConsts.FB);
                    String ctg = (String) SPUtil.get(GlobalConfig.getInstance().getContext(), Constants.CTG, "");
                    if (!ctg.equals("")) {
                        bundle.putString(EventConsts.CTG, ctg);
                    }
                    EventLogger.logEvent(config.getPlaceId(), bundle);
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    //广告被展示
                    Bundle bundle = new Bundle();
                    bundle.putString(EventConsts.AD_ACTION, EventConsts.AD_IMP);
                    bundle.putString(EventConsts.AD_SDK, EventConsts.FB);
                    String ctg = (String) SPUtil.get(GlobalConfig.getInstance().getContext(), Constants.CTG, "");
                    if (!ctg.equals("")) {
                        bundle.putString(EventConsts.CTG, ctg);
                    }
                    EventLogger.logEvent(config.getPlaceId(), bundle);
                }
            });
            //Set test ad.
//            AdSettings.addTestDevice("6b6e729e75521b83cd97dcb93b846153");
//            AdSettings.addTestDevice("52326667fc6a1550f7e80598aed27686");
//            AdSettings.addTestDevice("72b23c4df8991f9f59df8f4cb88ee367");
//            AdSettings.addTestDevice("78f1b2f38155e07e81dc47458c158d1b");
            nativeAd.loadAd();
        }
    }
}
