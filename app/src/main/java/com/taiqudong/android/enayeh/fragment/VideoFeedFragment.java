package com.taiqudong.android.enayeh.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.jcodecraeer.xrecyclerview.ArrowRefreshHeader;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.taiqudong.android.ad.AdConfig;
import com.taiqudong.android.ad.GlobalConfig;
import com.taiqudong.android.ad.stats.EventConsts;
import com.taiqudong.android.ad.stats.EventLogger;
import com.taiqudong.android.enayeh.R;
import com.taiqudong.android.enayeh.activity.FeedVideoActivity;
import com.taiqudong.android.enayeh.activity.FeedWebActivity;
import com.taiqudong.android.enayeh.adapter.FeedAdapter;
import com.taiqudong.android.enayeh.application.ClientSideFactory;
import com.taiqudong.android.enayeh.application.SysApplication;
import com.taiqudong.android.enayeh.application.retrofit.ServiceGenerator;
import com.taiqudong.android.enayeh.application.retrofit.api.ApiService;
import com.taiqudong.android.enayeh.application.retrofit.bean.ContentList;
import com.taiqudong.android.enayeh.application.retrofit.bean.Tab;
import com.taiqudong.android.enayeh.bean.Feed;
import com.taiqudong.android.enayeh.bean.NotifyEvent;
import com.taiqudong.android.enayeh.utils.Constants;
import com.taiqudong.android.enayeh.utils.DataUtil;
import com.taiqudong.android.enayeh.utils.DialogUtil;
import com.taiqudong.android.enayeh.utils.NetUtil;
import com.taiqudong.android.enayeh.utils.SqliteUtil;
import com.taiqudong.android.enayeh.view.FeedDataItemView;
import com.taiqudong.android.enayeh.view.SlowRecycleView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by taiqudong on 2017/8/18.
 * 展示信息流的fragment.
 */

public class VideoFeedFragment extends Fragment {
    private SlowRecycleView rv_feed;//Feed
    private ProgressBar pb_main;
    private ArrayList<Feed> rowsBeans = new ArrayList<>();
    public static String lastID = "0";
    FeedAdapter feedAdapter;
    int loadState = -1;
    YouTubePlayerSupportFragment fg_video;
    FrameLayout fl;
    RelativeLayout rootRl;
    Tab.DataBean.VideoTabsBean videoTabsBean;
    String nextToken;
    boolean isInit = true;
    RelativeLayout netWorkRl;
    YouTubePlayer mYouTubePlayer;
    AdConfig config = null;
    FrameLayout fl_adcontainer;
    private static final String TAG = "VideoFeedFragment";

    private interface LoadState {
        int REFRESH = 0;
        int LOAD_MORE = 1;
    }

