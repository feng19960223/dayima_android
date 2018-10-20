package com.taiqudong.android.enayeh.application.retrofit;

import android.content.Context;
import android.util.Log;


import com.taiqudong.android.ad.SPUtil;
import com.taiqudong.android.enayeh.BuildConfig;
import com.taiqudong.android.enayeh.application.AppLogic;
import com.taiqudong.android.enayeh.utils.Constants;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by tangxianming on 2017/8/23.
 */

public class ServiceGenerator {
    public static final String API_BASE_URL = BuildConfig.API_URL;

    public static <S> S createServcieWithoutAccess(Class<S> serviceClass, final Context context) {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .header("device-id", AppLogic.getInstance().getDeviceId())
                        .header("appLanguages", Locale.getDefault().getLanguage())
                        .method(original.method(), original.body());
                Request request = requestBuilder.build();
                Log.d("ServiceGenerator", "intercept: " + AppLogic.getInstance().getDeviceId());
                Log.d("ServiceGenerator", "intercept: " + AppLogic.getInstance().getDeviceId());
                return chain.proceed(request);
            }
        });
        OkHttpClient client = clientBuilder.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        return retrofit.create(serviceClass);
    }

    public static <S> S createServcie(final Context context, Class<S> serviceClass) {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                //获取保存在本地的token.
                String token = (String) SPUtil.get(context, Constants.SP_TOKEN, "");
                Log.d("onLoginSuccess", "intercept1: " + token);
                Log.d("onLoginSuccess", "intercept2: " + AppLogic.getInstance().getDeviceId());
                Log.d("onLoginSuccess", chain.request().url().toString());
                if (token.equals("")) {
                    //TODO 这里需要获取token
                    throw new UnsupportedEncodingException("Your auth token is null.");
                }
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .header("device-id", AppLogic.getInstance().getDeviceId())
                        .header("appLanguages", Locale.getDefault().getLanguage())
                        .header("token", token)
                        .method(original.method(), original.body());
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });
        OkHttpClient client = clientBuilder.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        return retrofit.create(serviceClass);
    }
}
