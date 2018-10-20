package com.taiqudong.android.enayeh.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.Window;

import com.taiqudong.android.enayeh.R;

import java.lang.reflect.Method;

/**
 * Created by taiqudong on 2017/7/15.
 */

public class DialogUtil {

    public interface OnDialogUtilListener {
        void onDialogUtil_YES();//点击yes

        void onDialogUtil_CANCEL();//点击cancel
    }


    public static void showDialog(Context context, String title, String message, final OnDialogUtilListener onDialogUtilListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton(context.getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (onDialogUtilListener != null) {
                    onDialogUtilListener.onDialogUtil_CANCEL();
                    dialog.dismiss();
                }
            }
        });
        builder.setPositiveButton(context.getString(R.string.OK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (onDialogUtilListener != null) {
                    onDialogUtilListener.onDialogUtil_YES();
                }
            }
        });
        builder.show();
    }

    //默认title是【提示】
    public static void showDialog(Context context, String message, final OnDialogUtilListener onDialogUtilListener) {
        showDialog(context, context.getString(R.string.dialogTitle), message, onDialogUtilListener);
    }


    // 下面的代码是第一版用到的代码，主要是解决一些手机的虚拟按键会影响弹出的dialog被虚拟按键覆盖，
    // 但是现在ui做了修改，把dialog弹出到了屏幕中间，所以不再出现底部弹出的dialog被覆盖的问题
    public static Window getDialogWindow(Context context, Dialog mDialog) {
        final Window dialogWindow = mDialog.getWindow();

        if (checkDeviceHasNavigationBar(context)) {
            //存在的时候设置
            dialogWindow.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            dialogWindow.getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    int uiOptions =
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                                    // 布局位于状态栏下方
                                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                                    // 全屏
                                    // View.SYSTEM_UI_FLAG_FULLSCREEN |
                                    //隐藏导航栏
                                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                    if (Build.VERSION.SDK_INT >= 19) {
                        uiOptions |= 0x00001000;
                    } else {
                        uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
                    }
                    dialogWindow.getDecorView().setSystemUiVisibility(uiOptions);
                }
            });
        }
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setWindowAnimations(R.style.DialogStyle);

        return dialogWindow;
    }

    private static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
        }
        return hasNavigationBar;
    }


}
