package com.taiqudong.android.enayeh.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.taiqudong.android.enayeh.R;
import com.taiqudong.android.enayeh.application.SysApplication;
import com.taiqudong.android.enayeh.utils.StatusBarUtils;

/**
 * 主页面日历页面点击箭头，跳转到名词说明页面
 * Created by taiqudong on 2017/8/15.
 */

public class ExplainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explain);
        SysApplication.getInstance().addActivit(this);
        StatusBarUtils.statusbar(this);

        findViewById(R.id.iv_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExplainActivity.this.finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SysApplication.getInstance().removeActivity(this);
    }
}
