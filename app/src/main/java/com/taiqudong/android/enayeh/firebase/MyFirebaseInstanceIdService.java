package com.taiqudong.android.enayeh.firebase;

import android.util.Log;

import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by taiqudong on 2017/9/20.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseInstanceId";

    @Override
    public void onTokenRefresh() {
        // 得到更新InstanceID令牌。
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.i(TAG, "onTokenRefresh: " + refreshedToken);
        try {
            AppEventsLogger.setPushNotificationsRegistrationId(refreshedToken);
        } catch (Exception e) {
            Log.e(TAG, "Failed to complete token refresh", e);
        }

        // 如果你想发送消息给这个应用程序实例或管理应用程序订阅在服务器端,发送到您的应用服务器实例ID标记。
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }
}
