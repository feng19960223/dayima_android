package com.taiqudong.android.enayeh.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.taiqudong.android.ad.stats.EventConsts;
import com.taiqudong.android.ad.stats.EventLogger;
import com.taiqudong.android.enayeh.R;
import com.taiqudong.android.enayeh.adapter.FeedViewPagerFragmentAdapter;
import com.taiqudong.android.enayeh.application.SysApplication;
import com.taiqudong.android.enayeh.application.retrofit.ServiceGenerator;
import com.taiqudong.android.enayeh.application.retrofit.api.ApiService;
import com.taiqudong.android.enayeh.application.retrofit.bean.Tab;
import com.taiqudong.android.enayeh.utils.Constants;
import com.taiqudong.android.enayeh.utils.LanguageUtil;
import com.taiqudong.android.enayeh.utils.LoadingDialog;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by taiqudong on 2017/8/19.
 * 视频fragment
 */

public class VideoFragment extends Fragment {
    RelativeLayout titleRl;
    RelativeLayout networkErrorMainRl;
    LoadingDialog.Builder builder;
    LoadingDialog dialog;
    int currentPosition;//当前的位置
    List<Tab.DataBean.VideoTabsBean> videoTabsBeens = new ArrayList<>();

    public static VideoFragment newInstance() {
        Bundle args = new Bundle();
        VideoFragment fragment = new VideoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video, container, false);
    }

    protected int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 38;//38大部分是这样的
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        builder = new LoadingDialog.Builder(getContext(), getString(R.string.loading));
        dialog = builder.create();
        dialog.show();
        titleRl = (RelativeLayout) getActivity().findViewById(R.id.ll);
        networkErrorMainRl = (RelativeLayout) getActivity().findViewById(R.id.rl_network_error_main);
        TextView tv_retry = (TextView) getActivity().findViewById(R.id.tv_retry);
        tv_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initTab();
            }
        });
        titleRl.post(new Runnable() {
            @Override
            public void run() {
                int height = titleRl.getBottom() - titleRl.getTop();
                SysApplication.getInstance().setTitleHeight(height);
                SysApplication.getInstance().setStateHeight(getStatusBarHeight());
                Log.d("height", "height: " + height + ";statusHeight:" + getStatusBarHeight());
            }
        });
        initTab();
    }

    /**
     * 初始化feed数据，配置其滑动事件。
     */
    void initRecycleViewPager(List<Tab.DataBean.VideoTabsBean> videoTabsBeen) {
        //通过Tablayout设置viewpager
        FeedViewPagerFragmentAdapter adapter = new FeedViewPagerFragmentAdapter(getChildFragmentManager(), null, videoTabsBeen);
        adapter.setViewpagerType(FeedViewPagerFragmentAdapter.VIEWPAGER_TYPE.VIDEO_VIEWPAGER);
        ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.mviewPager);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.mtab);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentPosition = tab.getPosition();
                if (videoTabsBeens.size() > 0 && videoTabsBeens.get(currentPosition) != null) {
                    EventLogger.logEvent(EventConsts.VideoFeedShow, EventConsts.tid, videoTabsBeens.get(currentPosition).getTid());
                    EventLogger.logEvent(EventConsts.HomeFeedShow, EventConsts.tid, videoTabsBeens.get(currentPosition).getTid());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //如果是阿拉伯语，则显示最后一个
        if (LanguageUtil.isALB()) {
            viewPager.setCurrentItem(tabLayout.getTabCount());
        }
    }


    /**
     * 网络初始化tab标签
     */
    void initTab() {
        ServiceGenerator.createServcie(getContext(), ApiService.class).AppCfg().enqueue(new Callback<Tab>() {
            @Override
            public void onResponse(Call<Tab> call, Response<Tab> response) {
                Tab tab = response.body();
                if (tab == null) {
                    dialog.dismiss();
                    return;
                }
                if (tab.getCode() == Constants.REQUEST_SUCCESS) {
                    videoTabsBeens = tab.getData().getVideo_tabs();
                    if (LanguageUtil.isALB()) {//如果是阿拉伯，反转list数据
                        Collections.reverse(videoTabsBeens);
                    }
                    initRecycleViewPager(videoTabsBeens);
                    networkErrorMainRl.setVisibility(View.GONE);
                } else {
                    networkErrorMainRl.setVisibility(View.VISIBLE);
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<Tab> call, Throwable t) {
                networkErrorMainRl.setVisibility(View.VISIBLE);
                dialog.dismiss();
                Log.d("VideoFragment", "onFailure: " + t.getMessage());
            }
        });
    }

}
