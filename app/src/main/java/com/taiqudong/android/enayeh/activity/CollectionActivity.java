package com.taiqudong.android.enayeh.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.taiqudong.android.enayeh.application.retrofit.bean.Basic;
import com.taiqudong.android.enayeh.application.retrofit.bean.ContentList;
import com.taiqudong.android.enayeh.application.retrofit.bean.RemoveMark;
import com.taiqudong.android.enayeh.bean.Feed;
import com.taiqudong.android.enayeh.utils.Constants;
import com.taiqudong.android.enayeh.utils.DataUtil;
import com.taiqudong.android.enayeh.utils.DialogUtil;
import com.taiqudong.android.enayeh.utils.LoadingDialog;
import com.taiqudong.android.enayeh.utils.NetUtil;
import com.taiqudong.android.enayeh.utils.StatusBarUtils;
import com.taiqudong.android.enayeh.utils.ToastUtil;
import com.taiqudong.android.enayeh.view.FeedDataItemView;
import com.taiqudong.android.enayeh.view.NestedRecycleView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 收藏
 * Created by taiqudong on 2017/8/17.
 */

public class CollectionActivity extends AppCompatActivity implements View.OnClickListener, YouTubePlayer.OnFullscreenListener {
    private ImageView iv_return;
    private TextView tv_edit;//编辑或取消编辑
    private NestedRecycleView rv_collection;//收藏的内容
    private LinearLayout ll_edit;//点击编辑显示lledit，点击取消隐藏
    private ImageView iv_allselect;//点击全选，再次点击全不选
    private TextView tv_allselect;//点击全选，再次点击全不选
    private LinearLayout ll_collection;
    String nextToken;
    LoadingDialog.Builder builder;
    LoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
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
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (fg_video != null) {
            fg_video.pause();
        }
        fl.setVisibility(View.GONE);
    }

    CollectAndCommentFeedAdapter feedAdapter;
    private ArrayList<Feed> rowsBeans = new ArrayList<>();

    private void initData() {
        rv_collection.setLayoutManager(new LinearLayoutManager(this));
        rv_collection.setLoadingMoreEnabled(true);
        rv_collection.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        feedAdapter = new CollectAndCommentFeedAdapter(rowsBeans, this);
        rv_collection.setAdapter(feedAdapter);

        feedAdapter.setSelectedListener(new CollectAndCommentFeedAdapter.SelectedListener() {
            @Override
            public void onSelectAll(boolean isAllSelected) {
                isSeletAll = isAllSelected;
                tv_allselect.setText(getString(isAllSelected ? R.string.allNoselect : R.string.allselect));
                tv_allselect.setTextColor(Color.parseColor(isAllSelected ? "#FF189F" : "#999999"));
                iv_allselect.setImageResource(isAllSelected ? R.mipmap.ic_quan_s : R.mipmap.ic_quan_d);
            }
        });
        feedAdapter.setDeleteListener(new CollectAndCommentFeedAdapter.DeleteListener() {
            @Override
            public void onDelete(ArrayList<Feed> deletedFeeds) {
                cancelCollection(deletedFeeds);

            }
        });
        rv_collection.setPullRefreshEnabled(false);
        rv_collection.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                if (fg_video != null) {
                    fg_video.pause();
                }
                fl.setVisibility(View.GONE);
                //                initFeed();
                //                loadState = CollectionActivity.LoadState.REFRESH;
                //                rv_collection.refreshComplete();
            }

            @Override
            public void onLoadMore() {
                loadState = CollectionActivity.LoadState.LOAD_MORE;
                loadMoreFeed();
            }
        });

        rv_collection.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

        rv_collection.getXRecyclerViewArrowRefreshHeaderView().setOnArrowRefreshHeaderVisibleHeight(new ArrowRefreshHeader.OnArrowRefreshHeaderVisibleHeight() {
            @Override
            public void visibleHeight(int height) {
                if (height != 0) {
                    fl.setY(height + flY);
                }
            }
        });

        initFeed();
    }

    float flY = 0;


    private void initView() {
        fl = new FrameLayout(this);
        fl_root = (FrameLayout) findViewById(R.id.fl_root);
        iv_return = (ImageView) findViewById(R.id.iv_return);
        tv_edit = (TextView) findViewById(R.id.tv_edit);
        rv_collection = (NestedRecycleView) findViewById(R.id.rv_collection);
        ll_edit = (LinearLayout) findViewById(R.id.ll_edit);
        iv_allselect = (ImageView) findViewById(R.id.iv_allselect);
        tv_allselect = (TextView) findViewById(R.id.tv_allselect);
        ll_collection = (LinearLayout) findViewById(R.id.ll_collection);
    }

    int loadState = -1;
    CollectionActivity.VideoFragment fg_video;
    static FrameLayout fl;
    FragmentManager manager;
    FrameLayout fl_root;

    private interface LoadState {
        int REFRESH = 0;
        int LOAD_MORE = 1;
    }

    private void initListener() {
        tv_edit.setOnClickListener(this);
        iv_allselect.setOnClickListener(this);
        findViewById(R.id.iv_return).setOnClickListener(this);
        findViewById(R.id.tv_allselect).setOnClickListener(this);
        findViewById(R.id.tv_delete).setOnClickListener(this);
        findViewById(R.id.tv_goto).setOnClickListener(this);
    }

    private boolean isEdit = false;//是否是编辑状态
    private boolean isSeletAll = false;//是否是全选状态

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_return:
                this.finish();
                break;
            case R.id.tv_edit:
                if (fg_video != null) {
                    fg_video.pause();
                }
                fl.setVisibility(View.GONE);

                iv_return.setVisibility(iv_return.getVisibility() == View.VISIBLE
                        ?
                        View.GONE
                        :
                        View.VISIBLE);//点击取消无返回
                ll_edit.setVisibility(ll_edit.getVisibility() == View.VISIBLE
                        ?
                        View.GONE
                        :
                        View.VISIBLE);//如果显示则隐藏，如果隐藏则显示
                tv_edit.setText(getString(R.string.edit).equals(tv_edit.getText().toString())
                        ?
                        getString(R.string.CANCEL)
                        :
                        getString(R.string.edit));//如果显示edit则cancel，如果cancel则edit
                isEdit = !isEdit;
                feedAdapter.canSelectable(isEdit);
                tv_allselect.setText(getString(isSeletAll ? R.string.allNoselect : R.string.allselect));
                iv_allselect.setImageResource(isSeletAll ? R.mipmap.ic_quan_s : R.mipmap.ic_quan_d);
                break;
            case R.id.iv_allselect:
                isSeletAll = !isSeletAll;
                feedAdapter.seletAllItem(isSeletAll);
                break;
            case R.id.tv_allselect:
                isSeletAll = !isSeletAll;
                feedAdapter.seletAllItem(isSeletAll);
                break;
            case R.id.tv_delete:
                feedAdapter.deleteSelectedItem();

                if (isSeletAll) {//如果是全选，编辑按钮不再显示
                    tv_edit.setVisibility(View.GONE);
                    ll_collection.setVisibility(View.VISIBLE);
                }
                isSeletAll = false;
                feedAdapter.seletAllItem(isSeletAll);//全不选
                isEdit = false;
                feedAdapter.canSelectable(isEdit);//不编辑
                iv_allselect.setImageResource(R.mipmap.ic_quan_d);
                tv_allselect.setText(getString(R.string.allselect));
                iv_return.setVisibility(View.VISIBLE);
                ll_edit.setVisibility(View.GONE);
                tv_edit.setText(getString(R.string.edit));
                iv_return.setVisibility(View.VISIBLE);//显示返回
                break;
            case R.id.tv_goto:
                AppLogic.getInstance().setFeedState(1);
                this.finish();
                break;
            default:
        }
    }

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
        fg_video = CollectionActivity.VideoFragment.newInstance();
        transaction.replace(R.id.fl_video, fg_video);
        transaction.commit();
        fg_video.setVideoId(url);
        fl.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (isFullscreen) {
            fg_video.setXiao();
            isFullscreen = false;
            return;
        }
        if (ll_edit.getVisibility() == View.VISIBLE) {//编辑状态
            isEdit = false;
            isSeletAll = false;
            feedAdapter.canSelectable(isEdit);
            feedAdapter.seletAllItem(isSeletAll);
            iv_allselect.setImageResource(R.mipmap.ic_quan_d);
            tv_allselect.setText(getString(R.string.allselect));
            iv_return.setVisibility(View.VISIBLE);
            ll_edit.setVisibility(View.GONE);
            tv_edit.setText(getString(R.string.edit));
            return;
        }
        super.onBackPressed();
    }

    public static final class VideoFragment extends YouTubePlayerSupportFragment
            implements YouTubePlayer.OnInitializedListener {

        private YouTubePlayer player;
        private String videoId;

        public static CollectionActivity.VideoFragment newInstance() {
            return new CollectionActivity.VideoFragment();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            initialize(ClientSideFactory.GOOGLE_YOUTUBE_KEY, this);
        }

        public void setXiao() {//从大屏会到小屏幕
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
            player.setOnFullscreenListener((CollectionActivity) getActivity());
            if (!restored && videoId != null) {
                player.loadVideo(videoId);
                fl.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
            this.player = null;
            if (result.isUserRecoverableError()) {//说明此错误用户无法修复
//                return;
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


    /**
     * 初始化列表数据
     */
    void initFeed() {
        ServiceGenerator.createServcie(this, ApiService.class).markList(null, null).enqueue(new Callback<ContentList>() {
            @Override
            public void onResponse(Call<ContentList> call, Response<ContentList> response) {
                ContentList contentList = response.body();
                if (contentList == null) {
                    ll_collection.setVisibility(View.VISIBLE);
                    tv_edit.setVisibility(View.GONE);
                    return;
                }
                if (contentList.getData() == null) {
                    ll_collection.setVisibility(View.VISIBLE);
                    tv_edit.setVisibility(View.GONE);
                    return;
                }
                nextToken = contentList.getData().getNextToken();

                List<ContentList.DataBean.RowsBean> rows = contentList.getData().getRows();
                if (rows.size() == 0) {
                    ll_collection.setVisibility(View.VISIBLE);
                    tv_edit.setVisibility(View.GONE);
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
                    feed.setSave(rowsBean.isIsMarked());//在收藏页面的必须是true
                    feed.setDateTime(rowsBean.getPublish_time());
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
                }
                feedAdapter.notifyDataSetChanged();
                if (rowsBeans.size() < 1) {
                    ll_collection.setVisibility(View.VISIBLE);
                    tv_edit.setVisibility(View.GONE);
                } else {
                    ll_collection.setVisibility(View.GONE);
                    tv_edit.setVisibility(View.VISIBLE);
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<ContentList> call, Throwable t) {
                dialog.dismiss();
            }
        });
        feedAdapter.setOnItemClickListener(new CollectAndCommentFeedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                Feed feed = rowsBeans.get(position);
                if (feed.getType() == CollectAndCommentFeedAdapter.ITEM_TYEP.TYPE_ITEM_VIEW_ONE_PICTURE.ordinal() || feed.getType() == CollectAndCommentFeedAdapter.ITEM_TYEP.TYPE_ITEM_VIEW_THREE_PICTURE.ordinal()) {
                    startActivity(FeedWebActivity.newIntent(CollectionActivity.this, feed, FeedWebActivity.LastViewType.OTHER));
                } else {
                    startActivity(FeedVideoActivity.newIntent(CollectionActivity.this, feed, FeedVideoActivity.LastViewType.OTHER));
                }
            }
        });
        feedAdapter.setOnPlayClickListener(new CollectAndCommentFeedAdapter.OnPlayClickListener() {
            @Override
            public void onPlayClick(final int position) {
                if (NetUtil.isWifi(CollectionActivity.this)) {//wifi
                    showVedio(position);
                } else {//非wifi
                    if (SysApplication.isNoWiFiVedioPlay == false) {//没同意过
                        DialogUtil.showDialog(CollectionActivity.this, getString(R.string.wifiContent), new DialogUtil.OnDialogUtilListener() {
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

    }

    private void showVedio(int position) {
        //设置视频播放的逻辑
        fl.setVisibility(View.VISIBLE);//显示播放器控件
        Feed feed = rowsBeans.get(position);
        int itemType = feed.getType();
        if (itemType == CollectAndCommentFeedAdapter.ITEM_TYEP.TYPE_ITEM_VIEW_VIDEO.ordinal()) {
            View view = rv_collection.getLayoutManager().findViewByPosition(position + 1);
            if (view == null) {
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
        }
    }

    /**
     * 刷新加载列表数据
     */
    void loadMoreFeed() {
        String action = "scroll";
        ServiceGenerator.createServcie(this, ApiService.class).markList(nextToken, action).enqueue(new Callback<ContentList>() {
            @Override
            public void onResponse(Call<ContentList> call, Response<ContentList> response) {
                ContentList contentList = response.body();
                if (contentList.getData() == null) {
                    return;
                }
                nextToken = contentList.getData().getNextToken();
                List<ContentList.DataBean.RowsBean> rows = contentList.getData().getRows();
                ArrayList<Feed> feedTemps = new ArrayList<Feed>();
                //转换数据
                for (ContentList.DataBean.RowsBean rowsBean : rows) {
                    Feed feed = new Feed();
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
                    } else if (DataUtil.getFeedType(rowsBean) == FeedAdapter.ITEM_TYEP.TYPE_ITEM_VIEW_THREE_PICTURE.ordinal()) {
                        feed.setType(FeedDataItemView.TYPE_ITEM_VIEW_THREE_PICTURE);
                        feed.setAuthor(rowsBean.getSource());
                        feed.setCommitCount(rowsBean.getCommentCount());
                        feed.setThreePicUrl1(rowsBean.getImage().get(0));
                        feed.setThreePicUrl2(rowsBean.getImage().get(1));
                        feed.setThreePicUrl3(rowsBean.getImage().get(2));
                        feed.setTitle(rowsBean.getTitle());
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
                feedAdapter.notifyDataSetChanged();
                rv_collection.loadMoreComplete();
            }

            @Override
            public void onFailure(Call<ContentList> call, Throwable t) {

            }
        });
    }


    void cancelCollection(ArrayList<Feed> deletedFeeds) {
        dialog.show();
        if (deletedFeeds.size() == 0) {
            return;
        }
        RemoveMark rmMark = new RemoveMark();
        List<RemoveMark.IdListBean> idListBeans = new ArrayList<RemoveMark.IdListBean>();
        for (Feed feed : deletedFeeds) {
            RemoveMark.IdListBean idListBean = new RemoveMark.IdListBean();
            idListBean.setId(feed.getId());
            idListBean.setType(feed.getFeedType());
            idListBeans.add(idListBean);
        }
        rmMark.setId_list(idListBeans);
        ServiceGenerator.createServcie(this, ApiService.class).removeMark(rmMark).enqueue(new Callback<Basic>() {
            @Override
            public void onResponse(Call<Basic> call, Response<Basic> response) {
                Basic basic = response.body();
                if (basic.getCode() == Constants.REQUEST_SUCCESS) {
                    new ToastUtil(CollectionActivity.this, getString(R.string.operationSuccess), R.mipmap.ic_toast_yes).show();
                } else {
                    new ToastUtil(CollectionActivity.this, getString(R.string.operationFail), R.mipmap.ic_toast_no).show();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<Basic> call, Throwable t) {
                new ToastUtil(CollectionActivity.this, getString(R.string.operationFail), R.mipmap.ic_toast_no).show();
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
