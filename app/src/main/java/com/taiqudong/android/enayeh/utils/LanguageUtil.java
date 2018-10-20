package com.taiqudong.android.enayeh.utils;

import java.util.Locale;

/**
 * Created by taiqudong on 2017/8/16.
 */

public class LanguageUtil {
    public static  boolean isALB(){//如果是阿拉伯语言，返回true
        String language = Locale.getDefault().getLanguage();
//        if (language.equals("ar") | language.equals("fa")){
        if (language.equals("ar")){
            return true;
        }
        return false;
    }
}
