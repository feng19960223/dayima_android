package com.taiqudong.android.enayeh.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.taiqudong.android.enayeh.application.retrofit.bean.Tab;
import com.taiqudong.android.enayeh.fragment.MainFeedFragment;
import com.taiqudong.android.enayeh.fragment.VideoFeedFragment;

import java.util.List;

/**
 * Created by tangxianming on 2017/8/17.
 */

public class FeedViewPagerFragmentAdapter extends FragmentPagerAdapter {
    List<Tab.DataBean.ArticleTabsBean> articleTabsBeens;
    List<Tab.DataBean.VideoTabsBean> videoTabsBeens;
    int viewpagerType = -1;

    public interface VIEWPAGER_TYPE {
        int MAIN_VIEWPAGER = 0;
        int VIDEO_VIEWPAGER = 1;
    }

    public void setViewpagerType(int viewpagerType) {
        this.viewpagerType = viewpagerType;
    }

    public FeedViewPagerFragmentAdapter(FragmentManager fm, List<Tab.DataBean.ArticleTabsBean> articleTabsBeens, List<Tab.DataBean.VideoTabsBean> videoTabsBeens) {
        super(fm);
        this.articleTabsBeens = articleTabsBeens;
        this.videoTabsBeens = videoTabsBeens;
    }

    @Override
    public Fragment getItem(int position) {
        if (viewpagerType == VIEWPAGER_TYPE.MAIN_VIEWPAGER) {
            return MainFeedFragment.newInstance(articleTabsBeens.get(position));
        } else if (viewpagerType == VIEWPAGER_TYPE.VIDEO_VIEWPAGER) {
            return VideoFeedFragment.newInstance(videoTabsBeens.get(position));
        } else {
            throw new UnsupportedOperationException("viewpager's type should be config.");
        }
    }

    @Override
    public int getCount() {
        if (viewpagerType == VIEWPAGER_TYPE.MAIN_VIEWPAGER) {
            return articleTabsBeens.size();
        } else if (viewpagerType == VIEWPAGER_TYPE.VIDEO_VIEWPAGER) {
            return videoTabsBeens.size();
        } else {
            throw new UnsupportedOperationException("viewpager's type should be config.");
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
            if (viewpagerType == VIEWPAGER_TYPE.MAIN_VIEWPAGER) {
                return articleTabsBeens.get(position).getName();
            } else if (viewpagerType == VIEWPAGER_TYPE.VIDEO_VIEWPAGER) {
                return videoTabsBeens.get(position).getName();
            } else {
                throw new UnsupportedOperationException("viewpager's type should be config.");
            }
    }
}
