package com.taiqudong.android.enayeh.bean;

/**
 * Created by zhangxiang on 2017/7/14.
 */

abstract class BaseResp<T> {

    protected int code;



    protected T data;
    protected String desc;

    public boolean isOk(){
        return code == 0;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
