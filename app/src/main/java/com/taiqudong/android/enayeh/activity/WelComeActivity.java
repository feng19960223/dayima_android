package com.taiqudong.android.enayeh.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.taiqudong.android.ad.AdConfig;
import com.taiqudong.android.ad.AdParallelLoader;
import com.taiqudong.android.ad.AdSplashView;
import com.taiqudong.android.ad.GlobalConfig;
import com.taiqudong.android.ad.SPUtil;
import com.taiqudong.android.enayeh.R;
import com.taiqudong.android.enayeh.application.AppLogic;
import com.taiqudong.android.enayeh.application.SysApplication;
import com.taiqudong.android.enayeh.application.retrofit.ServiceGenerator;
import com.taiqudong.android.enayeh.application.retrofit.api.ApiService;
import com.taiqudong.android.enayeh.application.retrofit.api.StartService;
import com.taiqudong.android.enayeh.application.retrofit.bean.MenstrualLogs;
import com.taiqudong.android.enayeh.application.retrofit.bean.MenstrualUserInfo;
import com.taiqudong.android.enayeh.application.retrofit.bean.RegisterAnon;
import com.taiqudong.android.enayeh.bean.UserInfo;
import com.taiqudong.android.enayeh.firebase.MyFirebaseMessagingService;
import com.taiqudong.android.enayeh.utils.ApiUtil;
import com.taiqudong.android.enayeh.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WelComeActivity extends AppCompatActivity {
    private static final String TAG = "WelComeActivity";
    private Bundle bundle = null;

    @Override
    protected void onStart() {
        super.onStart();
    }

    int mLogoutSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        if (getIntent().getExtras() != null) {
            bundle = (Bundle) getIntent().getExtras().get(MyFirebaseMessagingService.NOTIFICATION_PUSH);
        }
        SPUtil.put(this, Constants.SP_NEED_REFRESH_MAINDATA, false);
        SysApplication.getInstance().addActivit(this);
        Log.d(TAG, "app is started");
        initConfig();
        //TODO 这里为了保证config的数据优先获取，不做成同步的原因是担心广告的配置数据获取时间过程长。
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                registerAnon();
            }
        }, 0);
    }

    /**
     * 匿名注册+登录逻辑(这里包含了gg/fb登录以后的操作。)
     */
    void registerAnon() {
        StartService startService = ServiceGenerator.createServcieWithoutAccess(StartService.class, this);
        String deviceID = AppLogic.getInstance().getDeviceId();
        HashMap<String, String> map = new HashMap<>();
        map.put("did", deviceID);
        startService.registerAnon(map).enqueue(new Callback<RegisterAnon>() {
            @Override
            public void onResponse(Call<RegisterAnon> call, Response<RegisterAnon> response) {
                RegisterAnon data = response.body();
                if (data == null) {
                    return;
                }
                if (data.getCode() == Constants.REQUEST_SUCCESS) {
                    mLogoutSteps++;
                    int loginType = (int) SPUtil.get(WelComeActivity.this, Constants.SP_LOGIN_TYPE, Constants.LOGIN_TYPE.NON_USER);
                    if (loginType == Constants.LOGIN_TYPE.NON_USER) {
                        UserInfo userInfo = new UserInfo();
                        userInfo.setAvatar(data.getData().getUser().getAvatar());
                        userInfo.setIntro(data.getData().getUser().getIntro());
                        userInfo.setName(data.getData().getUser().getName());
                        userInfo.setUid(data.getData().getUser().getUid());
                        AppLogic.getInstance().setUserInfo(userInfo);
                        //当得知不是通过google/fb登录时，保持匿名注册的token。
                        AppLogic.getInstance().setToken(data.getData().getToken());
                        SPUtil.put(WelComeActivity.this, Constants.SP_TOKEN, data.getData().getToken());
                        Log.d(TAG, "onResponse: loginType is NON_USER.");
                    }
                    //TODO 第一次进入应用时，记录当时的时间。
                    if (AppLogic.getInstance().getPrefs(WelComeActivity.this).getBoolean("isInitFist", true)) {
                        SPUtil.put(WelComeActivity.this, Constants.SP_TIMER, System.currentTimeMillis());
                    }

                    if (AppLogic.getInstance().getPrefs(WelComeActivity.this).getBoolean("isInitFist", true)) {//第一次打开应用，初始化数据库
                        AppLogic.getInstance().cleanInit(WelComeActivity.this);
                        AppLogic.getInstance().getPrefs(WelComeActivity.this).edit().putBoolean("isInitFist", false).commit();
                        //获取用户信息
                        ApiUtil.userinfoGet(WelComeActivity.this, new ApiUtil.ApiCallback<MenstrualUserInfo>() {
                            @Override
                            public void run(boolean isSuccess, MenstrualUserInfo resp) {
                                mLogoutSteps++;
                                loginToMain();
                            }
                        });
                        //获取经期数据
                        ApiUtil.menstrualLogGet(WelComeActivity.this, new ApiUtil.ApiCallback<MenstrualLogs>() {
                            @Override
                            public void run(boolean isSuccess, MenstrualLogs resp) {
                                mLogoutSteps++;
                                loginToMain();
                            }
                        });
                    }
                    //下次进去应用之后，也就说第一次不上传数据，因为这些数据在本地也不会有，是垃圾数据。
                    else {
                        Log.d(TAG, "register success(not first login)");
                        //经期数据被设置之后
                        if (AppLogic.getInstance().isInitialized()) {
                            //用户信息上传
                            ApiUtil.postUserinfo(WelComeActivity.this);
                            //经期数据上传
                            ApiUtil.postMenstrualLog(WelComeActivity.this);
                            Log.d(TAG, "onResponse: afterInit is true.");
                        }
                        //TODO 弹出插屏广告
                        Map<String, AdConfig> map = (Map<String, AdConfig>) SPUtil.getHashMapData(WelComeActivity.this, com.taiqudong.android.ad.Constants.SP_AD_SPLASH_CONFIG, AdConfig.class);
                        if (map.size() != 0 && map.get(com.taiqudong.android.ad.Constants.AD_SPLASH_LOGIN) != null) {
                            AdSplashView.start(WelComeActivity.this, com.taiqudong.android.ad.Constants.AD_SPLASH_LOGIN, AdSplashView.Action.FROM_LOGIN_TO_MAIN, MainActivity.class, bundle);
                        } else {
                            if (map.size() == 0) {
                                Log.d(TAG, "local config map is null!");
                            }
                            startActivity(MainActivity.newInstance(WelComeActivity.this, bundle));
                            finish();
                        }
                    }
                } else {
                    showExitDialog();
                }
            }

            @Override
            public void onFailure(Call<RegisterAnon> call, Throwable t) {
                Log.d("MainActivity", "onResponse: " + t.getMessage());
                showExitDialog();
            }
        });
    }


    void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WelComeActivity.this);
        builder.setMessage(getString(R.string.network_error)).setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                SysApplication.getInstance().exit(WelComeActivity.this);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 用户数据更新后重载activity
     */
    private void loginToMain() {
        if (mLogoutSteps >= 3) {
            Log.d(TAG, "register success(first login)");
            mLogoutSteps = 0;
            //TODO 弹出插屏广告
            if (GlobalConfig.getInstance().getAllAdConfigs() != null) {
                AdSplashView.start(WelComeActivity.this, com.taiqudong.android.ad.Constants.AD_SPLASH_LOGIN, AdSplashView.Action.FROM_LOGIN_TO_MAIN, MainActivity.class, bundle);
            } else {
                startActivity(MainActivity.newInstance(WelComeActivity.this, bundle));
                finish();
            }
        }
    }


    /**
     * 初始化广告配置数据
     * txm 20170906
     */
    void initConfig() {
        GlobalConfig.getInstance().setContext(getApplicationContext());
        ServiceGenerator.createServcie(this, ApiService.class).adCfg().enqueue(new Callback<com.taiqudong.android.enayeh.application.retrofit.bean.AdConfig>() {
            @Override
            public void onResponse(Call<com.taiqudong.android.enayeh.application.retrofit.bean.AdConfig> call, Response<com.taiqudong.android.enayeh.application.retrofit.bean.AdConfig> response) {
                // TODO: 2017/9/21
                // TODO: 2017/9/21
                // TODO: 2017/9/21
                // TODO: 2017/9/21
                int i = 1;
                if (i == 1) {
                    startActivity(new Intent(WelComeActivity.this, MainActivity.class));
                }
                if (response.isSuccessful()) {
                    Log.d(TAG, "config network is success");
                    com.taiqudong.android.enayeh.application.retrofit.bean.AdConfig adConfig = response.body();
                    com.taiqudong.android.enayeh.application.retrofit.bean.AdConfig.DataBean dataBean = adConfig.getData();
                    //TODO 配置context、请求超时时间、缓存超时时间、闪屏的超时时间、闪屏展示间隔时间
                    if (dataBean.getGlobal() == null) {
                        Log.d(TAG, "global is null!");
                        //TODO 这里实现配置的清空。
                        SPUtil.putHashMapData(WelComeActivity.this, com.taiqudong.android.ad.Constants.SP_AD_SPLASH_CONFIG, new HashMap<String, AdConfig>());
                        return;
                    }
                    GlobalConfig.getInstance().setRequestTimeoutMs(dataBean.getGlobal().getRequestTimeout());
                    GlobalConfig.getInstance().setCacheTimeoutMs(dataBean.getGlobal().getCacheTimeout());
                    GlobalConfig.getInstance().setLaunchAdTimeout(dataBean.getGlobal().getLaunchAdTimeout());
                    GlobalConfig.getInstance().setBackhomeAdInterval(dataBean.getGlobal().getBackhomeAdInterval());
                    //TODO 将广告配置存到map中
                    Map<String, AdConfig> map = new HashMap<>();
                    List<com.taiqudong.android.enayeh.application.retrofit.bean.AdConfig.DataBean.AdsBean> adsBean = dataBean.getAds();
                    for (com.taiqudong.android.enayeh.application.retrofit.bean.AdConfig.DataBean.AdsBean bean : adsBean) {
                        //TODO 配置广告
                        AdConfig config = new AdConfig();
                        config.setStep(0);
                        config.setType(bean.getType());
                        config.setPlaceId(bean.getPlaceId());
                        List<AdConfig.Source> sources = new ArrayList<>();
                        //TODO 配置广告信息。
                        for (com.taiqudong.android.enayeh.application.retrofit.bean.AdConfig.DataBean.AdsBean.SourcesBeanX sourcesBean : bean.getSources()) {
                            AdConfig.Source source = new AdConfig.Source();
                            source.setAdType(sourcesBean.getAdType());
                            source.setAdKey(sourcesBean.getAdKey());
                            sources.add(source);
                        }
                        config.setSources(sources);
                        map.put(bean.getPlaceId(), config);
                    }
                    GlobalConfig.getInstance().setAllAdConfigs(map);
                    //缓存插屏广告配置到本地
                    SPUtil.putHashMapData(WelComeActivity.this, com.taiqudong.android.ad.Constants.SP_AD_SPLASH_CONFIG, map);
                    Log.d(TAG, "cache splash config to local is success!");
                    //TODO 配置全局缓存的配置信息
                    com.taiqudong.android.enayeh.application.retrofit.bean.AdConfig.DataBean.GlobalBean globalBean = dataBean.getGlobal();
                    com.taiqudong.android.enayeh.application.retrofit.bean.AdConfig.DataBean.GlobalBean.GlobalAdBean globalAdBean = globalBean.getGlobalAd();
                    AdConfig adc = new AdConfig();
                    adc.setStep(globalAdBean.getStep());
                    adc.setType(globalAdBean.getType());
                    adc.setPlaceId(globalAdBean.getPlaceId());
                    List<AdConfig.Source> sources = new ArrayList<AdConfig.Source>();
                    for (com.taiqudong.android.enayeh.application.retrofit.bean.AdConfig.DataBean.GlobalBean.GlobalAdBean.SourcesBean s : globalAdBean.getSources()) {
                        AdConfig.Source source = new AdConfig.Source();
                        source.setAdType(s.getAdType());
                        source.setAdKey(s.getAdKey());
                        sources.add(source);
                    }
                    adc.setSources(sources);
                    GlobalConfig.getInstance().setGlobalAdConfig(adc);
                    //TODO 预加载全局缓存备用
                    AdParallelLoader parallelLoader = new AdParallelLoader(adc);
                    parallelLoader.obtainGlobalAdContent();
                    //TODO 预加载广告到缓存
                    AdConfig config = GlobalConfig.getInstance().getAdConfig(com.taiqudong.android.ad.Constants.AD_SPLASH_LOGIN);
                    if (config == null) {
                        return;
                    }
                    Log.d(TAG, "config is not null");
                    AdParallelLoader adParallelLoader = new AdParallelLoader(config);
                    adParallelLoader.prefetch();
                } else {
                    Log.e(TAG, "config network is not normal:" + response.code());

                }
            }

            @Override
            public void onFailure(Call<com.taiqudong.android.enayeh.application.retrofit.bean.AdConfig> call, Throwable t) {
                Log.e(TAG, "config network is not normal:" + t.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SysApplication.getInstance().removeActivity(this);
    }

}
