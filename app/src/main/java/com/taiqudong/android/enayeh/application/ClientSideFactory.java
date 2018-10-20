package com.taiqudong.android.enayeh.application;

import android.util.Log;

import com.taiqudong.android.enayeh.BuildConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by zhangxiang on 2017/7/13.
 * 使用新的网络API：ServiceGnerator
 *
 * @deprecated
 */
public class ClientSideFactory {

    public static final String GOOGLE_YOUTUBE_KEY = "AIzaSyDtK_QTqs40nG1xgQScnRmI4jTyrB8HHRg";
    private final static String BASE_URL = BuildConfig.API_URL;

    //NOT USED
    private static final String REGISTER = "api/user/register.action";
    private static final String LOGIN_BY_FB = "api/user/login_by_fb.action";
    private static final String LOGIN_BY_GG = "api/user/login_by_gg.action";
    private static final String FEED_LIST = "api/article/index_list.action";
    public static final String FEED_URL = BASE_URL + "article/get.html?article_id=";
    public static final String ABOUT_US_URL = BASE_URL + "about_us.html";
    public static final String TERMS_URL = BASE_URL + "terms.html";
    public static final String PRIVACY_POLICY_URL = BASE_URL + "privacy_policy.html";
    public static final String EMAIL_RUL = "feedback@taiqudong.com";
    private static final String YES = "yes";
    private static final String NO = "no";

    private static final String TAG = "ClientSideFactory";

    private static String HEADER_DEVICE_ID = "device-id";
    private static Map<String, Boolean> sAuthMap = null;

    private static ClientSide sClientSide = null;


    private static void buidAuthMap() {

        String[][] isNeedAuth = {
                {REGISTER, NO},
                {LOGIN_BY_FB, NO},
                {LOGIN_BY_GG, NO},

        };

        sAuthMap = new HashMap<>();
        for (int i = 0; i < isNeedAuth.length; ++i) {
            String[] row = isNeedAuth[i];
            sAuthMap.put(row[0], row[1].equals(YES));
        }
    }


    public static ClientSide getClient() {

        if (sAuthMap == null) {
            buidAuthMap();
        }
        if (sClientSide == null) {
            sClientSide = new ClientSide();
        }
        return sClientSide;
    }

    public static class ClientSide {

        private int mTimeoutSecs = 10;

        private OkHttpClient mClient;

        public ClientSide() {

            mClient = buildClient();


        }

        private Request buildPost(String api, RequestBody body) {
            Request.Builder builder = (new Request.Builder())
                    .addHeader(HEADER_DEVICE_ID, AppLogic.getInstance().getDeviceId())
                    .url(BASE_URL + api)
                    .post(body);

            return builder.build();
        }

        //使用facebook登录
        public void loginByFB(String accessToken, Callback callback) {

            FormBody.Builder builder = new FormBody.Builder();
            builder.add("access_token", accessToken);

            Request request = buildPost(LOGIN_BY_FB, builder.build());
            mClient.newCall(request).enqueue(callback);
        }

        //使用google登录
        public void loginByGG(String authCode, Callback callback) {
            FormBody.Builder builder = new FormBody.Builder();
            builder.add("auth_code", authCode);

            Request request = buildPost(LOGIN_BY_GG, builder.build());
            mClient.newCall(request).enqueue(callback);
        }

        private OkHttpClient buildClient() {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            if (BuildConfig.DEBUG) {
                builder.addInterceptor(new LoggingInterceptor());
            }
            return builder.connectTimeout(mTimeoutSecs, TimeUnit.SECONDS)
                    .readTimeout(mTimeoutSecs, TimeUnit.SECONDS)
                    .writeTimeout(mTimeoutSecs, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(false)
                    .build();
        }

        // 加载feed列表
        public void feedList(String lastId, Callback callback) {

            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("lastid", lastId);

            Request request = buildGet(FEED_LIST, queryMap);

            mClient.newCall(request).enqueue(callback);
        }

        private Request buildGet(String api, Map<String, String> queryMap) {

            HttpUrl url = HttpUrl.parse(BASE_URL + api);
            HttpUrl.Builder urlBuilder = url.newBuilder();
            for (Map.Entry<String, String> entry : queryMap.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
            Request.Builder builder = (new Request.Builder())
                    .addHeader(HEADER_DEVICE_ID, AppLogic.getInstance().getDeviceId())
                    .url(urlBuilder.build());
            return builder.build();
        }
    }
}

class LoggingInterceptor implements Interceptor {
    private static final String TAG = "OkHTTP";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        long t1 = System.nanoTime();
        Log.d(TAG, String.format("Sending request %s on %s%n%s",
                request.url(), chain.connection(), request.headers()));

        Response response = chain.proceed(request);

        long t2 = System.nanoTime();
        Log.d(TAG, String.format("Received response for %s in %.1fms%n%s",
                response.request().url(), (t2 - t1) / 1e6d, response.headers()));

        return response;
    }
}
