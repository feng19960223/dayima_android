package com.taiqudong.android.enayeh.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.jcodecraeer.xrecyclerview.ArrowRefreshHeader;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.taiqudong.android.enayeh.R;
import com.taiqudong.android.enayeh.adapter.CollectAndCommentFeedAdapter;
import com.taiqudong.android.enayeh.adapter.FeedAdapter;
import com.taiqudong.android.enayeh.application.AppLogic;
import com.taiqudong.android.enayeh.application.ClientSideFactory;
import com.taiqudong.android.enayeh.application.SysApplication;
import com.taiqudong.android.enayeh.application.retrofit.ServiceGenerator;
import com.taiqudong.android.enayeh.application.retrofit.api.ApiService;
import com.taiqudong.android.enayeh.application.retrofit.bean.ContentList;
import com.taiqudong.android.enayeh.bean.Feed;
import com.taiqudong.android.enayeh.utils.DataUtil;
import com.taiqudong.android.enayeh.utils.DialogUtil;
import com.taiqudong.android.enayeh.utils.LoadingDialog;
import com.taiqudong.android.enayeh.utils.NetUtil;
import com.taiqudong.android.enayeh.utils.StatusBarUtils;
import com.taiqudong.android.enayeh.view.FeedDataItemView;
import com.taiqudong.android.enayeh.view.NestedRecycleView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 我的评论
 * Created by taiqudong on 2017/8/21.
 */

public class CommentActivity extends AppCompatActivity implements View.OnClickListener, YouTubePlayer.OnFullscreenListener {
    private NestedRecycleView rv_comment;
    private LinearLayout ll_comment;
    String nextToken;
    CollectAndCommentFeedAdapter feedAdapter;
    private ArrayList<Feed> rowsBeans = new ArrayList<>();
    LoadingDialog.Builder builder;
    LoadingDialog dialog;

    private interface LoadState {
        int REFRESH = 0;
        int LOAD_MORE = 1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        SysApplication.getInstance().addActivit(this);
        builder = new LoadingDialog.Builder(this, getString(R.string.loading));
        dialog = builder.create();
        dialog.show();
        final Toolbar titleRl = (Toolbar) findViewById(R.id.toolbar);
        titleRl.post(new Runnable() {
            @Override
            public void run() {
                int height = titleRl.getBottom() - titleRl.getTop();
                SysApplication.getInstance().setTitleHeight(height);
            }
        });
        StatusBarUtils.statusbar(this);
        initView();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (fg_video != null) {
            fg_video.pause();
        }
        fl.setVisibility(View.GONE);
    }

