package com.taiqudong.android.ad.loader;

import com.taiqudong.android.ad.AdConfig;

/**
 * 这里实现每种广告的加载
 * Created by zhangxiang on 2017/9/5.
 */
public interface SingleAdLoader {

    void setSingleAdLoaderListener(SingleAdLoaderListener listener);

    void setAdConfig(AdConfig config, AdConfig.Source source);

    void loadAd();
}
