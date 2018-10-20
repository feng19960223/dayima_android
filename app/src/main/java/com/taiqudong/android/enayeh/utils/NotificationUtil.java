package com.taiqudong.android.enayeh.utils;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.taiqudong.android.enayeh.R;
import com.taiqudong.android.enayeh.activity.FeedVideoActivity;
import com.taiqudong.android.enayeh.activity.FeedWebActivity;
import com.taiqudong.android.enayeh.activity.MainActivity;
import com.taiqudong.android.enayeh.activity.WebActivity;
import com.taiqudong.android.enayeh.activity.WelComeActivity;
import com.taiqudong.android.enayeh.bean.Feed;
import com.taiqudong.android.enayeh.firebase.MyFirebaseMessagingService;

import java.util.List;

/**
 * Created by taiqudong on 2017/7/15.
 */

public class NotificationUtil {
    public static final String TAG = "NotificationUtil";

    public static void showNotification(Context context, String title, String text) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, ++id, intent, 0);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_notification)//纯alpha图层的图片
                .setColor(Color.parseColor("#FF1878"))
                //                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build();
        notificationManager.notify(++id, notification);
    }

    //push用
    public static void showNotification(Context context, Bundle bundle) {
        String title = bundle.getString(MyFirebaseMessagingService.NOTIFICATION_TITLE);
        String body = bundle.getString(MyFirebaseMessagingService.NOTIFICATION_BODY);
        //        if (MainActivity.isEmpty(title, body)) {
        //            return;
        //        }

        Intent intent = new Intent(context, WelComeActivity.class);//后台应用，打开欢迎界面
        //// TODO: 2017/9/21 if判断，第一次开那个界面
        if (!isBackground(context)) {//如果是前台应用
            //在这里根据type，判断要打开那个activity
            if (bundle != null) {
                String type = bundle.getString(MyFirebaseMessagingService.NOTIFICATION_TYPE);
                // TODO: 2017/9/21 根据type，到不同的界面
                if (!MainActivity.isEmpty(type)) {
                    android.util.Log.i(TAG, "showNotification: type != null");
                    if (MyFirebaseMessagingService.NOTIFICATION_TYPE_ARTICLE.equals(type)) {//类型 文章
                        android.util.Log.i(TAG, "showNotification: 类型 文章");
                        String id = bundle.getString(MyFirebaseMessagingService.NOTIFICATION_ID);
                        String ctg = bundle.getString(MyFirebaseMessagingService.NOTIFICATION_CTG);
                        String statisticsIndex = bundle.getString(MyFirebaseMessagingService.NOTIFICATION_STATISTICS_INDEX);
                        String articleUrl = bundle.getString(MyFirebaseMessagingService.NOTIFICATION_ARTICLE_URL);//文章url
                        if (!MainActivity.isEmpty(id, ctg, statisticsIndex, articleUrl)) {
                            //字符串全部不为空
                            android.util.Log.i(TAG, "showNotification: 类型 文章字符串全部不为空");
                            Feed feed = new Feed();
                            intent = FeedWebActivity.newIntent(context, feed, FeedWebActivity.LastViewType.OTHER);
                        }
                    } else if (MyFirebaseMessagingService.NOTIFICATION_TYPE_VIDEO.equals(type)) {//类型 视频
                        android.util.Log.i(TAG, "showNotification: 类型 视频");
                        String id = bundle.getString(MyFirebaseMessagingService.NOTIFICATION_ID);
                        String ctg = bundle.getString(MyFirebaseMessagingService.NOTIFICATION_CTG);
                        String statisticsIndex = bundle.getString(MyFirebaseMessagingService.NOTIFICATION_STATISTICS_INDEX);
                        String target_id = bundle.getString(MyFirebaseMessagingService.NOTIFICATION_TARGET_ID);
                        String cover = bundle.getString(MyFirebaseMessagingService.NOTIFICATION_COVER);
                        if (!MainActivity.isEmpty(id, ctg, statisticsIndex, target_id, cover)) { //字符串全部不为空
                            android.util.Log.i(TAG, "showNotification: 类型 视频字符串全部不为空");
                            Feed feed = new Feed();
                            intent = FeedVideoActivity.newIntent(context, feed, FeedVideoActivity.LastViewType.OTHER);
                        }
                    } else if (MyFirebaseMessagingService.NOTIFICATION_TYPE_WEB.equals(type)) {//类型 网页
                        android.util.Log.i(TAG, "showNotification: 类型 网页");
                        String webUrl = bundle.getString(MyFirebaseMessagingService.NOTIFICATION_WEB_URL);
                        if (!MainActivity.isEmpty(webUrl)) { //字符串全部不为空
                            android.util.Log.i(TAG, "showNotification: 类型 网页字符串全部不为空");
                            intent = new Intent(context, WebActivity.class);
                            intent.putExtra("from", WebActivity.FROM_PUSH);
                            intent.putExtra(MyFirebaseMessagingService.NOTIFICATION_WEB_URL, webUrl);
                        }
                    } else if (MyFirebaseMessagingService.NOTIFICATION_TYPE_HOME.equals(type)) {//类型 主页
                        android.util.Log.i(TAG, "showNotification: 类型 home");
                        intent = new Intent(context, MainActivity.class);
                    }
                }
            }
        }
        intent.putExtra(MyFirebaseMessagingService.NOTIFICATION_PUSH, bundle);//Intent的bundle里面包含了push的所有信息,包括要跳转到什么界面
        // TODO: 2017/9/22  这句要不要加，需要测试↓
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//这句要不要加，需要测试
        PendingIntent pendingIntent = PendingIntent.getActivity(context, ++id, intent, 0);

        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(body)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_notification)//纯alpha图层的图片
                .setColor(Color.parseColor("#FF1878"))
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build();

        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(++id, notification);
    }

    private static int id = 0;

    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                /*
                BACKGROUND=400 EMPTY=500 FOREGROUND=100
                GONE=1000 PERCEPTIBLE=130 SERVICE=300 ISIBLE=200
                 */
                Log.i(context.getPackageName(), "此appimportace ="
                        + appProcess.importance
                        + ",context.getClass().getName()="
                        + context.getClass().getName());
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Log.i(context.getPackageName(), "处于后台"
                            + appProcess.processName);
                    return true;
                } else {
                    Log.i(context.getPackageName(), "处于前台"
                            + appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }
}
