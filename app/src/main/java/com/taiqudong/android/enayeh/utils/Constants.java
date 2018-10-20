package com.taiqudong.android.enayeh.utils;

/**
 * Created by tangxianming on 2017/8/24.
 */

public class Constants {
    public Constants() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static String SP_TOKEN = "sp_token";//保存token
    public static String SP_REGISTER_ANNO = "sp_register_anno";//保存是否匿名注册
    public static String HEAD_CONTENT_TYPE = "head_content_type";
    public static String HEAD_DEVICE_ID = "head_device_id";
    public static int USER_DUPLICATE = -100004;
    public static int REQUEST_SUCCESS = 0;
    public static String ARTICLE_TABS_BEAN = "ArticleTabsBean";
    public static String VIDEO_TABS_BEAN = "VideoTabsBean";
    public static String SP_USER = "sp_user";//保存user
    public static String SP_LOGIN_TYPE = "sp_login_type";//登录类型
    public static String SP_TIMER = "sp_time";//当前时间
    public static String SP_NEED_REFRESH_MAINDATA = "sp_need_refresh_maindata";//是否需要重新加载主页数据

    public interface LOGIN_TYPE {
        int NON_USER = 0;
        int REAL_USER = 1;
    }

}
