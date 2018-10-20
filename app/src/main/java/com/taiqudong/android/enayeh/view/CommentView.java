package com.taiqudong.android.enayeh.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.taiqudong.android.enayeh.R;
import com.taiqudong.android.enayeh.listener.SoftKeyboardStateWatcher;

/**
 * 评论view
 * Created by taiqudong on 2017/8/16.
 */

public class CommentView extends LinearLayout implements View.OnClickListener {

    private Context context;

    OnSendClickListener onSendClickListener;

    public CommentView(Context context) {
        this(context, null, 0);
    }

    public CommentView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.view_comment, this);
        init();
        title = context.getString(R.string.app_name);
    }

    private EditText et_comment_main;//内容

    private ImageView iv_comment;//评论
    private ImageView iv_collect;//收藏
    private ImageView iv_share;//分享
    private TextView tv_count;//评论数

    private LinearLayout ll_comment_one;//包括分享，收藏，评论
    private LinearLayout ll_comment_two;//包括editText，和发布

    private TextView tv_comment_show;//点击显示ll_comment_two页面
    private TextView tv_comment_send;//发布


    private String url;//默认分享的内容
    private String title;//分享弹框的title

    private void init() {
        et_comment_main = (EditText) findViewById(R.id.et_comment_main);
        iv_comment = (ImageView) findViewById(R.id.iv_comment);
        iv_collect = (ImageView) findViewById(R.id.iv_collect);
        iv_share = (ImageView) findViewById(R.id.iv_share);
        tv_count = (TextView) findViewById(R.id.tv_count);

        ll_comment_one = (LinearLayout) findViewById(R.id.ll_comment_one);
        ll_comment_two = (LinearLayout) findViewById(R.id.ll_comment_two);

        tv_comment_show = (TextView) findViewById(R.id.tv_comment_show);
        tv_comment_send = (TextView) findViewById(R.id.tv_comment_send);

        tv_comment_show.setOnClickListener(this);
        tv_comment_send.setOnClickListener(this);

        iv_comment.setOnClickListener(this);
        iv_collect.setOnClickListener(this);
        iv_share.setOnClickListener(this);
        tv_count.setOnClickListener(this);

        et_comment_main.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 1) {
                    tv_comment_send.setTextColor(Color.parseColor("#999999"));
                } else {
                    tv_comment_send.setTextColor(Color.parseColor("#FF189F"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_comment_main.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 此处为得到焦点时的处理内容
                    ll_comment_two.setVisibility(VISIBLE);
                    ll_comment_one.setVisibility(GONE);
                } else {
                    // 此处为失去焦点时的处理内容
                    ll_comment_two.setVisibility(GONE);
                    ll_comment_one.setVisibility(VISIBLE);
                }
            }
        });
        final SoftKeyboardStateWatcher watcher = new SoftKeyboardStateWatcher(et_comment_main, context);
        watcher.addSoftKeyboardStateListener(
                new SoftKeyboardStateWatcher.SoftKeyboardStateListener() {
                    @Override
                    public void onSoftKeyboardOpened(int keyboardHeightInPx) {
                        //处理一些键盘打开的事情
                        Log.d("inpooooo", "onSoftKeyboardOpened: open");
                    }

                    @Override
                    public void onSoftKeyboardClosed() {
                        //处理一些键盘关闭的事情
                        Log.d("inpooooo", "onSoftKeyboardOpened: close");
                        if (TextUtils.isEmpty(et_comment_main.getText())) {
                            ll_comment_two.setVisibility(GONE);
                            ll_comment_one.setVisibility(VISIBLE);
                        }
                    }
                }
        );
    }


    public void clearEdit() {
        if (et_comment_main != null) {
            et_comment_main.setText("");
        }
    }

    private boolean isCollect = false;

    public void setCollect(boolean collect) {
        isCollect = collect;
        iv_collect.setImageResource(isCollect ? R.mipmap.ic_collect_s : R.mipmap.ic_collect_d);
    }

    // 评论布局隐藏
    public void gone() {
        ll_comment_two.setVisibility(GONE);
        ll_comment_one.setVisibility(VISIBLE);
        //隐藏软键盘
        if (((Activity) context).getCurrentFocus() != null && ((Activity) context).getCurrentFocus()
                .getWindowToken() != null) {
            ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(((Activity) context).getCurrentFocus()
                            .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_comment_show:
                ll_comment_one.setVisibility(GONE);
                ll_comment_two.setVisibility(VISIBLE);
                //制动弹出软键盘
                et_comment_main.setFocusable(true);
                et_comment_main.setFocusableInTouchMode(true);
                et_comment_main.requestFocus();
                //显示软键盘
                ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE))
                        .toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                break;
            case R.id.tv_comment_send:
                String content = et_comment_main.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
//                    Toast.makeText(context, context.getString(R.string.content_cannot_empty), Toast.LENGTH_LONG).show();
                } else {
                    ll_comment_two.setVisibility(GONE);
                    ll_comment_one.setVisibility(VISIBLE);
                    //隐藏软键盘
                    if (((Activity) context).getCurrentFocus()
                            != null) {
                        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE))
                                .hideSoftInputFromWindow(((Activity) context).getCurrentFocus()
                                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                    onSendClickListener.onSendClick(content);
                }
            case R.id.iv_comment://点击评论
                if (getOnCommentClickListener() != null) {
                    getOnCommentClickListener().onCommentClick();
                } else {//执行默认的逻辑

                }
                break;
            case R.id.iv_collect://点击收藏
                if (getOnCollectClickListener() != null) {
                    getOnCollectClickListener().onCollectClick(isCollect);
                } else {//执行默认的逻辑

                }
                break;
            case R.id.iv_share://点击分享
                if (getOnShareClickListener() != null) {
                    getOnShareClickListener().onShareClick();
                } else {//执行默认分享的逻辑
                    if (TextUtils.isEmpty(url)) {//如果url为null或"",不执行分享事件
                        return;
                    }
                    Intent textIntent = new Intent(Intent.ACTION_SEND);
                    textIntent.setType("text/plain");
                    textIntent.putExtra(Intent.EXTRA_TEXT, url);
                    context.startActivity(Intent.createChooser(textIntent, title));
                }
                break;
            case R.id.tv_count://数字
                if (getOnTvCountClickListener() != null) {
                    getOnTvCountClickListener().onTvCountClick();
                } else {//执行默认的逻辑

                }
                break;
            default:
        }
    }


    /*
    * 调用的时候，要判断用户什么也没输入的情况
    * */
    public String getEditTextString() {
        return et_comment_main.getText().toString().trim();
    }

    /*
    * 设置评论数，null、""、0,三种情况不显示
    * */
    public void setTvCount(String count) {
        tv_count.setText(count);
        tv_count.setVisibility(VISIBLE);

        if (TextUtils.isEmpty(count)) {
            tv_count.setVisibility(INVISIBLE);
            return;
        }

        int c = 0;
        try {
            c = Integer.parseInt(count);
        } catch (Exception e) {
        }
        if (c < 1) {
            tv_count.setVisibility(INVISIBLE);
        } else {
            tv_count.setText("" + count);
            tv_count.setVisibility(VISIBLE);
        }
    }

    public void setTvCount(int count) {
        if (count < 1) {
            tv_count.setVisibility(INVISIBLE);
        } else {
            tv_count.setText("" + count);
            tv_count.setVisibility(VISIBLE);
        }
    }

    public int getTvCount() {
        if (TextUtils.isEmpty(tv_count.getText())) {
            return 0;
        }
        return Integer.valueOf(tv_count.getText().toString());
    }

    /*
    * 设置分享的title，null、""，显示appname，默认显示appname
    * */
    public void setShareTitle(String titleText) {
        if (TextUtils.isEmpty(titleText)) {
            title = context.getString(R.string.app_name);
        } else {
            title = titleText;
        }
    }

    /*
    * 设置分享的内容，null、""，为""，默认显示""
    * 如果url是null或"",不会响应分享
    * */
    public void setShareUrlString(String urlText) {
        if (TextUtils.isEmpty(urlText)) {
            url = "";
        } else {
            url = urlText;
        }
    }

    public interface OnCommentClickListener {
        void onCommentClick();
    }

    public interface OnCollectClickListener {
        void onCollectClick(Boolean isCollect);
    }

    public interface OnShareClickListener {
        void onShareClick();
    }

    interface OnTvCountClickListener {
        void onTvCountClick();
    }

    private OnCommentClickListener onCommentClickListener;
    private OnCollectClickListener onCollectClickListener;
    private OnShareClickListener onShareClickListener;
    private OnTvCountClickListener onTvCountClickListener;

    private OnCommentClickListener getOnCommentClickListener() {
        return onCommentClickListener;
    }

    public void setOnCommentClickListener(OnCommentClickListener onCommentClickListener) {
        this.onCommentClickListener = onCommentClickListener;
    }

    private OnCollectClickListener getOnCollectClickListener() {
        return onCollectClickListener;
    }

    public void setOnCollectClickListener(OnCollectClickListener onCollectClickListener) {
        this.onCollectClickListener = onCollectClickListener;
    }

    private OnShareClickListener getOnShareClickListener() {
        return onShareClickListener;
    }

    /*
    * 如果设置分享事件，默认的分享不会被执行
    * */
    public void setOnShareClickListener(OnShareClickListener onShareClickListener) {
        this.onShareClickListener = onShareClickListener;
    }

    private OnTvCountClickListener getOnTvCountClickListener() {
        return onTvCountClickListener;
    }

    public void setOnTvCountClickListener(OnTvCountClickListener onTvCountClickListener) {
        this.onTvCountClickListener = onTvCountClickListener;
    }


    public OnSendClickListener getOnSendClickListener() {
        return onSendClickListener;
    }

    public void setOnSendClickListener(OnSendClickListener onSendClickListener) {
        this.onSendClickListener = onSendClickListener;
    }

    public interface OnSendClickListener {
        public void onSendClick(String content);
    }

}
