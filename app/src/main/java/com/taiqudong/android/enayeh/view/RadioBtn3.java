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
 * 绑定的布局是btn_radio3.xml
 * 3个图片的单选控件，点击取消功能，单选功能，onClickRadioBtn3ItemListener返回的是123，分別代表选中第123个
 * 不设置string，就会隐藏文字
 */

public class RadioBtn3 extends LinearLayout implements View.OnClickListener {
    private int[] defaultResIds = new int[3];//默认的图片
    private int[] clickResIds = new int[3];//点击的图片
    private String[] stringResIds = new String[3];//文字
    private boolean[] isFx = new boolean[]{true, true, true};//true，点击变红，false点击变黑，setItem（int pos）

    private TextView[] tvs = new TextView[3];
    private ImageView[] ivs = new ImageView[3];

    private OnClickRadioBtn3ItemListener onClickRadioBtn3ItemListener;

    public RadioBtn3(Context context) {
        this(context, null, 0);
    }

    public RadioBtn3(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadioBtn3(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.btn_radio3, this);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void init() {
        tvs[0] = (TextView) findViewById(R.id.tv_f1);
        tvs[1] = (TextView) findViewById(R.id.tv_f2);
        tvs[2] = (TextView) findViewById(R.id.tv_f3);
        ivs[0] = (ImageView) findViewById(R.id.iv_f1);
        ivs[1] = (ImageView) findViewById(R.id.iv_f2);
        ivs[2] = (ImageView) findViewById(R.id.iv_f3);
        findViewById(R.id.ll_f1).setOnClickListener(this);
        findViewById(R.id.ll_f2).setOnClickListener(this);
        findViewById(R.id.ll_f3).setOnClickListener(this);
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
        for (int i = 0; i < 3; i++) {//如果没字，隐藏
            if (TextUtils.isEmpty(stringResIds[i])) {
                tvs[i].setVisibility(GONE);
            } else {
                tvs[i].setVisibility(VISIBLE);
            }
        }
        notifyDate();
    }

    private void notifyDate() {
        for (int i = 0; i < 3; i++) {
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
            default:
                break;
        }
    }

    //点击时还原
    private void Click() {
        for (int i = 0; i < 3; i++) {
            tvs[i].setTextColor(Color.parseColor("#84000000"));//灰色
            ivs[i].setImageResource(defaultResIds[i]);
        }
    }

    private void onClickItem(int itemPos) {
        if (isFx[itemPos]) {//确定
            tvs[itemPos].setTextColor(Color.parseColor("#FF189F"));
            ivs[itemPos].setImageResource(clickResIds[itemPos]);
            onClickRadioBtn3ItemListener.onClickRadioBtn3Item(itemPos + 1);
        }
        if (!isFx[itemPos]) {//取消
            tvs[itemPos].setTextColor(Color.parseColor("#84000000"));
            ivs[itemPos].setImageResource(defaultResIds[itemPos]);
            onClickRadioBtn3ItemListener.onClickRadioBtn3Item(0);
        }
        for (int i = 0; i < 3; i++) {//为下次做准备
            if (i == itemPos) {
                isFx[itemPos] = !isFx[itemPos];
            } else {
                isFx[i] = true;
            }
        }
    }

    //必须设置在上面三个set方法以后,选中一个item
    public void setItem(int itemPos) {//1-3
        notifyDate();
        if (itemPos < 1) {
            return;
        }
        if (itemPos > 3) {
            return;
        }
        tvs[itemPos - 1].setTextColor(Color.parseColor("#FF189F"));
        ivs[itemPos - 1].setImageResource(clickResIds[itemPos - 1]);
        isFx[itemPos - 1] = false;
    }

    public void setOnClickRadioBtn3ItemListener(OnClickRadioBtn3ItemListener onClickRadioBtn3ItemListener) {
        this.onClickRadioBtn3ItemListener = onClickRadioBtn3ItemListener;
    }

    public interface OnClickRadioBtn3ItemListener {
        void onClickRadioBtn3Item(int i);
    }

}

