package com.taiqudong.android.ad;

/**
 * Created by zhangxiang on 2017/9/5.
 */

public class AdError extends Error {
    /**
     * TODO
     * 这里新增code的逻辑
     */
    String msg = null;

    public final static String GG_CODE_PREFIX = "gg";
    public final static String FB_CODE_PREFIX = "fb";

    //错误的广告类型
    int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setMessage(String msg) {
        this.msg = msg;
    }

    public String getMessage() {
        return msg + "";
    }

    @Override
    public String toString() {
        return String.format("msg=%s", msg);
    }

}
