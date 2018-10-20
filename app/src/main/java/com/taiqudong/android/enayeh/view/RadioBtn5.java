package com.taiqudong.android.enayeh.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.taiqudong.android.enayeh.R;

/**
 * Created by taiqudong on 2017/8/5.
 * 绑定的布局是btn_radio5.xml
 * 5个图片的单选控件，点击取消功能，单选功能，onClickRadioBtn5ItemListener返回的是12345，分別代表选中第12345个
 * 不设置string，就会隐藏文字
 */

public class RadioBtn5 extends LinearLayout implements View.OnClickListener {
    private int[] defaultResIds = new int[5];//默认的图片
    private int[] clickResIds = new int[5];//点击的图片
    private String[] stringResIds = new String[5];//文字
    private boolean[] isFx = new boolean[]{true, true, true, true, true};//true，点击变红，false点击变黑，setItem（int pos）

    private TextView[] tvs = new TextView[5];
    private ImageView[] ivs = new ImageView[5];

    private OnClickRadioBtn5ItemListener onClickRadioBtn5ItemListener;

    public RadioBtn5(Context context) {
        this(context, null, 0);
    }

    public RadioBtn5(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadioBtn5(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.btn_radio5, this);
        init();
    }

    private void init() {
        tvs[0] = (TextView) findViewById(R.id.tv_f1);
        tvs[1] = (TextView) findViewById(R.id.tv_f2);
        tvs[2] = (TextView) findViewById(R.id.tv_f3);
        tvs[3] = (TextView) findViewById(R.id.tv_f4);
        tvs[4] = (TextView) findViewById(R.id.tv_f5);
        ivs[0] = (ImageView) findViewById(R.id.iv_f1);
        ivs[1] = (ImageView) findViewById(R.id.iv_f2);
        ivs[2] = (ImageView) findViewById(R.id.iv_f3);
        ivs[3] = (ImageView) findViewById(R.id.iv_f4);
        ivs[4] = (ImageView) findViewById(R.id.iv_f5);
        findViewById(R.id.ll_f1).setOnClickListener(this);
        findViewById(R.id.ll_f2).setOnClickListener(this);
        findViewById(R.id.ll_f3).setOnClickListener(this);
        findViewById(R.id.ll_f4).setOnClickListener(this);
        findViewById(R.id.ll_f5).setOnClickListener(this);
    }

    //默认的图片
    public void setDefaultResIds(int[] defaultResIds) {
        this.defaultResIds = defaultResIds;
        notifyDate();
    }

    //点击的图片
    public void setClickResIds(int[] clickResIds) {
        this.clickResIds = clickResIds;
        notifyDate();
    }

    //文字
    public void setStringResIds(String[] stringResIds) {
        this.stringResIds = stringResIds;
        for (int i = 0; i < 5; i++) {//如果没字，隐藏
            if (TextUtils.isEmpty(stringResIds[i])) {
                tvs[i].setVisibility(GONE);
            } else {
                tvs[i].setVisibility(VISIBLE);
            }
        }
        notifyDate();
    }

    private void notifyDate() {
        for (int i = 0; i < 5; i++) {
            ivs[i].setImageResource(defaultResIds[i]);
            tvs[i].setText(stringResIds[i]);
            tvs[i].setTextColor(Color.parseColor("#84000000"));
            isFx[i] = true;
        }
    }

    @Override
    public void onClick(View v) {
        Click();//还原
        switch (v.getId()) {
            case R.id.ll_f1:
                onClickItem(0);
                break;
            case R.id.ll_f2:
                onClickItem(1);
                break;
            case R.id.ll_f3:
                onClickItem(2);
                break;
            case R.id.ll_f4:
                onClickItem(3);
                break;
            case R.id.ll_f5:
                onClickItem(4);
                break;
            default:
                break;
        }
    }

    //点击时还原
    private void Click() {
        for (int i = 0; i < 5; i++) {
            tvs[i].setTextColor(Color.parseColor("#84000000"));//灰色
            ivs[i].setImageResource(defaultResIds[i]);
        }
    }

    private void onClickItem(int itemPos) {
        if (isFx[itemPos]) {//确定
            tvs[itemPos].setTextColor(Color.parseColor("#FF189F"));
            ivs[itemPos].setImageResource(clickResIds[itemPos]);
            onClickRadioBtn5ItemListener.onClickRadioBtn5Item(itemPos + 1);
        }
        if (!isFx[itemPos]) {//取消
            tvs[itemPos].setTextColor(Color.parseColor("#84000000"));
            ivs[itemPos].setImageResource(defaultResIds[itemPos]);
            onClickRadioBtn5ItemListener.onClickRadioBtn5Item(0);
        }
        for (int i = 0; i < 5; i++) {//为下次做准备
            if (i == itemPos) {
                isFx[itemPos] = !isFx[itemPos];
            } else {
                isFx[i] = true;
            }
        }
    }

    //必须设置在上面三个set方法以后,选中一个item
    public void setItem(int itemPos) {//1-5
        notifyDate();
        if (itemPos < 1) {
            return;
        }
        if (itemPos > 5) {
            return;
        }
        tvs[itemPos - 1].setTextColor(Color.parseColor("#FF189F"));
        ivs[itemPos - 1].setImageResource(clickResIds[itemPos - 1]);
        isFx[itemPos - 1] = false;
    }

    public void setOnClickRadioBtn5ItemListener(OnClickRadioBtn5ItemListener onClickRadioBtn5ItemListener) {
        this.onClickRadioBtn5ItemListener = onClickRadioBtn5ItemListener;
    }

    public interface OnClickRadioBtn5ItemListener {
        void onClickRadioBtn5Item(int i);
    }

}

