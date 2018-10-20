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
import com.taiqudong.android.enayeh.view.NestedRecycleView;

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

public class MainFeedFragment extends Fragment {
    public NestedRecycleView rv_feed;//Feed
    private ArrayList<Feed> rowsBeans = new ArrayList<>();
    public static String lastID = "0";
    FeedAdapter feedAdapter;
    int loadState = -1;
    Tab.DataBean.ArticleTabsBean articleTabsBean;
    String nextToken;
    boolean isInit = true;
    RelativeLayout networkErrorRl;
    TextView retryTv;
    YouTubePlayer mYouTubePlayer;
    private static final String TAG = "MainFeedFragment";

    public static YouTubePlayerSupportFragment fg_video;
    public static FrameLayout fl;
    RelativeLayout rootRl;
    float flY = 0;


    private interface LoadState {
        int REFRESH = 0;
        int LOAD_MORE = 1;
    }

    public static MainFeedFragment newInstance(Tab.DataBean.ArticleTabsBean articleTabsBean) {
        Bundle args = new Bundle();
        args.putSerializable(Constants.ARTICLE_TABS_BEAN, (Serializable) articleTabsBean);
        MainFeedFragment fragment = new MainFeedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void Ry() {
        if (rv_feed != null) {
            rv_feed.scrollToPosition(0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_feed, container, false);
        networkErrorRl = (RelativeLayout) view.findViewById(R.id.rl_network_error);
        retryTv = (TextView) view.findViewById(R.id.tv_retry);
        retryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initFeed();
            }
        });
        EventBus.getDefault().register(this);
        articleTabsBean = (Tab.DataBean.ArticleTabsBean) getArguments().getSerializable(Constants.ARTICLE_TABS_BEAN);
        rv_feed = (NestedRecycleView) view.findViewById(R.id.rv_feed);
        rv_feed.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_feed.setLoadingMoreEnabled(true);
        rv_feed.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        fl = new FrameLayout(getContext());
        fl.setId(R.id.fl_video);
        rootRl = (RelativeLayout) view.findViewById(R.id.rl_root);
        Log.d("MainFeedFragment", "onCreateView: ");
        return view;
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
                Log.d("loadmore", "onLoadMore");
                loadState = LoadState.LOAD_MORE;
                queryFeed();
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

