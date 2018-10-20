package com.taiqudong.android.ad;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * TODO 广告全屏页的展示逻辑
 * txm 20170907
 */
@SuppressWarnings({"unchecked"})
public class AdSplashView extends AppCompatActivity {
    private static final String PUSH = "push";
    private Bundle bundle = null;

    private static final String PLACE_ID = "place_id";
    private static final String ACTION = "action";
    private static final String NEXT_ACTIVITY = "next_activity";
    private static final String TAG = "AdSplashView";
    public static String SP_TIMER = "sp_time";//当前时间
    //拿到上一个界面的context.
    static Context preContext;

    Handler handler = null;

    private Runnable timeReachRunnable;

    public interface Action {
        int FROM_LOGIN_TO_MAIN = 0;
        int FROM_APP_TO_MAIN = 1;
    }

    public static void start(Context context, String placeId, int action, Class<? extends AppCompatActivity> nextActivity,Bundle bundle) {
        preContext = context;
        Intent starter = new Intent(context, AdSplashView.class);
        starter.putExtra(PLACE_ID, placeId);
        starter.putExtra(ACTION, action);
        starter.putExtra(NEXT_ACTIVITY, nextActivity);
        starter.putExtra(PUSH, bundle);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_splash_ad);
        if (getIntent().getExtras() != null) {
            bundle = (Bundle) getIntent().getExtras().get(PUSH);
        }
        final Class<? extends AppCompatActivity> target = (Class<? extends AppCompatActivity>) getIntent().getSerializableExtra(NEXT_ACTIVITY);
        handler = new Handler();
        timeReachRunnable = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "step to main view.");
                Intent intent = new Intent(AdSplashView.this, target);
                intent.putExtra(PUSH,bundle);
                startActivity(intent);
                finish();
            }
        };
        String placeId = getIntent().getStringExtra(PLACE_ID);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        FrameLayout adContainerFl = (FrameLayout) findViewById(R.id.fl_ad_container);
        TNativeAd nativeAd = AdManager.getNativeAd4Splash(placeId, adContainerFl);
        final int action = getIntent().getIntExtra(ACTION, 1);

        //TODO 从详情页返回主页面，并且时间没有到达4小时
        if (action == Action.FROM_APP_TO_MAIN && !isTimeReached()) {
            finish();
            //TODO 销毁上一个界面
            ((Activity) preContext).finish();
        }
        nativeAd.loadSplashAd(new TNativeAd.ViewListener() {
            @Override
            public void onClose() {
                //TODO 判断跳转的逻辑
                Log.d(TAG, "ad close button is clicked");
                if (action == Action.FROM_APP_TO_MAIN) {
                    Log.d(TAG, "FROM_APP_TO_MAIN");
                    //TODO 销毁当前界面
                    finish();
                    //TODO 销毁上一个界面
                    ((Activity) preContext).finish();
                } else {
                    Log.d(TAG, "FROM_LOGIN_TO_MAIN");
                    Intent intent = new Intent(AdSplashView.this, target);
                    intent.putExtra(PUSH,bundle);
                    startActivity(intent);
                    handler.removeCallbacks(timeReachRunnable);
                    finish();
                }
            }

            @Override
            public void loaded() {
                if (action == Action.FROM_LOGIN_TO_MAIN) {
                    Log.d(TAG, "load view finish.");
                    //TODO 加载完数据到视图
                    handler.postDelayed(timeReachRunnable, 3000);
                }
            }

            @Override
            public void error(AdError error) {
                if (action == Action.FROM_APP_TO_MAIN) {
                    Log.d(TAG, "FROM_APP_TO_MAIN");
                    //TODO 销毁当前界面
                    finish();
                    //TODO 销毁上一个界面
                    ((Activity) preContext).finish();
                } else {
                    Log.d(TAG, "FROM_LOGIN_TO_MAIN");
                    Intent intent = new Intent(AdSplashView.this, target);
                    intent.putExtra(PUSH,bundle);
                    startActivity(intent);
                    handler.removeCallbacks(timeReachRunnable);
                    finish();
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        //不做任何处理，方式按返回键退出
        Log.d(TAG, "back button is clicked,but we don't deal with it");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        preContext = null;
        Log.d(TAG, "onDestroy");
    }

    /**
     * TODO 是否时间到达,展示广告
     */
    boolean isTimeReached() {
        long startTime = (long) SPUtil.get(this, SP_TIMER, 0L);
        long duration = System.currentTimeMillis() - startTime;
        Log.d(TAG, "current interval is:" + duration);
        //根据服务端的配置设置闪屏的间隔时长。
        long interval = GlobalConfig.getInstance().getBackhomeAdInterval();
        Log.d(TAG, "interval on server is:" + interval);
        if (duration >= interval) {
            SPUtil.put(this, SP_TIMER, System.currentTimeMillis());
            return true;
        }
        return false;
    }
}
