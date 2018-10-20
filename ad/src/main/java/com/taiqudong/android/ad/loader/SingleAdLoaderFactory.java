package com.taiqudong.android.ad.loader;

import com.taiqudong.android.ad.AdConfig;

/**
 * Created by zhangxiang on 2017/9/5.
 */

public abstract class SingleAdLoaderFactory {


    public abstract SingleAdLoader getSingleAdLoader(AdConfig config, AdConfig.Source source, SingleAdLoaderListener listener);

}
