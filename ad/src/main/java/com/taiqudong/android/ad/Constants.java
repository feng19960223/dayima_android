package com.taiqudong.android.ad;

/**
 * 公共的变量，以及控制
 * Created by zhangxiang on 2017/9/5.
 */
public interface Constants {

    //是否开启 DEBUG 模式
    boolean DEBUG = true;

    String AD_TYPE_GG = "gg";
    String AD_TYPE_FB = "fb";

    // TODO 本地的广告位，确保和服务端同步
    String AD_SPLASH_LOGIN = "Splash";
    String AD_SPLASH_BACKHOME = "Backhome";


    //TODO 详情页的ctg
    String CTG = "ctg";

    public static String SP_AD_SPLASH_CONFIG = "sp_ad_splash_config";//广告配置，这里只针对插屏广告。
}
