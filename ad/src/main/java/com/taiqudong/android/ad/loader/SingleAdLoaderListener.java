package com.taiqudong.android.ad.loader;

import com.taiqudong.android.ad.AdContent;
import com.taiqudong.android.ad.AdError;

/**
 * Created by zhangxiang on 2017/9/5.
 */

public interface SingleAdLoaderListener {

    /**
     * 广告成功后调用
     * @param ad
     */
    void onAdLoaded(AdContent ad);

    /**
     * 广告失败后调用
     * @param error
     */
    void onError(AdError error);
}
