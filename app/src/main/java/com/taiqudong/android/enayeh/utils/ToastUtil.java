package com.taiqudong.android.enayeh.utils;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.taiqudong.android.enayeh.R;

/**
 * 弹出带图片的Toast，并且位置可以显示在中间
 * Created by taiqudong on 2017/8/26.
 */


public class ToastUtil {

    private static Toast toast;
    private static TextView tv_toast;
    private static ImageView iv_toast;
    private static LinearLayout ll_toast;



    public ToastUtil(Context context, String text, int resId) {
        this(context, text, Color.WHITE, resId, Color.parseColor("#CC000000"), Gravity.CENTER);
    }

    public ToastUtil(Context context, String text) {
        this(context, text, Color.WHITE, 0, Color.parseColor("#CC000000"), Gravity.CENTER);
    }


    public ToastUtil(Context context, String text, int textColor, int resId, int bgColor, int gravity) {
        if (toast == null) {
            toast = new Toast(context);
            View toastRoot = LayoutInflater.from(context).inflate(R.layout.toast, null);
            ll_toast = (LinearLayout) toastRoot.findViewById(R.id.ll_toast);
            iv_toast = (ImageView) toastRoot.findViewById(R.id.iv_toast);
            tv_toast = (TextView) toastRoot.findViewById(R.id.tv_toast);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setView(toastRoot);
            toast.setDuration(Toast.LENGTH_SHORT);
        }

        setTvToast(text);
        setTvToastColor(textColor);
        setIvToast(resId);
        setToastBg(bgColor);
        setGravity(gravity);

    }

    public void setToastBg(int color) {
        ll_toast.setBackgroundColor(color);
    }

    public void setIvToast(int resId) {
        if (resId != 0) {
            iv_toast.setVisibility(View.VISIBLE);
            iv_toast.setImageResource(resId);
        } else {
            iv_toast.setVisibility(View.GONE);
        }
    }

    public void setTvToast(String content) {
        if (!TextUtils.isEmpty(content)) {
            tv_toast.setText(content);
        }
    }

    public void setTvToastColor(int color) {
        tv_toast.setTextColor(color);
    }

    public void show() {
        toast.show();
    }

    public void setGravity(int gravity) {
        toast.setGravity(gravity, 0, 0);
    }

}
