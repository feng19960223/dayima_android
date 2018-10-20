package com.taiqudong.android.ad;

import com.facebook.ads.Ad;
import com.facebook.ads.NativeAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeContentAd;

import java.io.Serializable;

/**
 * 广告的基本数据类
 * Created by zhangxiang on 2017/9/4.
 */

public class AdContent {

    public final static int GG_CONTENT_AD = 1;
    public final static int GG_APPINSTALL_AD = 2;
    public final static int FB_AD = 3;
    public final static int NO_AD = -1;
    public final static int GG_AD = 4;


    private NativeContentAd ggContentAd; //google的内容广告
    private NativeAppInstallAd ggAppInstallAd; //google的安装广告
    private NativeAd fbAd; //
    private int adType;


    public NativeContentAd getGgContentAd() {
        return ggContentAd;
    }

    public void setGgContentAd(NativeContentAd ggContentAd) {
        this.ggContentAd = ggContentAd;
    }

    public NativeAppInstallAd getGgAppInstallAd() {
        return ggAppInstallAd;
    }

    public void setGgAppInstallAd(NativeAppInstallAd ggAppInstallAd) {
        this.ggAppInstallAd = ggAppInstallAd;
    }

    public NativeAd getFbAd() {
        return fbAd;
    }

    public void setFbAd(NativeAd fbAd) {
        this.fbAd = fbAd;
    }

    public int getAdType() {
        return adType;
    }

    public void setAdType(int adType) {
        this.adType = adType;
    }

    /**
     * 广告类型验证
     *
     * @param sAdType "gg", "fb" 等广告类型
     * @return
     */
    public boolean isEqualAdType(String sAdType) {
        if (Constants.AD_TYPE_GG.equals(sAdType)) {
            return adType == GG_APPINSTALL_AD || adType == GG_CONTENT_AD;
        } else if (Constants.AD_TYPE_FB.equals(sAdType)) {
            return adType == FB_AD;
        } else {
            return false;
        }
    }
}
