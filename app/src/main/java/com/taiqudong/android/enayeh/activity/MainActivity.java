package com.taiqudong.android.enayeh.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.taiqudong.android.ad.AdConfig;
import com.taiqudong.android.ad.AdParallelLoader;
import com.taiqudong.android.ad.SPUtil;
import com.taiqudong.android.enayeh.R;
import com.taiqudong.android.enayeh.application.AppLogic;
import com.taiqudong.android.enayeh.application.SysApplication;
import com.taiqudong.android.enayeh.bean.Feed;
import com.taiqudong.android.enayeh.bean.NotifyEvent;
import com.taiqudong.android.enayeh.firebase.MyFirebaseMessagingService;
import com.taiqudong.android.enayeh.fragment.MainFragment;
import com.taiqudong.android.enayeh.fragment.PersonalFragment;
import com.taiqudong.android.enayeh.fragment.VideoFragment;
import com.taiqudong.android.enayeh.utils.Constants;
import com.taiqudong.android.enayeh.utils.NotificationUtil;
import com.taiqudong.android.enayeh.utils.StatusBarUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivityTag";
    ImageView main;
    ImageView video;
    ImageView people;
    FragmentManager mFragmentManager;
    MainFragment mMainFragment;
    VideoFragment mVideoFragment;
    PersonalFragment mPersonFragment;
    TextView mMainTv;
    TextView mVideoTv;
    TextView mPersonTv;
    public LinearLayout mBottomLl;
    int statue;//当前所处的位置：1.主页 2.视频页 3.我的页。

    public static Intent newInstance(Context context, Bundle bundle) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MyFirebaseMessagingService.NOTIFICATION_PUSH, bundle);
        return intent;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppLogic.getInstance().getFeedState() == 1) {
            switchToMain();
            AppLogic.getInstance().setFeedState(0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //push处理
        pushNotification();

        //TODO 预加载返回主页的插屏广告到缓存
        Map<String, AdConfig> map = (Map<String, AdConfig>) SPUtil.getHashMapData(this, com.taiqudong.android.ad.Constants.SP_AD_SPLASH_CONFIG, AdConfig.class);
        if (map.size() != 0 && map.get(com.taiqudong.android.ad.Constants.AD_SPLASH_BACKHOME) != null) {
            AdConfig config = map.get(com.taiqudong.android.ad.Constants.AD_SPLASH_BACKHOME);
            if (config == null) {
                return;
            }
            Log.d(TAG, "config is not null in prefetch!");
            AdParallelLoader adParallelLoader = new AdParallelLoader(config);
            adParallelLoader.prefetch();
        }
        Log.d("ssssss", "onCreate: " + AppLogic.getInstance().getLastMemstrualDate());
        SysApplication.getInstance().finishBeforeActivity(this);
        SysApplication.getInstance().addActivit(this);
        StatusBarUtils.statusbar(this);
        mFragmentManager = getSupportFragmentManager();
        main = (ImageView) findViewById(R.id.iv_main);
        video = (ImageView) findViewById(R.id.iv_video);
        people = (ImageView) findViewById(R.id.iv_people);
        mBottomLl = (LinearLayout) findViewById(R.id.ll_bottom);
        mMainTv = (TextView) findViewById(R.id.tv_main);
        mVideoTv = (TextView) findViewById(R.id.tv_video);
        mPersonTv = (TextView) findViewById(R.id.tv_people);
        main.setOnClickListener(this);
        video.setOnClickListener(this);
        people.setOnClickListener(this);
        mMainTv.setOnClickListener(this);
        mVideoTv.setOnClickListener(this);
        mPersonTv.setOnClickListener(this);
        switchToMain();
        Log.d("MainActivity", "onCreate: ");
    }

    void switchToMain() {
        boolean isNeedRefresh = (boolean) SPUtil.get(this, Constants.SP_NEED_REFRESH_MAINDATA, false);
        statue = 1;
        //关闭播放器的实例
        EventBus.getDefault().post(new NotifyEvent());
        mMainTv.setVisibility(View.VISIBLE);
        mVideoTv.setVisibility(View.GONE);
        mPersonTv.setVisibility(View.GONE);
        Log.d("vdddd", "text:" + mVideoTv.getText().toString());
        main.setImageResource(R.drawable.ic_main_press);
        video.setImageResource(R.drawable.ic_video_nopress);
        people.setImageResource(R.drawable.ic_people_nopress);
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (mMainFragment == null || isNeedRefresh) {
            mMainFragment = MainFragment.newInstance();
            if (isNeedRefresh) {
                transaction.replace(R.id.fl_main_container, mMainFragment);
                transaction.addToBackStack(null);//将personFragment保存到回退栈，此时是为了在任意时刻都有fragment与activity关联。
            } else {
                transaction.add(R.id.fl_main_container, mMainFragment);
            }
        } else {
            transaction.show(mMainFragment);
        }
        if (mVideoFragment != null) {
            transaction.hide(mVideoFragment);
        }
        if (mPersonFragment != null) {
            transaction.hide(mPersonFragment);
        }
        transaction.commit();
        SPUtil.put(this, Constants.SP_NEED_REFRESH_MAINDATA, false);
    }

    void switchToVideo() {
        statue = 2;
        //关闭播放器的实例
        EventBus.getDefault().post(new NotifyEvent());
        mMainTv.setVisibility(View.GONE);
        mVideoTv.setVisibility(View.VISIBLE);
        mPersonTv.setVisibility(View.GONE);
        video.setImageResource(R.drawable.ic_video_press);
        main.setImageResource(R.drawable.ic_main_nopress);
        people.setImageResource(R.drawable.ic_people_nopress);
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (mVideoFragment == null || mVideoFragment.isRemoving()) {
            mVideoFragment = VideoFragment.newInstance();
            transaction.add(R.id.fl_main_container, mVideoFragment);
        } else {
            transaction.show(mVideoFragment);
        }
        if (mMainFragment != null) {
            transaction.hide(mMainFragment);
        }
        if (mPersonFragment != null) {
            transaction.hide(mPersonFragment);
        }
        transaction.commit();
    }

    void switchToPeople() {
        statue = 3;
        //关闭播放器的实例
        EventBus.getDefault().post(new NotifyEvent());
        mMainTv.setVisibility(View.GONE);
        mVideoTv.setVisibility(View.GONE);
        mPersonTv.setVisibility(View.VISIBLE);
        Log.d("vdddd", "text:" + mVideoTv.getText().toString());

        video.setImageResource(R.drawable.ic_video_nopress);
        main.setImageResource(R.drawable.ic_main_nopress);
        people.setImageResource(R.drawable.ic_people_press);
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (mPersonFragment == null || mPersonFragment.isRemoving()) {
            mPersonFragment = new PersonalFragment();
            transaction.add(R.id.fl_main_container, mPersonFragment);
        } else {
            transaction.show(mPersonFragment);
        }
        if (mVideoFragment != null) {
            transaction.hide(mVideoFragment);
        }
        if (mMainFragment != null) {
            transaction.hide(mMainFragment);
        }
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_main:
                switchToMain();
                break;
            case R.id.iv_video:
                switchToVideo();
                break;
            case R.id.iv_people:
                switchToPeople();
                break;
            case R.id.tv_main:
                switchToMain();
                break;
            case R.id.tv_video:
                switchToVideo();
                break;
            case R.id.tv_people:
                switchToPeople();
                break;
        }
    }

    public static int b = 0;//返回状态

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (b == 2) {
            SysApplication.getInstance().exit(this);
        }
        if (statue == 1) {
            int back = mMainFragment.onBackPressed();
            if (back == 1) {//执行了回主界面
                return true;
            } else if (back == 2) {//回弹
                b = 2;
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d(TAG, "onKeyDown: " + "otherFragment exit");
            SysApplication.getInstance().exit(this);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SysApplication.getInstance().removeActivity(this);
    }

    private Bundle bundle;

    //处理push
    private void pushNotification() {
        Log.i(TAG, "onCreate: ");
        if (getIntent().getExtras() != null) {
            Log.i(TAG, "onCreate: getExtras != null");
            bundle = (Bundle) getIntent().getExtras().get(MyFirebaseMessagingService.NOTIFICATION_PUSH);
            if (bundle != null) {
                Log.i(TAG, "onCreate: bundle != null");
                String type = bundle.getString(MyFirebaseMessagingService.NOTIFICATION_TYPE);
                // TODO: 2017/9/21 根据type，到不同的界面
                if (!isEmpty(type)) {
                    Log.i(TAG, "onCreate: type != null");
                    if (MyFirebaseMessagingService.NOTIFICATION_TYPE_ARTICLE.equals(type)) {//类型 文章
                        Log.i(TAG, "onCreate: 类型 文章");
                        String id = bundle.getString(MyFirebaseMessagingService.NOTIFICATION_ID);
                        String ctg = bundle.getString(MyFirebaseMessagingService.NOTIFICATION_CTG);
                        String statisticsIndex = bundle.getString(MyFirebaseMessagingService.NOTIFICATION_STATISTICS_INDEX);
                        String articleUrl = bundle.getString(MyFirebaseMessagingService.NOTIFICATION_ARTICLE_URL);//文章url
                        if (!isEmpty(id, ctg, statisticsIndex, articleUrl)) {//字符串全部不为空
                            Log.i(TAG, "onCreate: 类型 文章字符串全部不为空");
                            //构建一个完整的feed
                            Feed feed = new Feed();
                            // TODO: 2017/9/22
                            startActivity(FeedWebActivity.newIntent(this, feed, FeedWebActivity.LastViewType.OTHER));
                        }
                    } else if (MyFirebaseMessagingService.NOTIFICATION_TYPE_VIDEO.equals(type)) {//类型 视频
                        Log.i(TAG, "onCreate: 类型 视频");
                        String id = bundle.getString(MyFirebaseMessagingService.NOTIFICATION_ID);
                        String ctg = bundle.getString(MyFirebaseMessagingService.NOTIFICATION_CTG);
                        String statisticsIndex = bundle.getString(MyFirebaseMessagingService.NOTIFICATION_STATISTICS_INDEX);
                        String target_id = bundle.getString(MyFirebaseMessagingService.NOTIFICATION_TARGET_ID);
                        String cover = bundle.getString(MyFirebaseMessagingService.NOTIFICATION_COVER);
                        if (!isEmpty(id, ctg, statisticsIndex, target_id, cover)) { //字符串全部不为空
                            Log.i(TAG, "onCreate: 类型 视频字符串全部不为空");
                            //构建一个完整的feed
                            Feed feed = new Feed();
                            // TODO: 2017/9/22
                            startActivity(FeedVideoActivity.newIntent(this, feed, FeedVideoActivity.LastViewType.OTHER));
                        }
                    } else if (MyFirebaseMessagingService.NOTIFICATION_TYPE_WEB.equals(type)) {//类型 网页
                        Log.i(TAG, "onCreate: 类型 网页");
                        String webUrl = bundle.getString(MyFirebaseMessagingService.NOTIFICATION_WEB_URL);
                        if (!isEmpty(webUrl)) { //字符串全部不为空
                            Log.i(TAG, "onCreate: 类型 网页字符串全部不为空");
                            Intent intent = new Intent(this, WebActivity.class);
                            intent.putExtra("from", WebActivity.FROM_PUSH);
                            intent.putExtra(MyFirebaseMessagingService.NOTIFICATION_WEB_URL, webUrl);
                            startActivity(intent);
                        }
                    } else if (MyFirebaseMessagingService.NOTIFICATION_TYPE_HOME.equals(type)) {//类型 主页
                    }
                }
            }
        }
    }

    //判断多个字符串为空
    public static boolean isEmpty(CharSequence... str) {
        for (int i = 0; i < str.length; i++) {
            Log.i(TAG, "isEmpty: " + i + ":" + str[i]);
            if (str[i] == null || str[i].length() == 0) {
                return true;
            }
        }
        return false;
    }

    //测试代码push notification
    private void testPushNotification() {
        Bundle bundle = new Bundle();
        bundle.putString(MyFirebaseMessagingService.NOTIFICATION_TITLE, "TITLE");
        bundle.putString(MyFirebaseMessagingService.NOTIFICATION_BODY, "BODY");

        bundle.putString(MyFirebaseMessagingService.NOTIFICATION_TYPE, MyFirebaseMessagingService.NOTIFICATION_TYPE_ARTICLE);//文章
        bundle.putString(MyFirebaseMessagingService.NOTIFICATION_ID, "aaaaaaaaa");
        bundle.putString(MyFirebaseMessagingService.NOTIFICATION_CTG, "1006");
        bundle.putString(MyFirebaseMessagingService.NOTIFICATION_STATISTICS_INDEX, "9999");
        bundle.putString(MyFirebaseMessagingService.NOTIFICATION_ARTICLE_URL, "http://www.baidu.com");


        //        bundle.putString(MyFirebaseMessagingService.NOTIFICATION_TYPE, MyFirebaseMessagingService.NOTIFICATION_TYPE_VIDEO);//视频
        //        bundle.putString(MyFirebaseMessagingService.NOTIFICATION_ID, "aaaaaaaaaaa");
        //        bundle.putString(MyFirebaseMessagingService.NOTIFICATION_CTG, "1006");
        //        bundle.putString(MyFirebaseMessagingService.NOTIFICATION_STATISTICS_INDEX, "9999");
        //        bundle.putString(MyFirebaseMessagingService.NOTIFICATION_TARGET_ID, "132");
        //        bundle.putString(MyFirebaseMessagingService.NOTIFICATION_COVER, "http://www.we.com/awdf/awd.jpg");//视频封面？？？
        //
        //        bundle.putString(MyFirebaseMessagingService.NOTIFICATION_TYPE, MyFirebaseMessagingService.NOTIFICATION_TYPE_WEB);//网页
        //        bundle.putString(MyFirebaseMessagingService.NOTIFICATION_WEB_URL, "http://www.baidu.com");
        //
        //        bundle.putString(MyFirebaseMessagingService.NOTIFICATION_TYPE, MyFirebaseMessagingService.NOTIFICATION_TYPE_HOME);//主页


        NotificationUtil.showNotification(this, bundle);
    }
}