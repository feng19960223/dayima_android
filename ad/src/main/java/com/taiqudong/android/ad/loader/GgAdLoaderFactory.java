package com.taiqudong.android.ad.loader;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.taiqudong.android.ad.AdConfig;
import com.taiqudong.android.ad.AdContent;
import com.taiqudong.android.ad.AdError;
import com.taiqudong.android.ad.Constants;
import com.taiqudong.android.ad.GlobalConfig;
import com.taiqudong.android.ad.SPUtil;
import com.taiqudong.android.ad.stats.EventConsts;
import com.taiqudong.android.ad.stats.EventLogger;

import java.util.Locale;

/**
 * Google 广告加载代码写着这里
 * Created by zhangxiang on 2017/9/5.
 */
public class GgAdLoaderFactory extends SingleAdLoaderFactory {
    private static final String TAG = "GgAdLoaderFactory";

    @Override
    public SingleAdLoader getSingleAdLoader(AdConfig config, AdConfig.Source source, final SingleAdLoaderListener listener) {
        GGSingleAdLoader ggSingleAdLoader = new GGSingleAdLoader();
        ggSingleAdLoader.setAdConfig(config, source);
        ggSingleAdLoader.setSingleAdLoaderListener(listener);
        return ggSingleAdLoader;
    }


    public static class GGSingleAdLoader implements SingleAdLoader {
        AdConfig config;
        AdConfig.Source source;
        SingleAdLoaderListener listener;
        public static int status = 0;//判断广告是否请求过的状态值，0表示没有请求，1表示请求过。

        public GGSingleAdLoader() {

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
            Log.i(TAG, "loadAd: ");
            //请求谷歌广告
            Bundle bundle = new Bundle();
            bundle.putString(EventConsts.AD_ACTION, EventConsts.AD_REQUEST);
            bundle.putString(EventConsts.AD_SDK, EventConsts.GG);
            String ctg = (String) SPUtil.get(GlobalConfig.getInstance().getContext(), Constants.CTG, "");
            if (!ctg.equals("")) {
                bundle.putString(EventConsts.CTG, ctg);
            }
            EventLogger.logEvent(config.getPlaceId(), bundle);
            /**
             * TODO
             * 谷歌广告加载的代码
             * txm 20170905
             */
            String ADMOB_AD_UNIT_ID = source.getAdKey();
            AdLoader.Builder builder = new AdLoader.Builder(GlobalConfig.getInstance().getContext(), ADMOB_AD_UNIT_ID);
            builder.forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
                @Override
                public void onContentAdLoaded(NativeContentAd nativeContentAd) {
                    status = 1;
                    AdContent adContent = new AdContent();
                    adContent.setGgContentAd(nativeContentAd);
                    Log.d(TAG, "onContentAdLoaded: " + nativeContentAd.getHeadline());
                    adContent.setAdType(AdContent.GG_CONTENT_AD);
                    listener.onAdLoaded(adContent);
                }
            });
            builder.forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
                @Override
                public void onAppInstallAdLoaded(NativeAppInstallAd nativeAppInstallAd) {
                    status = 1;
                    AdContent adContent = new AdContent();
                    adContent.setGgAppInstallAd(nativeAppInstallAd);
                    adContent.setAdType(AdContent.GG_APPINSTALL_AD);
                    listener.onAdLoaded(adContent);
                }
            });
            VideoOptions videoOptions = new VideoOptions.Builder()
                    .setStartMuted(true)
                    .build();

            //gg广告，阿拉伯放到左面，英语放到右面
            String language = Locale.getDefault().getLanguage();
            NativeAdOptions.Builder optionsBuilder = new NativeAdOptions.Builder()
                    .setVideoOptions(videoOptions);
            if (language.equals("ar")) {
                optionsBuilder.setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_LEFT);
            } else {
                optionsBuilder.setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT);
            }
            builder.withNativeAdOptions(optionsBuilder.build());

            final AdLoader adLoader = builder.withAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(int errorCode) {
                    status = 1;
                    AdError adError = new AdError();
                    adError.setMessage(AdError.GG_CODE_PREFIX + errorCode);
                    adError.setType(AdContent.GG_AD);
                    listener.onError(adError);
                    Log.d(TAG, "onAdFailedToLoad: " + errorCode);
                    //请求广告失败
                    Bundle bundle = new Bundle();
                    bundle.putString(EventConsts.AD_ACTION, EventConsts.AD_REQUEST_FAILED);
                    bundle.putString(EventConsts.AD_SDK, EventConsts.GG);
                    String ctg = (String) SPUtil.get(GlobalConfig.getInstance().getContext(), Constants.CTG, "");
                    if (!ctg.equals("")) {
                        bundle.putString(EventConsts.CTG, ctg);
                    }
                    EventLogger.logEvent(config.getPlaceId(), bundle);
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                    //广告展示成功
                    Bundle bundle = new Bundle();
                    bundle.putString(EventConsts.AD_ACTION, EventConsts.AD_IMP);
                    bundle.putString(EventConsts.AD_SDK, EventConsts.GG);
                    String ctg = (String) SPUtil.get(GlobalConfig.getInstance().getContext(), Constants.CTG, "");
                    if (!ctg.equals("")) {
                        bundle.putString(EventConsts.CTG, ctg);
                    }
                    EventLogger.logEvent(config.getPlaceId(), bundle);
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    //广告被点击
                    Bundle bundle = new Bundle();
                    bundle.putString(EventConsts.AD_ACTION, EventConsts.AD_CLICK);
                    bundle.putString(EventConsts.AD_SDK, EventConsts.GG);
                    String ctg = (String) SPUtil.get(GlobalConfig.getInstance().getContext(), Constants.CTG, "");
                    if (!ctg.equals("")) {
                        bundle.putString(EventConsts.CTG, ctg);
                    }
                    EventLogger.logEvent(config.getPlaceId(), bundle);
                }
            }).build();
            adLoader.loadAd(new AdRequest.Builder()
//                    .addTestDevice("990DF97ECC571321AB91A19B80D3C003")
//                    .addTestDevice("D1A949AD1637AA35AC769EC20CC230DC")
//                    .addTestDevice("224D723F508C8DBB10365773E83F6A70")
//                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build());
        }
    }
}