    private void initView() {
        fl = new FrameLayout(this);
        fl_root = (FrameLayout) findViewById(R.id.fl_root);
        ll_comment = (LinearLayout) findViewById(R.id.ll_comment);
        rv_comment = (NestedRecycleView) findViewById(R.id.rv_comment);
        rv_comment.setLayoutManager(new LinearLayoutManager(this));
        findViewById(R.id.tv_goto).setOnClickListener(this);
        rv_comment.setLoadingMoreEnabled(true);
        rv_comment.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        feedAdapter = new CollectAndCommentFeedAdapter(rowsBeans, this);
        feedAdapter.setOnItemClickListener(new CollectAndCommentFeedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                Feed feed = rowsBeans.get(position);
                if (feed.getType() == CollectAndCommentFeedAdapter.ITEM_TYEP.TYPE_ITEM_VIEW_ONE_PICTURE.ordinal() || feed.getType() == CollectAndCommentFeedAdapter.ITEM_TYEP.TYPE_ITEM_VIEW_THREE_PICTURE.ordinal()) {
                    startActivity(FeedWebActivity.newIntent(CommentActivity.this, feed, FeedWebActivity.LastViewType.OTHER));
                } else {
                    startActivity(FeedVideoActivity.newIntent(CommentActivity.this, feed, FeedVideoActivity.LastViewType.OTHER));
                }
            }
        });
        feedAdapter.setOnPlayClickListener(new CollectAndCommentFeedAdapter.OnPlayClickListener() {
            @Override
            public void onPlayClick(final int position) {
                //设置视频播放的逻辑
                if (NetUtil.isWifi(CommentActivity.this)) {//wifi
                    showVedio(position);
                } else {//非wifi
                    if (SysApplication.isNoWiFiVedioPlay == false) {//没同意过
                        DialogUtil.showDialog(CommentActivity.this, getString(R.string.wifiContent), new DialogUtil.OnDialogUtilListener() {
                            @Override
                            public void onDialogUtil_YES() {//同意
                                SysApplication.isNoWiFiVedioPlay = true;
                                showVedio(position);
                            }

                            @Override
                            public void onDialogUtil_CANCEL() {//不同意
                            }
                        });
                    } else {//同意过
                        showVedio(position);
                    }
                }
            }
        });
        rv_comment.setPullRefreshEnabled(false);
        rv_comment.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                if (fg_video != null) {
                    fg_video.pause();
                }
                fl.setVisibility(View.GONE);
                loadCommentList();
                loadState = CommentActivity.LoadState.REFRESH;
                rv_comment.refreshComplete();
            }

            @Override
            public void onLoadMore() {
                loadState = CommentActivity.LoadState.LOAD_MORE;
                loadCommentList();
            }
        });
        rv_comment.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                fl.setY(fl.getY() - dy);
            }
        });
        rv_comment.getXRecyclerViewArrowRefreshHeaderView().setOnArrowRefreshHeaderVisibleHeight(new ArrowRefreshHeader.OnArrowRefreshHeaderVisibleHeight() {
            @Override
            public void visibleHeight(int height) {
                if (height != 0) {
                    fl.setY(height + flY);
                }
            }
        });
        rv_comment.setAdapter(feedAdapter);
        loadCommentList();
    }

    private void showVedio(int position) {
        //设置视频播放的逻辑
        fl.setVisibility(View.VISIBLE);//显示播放器控件
        Feed feed = rowsBeans.get(position);
        int itemType = feed.getType();
        if (itemType == CollectAndCommentFeedAdapter.ITEM_TYEP.TYPE_ITEM_VIEW_VIDEO.ordinal()) {
            View view = rv_comment.getLayoutManager().findViewByPosition(position + 1);
            if (view == null) {
                return;
            }
            CardView fl_video = (CardView) view.findViewById(R.id.cv_video);
            int width = fl_video.getRight() - fl_video.getLeft();
            int height = fl_video.getBottom() - fl_video.getTop();
            int[] location = new int[2];
            fl_video.getLocationOnScreen(location);//获取绝对坐标

            float x = location[0];
            int allTitleHeight = SysApplication.getInstance().getTitleHeight();//标题和状态栏的高度
            float y = location[1] - allTitleHeight;
            showVideo(width, height, x, y, feed.getVideoUrl(), position);
        }
    }

    private void initListener() {
        findViewById(R.id.iv_return).setOnClickListener(this);
        findViewById(R.id.ll_comment).setOnClickListener(this);
    }


    float flY = 0;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return:
                this.finish();
                break;
            case R.id.tv_goto:
                AppLogic.getInstance().setFeedState(1);
                this.finish();
                break;
        }
    }

    int loadState = -1;
    VideoFragment fg_video;
    static FrameLayout fl;
    FragmentManager manager;
    FrameLayout fl_root;

    /**
     * 显示视频视图
     */
    void showVideo(int width, int height, float x, float y, final String url, final int position) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        fl.setLayoutParams(params);
        fl.setId(R.id.fl_video);
        if (fl.getParent() != null) {
            ((ViewGroup) fl.getParent()).removeView(fl);
        }
        fl_root.addView(fl);
        fl.setX(x);
        fl.setY(y);
        flY = fl.getY();
        manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        //        fg_video = YouTubePlayerSupportFragment.newInstance();
        fg_video = VideoFragment.newInstance();
        transaction.replace(R.id.fl_video, fg_video);
        transaction.commit();
        fg_video.setVideoId(url);
        fl.setVisibility(View.GONE);
    }


    public static final class VideoFragment extends YouTubePlayerSupportFragment
            implements YouTubePlayer.OnInitializedListener {

        private YouTubePlayer player;
        private String videoId;

        public static VideoFragment newInstance() {
            return new VideoFragment();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            initialize(ClientSideFactory.GOOGLE_YOUTUBE_KEY, this);
        }

        public void setXiao() {
            if (player != null) {
                player.setFullscreen(false);
            }
        }

        @Override
        public void onDestroy() {
            if (player != null) {
                player.release();
            }
            super.onDestroy();
        }

        public void setVideoId(String videoId) {
            if (videoId != null && !videoId.equals(this.videoId)) {
                this.videoId = videoId;
                if (player != null) {
                    player.loadVideo(videoId);
                }
            }
        }

        public void pause() {
            if (player != null) {
                player.pause();
            }
        }

        @Override
        public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean restored) {
            this.player = player;
            player.setOnFullscreenListener((CommentActivity) getActivity());
            if (!restored && videoId != null) {
                player.loadVideo(videoId);
                fl.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
            if (result.isUserRecoverableError()) {//说明此错误用户无法修复
                this.player = null;
                result.getErrorDialog(getActivity(), result.ordinal()).show();
                fl.setVisibility(View.GONE);
            }
        }

    }

    private boolean isFullscreen;

    @Override
    public void onFullscreen(boolean isFullscreen) {
        this.isFullscreen = isFullscreen;
    }

    @Override
    public void onBackPressed() {
        if (isFullscreen) {
            fg_video.setXiao();
            isFullscreen = false;
            return;
        }
        super.onBackPressed();
    }

    /**
     * 加载评论过的文章列表数据
     */
    void loadCommentList() {
        ServiceGenerator.createServcie(this, ApiService.class).commentList(nextToken).enqueue(new Callback<ContentList>() {
            @Override
            public void onResponse(Call<ContentList> call, Response<ContentList> response) {
                ContentList contentList = response.body();
                if (contentList == null) {
                    ll_comment.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                    return;
                }
                if (contentList.getData() == null) {
                    ll_comment.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                    ;
                    return;
                }
                Log.d("loadCommentList", "onResponse1: " + nextToken);
                List<ContentList.DataBean.RowsBean> rows = contentList.getData().getRows();
                if (rows.size() == 0) {
                    ll_comment.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                    return;
                }
                nextToken = contentList.getData().getNextToken();
                //转换数据

                for (ContentList.DataBean.RowsBean rowsBean : rows) {
                    Feed feed = new Feed();
                    feed.setIntro(rowsBean.getIntro());
                    feed.setId(rowsBean.getId());
                    feed.setCtg(rowsBean.getCtg());
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
                        feed.setShareUrl(rowsBean.getShare_url());
                    } else if (DataUtil.getFeedType(rowsBean) == FeedAdapter.ITEM_TYEP.TYPE_ITEM_VIEW_THREE_PICTURE.ordinal()) {
                        feed.setType(FeedDataItemView.TYPE_ITEM_VIEW_THREE_PICTURE);
                        feed.setAuthor(rowsBean.getSource());
                        feed.setCommitCount(rowsBean.getCommentCount());
                        feed.setThreePicUrl1(rowsBean.getImage().get(0));
                        feed.setThreePicUrl2(rowsBean.getImage().get(1));
                        feed.setThreePicUrl3(rowsBean.getImage().get(2));
                        feed.setTitle(rowsBean.getTitle());
                        feed.setUrl(rowsBean.getUrl());
                        feed.setShareUrl(rowsBean.getShare_url());
                    } else if (DataUtil.getFeedType(rowsBean) == FeedAdapter.ITEM_TYEP.TYPE_ITEM_VIEW_VIDEO.ordinal()) {
                        feed.setType(FeedDataItemView.TYPE_ITEM_VIEW_VIDEO);
                        feed.setAuthor(rowsBean.getSource());
                        feed.setCommitCount(rowsBean.getCommentCount());
                        feed.setCanLoadVideo(true);
                        feed.setVideoUrl(rowsBean.getTarget_id());
                        feed.setTitle(rowsBean.getTitle());
                        feed.setCoverUrl(rowsBean.getCover());
                    }
                    rowsBeans.add(feed);
                    feedAdapter.notifyDataSetChanged();
                    if (rowsBeans.size() < 1) {
                        ll_comment.setVisibility(View.VISIBLE);
                    } else {
                        ll_comment.setVisibility(View.INVISIBLE);
                    }
                }
                rv_comment.refreshComplete();
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<ContentList> call, Throwable t) {
                rv_comment.refreshComplete();
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SysApplication.getInstance().removeActivity(this);
    }
}
