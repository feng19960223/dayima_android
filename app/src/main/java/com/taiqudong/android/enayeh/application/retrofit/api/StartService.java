package com.taiqudong.android.enayeh.application.retrofit.api;

import com.taiqudong.android.enayeh.application.retrofit.bean.RegisterAnon;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by tangxianming on 2017/8/23.
 */

public interface StartService {
    @POST("api/user/register.action")
    Call<RegisterAnon> register(@Body HashMap<String, String> data);

    @POST("api/user/register_anon.action")
    Call<RegisterAnon> registerAnon(@Body HashMap<String, String> data);

    @POST("api/user/login_by_fb.action")
    Call<RegisterAnon> loginByFb(@Body HashMap<String, String> data);

    @POST("api/user/login_by_gg.action")
    Call<RegisterAnon> loginByGg(@Body HashMap<String, String> data);

    @POST("api/user/login.action")
    Call<RegisterAnon> login(@Body HashMap<String, String> data);

    @POST("api/user/register_by_mobile.action")
    Call<RegisterAnon> registerByMobile(@Body HashMap<String, String> data);

    @POST("api/user/register_vcode.action")
    Call<RegisterAnon> registerVcode(@Body HashMap<String, String> data);
}
