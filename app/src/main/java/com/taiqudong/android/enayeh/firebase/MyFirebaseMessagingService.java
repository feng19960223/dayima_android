package com.taiqudong.android.enayeh.firebase;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.taiqudong.android.enayeh.utils.NotificationUtil;

import java.util.Map;

/**
 * Created by taiqudong on 2017/9/20.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMessagin";

    public static final String NOTIFICATION_PUSH = "push";//push
    public static final String NOTIFICATION_TITLE = "title";//标题 对应 高级选项 标题
    public static final String NOTIFICATION_BODY = "body";//内容 对应 消息文字

    public static final String NOTIFICATION_TYPE = "type";//类型
    public static final String NOTIFICATION_TYPE_VIDEO = "video";//类型 视频
    public static final String NOTIFICATION_TYPE_ARTICLE = "article";//类型 文章
    public static final String NOTIFICATION_TYPE_HOME = "home";//类型 主页
    public static final String NOTIFICATION_TYPE_WEB = "web";//类型 网页

    public static final String NOTIFICATION_WEB_URL = "webUrl";// 网页url

    public static final String NOTIFICATION_ID = "id";//文章和视频公共字段
    public static final String NOTIFICATION_CTG = "CTG";//文章和视频公共字段
    public static final String NOTIFICATION_STATISTICS_INDEX = "statisticsIndex";//文章和视频公共字段 是统计需要的，不要和现有的index混淆就好

    public static final String NOTIFICATION_ARTICLE_URL = "articleUrl";//文章url

    public static final String NOTIFICATION_TARGET_ID = "target_id";//视频的id
    public static final String NOTIFICATION_COVER = "cover";//视频封面的url


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        //不可以有super()
        logSystem(remoteMessage);

        Bundle bundle = new Bundle();
        Map<String, String> data = remoteMessage.getData();

        for (Map.Entry<String, String> entry : data.entrySet()) {
            bundle.putString(entry.getKey(), entry.getValue());
        }

        bundle.putString(NOTIFICATION_TITLE, remoteMessage.getNotification().getTitle());//高级选项 标题
        bundle.putString(NOTIFICATION_BODY, remoteMessage.getNotification().getBody());//消息文字

        NotificationUtil.showNotification(this, bundle);
    }

    //log输出所有信息
    private void logSystem(RemoteMessage remoteMessage) {
        Log.i(TAG, "onMessageReceived: remoteMessage" + remoteMessage);
        Log.i(TAG, "onMessageReceived: getFrom" + remoteMessage.getFrom());//334830438541
        Log.i(TAG, "onMessageReceived: getCollapseKey" + remoteMessage.getCollapseKey());//taiqudong.android.firebasedemo
        Log.i(TAG, "onMessageReceived: getMessageId" + remoteMessage.getMessageId());//0:1505802584778617%99fc150099fc1500
        Log.i(TAG, "onMessageReceived: getMessageType" + remoteMessage.getMessageType());//null
        Log.i(TAG, "onMessageReceived: getTo" + remoteMessage.getTo());//null
        Log.i(TAG, "onMessageReceived: getSentTime" + remoteMessage.getSentTime());//1505802584499
        Log.i(TAG, "onMessageReceived: getTtl" + remoteMessage.getTtl());//0
        Log.i(TAG, "onMessageReceived: getData" + remoteMessage.getData());//{5=6, 7=8, 9=10}

        Map<String, String> data = remoteMessage.getData();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            Log.i(TAG, "onMessageReceived: Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }

        if (remoteMessage.getNotification() != null) {
            Log.i(TAG, "onMessageReceived: getBody" + remoteMessage.getNotification().getBody());//1
            Log.i(TAG, "onMessageReceived: getBodyLocalizationKey" + remoteMessage.getNotification().getBodyLocalizationKey());//null
            Log.i(TAG, "onMessageReceived: getClickAction" + remoteMessage.getNotification().getClickAction());//null
            Log.i(TAG, "onMessageReceived: getColor" + remoteMessage.getNotification().getColor());//null
            Log.i(TAG, "onMessageReceived: getIcon" + remoteMessage.getNotification().getIcon());//null
            Log.i(TAG, "onMessageReceived: getSound" + remoteMessage.getNotification().getSound());//fault
            Log.i(TAG, "onMessageReceived: getTag" + remoteMessage.getNotification().getTag());//null
            Log.i(TAG, "onMessageReceived: getTitle" + remoteMessage.getNotification().getTitle());//3
            Log.i(TAG, "onMessageReceived: getTitleLocalizationKey" + remoteMessage.getNotification().getTitleLocalizationKey());//null
            Log.i(TAG, "onMessageReceived: getBodyLocalizationArgs" + remoteMessage.getNotification().getBodyLocalizationArgs());//null
            Log.i(TAG, "onMessageReceived: getLink" + remoteMessage.getNotification().getLink());//null
            Log.i(TAG, "onMessageReceived: getTitleLocalizationArgs" + remoteMessage.getNotification().getTitleLocalizationArgs());//null
        }
    }

}