        feedAdapter = new FeedAdapter(rowsBeans, getContext());
        rv_feed.setAdapter(feedAdapter);
        feedAdapter.setOnItemClickListener(new FeedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Feed feed = rowsBeans.get(position);
                Bundle bundle = new Bundle();
                bundle.putString(EventConsts.ACTION, EventConsts.click);
                bundle.putString(EventConsts.tid, articleTabsBean.getTid());
                bundle.putString(EventConsts.index, String.valueOf(rowsBeans.get(position).getIndex()));
                EventLogger.logEvent(EventConsts.DetailPicShow, bundle);
                Log.d("feed_onitemclick", "onItemClick: " + feed.toString());
                if (feed.getType() == FeedAdapter.ITEM_TYEP.TYPE_ITEM_VIEW_ONE_PICTURE.ordinal() || feed.getType() == FeedAdapter.ITEM_TYEP.TYPE_ITEM_VIEW_THREE_PICTURE.ordinal()) {
                    getActivity().startActivity(FeedWebActivity.newIntent(getContext(), feed, FeedWebActivity.LastViewType.MAIN_ACTIVITY));
                } else {
                    getActivity().startActivity(FeedVideoActivity.newIntent(getContext(), feed, FeedVideoActivity.LastViewType.MAIN_ACTIVITY));
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
        initFeed();
    }

    void refreshLocalCache() {
        Log.d(TAG, "refreshLocalCache: ");
        ServiceGenerator.createServcie(getContext(), ApiService.class).contentList(articleTabsBean.getTid(), null, "pull").enqueue(new Callback<ContentList>() {
            @Override
            public void onResponse(Call<ContentList> call, Response<ContentList> response) {
                ContentList contentList = response.body();
                Log.d(TAG, "refreshLocalCache: " + contentList);
                if (contentList == null) {
                    return;
                }
                ArrayList<Feed> feedTemps = new ArrayList<Feed>();
                List<ContentList.DataBean.RowsBean> rows = contentList.getData().getRows();
                Log.d(TAG, "refreshLocalCache: " + rows.size());
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
                    feed.setTotalType(articleTabsBean.getTid());
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
                        feed.setTotalType(articleTabsBean.getTid());
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
                if (SqliteUtil.getInstance(getContext()).isEmpty(articleTabsBean.getTid())) {
                    SqliteUtil.getInstance(getContext()).insertFeedBatch(feedTemps);
                } else {
                    //更新相应的数据
                    SqliteUtil.getInstance(getContext()).deleteFeedBatch(articleTabsBean.getTid());
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
        ServiceGenerator.createServcie(getContext(), ApiService.class).contentList(articleTabsBean.getTid(), nextToken, action).enqueue(new Callback<ContentList>() {
            @Override
            public void onResponse(Call<ContentList> call, Response<ContentList> response) {
//                if (isInit) {
//                    rowsBeans.clear();
//                }
//                isInit = false;//不是首次数据库初始化界面
                ContentList contentList = response.body();
                if (contentList == null) {
                    if (loadState == LoadState.REFRESH) {
                        rv_feed.refreshComplete();
                    } else {
                        rv_feed.loadMoreComplete();
                    }
                    return;
                }
                ArrayList<Feed> feedTemps = new ArrayList<Feed>();
                List<ContentList.DataBean.RowsBean> rows = contentList.getData().getRows();
                if (rows.size() == 0) {
                    return;
                }
                String tempNextToken = nextToken;
                nextToken = contentList.getData().getNextToken();
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
                    feed.setTotalType(articleTabsBean.getTid());
                    feed.setNextToken(nextToken);
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
                        feed.setNextToken(nextToken);
                        feed.setId(String.valueOf(System.currentTimeMillis()));
                        feed.setType(FeedDataItemView.TYPE_ITEM_VIEW_AD);
                        feed.setTotalType(articleTabsBean.getTid());
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
                //销毁播放器
                if (fl.getParent() != null) {
                    ((ViewGroup) fl.getParent()).removeView(fl);
                }
                if (loadState == LoadState.REFRESH) {
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
                if (loadState == LoadState.REFRESH && tempNextToken == null) {
                    //如果本地无数据的话就增加数据
                    if (SqliteUtil.getInstance(getContext()).isEmpty(articleTabsBean.getTid())) {
                        SqliteUtil.getInstance(getContext()).insertFeedBatch(feedTemps);
                    } else {
                        //更新相应的数据
                        SqliteUtil.getInstance(getContext()).deleteFeedBatch(articleTabsBean.getTid());
                        SqliteUtil.getInstance(getContext()).insertFeedBatch(feedTemps);
                    }
                }
                if (loadState == LoadState.REFRESH) {
                    rv_feed.refreshComplete();
                    Bundle bundle = new Bundle();
                    bundle.putString(EventConsts.ACTION, EventConsts.REFRESH);
                    bundle.putString(EventConsts.tid, articleTabsBean.getTid());
                    EventLogger.logEvent(EventConsts.TurnPagePic, bundle);
                } else {
                    rv_feed.loadMoreComplete();
                    Bundle bundle = new Bundle();
                    bundle.putString(EventConsts.ACTION, EventConsts.LOADMORE);
                    bundle.putString(EventConsts.tid, articleTabsBean.getTid());
                    EventLogger.logEvent(EventConsts.TurnPagePic, bundle);
                }
                if (rowsBeans.size() != 0) {
                    networkErrorRl.setVisibility(View.GONE);
                } else {
                }
            }

            @Override
            public void onFailure(Call<ContentList> call, Throwable t) {
                if (rowsBeans.size() == 0) {
                    networkErrorRl.setVisibility(View.VISIBLE);
                } else {
                    networkErrorRl.setVisibility(View.GONE);
                }
                if (loadState == LoadState.REFRESH) {
                    rv_feed.refreshComplete();
                } else {
                    rv_feed.loadMoreComplete();
                }
            }
        });
    }

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
                if (youTubeInitializationResult.isUserRecoverableError()) {//说明此错误用户无法修复
//                    return;
                    youTubeInitializationResult.getErrorDialog(getActivity(), youTubeInitializationResult.ordinal()).show();
                    fl.setVisibility(View.GONE);
                }
//
//                SysApplication.getInstance().setYouTubeInitializationResult(youTubeInitializationResult);
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        fl.setVisibility(View.GONE);
//                        if (SysApplication.getInstance().getYouTubeInitializationResult() != null) {
//                            if (SysApplication.getInstance().getYouTubeInitializationResult().getErrorDialog(getActivity(), SysApplication.getInstance().getYouTubeInitializationResult().ordinal()) != null) {
//                                SysApplication.getInstance().getYouTubeInitializationResult().getErrorDialog(getActivity(), SysApplication.getInstance().getYouTubeInitializationResult().ordinal()).show();
//                            }
//                        }
//                    }
//                });
            }
        });

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
            // TODO: 2017/9/18 fgr 修改视频显示位置的计算
//            int allTitleHeight = SysApplication.getInstance().getTitleHeight();//标题和状态栏的高度
            float y = location[1] - MainFragment.jisuan;
//            float y = location[1] - allTitleHeight;
            showVideo(width, height, x, y, feed.getVideoUrl(), position);
        }
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
        //销毁播放器
        playReset();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        if (mYouTubePlayer != null) {
            mYouTubePlayer.release();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lastID = "0";
    }

    /**
     * 初始化列表数据
     */
    void initFeed() {
        rowsBeans.clear();
        if (SqliteUtil.getInstance(getContext()).isEmpty(articleTabsBean.getTid())) {
            rv_feed.refresh();
        } else {
            List<Feed> feeds = SqliteUtil.getInstance(getContext()).obtainFeedAll(articleTabsBean.getTid());
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
