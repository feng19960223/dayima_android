package com.taiqudong.android.enayeh.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.taiqudong.android.ad.AdConfig;
import com.taiqudong.android.ad.AdParallelLoader;
import com.taiqudong.android.ad.AdSplashView;
import com.taiqudong.android.ad.GlobalConfig;
import com.taiqudong.android.ad.SPUtil;
import com.taiqudong.android.ad.cache.GlobalCache;
import com.taiqudong.android.ad.stats.EventConsts;
import com.taiqudong.android.ad.stats.EventLogger;
import com.taiqudong.android.enayeh.R;
import com.taiqudong.android.enayeh.adapter.CommentAdapter;
import com.taiqudong.android.enayeh.adapter.FeedAdapter;
import com.taiqudong.android.enayeh.application.ClientSideFactory;
import com.taiqudong.android.enayeh.application.SysApplication;
import com.taiqudong.android.enayeh.application.retrofit.ServiceGenerator;
import com.taiqudong.android.enayeh.application.retrofit.api.ApiService;
import com.taiqudong.android.enayeh.application.retrofit.bean.Basic;
import com.taiqudong.android.enayeh.application.retrofit.bean.Comments;
import com.taiqudong.android.enayeh.application.retrofit.bean.RemoveMark;
import com.taiqudong.android.enayeh.application.retrofit.bean.VideoInfo;
import com.taiqudong.android.enayeh.bean.Comment;
import com.taiqudong.android.enayeh.bean.Feed;
import com.taiqudong.android.enayeh.utils.Constants;
import com.taiqudong.android.enayeh.utils.DataUtil;
import com.taiqudong.android.enayeh.utils.DialogUtil;
import com.taiqudong.android.enayeh.utils.LoadingDialog;
import com.taiqudong.android.enayeh.utils.NumberUtil;
import com.taiqudong.android.enayeh.utils.StatusBarUtils;
import com.taiqudong.android.enayeh.utils.ToastUtil;
import com.taiqudong.android.enayeh.view.CommentView;
import com.taiqudong.android.enayeh.view.FeedDataItemView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedVideoActivity extends AppCompatActivity {
    private static final String TAG = "FeedVideoActivity";
    private static final String INTENT_FEED = "intent_feed";
    RecyclerView commentRcv;
    CommentAdapter commentAdapter;
    List<Comment> comments = new ArrayList<>();
    private ArrayList<Feed> rowsBeans = new ArrayList<>();
    RecyclerView relatedRcv;
    FeedAdapter relatedAdapter;
    RelativeLayout backRl;
    TextView showAllTv;
    RelativeLayout becomeFirstRl;
    LinearLayout commentLl;
    Feed currentIntent;
    TextView loadMoreTv;
    String nextToken;
    private CommentView cv_comment;
    TextView titleTv;
    TextView viewTv;
    TextView likeTv;
    TextView disLikeTv;
    LinearLayout dislikeLl;
    LinearLayout likeLl;
    ImageView dislikeIv;
    ImageView likeIv;
    TextView commentCountTv;
    LoadingDialog.Builder builder;
    LoadingDialog dialog;
    FrameLayout fl;
    RelativeLayout rootRl;
    FragmentManager manager;
    ScrollView sv;
    int like;//点赞数
    int dislike;//不喜欢数
    int comment;//评论
    private static final String LAST_TYPE = "last_type";
    int lastType;

    public interface LastViewType {
        int MAIN_ACTIVITY = 0;
        int FEED_DETAIL = 1;
        int OTHER = 2;
    }


    public static Intent newIntent(Context context, Feed feed, int lastType) {
        Intent intent = new Intent(context, FeedVideoActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(INTENT_FEED, feed);
        args.putInt(LAST_TYPE, lastType);
        intent.putExtras(args);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏状态栏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_feed_video);
        //设置进入的界面类型
        currentIntent = (Feed) getIntent().getSerializableExtra(INTENT_FEED);
        lastType = getIntent().getIntExtra(LAST_TYPE, LastViewType.FEED_DETAIL);
        if (lastType != LastViewType.OTHER && currentIntent != null) {
            SPUtil.put(this, com.taiqudong.android.ad.Constants.CTG, currentIntent.getTotalType());
        }
        SysApplication.getInstance().addActivit(this);
        manager = getSupportFragmentManager();
        rootRl = (RelativeLayout) findViewById(R.id.rl_video_container);
        sv = (ScrollView) findViewById(R.id.sv);
        sv.post(new Runnable() {
            @Override
            public void run() {
                sv.scrollTo(0, 0);
            }
        });
        fl = new FrameLayout(this);
        builder = new LoadingDialog.Builder(this, getString(R.string.loading));
        dialog = builder.create();
        Log.d("feedvideoActivity", "onCreate: " + currentIntent);
        StatusBarUtils.statusbar(this);
        initView();
        initData();
        rootRl.post(new Runnable() {
            @Override
            public void run() {
                showVideo(currentIntent.getVideoUrl());
            }
        });
        //TODO 预加载广告到缓存
        Map<String, AdConfig> map = (Map<String, AdConfig>) SPUtil.getHashMapData(this, com.taiqudong.android.ad.Constants.SP_AD_SPLASH_CONFIG, AdConfig.class);
        if (map.size() != 0 && map.get(com.taiqudong.android.ad.Constants.AD_SPLASH_BACKHOME) != null) {
            AdConfig config = map.get(com.taiqudong.android.ad.Constants.AD_SPLASH_BACKHOME);
            if (config == null) {
                return;
            }
            Log.d(TAG, "config is not null");
            AdParallelLoader adParallelLoader = new AdParallelLoader(config);
            adParallelLoader.prefetch();
        }
    }

    private void initView() {
        commentCountTv = (TextView) findViewById(R.id.tv_comment_count);
        dislikeIv = (ImageView) findViewById(R.id.iv_dislike);
        likeIv = (ImageView) findViewById(R.id.iv_like);
        dislikeLl = (LinearLayout) findViewById(R.id.ll_dislike);
        likeLl = (LinearLayout) findViewById(R.id.ll_like);
        //不喜欢的按钮点击
        dislikeLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = (String) dislikeLl.getTag();
                Log.d("feedvideoActivity", "dislikeonClick: " + tag);
                disLikeVideo();
            }
        });
        //喜欢的按钮点击
        likeLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = (String) likeLl.getTag();
                Log.d("feedvideoActivity", "likeonClick: " + tag);
                likeVideo();
            }
        });
        disLikeTv = (TextView) findViewById(R.id.tv_dislike);
        likeTv = (TextView) findViewById(R.id.tv_like);

        viewTv = (TextView) findViewById(R.id.tv_view_count);
        titleTv = (TextView) findViewById(R.id.tv_title);
        commentRcv = (RecyclerView) findViewById(R.id.rcv_comment);
        relatedRcv = (RecyclerView) findViewById(R.id.rcv_related_video);
        backRl = (RelativeLayout) findViewById(R.id.rl_back);
        showAllTv = (TextView) findViewById(R.id.tv_showall);
        commentLl = (LinearLayout) findViewById(R.id.ll_comment);
        loadMoreTv = (TextView) findViewById(R.id.tv_loadmore);
        becomeFirstRl = (RelativeLayout) findViewById(R.id.rl_become_first);
        backRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //相关类别列表
        relatedRcv.setLayoutManager(new LinearLayoutManager(this));
        relatedAdapter = new FeedAdapter(rowsBeans, this);
        relatedRcv.setAdapter(relatedAdapter);
        relatedAdapter.setOnItemClickListener(new FeedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                startActivity(FeedVideoActivity.newIntent(FeedVideoActivity.this, rowsBeans.get(position), LastViewType.FEED_DETAIL));
            }
        });
        //评论列表
        commentAdapter = new CommentAdapter(this, comments);
        commentRcv.setLayoutManager(new LinearLayoutManager(this));
        commentRcv.setAdapter(commentAdapter);
        commentAdapter.setOnItemClickListener(new CommentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Comment comment) {
                Log.d("FeedWebActivity", "onItemClick: " + comment.toString());
            }
        });
        commentAdapter.setOnLikeClickListener(new CommentAdapter.OnLikeClickListener() {
            @Override
            public void onLikeClick(int position) {
                likeComment(position);
            }

            @Override
            public void onDisLikeClick(int position) {
                //                disLikeComment(position);
            }
        });
        //评论控件
        cv_comment = (CommentView) findViewById(R.id.cv_comment);
        cv_comment.setOnSendClickListener(new CommentView.OnSendClickListener() {
            @Override
            public void onSendClick(String content) {
                Log.d("FeedWebActivity", "onSendClick " + content);
                int loginType = (int) SPUtil.get(FeedVideoActivity.this, Constants.SP_LOGIN_TYPE, Constants.LOGIN_TYPE.NON_USER);
                if (loginType == Constants.LOGIN_TYPE.NON_USER) {

                    //弹出登录对话框
                    EventLogger.logEvent(EventConsts.DialogLoginShow);

                    DialogUtil.showDialog(FeedVideoActivity.this, getString(R.string.LoginContent), new DialogUtil.OnDialogUtilListener() {
                        @Override
                        public void onDialogUtil_YES() {
                            Intent intent = new Intent(FeedVideoActivity.this, LoginMainActivity.class);
                            intent.putExtra(EventConsts.source, EventConsts.dialog);
                            startActivity(intent);//到登录页
                        }

                        @Override
                        public void onDialogUtil_CANCEL() {

                        }
                    });
                } else {
                    postComment(content);
                }
            }
        });
        cv_comment.setCollect(currentIntent.getSave());
        cv_comment.setOnCollectClickListener(new CommentView.OnCollectClickListener() {
            @Override
            public void onCollectClick(Boolean isCollect) {
                postCollect();
                Log.d("FeedWebActivity", "onCommentClick: ");
            }
        });
        cv_comment.setOnShareClickListener(new CommentView.OnShareClickListener() {
            @Override
            public void onShareClick() {
                share();
            }
        });
        loadMoreTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMoreComment();
            }
        });
        titleTv.setText(currentIntent.getTitle());
    }

    void share() {
        String url = currentIntent.getShareUrl();
        Intent textIntent = new Intent(Intent.ACTION_SEND);
        //        textIntent.setType("text/*");
        textIntent.setType("text/plain");
        //        textIntent.setType("image/jpg");
        //        textIntent.putExtra(Intent.EXTRA_SUBJECT, url);
        //        textIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.Careforwomen));
        textIntent.putExtra(Intent.EXTRA_TEXT, url);
        Log.i(TAG, "share: url" + url);
        startActivity(Intent.createChooser(textIntent, getString(R.string.Careforwomen)));
        //分享
        EventLogger.logEvent(EventConsts.ShareSuccessfully);
    }

    private void initData() {
        initSuggestData();
        initCommentData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SysApplication.getInstance().removeActivity(this);
    }

    @Override
    public void onBackPressed() {
        if (isFullscreen) {
            isFullscreen = false;
            mYouTubePlayer.setFullscreen(isFullscreen);
            return;
        }
        int lastViewType = getIntent().getIntExtra(LAST_TYPE, 1);
        //TODO 弹出插屏广告
        Map<String, AdConfig> map = (Map<String, AdConfig>) SPUtil.getHashMapData(this, com.taiqudong.android.ad.Constants.SP_AD_SPLASH_CONFIG, AdConfig.class);
        if (map.size() != 0 && map.get(com.taiqudong.android.ad.Constants.AD_SPLASH_BACKHOME) != null && lastViewType == LastViewType.MAIN_ACTIVITY) {
            AdConfig config = map.get(com.taiqudong.android.ad.Constants.AD_SPLASH_BACKHOME);
            if (config.getSources() != null && config.getSources().size() != 0 && GlobalCache.getInstance().hasAd(com.taiqudong.android.ad.Constants.AD_SPLASH_BACKHOME, config.getSources().get(0))) {
                AdSplashView.start(this, com.taiqudong.android.ad.Constants.AD_SPLASH_BACKHOME, AdSplashView.Action.FROM_APP_TO_MAIN, MainActivity.class, null);
                Log.d(TAG, "start display splash_en view");
            } else {
                if (GlobalCache.getInstance().hasAd(com.taiqudong.android.ad.Constants.AD_SPLASH_BACKHOME, config.getSources().get(0))) {
                    Log.d(TAG, "global has ad here!");
                }
                super.onBackPressed();
                Log.d(TAG, "onBackPressed:Has not ad content!");
            }
        } else {
            super.onBackPressed();
            if (map.size() == 0) {
                Log.d(TAG, "onBackPressed:GlobalConfig.getInstance().getAllAdConfigs() is null");
            } else if (map.get(com.taiqudong.android.ad.Constants.AD_SPLASH_BACKHOME) == null) {
                Log.d(TAG, "onBackPressed:GlobalConfig.getInstance().getAllAdConfigs().get(com.taiqudong.android.ad.Constants.AD_SPLASH_BACKHOME) is null");
            } else {
                Log.d(TAG, "onBackPressed:lastViewType != LastViewType.MAIN_ACTIVITY");

            }

        }
    }

    /**
     * 初始化推荐信息
     */
    void initSuggestData() {
        ServiceGenerator.createServcie(this, ApiService.class).videoInfo(currentIntent.getId()).enqueue(new Callback<VideoInfo>() {
            @Override
            public void onResponse(Call<VideoInfo> call, Response<VideoInfo> response) {
                VideoInfo videoInfo = response.body();
                if (videoInfo == null) {
                    dialog.dismiss();
                    return;
                }
                if (videoInfo.getData() == null) {
                    dialog.dismiss();
                    return;
                }
                //设置视频详情
                commentCountTv.setText(String.valueOf(currentIntent.getCommitCount() > videoInfo.getData().getCommentCount()
                        ? currentIntent.getCommitCount() : videoInfo.getData().getCommentCount()));
                cv_comment.setTvCount(String.valueOf(
                        currentIntent.getCommitCount() > videoInfo.getData().getCommentCount()
                                ? currentIntent.getCommitCount() : videoInfo.getData().getCommentCount()));
                rowsBeans.clear();
                viewTv.setText(NumberUtil.getChangeCount(videoInfo.getData().getViews()));
                disLikeTv.setText(DataUtil.convertValue(videoInfo.getData().getDislikes()));
                likeTv.setText(DataUtil.convertValue(videoInfo.getData().getLikes()));
                if (videoInfo.getData().getLikeStatus().equals("like")) {
                    likeTv.setTextColor(Color.parseColor("#ff189f"));
                    likeIv.setImageResource(R.mipmap.like_press);
                    disLikeTv.setTextColor(Color.parseColor("#666666"));
                    dislikeIv.setImageResource(R.mipmap.dislike_nopress);
                    likeLl.setTag("click");
                    dislikeLl.setTag("unclick");
                } else if (videoInfo.getData().getLikeStatus().equals("dislike")) {
                    likeTv.setTextColor(Color.parseColor("#666666"));
                    likeIv.setImageResource(R.mipmap.like_nopress);
                    disLikeTv.setTextColor(Color.parseColor("#338ffc"));
                    dislikeIv.setImageResource(R.mipmap.dislike_press);
                    likeLl.setTag("unclick");
                    dislikeLl.setTag("click");
                } else {
                    likeTv.setTextColor(Color.parseColor("#666666"));
                    likeIv.setImageResource(R.mipmap.like_nopress);
                    disLikeTv.setTextColor(Color.parseColor("#666666"));
                    dislikeIv.setImageResource(R.mipmap.dislike_nopress);
                    likeLl.setTag("unclick");
                    dislikeLl.setTag("unclick");
                }
                //设置评论总数
                commentCountTv.setText(String.valueOf(currentIntent.getCommitCount()));
                List<VideoInfo.DataBean.SuggestBean> suggests = videoInfo.getData().getSuggest();
                //转换数据
                for (VideoInfo.DataBean.SuggestBean suggestBean : suggests) {
                    Feed feed = new Feed();
                    feed.setTotalType(currentIntent.getTotalType());
                    feed.setId(suggestBean.getId());
                    feed.setVideoUrl(suggestBean.getTarget_id());
                    feed.setFeedType(suggestBean.getType());
                    feed.setType(FeedDataItemView.TYPE_ITEM_VIEW_VIDEOLIST);
                    feed.setCoverUrl(suggestBean.getCover());
                    feed.setLookCount(suggestBean.getViews());
                    feed.setTitle(suggestBean.getTitle());
                    feed.setDuration(suggestBean.getDuration());
                    rowsBeans.add(feed);
                }
                //TODO 获取广告数据
                VideoInfo.DataBean.Ad1Bean ad1Bean = videoInfo.getData().getAd1();
                Log.d(TAG, "ad1Bean is " + ad1Bean);
                if (ad1Bean != null) {
                    //TODO 配置广告数据
                    AdConfig adConfig = new AdConfig();
                    adConfig.setPlaceId(ad1Bean.getPlaceId());
                    adConfig.setType(ad1Bean.getType());
                    List<AdConfig.Source> sources = new ArrayList<AdConfig.Source>();
                    for (VideoInfo.DataBean.Ad1Bean.SourcesBean sourcesBean : ad1Bean.getSources()) {
                        AdConfig.Source source = new AdConfig.Source();
                        source.setAdKey(sourcesBean.getAdKey());
                        source.setAdType(sourcesBean.getAdType());
                        sources.add(source);
                    }
                    adConfig.setSources(sources);
                    //TODO 第一个位置新增一条广告数据。
                    Feed feed = new Feed();
                    feed.setType(FeedDataItemView.TYPE_ITEM_VIEW_AD);
                    feed.setAdConfig(adConfig);
                    rowsBeans.add(0, feed);
                    //TODO 将配置文件加入全局内存
                    Map<String, AdConfig> map = null;
                    if (GlobalConfig.getInstance().getAllAdConfigs() != null) {//说明登录的时候配置成功了
                        map = GlobalConfig.getInstance().getAllAdConfigs();
                    } else {
                        map = new HashMap<>();
                    }
                    map.put(ad1Bean.getPlaceId(), adConfig);
                    GlobalConfig.getInstance().setAllAdConfigs(map);
                }
                relatedAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<VideoInfo> call, Throwable t) {
                dialog.dismiss();
            }
        });
    }


    /**
     * 初始化评论数据
     */
    void initCommentData() {
        Log.d("FeedWeb", "initCommentData: " + currentIntent.getId() + ";" + currentIntent.getFeedType());
        ServiceGenerator.createServcie(this, ApiService.class).comments(currentIntent.getId(), currentIntent.getFeedType(), null).enqueue(new Callback<Comments>() {
            @Override
            public void onResponse(Call<Comments> call, Response<Comments> response) {
                Comments comments = response.body();
                if (comments == null) {
                    dialog.dismiss();
                    return;
                }
                FeedVideoActivity.this.comments.clear();
                Comments.DataBean dataBean = comments.getData();
                if (dataBean == null) {
                    dialog.dismiss();
                    return;
                }
                List<Comments.DataBean.RowsBean> rows = dataBean.getRows();
                if (rows.size() == 0) {
                    relatedAdapter.setHasComment(false);
                    relatedAdapter.notifyDataSetChanged();
                    becomeFirstRl.setVisibility(View.VISIBLE);
                    showAllTv.setVisibility(View.GONE);
                    commentLl.setVisibility(View.GONE);
                    loadMoreTv.setVisibility(View.GONE);
                    dialog.dismiss();
                    return;
                }
                relatedAdapter.setHasComment(true);
                relatedAdapter.notifyDataSetChanged();
                nextToken = dataBean.getNextToken();
                //转换数据
                for (Comments.DataBean.RowsBean rowsBean : rows) {
                    Comment comment = new Comment();
                    comment.setId(rowsBean.getId());
                    comment.setLikeStatus(rowsBean.getLikeStatus());
                    if (rowsBean.getLikeStatus().equals("like")) {
                        comment.setLike(true);
                    } else {
                        comment.setLike(false);
                    }
                    comment.setUrl(rowsBean.getUser().getAvatar());
                    comment.setContent(rowsBean.getContent());
                    comment.setName(rowsBean.getUser().getName());
                    comment.setLikeNum(rowsBean.getLikes());
                    FeedVideoActivity.this.comments.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
                if (FeedVideoActivity.this.comments.size() != 0) {
                    showAllTv.setVisibility(View.GONE);
                    becomeFirstRl.setVisibility(View.GONE);
                    commentLl.setVisibility(View.VISIBLE);
                    loadMoreTv.setVisibility(View.VISIBLE);
                } else {
                    becomeFirstRl.setVisibility(View.VISIBLE);
                    showAllTv.setVisibility(View.GONE);
                    commentLl.setVisibility(View.GONE);
                    loadMoreTv.setVisibility(View.GONE);
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<Comments> call, Throwable t) {
                Log.d("FeedWeb", "onFailure: " + t.getMessage());
                dialog.dismiss();
            }
        });
    }

    /**
     * 加载更多评论
     */
    void loadMoreComment() {
        ServiceGenerator.createServcie(this, ApiService.class).comments(currentIntent.getId(), currentIntent.getFeedType(), nextToken).enqueue(new Callback<Comments>() {
            @Override
            public void onResponse(Call<Comments> call, Response<Comments> response) {
                Comments comments = response.body();
                if (comments == null) {
                    return;
                }
                Comments.DataBean dataBean = comments.getData();
                List<Comments.DataBean.RowsBean> rows = comments.getData().getRows();
                if (rows.size() == 0) {
                    loadMoreTv.setVisibility(View.GONE);
                    showAllTv.setVisibility(View.VISIBLE);
                    return;
                } else {
                    loadMoreTv.setVisibility(View.VISIBLE);
                    showAllTv.setVisibility(View.GONE);
                }
                nextToken = dataBean.getNextToken();
                //转换数据
                for (Comments.DataBean.RowsBean rowsBean : rows) {
                    Comment comment = new Comment();
                    comment.setId(rowsBean.getId());
                    comment.setLikeStatus(rowsBean.getLikeStatus());
                    if (rowsBean.getLikeStatus().equals("like")) {
                        comment.setLike(true);
                    } else {
                        comment.setLike(false);
                    }
                    comment.setUrl(rowsBean.getUser().getAvatar());
                    comment.setContent(rowsBean.getContent());
                    comment.setName(rowsBean.getUser().getName());
                    comment.setLikeNum(rowsBean.getLikes());
                    FeedVideoActivity.this.comments.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Comments> call, Throwable t) {

            }
        });
    }

    /**
     * 发表评论
     */
    void postComment(String content) {
        dialog.show();
        HashMap<String, String> map = new HashMap<>();
        map.put("id", currentIntent.getId());
        map.put("content", content);
        map.put("type", currentIntent.getFeedType());
        ServiceGenerator.createServcie(this, ApiService.class).comment(map).enqueue(new Callback<Basic>() {
            @Override
            public void onResponse(Call<Basic> call, Response<Basic> response) {
                Basic basic = response.body();
                if (basic == null) {
                    dialog.dismiss();
                    return;
                }
                if (basic.getCode() == 0) {
                    initCommentData();
                    cv_comment.setTvCount(String.valueOf(cv_comment.getTvCount() + 1));
                    commentCountTv.setText(String.valueOf(Integer.valueOf(commentCountTv.getText().toString()) + 1));
                    new ToastUtil(FeedVideoActivity.this, getString(R.string.operationSuccess), R.mipmap.ic_toast_yes).show();
                    cv_comment.clearEdit();

                    //评论图片信息流
                    EventLogger.logEvent(EventConsts.DetailVideoCommentSuccessfully);

                } else {
                    new ToastUtil(FeedVideoActivity.this, getString(R.string.operationFail), R.mipmap.ic_toast_no).show();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<Basic> call, Throwable t) {
                Log.d("FeedWebActivity", "onFailure: " + t.getMessage());
                dialog.dismiss();
            }
        });
    }

    /**
     * 收藏
     */
    void postCollect() {
        if (!currentIntent.getSave()) {//收藏
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("id", currentIntent.getId());
            map.put("type", currentIntent.getFeedType());
            ServiceGenerator.createServcie(this, ApiService.class).mark(map).enqueue(new Callback<Basic>() {
                @Override
                public void onResponse(Call<Basic> call, Response<Basic> response) {
                    Basic basic = response.body();
                    if (basic.getCode() == Constants.REQUEST_SUCCESS) {
                        cv_comment.setCollect(true);
                        currentIntent.setSave(true);
                        EventBus.getDefault().post(currentIntent);
                        new ToastUtil(FeedVideoActivity.this, getString(R.string.collectionSuccess), R.mipmap.ic_toast_yes).show();
                    } else {
                        new ToastUtil(FeedVideoActivity.this, getString(R.string.collectionFail), R.mipmap.ic_toast_no).show();
                    }
                }

                @Override
                public void onFailure(Call<Basic> call, Throwable t) {
                    new ToastUtil(FeedVideoActivity.this, getString(R.string.collectionFail), R.mipmap.ic_toast_no).show();
                }
            });
        } else {//取消收藏
            RemoveMark rmMark = new RemoveMark();
            List<RemoveMark.IdListBean> idListBeans = new ArrayList<RemoveMark.IdListBean>();
            RemoveMark.IdListBean idListBean = new RemoveMark.IdListBean();
            idListBean.setId(currentIntent.getId());
            idListBean.setType(currentIntent.getFeedType());
            idListBeans.add(idListBean);
            rmMark.setId_list(idListBeans);
            ServiceGenerator.createServcie(FeedVideoActivity.this, ApiService.class).removeMark(rmMark).enqueue(new Callback<Basic>() {
                @Override
                public void onResponse(Call<Basic> call, Response<Basic> response) {
                    Basic basic = response.body();
                    if (basic.getCode() == Constants.REQUEST_SUCCESS) {
                        cv_comment.setCollect(false);
                        currentIntent.setSave(false);
                        EventBus.getDefault().post(currentIntent);
                        new ToastUtil(FeedVideoActivity.this, getString(R.string.operationSuccess), R.mipmap.ic_toast_yes).show();
                    } else {
                        new ToastUtil(FeedVideoActivity.this, getString(R.string.operationFail), R.mipmap.ic_toast_no).show();
                    }
                }

                @Override
                public void onFailure(Call<Basic> call, Throwable t) {
                    new ToastUtil(FeedVideoActivity.this, getString(R.string.operationFail), R.mipmap.ic_toast_no).show();
                }
            });
        }
    }

    /**
     * 刷新UI
     */
    void refreshUi() {
        initCommentData();
        initSuggestData();
        titleTv.setText(currentIntent.getTitle());
        cv_comment.setCollect(currentIntent.getSave());
    }

    /**
     * 喜欢的评论
     */
    void likeComment(final int position) {
        Comment comment = comments.get(position);
        HashMap<String, String> map = new HashMap<>();
        map.put("id", comment.getId());
        ServiceGenerator.createServcie(this, ApiService.class).likeComment(map).enqueue(new Callback<Basic>() {
            @Override
            public void onResponse(Call<Basic> call, Response<Basic> response) {
                if (response.body() == null) {
                    return;
                }
                if (response.body().getCode() == 0) {
                    commentAdapter.likeOrDislike(position);
                    new ToastUtil(FeedVideoActivity.this, getString(R.string.operationSuccess), R.mipmap.ic_toast_yes).show();
                } else {
                    new ToastUtil(FeedVideoActivity.this, getString(R.string.operationFail), R.mipmap.ic_toast_no).show();
                    Log.d("feedvideoactivity", "onFailure:" + response.body().getDesc() + "；code:" + response.body().getCode());
                }
            }

            @Override
            public void onFailure(Call<Basic> call, Throwable t) {
                Log.d("feedvideoactivity", "onFailure: " + t.getMessage());
                new ToastUtil(FeedVideoActivity.this, getString(R.string.operationFail), R.mipmap.ic_toast_no).show();

            }
        });
    }

    /**
     * 不喜欢的评论
     */
    void disLikeComment(final int position) {
        Comment comment = comments.get(position);
        HashMap<String, String> map = new HashMap<>();
        map.put("id", comment.getId());
        ServiceGenerator.createServcie(this, ApiService.class).disLikeComment(map).enqueue(new Callback<Basic>() {
            @Override
            public void onResponse(Call<Basic> call, Response<Basic> response) {
                if (response.body() == null) {
                    return;
                }
                if (response.body().getCode() == 0) {
                    commentAdapter.likeOrDislike(position);
                    new ToastUtil(FeedVideoActivity.this, getString(R.string.operationSuccess), R.mipmap.ic_toast_yes).show();
                } else {
                    new ToastUtil(FeedVideoActivity.this, getString(R.string.operationFail), R.mipmap.ic_toast_no).show();
                    Log.d("feedvideoactivity", "onFailure:" + response.body().getDesc());
                }
            }

            @Override
            public void onFailure(Call<Basic> call, Throwable t) {
                Log.d("feedvideoactivity", "onFailure: " + t.getMessage());
                new ToastUtil(FeedVideoActivity.this, getString(R.string.operationFail), R.mipmap.ic_toast_no).show();
            }
        });
    }


    /**
     * 喜欢视频
     */
    void likeVideo() {
        HashMap<String, String> map = new HashMap<>();
        map.put("id", currentIntent.getId());
        ServiceGenerator.createServcie(this, ApiService.class).likeVideo(map).enqueue(new Callback<Basic>() {
            @Override
            public void onResponse(Call<Basic> call, Response<Basic> response) {
                if (response.body() == null) {
                    return;
                }
                if (response.body().getCode() == 0) {
                    if (likeLl.getTag().equals("unclick")) {//之前是点击状态
                        likeTv.setTextColor(Color.parseColor("#ff189f"));
                        likeIv.setImageResource(R.mipmap.like_press);
                        String value1 = likeTv.getText().toString();
                        //是否是数字
                        if (DataUtil.isDigital(value1)) {
                            likeTv.setText(DataUtil.convertValue(Integer.valueOf(value1) + 1));
                        } else {
                            likeTv.setText(DataUtil.convertValue(((Integer.valueOf(value1.substring(0, value1.indexOf("k"))) * 1000 + 1))));
                        }
                        likeLl.setTag("click");
                        if (dislikeLl.getTag().equals("click")) {
                            disLikeTv.setTextColor(Color.parseColor("#666666"));
                            dislikeIv.setImageResource(R.mipmap.dislike_nopress);
                            String value2 = disLikeTv.getText().toString();
                            //是否是数字
                            if (DataUtil.isDigital(value2)) {
                                disLikeTv.setText(DataUtil.convertValue(Integer.valueOf(value2) - 1));
                            } else {
                                disLikeTv.setText(DataUtil.convertValue(((Integer.valueOf(value2.substring(0, value2.indexOf("k"))) * 1000 - 1))));
                            }
                            dislikeLl.setTag("unclick");
                        }
                    } else {
                        likeTv.setTextColor(Color.parseColor("#666666"));
                        likeIv.setImageResource(R.mipmap.like_nopress);
                        String value3 = likeTv.getText().toString();
                        //是否是数字
                        if (DataUtil.isDigital(value3)) {
                            likeTv.setText(DataUtil.convertValue(Integer.valueOf(value3) - 1));
                        } else {
                            likeTv.setText(DataUtil.convertValue(((Integer.valueOf(value3.substring(0, value3.indexOf("k"))) * 1000 - 1))));
                        }
                        likeLl.setTag("unclick");
                    }
                    new ToastUtil(FeedVideoActivity.this, getString(R.string.operationSuccess), R.mipmap.ic_toast_yes).show();
                } else {
                    new ToastUtil(FeedVideoActivity.this, getString(R.string.operationFail), R.mipmap.ic_toast_no).show();
                    Log.d("feedvideoactivity", "onFailure:" + response.body().getDesc() + "；code:" + response.body().getCode());
                }
            }

            @Override
            public void onFailure(Call<Basic> call, Throwable t) {
                Log.d("feedvideoactivity", "onFailure: " + t.getMessage());
                new ToastUtil(FeedVideoActivity.this, getString(R.string.operationFail), R.mipmap.ic_toast_no).show();
            }
        });
    }

    /**
     * 不喜欢视频
     */
    void disLikeVideo() {
        HashMap<String, String> map = new HashMap<>();
        map.put("id", currentIntent.getId());
        ServiceGenerator.createServcie(this, ApiService.class).disLikeVideo(map).enqueue(new Callback<Basic>() {
            @Override
            public void onResponse(Call<Basic> call, Response<Basic> response) {
                if (response.body() == null) {
                    return;
                }
                if (response.body().getCode() == 0) {
                    if (dislikeLl.getTag().equals("unclick")) {//之前是点击状态
                        disLikeTv.setTextColor(Color.parseColor("#338ffc"));
                        dislikeIv.setImageResource(R.mipmap.dislike_press);
                        String value1 = disLikeTv.getText().toString();
                        //是否是数字
                        if (DataUtil.isDigital(value1)) {
                            disLikeTv.setText(DataUtil.convertValue(Integer.valueOf(value1) + 1));
                        } else {
                            disLikeTv.setText(DataUtil.convertValue(((Integer.valueOf(value1.substring(0, value1.indexOf("k"))) * 1000 + 1))));
                        }
                        dislikeLl.setTag("click");
                        if (likeLl.getTag().equals("click")) {
                            likeTv.setTextColor(Color.parseColor("#666666"));
                            likeIv.setImageResource(R.mipmap.like_nopress);
                            String value2 = likeTv.getText().toString();
                            //是否是数字
                            if (DataUtil.isDigital(value2)) {
                                likeTv.setText(DataUtil.convertValue(Integer.valueOf(value2) - 1));
                            } else {
                                likeTv.setText(DataUtil.convertValue(((Integer.valueOf(value2.substring(0, value2.indexOf("k"))) * 1000 - 1))));
                            }
                            likeLl.setTag("unclick");
                        }
                    } else {
                        disLikeTv.setTextColor(Color.parseColor("#666666"));
                        dislikeIv.setImageResource(R.mipmap.dislike_nopress);
                        String value3 = disLikeTv.getText().toString();
                        //是否是数字
                        if (DataUtil.isDigital(value3)) {
                            disLikeTv.setText(DataUtil.convertValue(Integer.valueOf(value3) - 1));
                        } else {
                            disLikeTv.setText(DataUtil.convertValue(((Integer.valueOf(value3.substring(0, value3.indexOf("k"))) * 1000 - 1))));
                        }
                        dislikeLl.setTag("unclick");
                    }
                    new ToastUtil(FeedVideoActivity.this, getString(R.string.operationSuccess), R.mipmap.ic_toast_yes).show();
                } else {
                    new ToastUtil(FeedVideoActivity.this, getString(R.string.operationFail), R.mipmap.ic_toast_no).show();
                    Log.d("feedvideoactivity", "onFailure:" + response.body().getDesc());
                }
            }

            @Override
            public void onFailure(Call<Basic> call, Throwable t) {
                Log.d("feedvideoactivity", "onFailure: " + t.getMessage());
                new ToastUtil(FeedVideoActivity.this, getString(R.string.operationFail), R.mipmap.ic_toast_no).show();
            }
        });
    }

    /**
     * 显示视频视图
     */
    void showVideo(final String url) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        fl.setLayoutParams(params);
        fl.setId(R.id.fl_video);
        if (fl.getParent() != null) {
            ((ViewGroup) fl.getParent()).removeView(fl);
        }
        rootRl.addView(fl);
        fl.setX(0);
        fl.setY(0);
        FragmentTransaction transaction = manager.beginTransaction();
        YouTubePlayerSupportFragment fg_video = YouTubePlayerSupportFragment.newInstance();
        transaction.replace(R.id.fl_video, fg_video);
        transaction.commitAllowingStateLoss();
        fg_video.initialize(ClientSideFactory.GOOGLE_YOUTUBE_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                mYouTubePlayer = youTubePlayer;
                youTubePlayer.loadVideo(url);
                youTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                    @Override
                    public void onFullscreen(boolean b) {
                        isFullscreen = b;
                    }
                });
                Log.d("translation", "onInitializationSuccess");
                youTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                    @Override
                    public void onLoading() {
                        Log.d("FeedVideo", "onLoading: ");
                    }

                    @Override
                    public void onLoaded(String s) {
                        Log.d("FeedVideo", "onLoaded: ");

                    }

                    @Override
                    public void onAdStarted() {
                        Log.d("FeedVideo", "onAdStarted: ");
                        Bundle bundle = new Bundle();
                        bundle.putString(EventConsts.ACTION, EventConsts.show);
                        bundle.putString(EventConsts.tid, currentIntent.getTotalType());
                        bundle.putString(EventConsts.index, String.valueOf(currentIntent.getIndex()));
                        EventLogger.logEvent(EventConsts.DetailVideoShow, bundle);

                        Bundle bundle2 = new Bundle();
                        bundle2.putString(EventConsts.ACTION, EventConsts.show);
                        bundle2.putString(EventConsts.tid, currentIntent.getTotalType());
                        bundle2.putString(EventConsts.index, String.valueOf(currentIntent.getIndex()));
                        EventLogger.logEvent(EventConsts.VideoPlaySuccessfully, bundle2);
                    }

                    @Override
                    public void onVideoStarted() {
                        Log.d("FeedVideo", "onVideoStarted: ");

                    }

                    @Override
                    public void onVideoEnded() {
                        Log.d("FeedVideo", "onVideoEnded: ");
                    }

                    @Override
                    public void onError(YouTubePlayer.ErrorReason errorReason) {
                        Log.d("FeedVideo", "onError: " + errorReason.toString());
                    }
                });
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                if (youTubeInitializationResult.isUserRecoverableError()) {
                    youTubeInitializationResult.getErrorDialog(FeedVideoActivity.this, youTubeInitializationResult.ordinal()).show();
                }
            }
        });
    }


    boolean isFullscreen;
    YouTubePlayer mYouTubePlayer;

    @Override
    protected void onStart() {
        super.onStart();
        if (lastType != LastViewType.OTHER && currentIntent != null) {
            SPUtil.put(this, com.taiqudong.android.ad.Constants.CTG, currentIntent.getTotalType());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //退出时，重置界面类型
        SPUtil.put(this, com.taiqudong.android.ad.Constants.CTG, "");
    }
}