package com.taiqudong.android.enayeh.bean;

/**
 * Created by zhangxiang on 2017/7/14.
 */

/***
 * Sample Resp
 *
 {
     "code": 0,
     "data": {
         "token": "e534f6b82fb7231dc289f10e0e1c8f36",
         "user": {
                 "name": "kipw74249",
                 "avatar": null,
                 "intro": null,
                 "uid": "ae00d4f0b3d343c97bea733adda9a643"
             }
         },
     "desc": "success"
 }
 *****/
public class LoginResp extends BaseResp<LoginResp.Data> {


    public static class Data {
        String token;
        UserInfo user;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public UserInfo getUser() {
            return user;
        }

        public void setUser(UserInfo user) {
            this.user = user;
        }
    }
}
