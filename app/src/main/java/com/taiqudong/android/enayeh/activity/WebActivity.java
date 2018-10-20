package com.taiqudong.android.enayeh.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.taiqudong.android.enayeh.R;
import com.taiqudong.android.enayeh.application.ClientSideFactory;
import com.taiqudong.android.enayeh.application.SysApplication;
import com.taiqudong.android.enayeh.firebase.MyFirebaseMessagingService;
import com.taiqudong.android.enayeh.utils.StatusBarUtils;

/**
 * Created by taiqudong on 2017/7/22.
 */

public class WebActivity extends AppCompatActivity implements View.OnClickListener {
    private WebView webview;
    private ProgressBar progressBar;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        SysApplication.getInstance().addActivit(this);
        StatusBarUtils.statusbar(this);
        initView();
        initData();
    }

    private void initView() {
        tv = (TextView) findViewById(R.id.tv);
        webview = (WebView) findViewById(R.id.webview);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        findViewById(R.id.iv_return).setOnClickListener(this);
    }

    private void initData() {
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }
        });
        WebSettings webSettings = webview.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //优先使用缓存
        webSettings.setJavaScriptEnabled(false);//禁止Javascript交互
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setBuiltInZoomControls(false);
        webSettings.setBuiltInZoomControls(false);

        String from = getIntent().getStringExtra("from");

        if (FROM_ABOUT_US.equals(from)) {
            tv.setText(getString(R.string.ABOUTUS));
            webview.loadUrl(ClientSideFactory.ABOUT_US_URL);
        } else if (FROM_TERMS.equals(from)) {
            tv.setText(getString(R.string.TERMS));
            webview.loadUrl(ClientSideFactory.TERMS_URL);
        } else if (FROM_POLICY.equals(from)) {
            tv.setText(getString(R.string.PRIVACYPOLICY));
            webview.loadUrl(ClientSideFactory.PRIVACY_POLICY_URL);
        } else if (FROM_PUSH.equals(from)) {//push
            tv.setText("");// TODO: 2017/9/22 push打开的网页，tv显示什么？？？
            webview.loadUrl(getIntent().getStringExtra(MyFirebaseMessagingService.NOTIFICATION_WEB_URL));
        }


    }

    public static final String FROM_ABOUT_US = "aboutus";
    public static final String FROM_TERMS = "terms";
    public static final String FROM_POLICY = "policy";
    public static final String FROM_PUSH = "push";

    @Override
    protected void onDestroy() {
        if (webview != null) {
            webview.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webview.clearHistory();
            ((ViewGroup) webview.getParent()).removeView(webview);
            webview.destroy();
            webview = null;
        }
        super.onDestroy();
        SysApplication.getInstance().removeActivity(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return:
                this.finish();
                break;
        }
    }

}