    public static VideoFeedFragment newInstance(Tab.DataBean.VideoTabsBean videoTabsBean) {
        Bundle args = new Bundle();
        args.putSerializable(Constants.VIDEO_TABS_BEAN, (Serializable) videoTabsBean);
        VideoFeedFragment fragment = new VideoFeedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_video_feed, container, false);
        netWorkRl = (RelativeLayout) rootView.findViewById(R.id.rl_network_error);
        TextView retryTv = (TextView) rootView.findViewById(R.id.tv_retry);
        retryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initFeed();
            }
        });
        EventBus.getDefault().register(this);
        Log.d("videofragment", "onCreateView");
        videoTabsBean = (Tab.DataBean.VideoTabsBean) getArguments().get(Constants.VIDEO_TABS_BEAN);
        rv_feed = (SlowRecycleView) rootView.findViewById(R.id.rv_feed);
        rv_feed.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_feed.setLoadingMoreEnabled(true);
        rv_feed.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        pb_main = (ProgressBar) rootView.findViewById(R.id.pb_main);
        fl = new FrameLayout(getContext());
        fl.setId(R.id.fl_video);
        rootRl = (RelativeLayout) rootView.findViewById(R.id.rl_root);
        pb_main.setVisibility(View.GONE);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rv_feed.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                loadState = LoadState.REFRESH;
                queryFeed();
            }

            @Override
            public void onLoadMore() {
                // load more data here
                loadState = LoadState.LOAD_MORE;
                queryFeed();
            }
        });
        //TODO 这里根据界面展示广告。
        if (GlobalConfig.getInstance().getAllAdConfigs() != null) {
            config = GlobalConfig.getInstance().getAdConfig("Backhome");
        }
        feedAdapter = new FeedAdapter(rowsBeans, getContext());
        rv_feed.setAdapter(feedAdapter);
        feedAdapter.setOnItemClickListener(new FeedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                Bundle bundle = new Bundle();
                bundle.putString(EventConsts.ACTION, EventConsts.click);
                bundle.putString(EventConsts.tid, videoTabsBean.getTid());
                bundle.putString(EventConsts.index, String.valueOf(rowsBeans.get(position).getIndex()));
                EventLogger.logEvent(EventConsts.DetailVideoShow, bundle);
                Feed feed = rowsBeans.get(position);
                if (feed.getType() == FeedAdapter.ITEM_TYEP.TYPE_ITEM_VIEW_ONE_PICTURE.ordinal() || feed.getType() == FeedAdapter.ITEM_TYEP.TYPE_ITEM_VIEW_THREE_PICTURE.ordinal()) {
                    getActivity().startActivity(FeedWebActivity.newIntent(getContext(), feed, FeedWebActivity.LastViewType.MAIN_ACTIVITY));
                } else if (feed.getType() == FeedAdapter.ITEM_TYEP.TYPE_ITEM_VIEW_VIDEO.ordinal()) {
                    getActivity().startActivity(FeedVideoActivity.newIntent(getContext(), feed, FeedVideoActivity.LastViewType.MAIN_ACTIVITY));
                } else {
                    //TODO 当是广告时
                }
                //销毁播放器
                if (fl.getParent() != null) {
                    ((ViewGroup) fl.getParent()).removeView(fl);
                }
                playReset();
            }
        });
        feedAdapter.setOnPlayClickListener(new FeedAdapter.OnPlayClickListener() {
            @Override
            public void onPlayClick(final int position) {
                //设置视频播放的逻辑
                if (NetUtil.isWifi(getContext())) {//wifi
                    playVideo(position);
                } else {//非wifi
                    if (SysApplication.isNoWiFiVedioPlay == false) {//没同意过
                        DialogUtil.showDialog(getContext(), getString(R.string.wifiContent), new DialogUtil.OnDialogUtilListener() {
                            @Override
                            public void onDialogUtil_YES() {//同意
                                SysApplication.isNoWiFiVedioPlay = true;
                                playVideo(position);
                            }

                            @Override
                            public void onDialogUtil_CANCEL() {//不同意
                            }
                        });
                    } else {//同意过
                        playVideo(position);
                    }
                }
            }
        });
        feedAdapter.setOnMoreClickListener(new FeedAdapter.OnMoreClickListener() {
            @Override
            public void onMoreClick(int position, View view) {
                //                feedAdapter.showMoreDialog(getContext(), rv_feed, position);
                feedAdapter.showMorePopupWindows(getContext(), rv_feed, position, view);
            }
        });
        rv_feed.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (((fl.getY() - dy)) != 0) {
                    fl.setY(fl.getY() - dy);
                }
            }
        });

        rv_feed.getXRecyclerViewArrowRefreshHeaderView().setOnArrowRefreshHeaderVisibleHeight(new ArrowRefreshHeader.OnArrowRefreshHeaderVisibleHeight() {
            @Override
            public void visibleHeight(int height) {
                fl.setY(height + flY);
            }
        });
        initFeed();
    }

    void playVideo(int position) {
        //设置视频播放的逻辑
        fl.setVisibility(View.VISIBLE);//显示播放器控件
        Feed feed = rowsBeans.get(position);
        int itemType = feed.getType();
        if (itemType == FeedAdapter.ITEM_TYEP.TYPE_ITEM_VIEW_VIDEO.ordinal()) {
            View view = rv_feed.getLayoutManager().findViewByPosition(position + 1);
            if (view == null) {
                Log.d("translation", "onItemClick: " + null);
                return;
            }
            CardView cv_video = (CardView) view.findViewById(R.id.cv_video);
            int width = cv_video.getRight() - cv_video.getLeft();
            int height = cv_video.getBottom() - cv_video.getTop();
            int[] location = new int[2];
            cv_video.getLocationOnScreen(location);//获取绝对坐标
            float x = location[0];
            int allTitleHeight = SysApplication.getInstance().getTitleHeight();//标题和状态栏的高度
            float y = location[1] - allTitleHeight;
            showVideo(width, height, x, y, feed.getVideoUrl(), position);
            Log.d("onplay", "onPlayClick: " + feed.getVideoUrl());
        }
    }


    float flY = 0;

    /**
     * 显示视频视图
     */
    void showVideo(int width, int height, float x, float y, final String url, final int position) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        fl.setLayoutParams(params);
        if (fl.getParent() != null) {
            ((ViewGroup) fl.getParent()).removeView(fl);
        }
        rootRl.addView(fl);
        fl.setX(x);
        fl.setY(y);
        flY = fl.getY();
        FragmentManager manager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        fg_video = YouTubePlayerSupportFragment.newInstance();
        transaction.replace(R.id.fl_video, fg_video);
        transaction.commitAllowingStateLoss();
        fg_video.initialize(ClientSideFactory.GOOGLE_YOUTUBE_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo(url);
                mYouTubePlayer = youTubePlayer;
                mYouTubePlayer.setShowFullscreenButton(false);
                Log.d("translation", "onInitializationSuccess");
                youTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                    @Override
                    public void onLoading() {

                    }

                    @Override
                    public void onLoaded(String s) {

                    }

                    @Override
                    public void onAdStarted() {

                    }

                    @Override
                    public void onVideoStarted() {
                        Bundle bundle = new Bundle();
                        bundle.putString(EventConsts.ACTION, EventConsts.click);
                        bundle.putString(EventConsts.tid, videoTabsBean.getTid());
                        bundle.putString(EventConsts.index, String.valueOf(rowsBeans.get(position).getIndex()));
                        EventLogger.logEvent(EventConsts.VideoPlaySuccessfully, bundle);
                    }

                    @Override
                    public void onVideoEnded() {

                    }

                    @Override
                    public void onError(YouTubePlayer.ErrorReason errorReason) {
                        Log.d("youtubeerro", "onError: " + errorReason.toString());
                    }
                });
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.d("youtubeerro", "onError: " + youTubeInitializationResult.isUserRecoverableError());
                if (youTubeInitializationResult.isUserRecoverableError()) {
                    //                    return;
                    youTubeInitializationResult.getErrorDialog(getActivity(), youTubeInitializationResult.ordinal()).show();
                    fl.setVisibility(View.GONE);
                }

                //                SysApplication.getInstance().setYouTubeInitializationResult(youTubeInitializationResult);
                //                getActivity().runOnUiThread(new Runnable() {
                //                    @Override
                //                    public void run() {
                //                        fl.setVisibility(View.GONE);
                //                        if (SysApplication.getInstance().getYouTubeInitializationResult() != null) {
                //                            SysApplication.getInstance().getYouTubeInitializationResult().getErrorDialog(getActivity(), SysApplication.getInstance().getYouTubeInitializationResult().ordinal()).show();
                //                        }
                //                    }
                //                });
            }
        });

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        lastID = "0";
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Feed feed) {
        //根据网络更新本地数据
        for (Feed f : rowsBeans) {
            if (f.getId().equals(feed.getId())) {
                f.setSave(feed.getSave());
            }
        }
        feedAdapter.notifyDataSetChanged();
    }


    @Subscribe
    public void destroy(NotifyEvent e) {
        playReset();
    }


    void refreshLocalCache() {
        ServiceGenerator.createServcie(getContext(), ApiService.class).contentList(videoTabsBean.getTid(), null, "pull").enqueue(new Callback<ContentList>() {
            @Override
            public void onResponse(Call<ContentList> call, Response<ContentList> response) {
                ContentList contentList = response.body();
                if (contentList == null) {
                    return;
                }
                ArrayList<Feed> feedTemps = new ArrayList<Feed>();
                List<ContentList.DataBean.RowsBean> rows = contentList.getData().getRows();
                if (rows.size() == 0) {
                    return;
                }
                String localToken = contentList.getData().getNextToken();
                //转换数据
                for (ContentList.DataBean.RowsBean rowsBean : rows) {
                    Feed feed = new Feed();
                    if (loadState == LoadState.REFRESH) {
                        if (rowsBeans.size() != 0) {
                            feed.setIndex(rowsBeans.get(0).getIndex() - 1);
                        }
                    } else {
                        if (rowsBeans.size() > 0) {
                            feed.setIndex(rowsBeans.get(rowsBeans.size() - 1).getIndex() + 1);
                        }
                    }
                    feed.setIntro(rowsBean.getIntro());
                    feed.setTotalType(videoTabsBean.getTid());
                    feed.setNextToken(localToken);
                    feed.setId(rowsBean.getId());
                    feed.setCtg(rowsBean.getCtg());
                    Log.d("initFeedffffff", "initFeed1: " + feed.getCtg());
                    feed.setFeedType(rowsBean.getType());
                    feed.setSave(rowsBean.isIsMarked());
                    feed.setDateTime(rowsBean.getPublish_time());
                    feed.setShareUrl(rowsBean.getShare_url());
                    if (DataUtil.getFeedType(rowsBean) == FeedAdapter.ITEM_TYEP.TYPE_ITEM_VIEW_ONE_PICTURE.ordinal()) {
                        feed.setType(FeedDataItemView.TYPE_ITEM_VIEW_ONE_PICTURE);
                        feed.setAuthor(rowsBean.getSource());
                        feed.setCommitCount(rowsBean.getCommentCount());
                        feed.setOnePicUrl(rowsBean.getImage().get(0));
                        feed.setTitle(rowsBean.getTitle());
                        feed.setUrl(rowsBean.getUrl());
                    } else if (DataUtil.getFeedType(rowsBean) == FeedAdapter.ITEM_TYEP.TYPE_ITEM_VIEW_THREE_PICTURE.ordinal()) {
                        feed.setType(FeedDataItemView.TYPE_ITEM_VIEW_THREE_PICTURE);
                        feed.setAuthor(rowsBean.getSource());
                        feed.setCommitCount(rowsBean.getCommentCount());
                        feed.setThreePicUrl1(rowsBean.getImage().get(0));
                        feed.setThreePicUrl2(rowsBean.getImage().get(1));
                        feed.setThreePicUrl3(rowsBean.getImage().get(2));
                        feed.setTitle(rowsBean.getTitle());
                        feed.setUrl(rowsBean.getUrl());
                    } else if (DataUtil.getFeedType(rowsBean) == FeedAdapter.ITEM_TYEP.TYPE_ITEM_VIEW_VIDEO.ordinal()) {
                        feed.setType(FeedDataItemView.TYPE_ITEM_VIEW_VIDEO);
                        feed.setAuthor(rowsBean.getSource());
                        feed.setCommitCount(rowsBean.getCommentCount());
                        feed.setCanLoadVideo(true);
                        feed.setVideoUrl(rowsBean.getTarget_id());
                        feed.setTitle(rowsBean.getTitle());
                        feed.setCoverUrl(rowsBean.getCover());
                    }
                    feedTemps.add(feed);
                }
                //TODO 插入广告配置内容
                List<ContentList.DataBean.ListAdBean> listAdBeens = contentList.getData().getListAd();
                Log.d(TAG, "listAdBeens is:" + listAdBeens);
                if (listAdBeens != null) {
                    Log.d(TAG, "listAdBeens size is:" + listAdBeens.size());
                    for (ContentList.DataBean.ListAdBean bean : listAdBeens) {
                        Feed feed = new Feed();
                        feed.setNextToken(localToken);
                        feed.setId(String.valueOf(System.currentTimeMillis()));
                        feed.setType(FeedDataItemView.TYPE_ITEM_VIEW_AD);
                        feed.setTotalType(videoTabsBean.getTid());
                        AdConfig adConfig = new AdConfig();
                        adConfig.setPlaceId(bean.getPlaceId());
                        adConfig.setType(bean.getType());
                        adConfig.setStep(bean.getStep());
                        List<AdConfig.Source> sources = new ArrayList<AdConfig.Source>();
                        for (ContentList.DataBean.ListAdBean.SourcesBeanX sourcesBeanX : bean.getSources()) {
                            AdConfig.Source source = new AdConfig.Source();
                            source.setAdKey(sourcesBeanX.getAdKey());
                            source.setAdType(sourcesBeanX.getAdType());
                            sources.add(source);
                        }
                        adConfig.setSources(sources);
                        feed.setAdConfig(adConfig);
                        //TODO 广告插入的位置计算
                        if (feedTemps.size() > bean.getIdx()) {
                            feedTemps.add(bean.getIdx(), feed);
                        }
                        //TODO 将配置文件加入全局内存
                        Map<String, AdConfig> map = null;
                        if (GlobalConfig.getInstance().getAllAdConfigs() != null) {//说明登录的时候配置成功了
                            map = GlobalConfig.getInstance().getAllAdConfigs();
                        } else {
                            map = new HashMap<>();
                        }
                        map.put(bean.getPlaceId(), adConfig);
                        GlobalConfig.getInstance().setAllAdConfigs(map);
                    }
                }
                //如果本地无数据的话就增加数据
                if (SqliteUtil.getInstance(getContext()).isEmpty(videoTabsBean.getTid())) {
                    SqliteUtil.getInstance(getContext()).insertFeedBatch(feedTemps);
                } else {
                    //更新相应的数据
                    SqliteUtil.getInstance(getContext()).deleteFeedBatch(videoTabsBean.getTid());
                    SqliteUtil.getInstance(getContext()).insertFeedBatch(feedTemps);
                }
            }

            @Override
            public void onFailure(Call<ContentList> call, Throwable t) {

            }
        });
    }


    /**
     * 刷新加载列表数据
     */
    void queryFeed() {
        String action;
        if (loadState == LoadState.REFRESH) {
            action = "pull";
        } else {
            action = "scroll";
        }
        ServiceGenerator.createServcie(getContext(), ApiService.class).contentList(videoTabsBean.getTid(), nextToken, action).enqueue(new Callback<ContentList>() {
            @Override
            public void onResponse(Call<ContentList> call, Response<ContentList> response) {
                ContentList contentList = response.body();
                //                if (isInit) {
                //                    rowsBeans.clear();
                //                }
                //                isInit = false;//不是数据库初始化
                if (contentList == null) {
                    if (loadState == LoadState.REFRESH) {
                        rv_feed.refreshComplete();
                    } else {
                        rv_feed.loadMoreComplete();
                    }
                    return;
                }
                List<ContentList.DataBean.RowsBean> rows = contentList.getData().getRows();
                if (rows.size() == 0) {
                    return;
                }
                String tempNextToken = nextToken;
                nextToken = contentList.getData().getNextToken();
                ArrayList<Feed> feedTemps = new ArrayList<Feed>();
                //转换数据
                for (ContentList.DataBean.RowsBean rowsBean : rows) {
                    Feed feed = new Feed();
                    if (loadState == VideoFeedFragment.LoadState.REFRESH) {
                        if (rowsBeans.size() != 0) {
                            feed.setIndex(rowsBeans.get(0).getIndex() - 1);
                        }
                    } else {
                        if (rowsBeans.size() > 0) {
                            feed.setIndex(rowsBeans.get(rowsBeans.size() - 1).getIndex() + 1);
                        }
                    }
                    feed.setId(rowsBean.getId());
                    feed.setTotalType(videoTabsBean.getTid());
                    feed.setCtg(rowsBean.getCtg());
                    feed.setFeedType(rowsBean.getType());
                    feed.setIntro(rowsBean.getIntro());
                    feed.setNextToken(nextToken);
                    feed.setSave(rowsBean.isIsMarked());
                    feed.setDateTime(rowsBean.getPublish_time());
                    feed.setShareUrl(rowsBean.getShare_url());
                    if (DataUtil.getFeedType(rowsBean) == FeedAdapter.ITEM_TYEP.TYPE_ITEM_VIEW_ONE_PICTURE.ordinal()) {
                        feed.setType(FeedDataItemView.TYPE_ITEM_VIEW_ONE_PICTURE);
                        feed.setAuthor(rowsBean.getSource());
                        feed.setCommitCount(rowsBean.getCommentCount());
                        feed.setOnePicUrl(rowsBean.getImage().get(0));
                        feed.setTitle(rowsBean.getTitle());
                        feed.setUrl(rowsBean.getUrl());
                    } else if (DataUtil.getFeedType(rowsBean) == FeedAdapter.ITEM_TYEP.TYPE_ITEM_VIEW_THREE_PICTURE.ordinal()) {
                        feed.setType(FeedDataItemView.TYPE_ITEM_VIEW_THREE_PICTURE);
                        feed.setAuthor(rowsBean.getSource());
                        feed.setCommitCount(rowsBean.getCommentCount());
                        feed.setThreePicUrl1(rowsBean.getImage().get(0));
                        feed.setThreePicUrl2(rowsBean.getImage().get(1));
                        feed.setThreePicUrl3(rowsBean.getImage().get(2));
                        feed.setTitle(rowsBean.getTitle());
                        feed.setUrl(rowsBean.getUrl());
                    } else if (DataUtil.getFeedType(rowsBean) == FeedAdapter.ITEM_TYEP.TYPE_ITEM_VIEW_VIDEO.ordinal()) {
                        feed.setType(FeedDataItemView.TYPE_ITEM_VIEW_VIDEO);
                        feed.setAuthor(rowsBean.getSource());
                        feed.setCommitCount(rowsBean.getCommentCount());
                        feed.setCanLoadVideo(true);
                        feed.setVideoUrl(rowsBean.getTarget_id());
                        feed.setTitle(rowsBean.getTitle());
                        feed.setCoverUrl(rowsBean.getCover());
                    }
                    feedTemps.add(feed);
                }
                //TODO 插入广告配置内容
                List<ContentList.DataBean.ListAdBean> listAdBeens = contentList.getData().getListAd();
                Log.d(TAG, "listAdBeens is:" + listAdBeens);
                if (listAdBeens != null) {
                    Log.d(TAG, "listAdBeens size is:" + listAdBeens.size());
                    for (ContentList.DataBean.ListAdBean bean : listAdBeens) {
                        Feed feed = new Feed();
                        feed.setId(String.valueOf(System.currentTimeMillis()));//其实是无效id，这里是为了和sqlite中id字段是主键适配。
                        feed.setType(FeedDataItemView.TYPE_ITEM_VIEW_AD);
                        feed.setNextToken(nextToken);
                        feed.setTotalType(videoTabsBean.getTid());
                        AdConfig adConfig = new AdConfig();
                        adConfig.setPlaceId(bean.getPlaceId());
                        adConfig.setType(bean.getType());
                        adConfig.setStep(bean.getStep());
                        List<AdConfig.Source> sources = new ArrayList<AdConfig.Source>();
                        for (ContentList.DataBean.ListAdBean.SourcesBeanX sourcesBeanX : bean.getSources()) {
                            AdConfig.Source source = new AdConfig.Source();
                            source.setAdKey(sourcesBeanX.getAdKey());
                            source.setAdType(sourcesBeanX.getAdType());
                            sources.add(source);
                        }
                        adConfig.setSources(sources);
                        feed.setAdConfig(adConfig);
                        //TODO 广告插入的位置计算
                        if (feedTemps.size() > bean.getIdx()) {
                            feedTemps.add(bean.getIdx(), feed);
                        }
                        //TODO 将配置文件加入全局内存
                        Map<String, AdConfig> map = null;
                        if (GlobalConfig.getInstance().getAllAdConfigs() != null) {//说明登录的时候配置成功了
                            map = GlobalConfig.getInstance().getAllAdConfigs();
                        } else {
                            map = new HashMap<>();
                        }
                        map.put(bean.getPlaceId(), adConfig);
                        GlobalConfig.getInstance().setAllAdConfigs(map);
                    }
                }
                if (loadState == VideoFeedFragment.LoadState.REFRESH) {
                    for (int i = 0; i < feedTemps.size(); i++) {
                        Feed feed = feedTemps.get(i);
                        if (rowsBeans.size() == 0) {
                            feed.setIndex(0 - feedTemps.size() + i + 1);
                        } else {
                            feed.setIndex(rowsBeans.get(0).getIndex() - feedTemps.size() + i);
                        }
                    }
                    rowsBeans.addAll(0, feedTemps);
                } else {
                    for (Feed feed : feedTemps) {
                        if (rowsBeans.size() == 0) {
                            feed.setIndex(0);
                        } else {
                            feed.setIndex(rowsBeans.get(rowsBeans.size() - 1).getIndex() + 1);
                        }
                        rowsBeans.add(feed);
                    }
                }
                feedAdapter.notifyDataSetChanged();
                if (loadState == LoadState.REFRESH) {
                    Bundle bundle = new Bundle();
                    bundle.putString(EventConsts.ACTION, EventConsts.REFRESH);
                    bundle.putString(EventConsts.tid, videoTabsBean.getTid());
                    EventLogger.logEvent(EventConsts.TurnPageVideo, bundle);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString(EventConsts.ACTION, EventConsts.LOADMORE);
                    bundle.putString(EventConsts.tid, videoTabsBean.getTid());
                    EventLogger.logEvent(EventConsts.TurnPageVideo, bundle);
                }

                if (loadState == LoadState.REFRESH && tempNextToken == null) {
                    //如果本地无数据的话就增加数据
                    if (SqliteUtil.getInstance(getContext()).isEmpty(videoTabsBean.getTid())) {
                        SqliteUtil.getInstance(getContext()).insertFeedBatch(feedTemps);
                    } else {
                        //更新相应的数据
                        SqliteUtil.getInstance(getContext()).deleteFeedBatch(videoTabsBean.getTid());
                        SqliteUtil.getInstance(getContext()).insertFeedBatch(feedTemps);
                    }
                }

                if (loadState == LoadState.REFRESH) {
                    rv_feed.refreshComplete();
                } else {
                    rv_feed.loadMoreComplete();
                }
                if (rowsBeans.size() != 0) {
                    netWorkRl.setVisibility(View.GONE);
                } else {

                }
                //销毁播放器
                if (fl.getParent() != null) {
                    ((ViewGroup) fl.getParent()).removeView(fl);
                }
                Log.d("videoonResponse", "onResponse: " + rowsBeans.toString());
            }

            @Override
            public void onFailure(Call<ContentList> call, Throwable t) {
                if (rowsBeans.size() == 0) {
                    netWorkRl.setVisibility(View.VISIBLE);
                } else {
                    netWorkRl.setVisibility(View.GONE);
                }
                if (loadState == LoadState.REFRESH) {
                    rv_feed.refreshComplete();
                } else {
                    rv_feed.loadMoreComplete();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        if (mYouTubePlayer != null) {
            mYouTubePlayer.release();
        }
        Log.d("videofragment", "onDestroyView");
    }

    /**
     * 初始化列表数据
     */
    void initFeed() {
        isInit = true;
        rowsBeans.clear();
        if (SqliteUtil.getInstance(getContext()).isEmpty(videoTabsBean.getTid())) {
            rv_feed.refresh();
        } else {
            List<Feed> feeds = SqliteUtil.getInstance(getContext()).obtainFeedAll(videoTabsBean.getTid());
            for (Feed feed : feeds) {
                nextToken = feed.getNextToken();
                if (feed.getType() != FeedDataItemView.TYPE_ITEM_VIEW_AD) {
                    continue;
                }
                //将配置文件加入全局内存
                Map<String, AdConfig> map = null;
                if (GlobalConfig.getInstance().getAllAdConfigs() != null) {//说明登录的时候配置成功了
                    map = GlobalConfig.getInstance().getAllAdConfigs();
                } else {
                    map = new HashMap<>();
                }
                AdConfig adConfig = feed.getAdConfig();
                map.put(adConfig.getPlaceId(), adConfig);
                GlobalConfig.getInstance().setAllAdConfigs(map);
            }
            rowsBeans.addAll(feeds);
            Log.d("qqqqqqq", "initFeed: " + rowsBeans.toString());
            feedAdapter.notifyDataSetChanged();
            refreshLocalCache();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //销毁播放器
        playReset();
    }

    /**
     * 播放重制
     */
    void playReset() {
        fl.setVisibility(View.GONE);
        if (fl.getParent() != null) {
            ((ViewGroup) fl.getParent()).removeView(fl);
        }
        if (mYouTubePlayer != null) {
            mYouTubePlayer.release();
        }
    }
}
