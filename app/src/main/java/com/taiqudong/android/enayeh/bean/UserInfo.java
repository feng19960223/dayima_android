package com.taiqudong.android.enayeh.bean;

/**
 * Created by zhangxiang on 2017/7/14.
 */

import java.io.Serializable;

/**
 *
 {
 "name": "kipw74249",
 "avatar": null,
 "intro": null,
 "uid": "ae00d4f0b3d343c97bea733adda9a643"
 }
 ***/
public class UserInfo implements Serializable{

    String name;
    String avatar;
    String intro;
    String uid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
