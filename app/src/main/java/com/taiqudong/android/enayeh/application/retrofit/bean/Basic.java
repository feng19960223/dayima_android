package com.taiqudong.android.enayeh.application.retrofit.bean;

/**
 * Created by tangxianming on 2017/8/24.
 */

public class Basic {
    private int code;
    private String desc;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "Basic{" +
                "code=" + code +
                ", desc='" + desc + '\'' +
                '}';
    }
}
