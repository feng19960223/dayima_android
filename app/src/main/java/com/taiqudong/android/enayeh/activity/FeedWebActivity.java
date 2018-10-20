package com.taiqudong.android.enayeh.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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
import com.taiqudong.android.enayeh.application.SysApplication;
import com.taiqudong.android.enayeh.application.retrofit.ServiceGenerator;
import com.taiqudong.android.enayeh.application.retrofit.api.ApiService;
import com.taiqudong.android.enayeh.application.retrofit.bean.ArticleInfo;
import com.taiqudong.android.enayeh.application.retrofit.bean.Basic;
import com.taiqudong.android.enayeh.application.retrofit.bean.Comments;
import com.taiqudong.android.enayeh.application.retrofit.bean.RemoveMark;
import com.taiqudong.android.enayeh.bean.Comment;
import com.taiqudong.android.enayeh.bean.Feed;
import com.taiqudong.android.enayeh.utils.Constants;
import com.taiqudong.android.enayeh.utils.DialogUtil;
import com.taiqudong.android.enayeh.utils.LoadingDialog;
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

//import android.widget.Toast;

/**
 * Created by taiqudong on 2017/7/13.
 */

public class FeedWebActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "FeedWebActivity";
    private WebView wv_feed;
    public static final String INTENT_FEED = "intent_feed";
    RecyclerView commentRcv;
    CommentAdapter commentAdapter;
    List<Comment> comments = new ArrayList<>();
    private ArrayList<Feed> rowsBeans = new ArrayList<>();
    RecyclerView relatedRcv;
    FeedAdapter feedAdapter;
    ImageView backIv;
    TextView showAllTv;
    RelativeLayout becomeFirstRl;
    LinearLayout commentLl;
    Feed currentIntent;
    String nextToken;
    TextView loadMoreTv;
    ScrollView forceScrollView;
    TextView commentCount;
    LoadingDialog.Builder builder;
    LoadingDialog dialog;
    int lastType;

    private static final String LAST_TYPE = "last_type";

    public interface LastViewType {
        int MAIN_ACTIVITY = 0;
        int FEED_DETAIL = 1;
        int OTHER = 2;
    }


    public static Intent newIntent(Context context, Feed feed, int lastType) {
        Intent intent = new Intent(context, FeedWebActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(INTENT_FEED, feed);
        args.putInt(LAST_TYPE, lastType);
        intent.putExtras(args);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_feed);
        currentIntent = (Feed) getIntent().getSerializableExtra(INTENT_FEED);
        lastType = getIntent().getIntExtra(LAST_TYPE, LastViewType.FEED_DETAIL);
        if (lastType != LastViewType.OTHER & currentIntent != null) {
            //设置进入的界面类型
            SPUtil.put(this, com.taiqudong.android.ad.Constants.CTG, currentIntent.getTotalType());
        }
        SysApplication.getInstance().addActivit(this);
        builder = new LoadingDialog.Builder(this, getString(R.string.loading));
        dialog = builder.create();
        dialog.show();
        StatusBarUtils.statusbar(this);
        initView();
        initListener();
        initData();
        //TODO 预加载广告到缓存
        Map<String, AdConfig> map = (Map<String, AdConfig>) SPUtil.getHashMapData(this, com.taiqudong.android.ad.Constants.SP_AD_SPLASH_CONFIG, AdConfig.class);
        if (map.size() != 0 && map.get(com.taiqudong.android.ad.Constants.AD_SPLASH_BACKHOME) != null) {
            AdConfig config = map.get(com.taiqudong.android.ad.Constants.AD_SPLASH_BACKHOME);
            Log.d(TAG, "config is not null");
            AdParallelLoader adParallelLoader = new AdParallelLoader(config);
            adParallelLoader.prefetch();
        }
    }

    private CommentView cv_comment;

    private void initView() {
        final ScrollView sv = (ScrollView) findViewById(R.id.fsv);
        sv.post(new Runnable() {
            @Override
            public void run() {
                sv.scrollTo(0, 0);
            }
        });
        commentCount = (TextView) findViewById(R.id.tv_comment_count);
        forceScrollView = (ScrollView) findViewById(R.id.fsv);
        forceScrollView.post(new Runnable() {
            @Override
            public void run() {
                forceScrollView.scrollTo(0, 0);
            }
        });
        findViewById(R.id.iv_return).setOnClickListener(this);
        loadMoreTv = (TextView) findViewById(R.id.tv_loadmore);
        wv_feed = (WebView) findViewById(R.id.wv_feed);
        commentRcv = (RecyclerView) findViewById(R.id.rcv_comment);
        relatedRcv = (RecyclerView) findViewById(R.id.rcv_related);
        backIv = (ImageView) findViewById(R.id.iv_return);
        showAllTv = (TextView) findViewById(R.id.tv_showall);
        becomeFirstRl = (RelativeLayout) findViewById(R.id.rl_become_first);
        commentLl = (LinearLayout) findViewById(R.id.ll_comment);
        //评论控件
        cv_comment = (CommentView) findViewById(R.id.cv_comment);
        cv_comment.setOnSendClickListener(new CommentView.OnSendClickListener() {
            @Override
            public void onSendClick(String content) {

                EventLogger.logEvent(EventConsts.DetailPicCommentSuccessfully);


                int loginType = (int) SPUtil.get(FeedWebActivity.this, Constants.SP_LOGIN_TYPE, Constants.LOGIN_TYPE.NON_USER);
                if (loginType == Constants.LOGIN_TYPE.NON_USER) {

                    //弹出登录对话框
                    EventLogger.logEvent(EventConsts.DialogLoginShow);

                    DialogUtil.showDialog(FeedWebActivity.this, getString(R.string.LoginContent), new DialogUtil.OnDialogUtilListener() {
                        @Override
                        public void onDialogUtil_YES() {
                            Intent intent = new Intent(FeedWebActivity.this, LoginMainActivity.class);
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
        //相关推荐
        relatedRcv.setLayoutManager(new LinearLayoutManager(this));
        feedAdapter = new FeedAdapter(rowsBeans, this);
        relatedRcv.setAdapter(feedAdapter);
        feedAdapter.setOnItemClickListener(new FeedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.d("FeedWebActivity", "onItemClick: " + position);
                startActivity(FeedWebActivity.newIntent(FeedWebActivity.this, rowsBeans.get(position), LastViewType.FEED_DETAIL));
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

        //加载更多评论
        loadMoreTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMoreComment();
            }
        });
    }

    private void initListener() {
        //findViewById(R.id.iv_feed_share).setOnClickListener(this);
    }

    private void initData() {
        wv_feed.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                Bundle bundle1 = new Bundle();
                bundle1.putString(EventConsts.ACTION, EventConsts.show);
                bundle1.putString(EventConsts.tid, currentIntent.getTotalType());
                bundle1.putString(EventConsts.index, String.valueOf(currentIntent.getIndex()));
                EventLogger.logEvent(EventConsts.DetailPicShow, bundle1);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                if (wv_feed != null) {
                    wv_feed.setLayoutParams(params);
                }
                super.onPageFinished(view, url);
                dialog.dismiss();
            }
        });
        WebSettings webSettings = wv_feed.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); //不使用缓存，只从网络获取数据.
        webSettings.setJavaScriptEnabled(false);//禁止Javascript交互
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setBuiltInZoomControls(false);
        webSettings.setBuiltInZoomControls(false);
        wv_feed.loadUrl(currentIntent.getUrl());
        initCommentData();
        initSuggestData();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return:
                //TODO 弹出插屏广告
                Map<String, AdConfig> map = (Map<String, AdConfig>) SPUtil.getHashMapData(this, com.taiqudong.android.ad.Constants.SP_AD_SPLASH_CONFIG, AdConfig.class);
                if (map.size() != 0 && map.get(com.taiqudong.android.ad.Constants.AD_SPLASH_BACKHOME) != null && lastType == LastViewType.MAIN_ACTIVITY) {
                    AdConfig config = map.get(com.taiqudong.android.ad.Constants.AD_SPLASH_BACKHOME);
                    if (config.getSources() != null && config.getSources().size() != 0 && GlobalCache.getInstance().hasAd(com.taiqudong.android.ad.Constants.AD_SPLASH_BACKHOME, config.getSources().get(0))) {
                        AdSplashView.start(this, com.taiqudong.android.ad.Constants.AD_SPLASH_BACKHOME, AdSplashView.Action.FROM_APP_TO_MAIN, MainActivity.class, null);//bundle null
                        Log.d(TAG, "start display splash_en view");
                    } else {
                        if (GlobalCache.getInstance().hasAd(com.taiqudong.android.ad.Constants.AD_SPLASH_BACKHOME, config.getSources().get(0))) {
                            Log.d(TAG, "global has not ad here!");
                        }
                        finish();
                        Log.d(TAG, "onBackPressed:Has not ad content!");
                    }
                } else {
                    finish();
                    if (map.size() == 0) {
                        Log.d(TAG, "onBackPressed:GlobalConfig.getInstance().getAllAdConfigs() is null");
                    } else if (map.get(com.taiqudong.android.ad.Constants.AD_SPLASH_BACKHOME) == null) {
                        Log.d(TAG, "onBackPressed:GlobalConfig.getInstance().getAllAdConfigs().get(com.taiqudong.android.ad.Constants.AD_SPLASH_BACKHOME) is null");
                    } else {
                        Log.d(TAG, "onBackPressed:lastViewType != LastViewType.MAIN_ACTIVITY");
                    }
                }
                break;
            /**
             case R.id.iv_feed_share:
             int loginType = (int) SPUtil.get(FeedWebActivity.this, Constants.SP_LOGIN_TYPE, Constants.LOGIN_TYPE.NON_USER);
             if (loginType == Constants.LOGIN_TYPE.NON_USER) {
             DialogUtil.showDialog(FeedWebActivity.this, getString(R.string.LoginContent), new DialogUtil.OnDialogUtilListener() {
            @Override public void onDialogUtil_YES() {
            startActivity(new Intent(FeedWebActivity.this, LoginMainActivity.class));//到登录页
            }

            @Override public void onDialogUtil_CANCEL() {

            }
            });

             } else {
             share();
             }

             break;
             **/
            default:
        }
    }

    void share() {
        String url = currentIntent.getShareUrl();
        Intent textIntent = new Intent(Intent.ACTION_SEND);
        //        textIntent.setType("text/*");
        textIntent.setType("text/plain");
        //        textIntent.setType("image/jpg");
        //        textIntent.putExtra(Intent.EXTRA_SUBJECT, url);
        textIntent.putExtra(Intent.EXTRA_TEXT, url);
        //        textIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.Careforwomen));
        startActivity(Intent.createChooser(textIntent, getString(R.string.Careforwomen)));

        //分享
        EventLogger.logEvent(EventConsts.ShareSuccessfully);
    }

    @Override
    protected void onDestroy() {
        if (wv_feed != null) {
            wv_feed.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            wv_feed.clearHistory();
            ((ViewGroup) wv_feed.getParent()).removeView(wv_feed);
            wv_feed.destroy();
            wv_feed = null;
        }
        SysApplication.getInstance().removeActivity(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        //TODO 弹出插屏广告
        Map<String, AdConfig> map = (Map<String, AdConfig>) SPUtil.getHashMapData(this, com.taiqudong.android.ad.Constants.SP_AD_SPLASH_CONFIG, AdConfig.class);
        if (map.size() != 0 && map.get(com.taiqudong.android.ad.Constants.AD_SPLASH_BACKHOME) != null && lastType == FeedVideoActivity.LastViewType.MAIN_ACTIVITY) {
            AdConfig config = map.get(com.taiqudong.android.ad.Constants.AD_SPLASH_BACKHOME);
            if (config.getSources() != null && config.getSources().size() != 0 && GlobalCache.getInstance().hasAd(com.taiqudong.android.ad.Constants.AD_SPLASH_BACKHOME, config.getSources().get(0))) {
                AdSplashView.start(this, com.taiqudong.android.ad.Constants.AD_SPLASH_BACKHOME, AdSplashView.Action.FROM_APP_TO_MAIN, MainActivity.class,null);
                Log.d(TAG, "start display splash_en view");
            } else {
                if (GlobalCache.getInstance().hasAd(com.taiqudong.android.ad.Constants.AD_SPLASH_BACKHOME, config.getSources().get(0))) {
                    Log.d(TAG, "global has not ad here!");
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
        ServiceGenerator.createServcie(this, ApiService.class).articleInfo(currentIntent.getId()).enqueue(new Callback<ArticleInfo>() {
            @Override
            public void onResponse(Call<ArticleInfo> call, Response<ArticleInfo> response) {
                ArticleInfo articleInfo = response.body();
                if (articleInfo == null) {
                    dialog.dismiss();
                    return;
                }
                if (articleInfo.getData() == null) {
                    dialog.dismiss();
                    return;
                }                //设置评论总数
                commentCount.setText(String.valueOf(
                        currentIntent.getCommitCount() > articleInfo.getData().getCommentCount()
                                ? currentIntent.getCommitCount() : articleInfo.getData().getCommentCount()));
                cv_comment.setTvCount(String.valueOf(
                        currentIntent.getCommitCount() > articleInfo.getData().getCommentCount()
                                ? currentIntent.getCommitCount() : articleInfo.getData().getCommentCount()));
                List<ArticleInfo.DataBean.SuggestBean> suggests = articleInfo.getData().getSuggest();

                //转换数据
                for (ArticleInfo.DataBean.SuggestBean suggestBean : suggests) {
                    Feed feed = new Feed();
                    feed.setId(suggestBean.getId());
                    feed.setTotalType(currentIntent.getTotalType());
                    feed.setFeedType(suggestBean.getType());
                    feed.setType(FeedDataItemView.TYPE_ITEM_VIEW_PICLIST);
                    feed.setUrl(suggestBean.getUrl());
                    feed.setCoverUrl(suggestBean.getImage().get(0));
                    feed.setLookCount(suggestBean.getViews());
                    feed.setTitle(suggestBean.getTitle());
                    rowsBeans.add(feed);
                }
                //TODO 获取广告数据
                ArticleInfo.DataBean.Ad1Bean ad1Bean = articleInfo.getData().getAd1();
                Log.d(TAG, "ad1Bean is " + ad1Bean);
                if (ad1Bean != null) {
                    //TODO 配置广告数据
                    AdConfig adConfig = new AdConfig();
                    adConfig.setPlaceId(ad1Bean.getPlaceId());
                    adConfig.setType(ad1Bean.getType());
                    List<AdConfig.Source> sources = new ArrayList<AdConfig.Source>();
                    for (ArticleInfo.DataBean.Ad1Bean.SourcesBean sourcesBean : ad1Bean.getSources()) {
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
                feedAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<ArticleInfo> call, Throwable t) {
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
                FeedWebActivity.this.comments.clear();
                Comments.DataBean dataBean = comments.getData();
                if (dataBean == null) {
                    dialog.dismiss();
                    return;
                }
                List<Comments.DataBean.RowsBean> rows = dataBean.getRows();
                if (rows.size() == 0) {
                    feedAdapter.setHasComment(false);
                    feedAdapter.notifyDataSetChanged();
                    becomeFirstRl.setVisibility(View.VISIBLE);
                    showAllTv.setVisibility(View.GONE);
                    commentLl.setVisibility(View.GONE);
                    loadMoreTv.setVisibility(View.GONE);
                    dialog.dismiss();
                    return;
                }
                nextToken = dataBean.getNextToken();
                feedAdapter.setHasComment(true);
                feedAdapter.notifyDataSetChanged();
                //转换数据
                for (Comments.DataBean.RowsBean rowsBean : rows) {
                    Comment comment = new Comment();
                    comment.setLikeStatus(rowsBean.getLikeStatus());
                    if (rowsBean.getLikeStatus().equals("like")) {
                        comment.setLike(true);
                    } else {
                        comment.setLike(false);
                    }
                    comment.setId(rowsBean.getId());
                    comment.setUrl(rowsBean.getUser().getAvatar());
                    comment.setContent(rowsBean.getContent());
                    comment.setName(rowsBean.getUser().getName());
                    comment.setLikeNum(rowsBean.getLikes());
                    FeedWebActivity.this.comments.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
                if (FeedWebActivity.this.comments.size() != 0) {
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
                    comment.setLikeStatus(rowsBean.getLikeStatus());
                    if (rowsBean.getLikeStatus().equals("like")) {
                        comment.setLike(true);
                    } else {
                        comment.setLike(false);
                    }
                    comment.setId(rowsBean.getId());
                    comment.setUrl(rowsBean.getUser().getAvatar());
                    comment.setContent(rowsBean.getContent());
                    comment.setName(rowsBean.getUser().getName());
                    comment.setLikeNum(rowsBean.getLikes());
                    FeedWebActivity.this.comments.add(comment);
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
                    commentCount.setText(String.valueOf(Integer.valueOf(commentCount.getText().toString()) + 1));
                    new ToastUtil(FeedWebActivity.this, getString(R.string.operationSuccess), R.mipmap.ic_toast_yes).show();
                    cv_comment.clearEdit();

                    //评论图片信息流
                    EventLogger.logEvent(EventConsts.DetailPicCommentSuccessfully);
                } else {
                    new ToastUtil(FeedWebActivity.this, getString(R.string.operationFail), R.mipmap.ic_toast_no).show();
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
                        new ToastUtil(FeedWebActivity.this, getString(R.string.collectionSuccess), R.mipmap.ic_toast_yes).show();
                    } else {
                        new ToastUtil(FeedWebActivity.this, getString(R.string.collectionFail), R.mipmap.ic_toast_no).show();
                    }
                }

                @Override
                public void onFailure(Call<Basic> call, Throwable t) {
                    new ToastUtil(FeedWebActivity.this, getString(R.string.collectionFail), R.mipmap.ic_toast_no).show();
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
            ServiceGenerator.createServcie(FeedWebActivity.this, ApiService.class).removeMark(rmMark).enqueue(new Callback<Basic>() {
                @Override
                public void onResponse(Call<Basic> call, Response<Basic> response) {
                    Basic basic = response.body();
                    if (basic.getCode() == Constants.REQUEST_SUCCESS) {
                        cv_comment.setCollect(false);
                        currentIntent.setSave(false);
                        EventBus.getDefault().post(currentIntent);
                        new ToastUtil(FeedWebActivity.this, getString(R.string.operationSuccess), R.mipmap.ic_toast_yes).show();
                    } else {
                        new ToastUtil(FeedWebActivity.this, getString(R.string.operationFail), R.mipmap.ic_toast_no).show();
                    }
                }

                @Override
                public void onFailure(Call<Basic> call, Throwable t) {
                    new ToastUtil(FeedWebActivity.this, getString(R.string.operationFail), R.mipmap.ic_toast_no).show();
                }
            });
        }
    }

    /**
     * 更新数据：推荐数据、评论数据、详情数据
     */
    void refreshUi() {
        initCommentData();
        initSuggestData();
        cv_comment.setCollect(currentIntent.getSave());
        wv_feed.loadUrl(currentIntent.getUrl());
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
                    new ToastUtil(FeedWebActivity.this, getString(R.string.operationSuccess), R.mipmap.ic_toast_yes).show();
                } else {
                    new ToastUtil(FeedWebActivity.this, getString(R.string.operationFail), R.mipmap.ic_toast_no).show();
                    Log.d("feedvideoactivity", "onFailure:" + response.body().getDesc());

                }
            }

            @Override
            public void onFailure(Call<Basic> call, Throwable t) {
                Log.d("feedvideoactivity", "onFailure: " + t.getMessage());
                new ToastUtil(FeedWebActivity.this, getString(R.string.operationFail), R.mipmap.ic_toast_no).show();

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
                    new ToastUtil(FeedWebActivity.this, getString(R.string.operationSuccess), R.mipmap.ic_toast_yes).show();
                } else {
                    new ToastUtil(FeedWebActivity.this, getString(R.string.operationFail), R.mipmap.ic_toast_no).show();
                    Log.d("feedvideoactivity", "onFailure:" + response.body().getDesc());
                }
            }

            @Override
            public void onFailure(Call<Basic> call, Throwable t) {
                Log.d("feedvideoactivity", "onFailure: " + t.getMessage());
                new ToastUtil(FeedWebActivity.this, getString(R.string.operationFail), R.mipmap.ic_toast_no).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (lastType != FeedVideoActivity.LastViewType.OTHER && currentIntent != null) {
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
