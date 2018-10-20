package com.taiqudong.android.enayeh.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.taiqudong.android.ad.AdConfig;
import com.taiqudong.android.ad.AdContent;
import com.taiqudong.android.ad.AdError;
import com.taiqudong.android.ad.AdManager;
import com.taiqudong.android.ad.TNativeAd;
import com.taiqudong.android.ad.stats.EventConsts;
import com.taiqudong.android.ad.stats.EventLogger;
import com.taiqudong.android.enayeh.R;
import com.taiqudong.android.enayeh.activity.ExplainActivity;
import com.taiqudong.android.enayeh.application.retrofit.ServiceGenerator;
import com.taiqudong.android.enayeh.application.retrofit.api.ApiService;
import com.taiqudong.android.enayeh.application.retrofit.bean.Basic;
import com.taiqudong.android.enayeh.application.retrofit.bean.RemoveMark;
import com.taiqudong.android.enayeh.bean.Feed;
import com.taiqudong.android.enayeh.utils.Constants;
import com.taiqudong.android.enayeh.utils.ToastUtil;
import com.taiqudong.android.enayeh.view.FeedDataItemView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by taiqudong on 2017/7/13.
 */

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewHoloder> {
    private final static String TAG = "FeedAdapter";

    public enum ITEM_TYEP {
        TYPE_ITEM_VIEW_ONE_PICTURE, TYPE_ITEM_VIEW_THREE_PICTURE, TYPE_ITEM_VIEW_VIDEO, TYPE_ITEM_VIEW_VIDEOLIST, TYPE_ITEM_VIEW_PICLIST, TYPE_ITEM_AD;
    }

    private boolean canItemSelectable = false;//item是否处于可选状态
    private List<Integer> selectedItemNums = new ArrayList<>();//被选中的item位置集
    private Context mContext;
    private ArrayList<Feed> mFeedList;
    private OnItemClickListener onItemClickListener;
    private OnMoreClickListener onMoreClickListener;
    private SelectedListener selectedListener;
    private DeleteListener deleteListener;
    private OnPlayClickListener onPlayClickListener;
    private boolean hasComment = false;

    public FeedAdapter(ArrayList<Feed> feedList, Context context) {
        mFeedList = feedList;
        mContext = context;
        mHeight = dp2px(mContext, 202f);
    }

    public boolean isHasComment() {
        return hasComment;
    }

    public void setHasComment(boolean hasComment) {
        this.hasComment = hasComment;
    }

    @Override
    public FeedAdapter.FeedViewHoloder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FeedViewHoloder(LayoutInflater.from(mContext).inflate(R.layout.item_feed, null));
    }

    @Override
    public void onBindViewHolder(FeedAdapter.FeedViewHoloder holder, int position) {
        FeedDataItemView item = holder.itemView;
        //分类设置4种数据：一张图片文字、三张图片文字、视频文字、视频文字list
        if (getItemViewType(position) == ITEM_TYEP.TYPE_ITEM_VIEW_ONE_PICTURE.ordinal()) {
            setViewOnePicture(item, position);
        } else if (getItemViewType(position) == ITEM_TYEP.TYPE_ITEM_VIEW_THREE_PICTURE.ordinal()) {
            setViewThreePicture(item, position);
        } else if (getItemViewType(position) == ITEM_TYEP.TYPE_ITEM_VIEW_VIDEO.ordinal()) {
            setViewVideo(item, position);
        } else if (getItemViewType(position) == ITEM_TYEP.TYPE_ITEM_VIEW_VIDEOLIST.ordinal()) {
            setViewVideoList(item, position);
        } else if (getItemViewType(position) == ITEM_TYEP.TYPE_ITEM_VIEW_PICLIST.ordinal()) {
            setPicList(item, position);
        } else if (getItemViewType(position) == ITEM_TYEP.TYPE_ITEM_AD.ordinal()) {
            setAd(item, position);
        } else {
            throw new UnsupportedOperationException("please set your item's type!");
        }
        //隐藏最后一项的分割线
        if (position == mFeedList.size() - 1 && hasComment) {
            item.setDividerVisible(View.INVISIBLE);
        } else {
            item.setDividerVisible(View.VISIBLE);
        }
        Log.d("onBindViewHolder", "onBindViewHolder: " + position);
    }

    @Override
    public int getItemCount() {
        return mFeedList == null ? 0 : mFeedList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mFeedList.get(position).getType();
    }

    class FeedViewHoloder extends RecyclerView.ViewHolder {
        FeedDataItemView itemView;

        FeedViewHoloder(View view) {
            super(view);
            itemView = (FeedDataItemView) view.findViewById(R.id.fdiv_feed);
        }
    }

    /**
     * 设置一张图片文字
     *
     * @param item
     */
    void setViewOnePicture(final FeedDataItemView item, final int position) {
        item.setType(FeedDataItemView.TYPE_ITEM_VIEW_ONE_PICTURE);
        if (canItemSelectable) {//显示可选状态
            item.showSelect();
        } else {
            item.goneSelect();
        }
        item.setSelect(selectedItemNums.contains(position));//设置选择的状态
        item.setOnePictureIV(mFeedList.get(position).getOnePicUrl());
        item.setOnePictureTV(mFeedList.get(position).getTitle());
        item.setNewCollectVisibility(mFeedList.get(position).getSave());
        // TODO: 2017/8/29  intro
        item.setOnePictureTVContent(mFeedList.get(position).getIntro());
        item.setCommentCount(String.valueOf(mFeedList.get(position).getCommitCount()));
        item.setSource(mFeedList.get(position).getAuthor());
        item.setOnItemViewClickListener(new FeedDataItemView.OnItemViewClickListener() {
            @Override
            public void onItemViewClick() {
                Log.d("onItemViewClick", "onItemViewClick: " + canItemSelectable);
                if (canItemSelectable) {
                    item.setSelect(!item.isSelect());//设置item的选中状态
                    //记录选中的位置
                    if (item.isSelect()) {
                        selectedItemNums.add(position);
                        Log.d("selectItem", "SelectedItem: " + position);
                    } else {
                        selectedItemNums.remove(selectedItemNums.indexOf(position));
                        Log.d("selectItem", "deleteSelectedItem: " + selectedItemNums.indexOf(position));
                    }
                    addSelectedListener();
                } else if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });
        item.setOnMoreClickListener(new FeedDataItemView.OnMoreClickListener() {
            @Override
            public void onMoreClick(View view) {
                if (onMoreClickListener != null) {
                    onMoreClickListener.onMoreClick(position, view);
                }

            }
        });
    }

    /**
     * 设置三张图片文字
     *
     * @param item
     */
    void setViewThreePicture(final FeedDataItemView item, final int position) {
        item.setType(FeedDataItemView.TYPE_ITEM_VIEW_THREE_PICTURE);
        if (canItemSelectable) {//显示可选状态
            item.showSelect();
        } else {
            item.goneSelect();
        }
        item.setSelect(selectedItemNums.contains(position));//设置选择的状态
        item.setThreePictureIV1(mFeedList.get(position).getThreePicUrl1());
        item.setThreePictureIV2(mFeedList.get(position).getThreePicUrl2());
        item.setThreePictureIV3(mFeedList.get(position).getThreePicUrl3());
        item.setNewCollectVisibility(mFeedList.get(position).getSave());
        item.setThreePictureTV(mFeedList.get(position).getTitle());
        item.setCommentCount(String.valueOf(mFeedList.get(position).getCommitCount()));
        item.setSource(mFeedList.get(position).getAuthor());
        item.setOnItemViewClickListener(new FeedDataItemView.OnItemViewClickListener() {
            @Override
            public void onItemViewClick() {
                if (canItemSelectable) {
                    item.setSelect(!item.isSelect());//设置item的选中状态
                    //记录选中的位置
                    if (item.isSelect()) {
                        selectedItemNums.add(position);
                        Log.d("selectItem", "SelectedItem: " + position);
                    } else {
                        selectedItemNums.remove(selectedItemNums.indexOf(position));
                        Log.d("selectItem", "deleteSelectedItem: " + selectedItemNums.indexOf(position));
                    }
                    addSelectedListener();
                } else if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });

        item.setOnMoreClickListener(new FeedDataItemView.OnMoreClickListener() {
            @Override
            public void onMoreClick(View view) {
                if (onMoreClickListener != null) {
                    onMoreClickListener.onMoreClick(position, view);
                }
            }
        });
    }

    /**
     * 设置视频文字
     *
     * @param item
     */
    void setViewVideo(final FeedDataItemView item, final int position) {
        item.setType(FeedDataItemView.TYPE_ITEM_VIEW_VIDEO);
        if (canItemSelectable) {//显示可选状态
            item.showSelect();
        } else {
            item.goneSelect();
        }
        item.setCover(mFeedList.get(position).getCoverUrl());
        item.setSelect(selectedItemNums.contains(position));//设置选择的状态
        item.setVideoTV(mFeedList.get(position).getTitle());
        item.setNewCollectVisibility(mFeedList.get(position).getSave());
        item.setCommentCount(String.valueOf(mFeedList.get(position).getCommitCount()));
        item.setSource(mFeedList.get(position).getAuthor());
        item.setOnItemViewClickListener(new FeedDataItemView.OnItemViewClickListener() {
            @Override
            public void onItemViewClick() {
                Log.d("onItemViewClick", "onItemViewClick: " + canItemSelectable);
                if (canItemSelectable) {
                    item.setSelect(!item.isSelect());//设置item的选中状态
                    //记录选中的位置
                    if (item.isSelect()) {
                        selectedItemNums.add(position);
                        Log.d("selectItem", "SelectedItem: " + position);
                    } else {
                        selectedItemNums.remove(selectedItemNums.indexOf(position));
                        Log.d("selectItem", "deleteSelectedItem: " + selectedItemNums.indexOf(position));
                    }
                    addSelectedListener();
                } else if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });
        item.setOnMoreClickListener(new FeedDataItemView.OnMoreClickListener() {
            @Override
            public void onMoreClick(View view) {
                if (onMoreClickListener != null) {
                    onMoreClickListener.onMoreClick(position, view);
                }

            }
        });
        item.setOnItemPlayClickListener(new FeedDataItemView.OnItemPlayClickListener() {
            @Override
            public void onItemPlayClick() {
                if (onPlayClickListener != null) {
                    onPlayClickListener.onPlayClick(position);
                }
            }
        });
    }

    /**
     * 设置视频文字list
     *
     * @param item
     */
    void setViewVideoList(final FeedDataItemView item, final int position) {
        item.setType(FeedDataItemView.TYPE_ITEM_VIEW_VIDEOLIST);
        if (canItemSelectable) {//显示可选状态
            item.showSelect();
        } else {
            item.goneSelect();
        }
        item.setVideoListIV(mFeedList.get(position).getCoverUrl());
        item.setSelect(selectedItemNums.contains(position));//设置选择的状态
        item.setVideolistTV(mFeedList.get(position).getTitle());
        item.setVideolistCountTV(String.valueOf(mFeedList.get(position).getLookCount()));
        item.setVideolistTimeTV(mFeedList.get(position).getDuration());
        item.goneComment();//视频列表没有评论条
        item.setOnItemViewClickListener(new FeedDataItemView.OnItemViewClickListener() {
            @Override
            public void onItemViewClick() {
                Log.d("onItemViewClick", "onItemViewClick: " + canItemSelectable);
                if (canItemSelectable) {
                    item.setSelect(!item.isSelect());//设置item的选中状态
                    //记录选中的位置
                    if (item.isSelect()) {
                        selectedItemNums.add(position);
                        Log.d("selectItem", "SelectedItem: " + position);
                    } else {
                        selectedItemNums.remove(selectedItemNums.indexOf(position));
                        Log.d("selectItem", "deleteSelectedItem: " + selectedItemNums.indexOf(position));
                    }
                    addSelectedListener();
                } else if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });
        item.setOnMoreClickListener(new FeedDataItemView.OnMoreClickListener() {
            @Override
            public void onMoreClick(View view) {
                if (onMoreClickListener != null) {
                    onMoreClickListener.onMoreClick(position, view);
                }

            }
        });
    }


    /**
     * 设置图片list
     *
     * @param item
     */
    void setPicList(final FeedDataItemView item, final int position) {
        item.setType(FeedDataItemView.TYPE_ITEM_VIEW_PICLIST);
        if (canItemSelectable) {//显示可选状态
            item.showSelect();
        } else {
            item.goneSelect();
        }
        item.setVideoListIV(mFeedList.get(position).getCoverUrl());
        item.setSelect(selectedItemNums.contains(position));//设置选择的状态
        item.setVideolistTV(mFeedList.get(position).getTitle());
        item.setVideolistCountTV(String.valueOf(mFeedList.get(position).getLookCount()));
        item.goneComment();//视频列表没有评论条
        item.setOnItemViewClickListener(new FeedDataItemView.OnItemViewClickListener() {
            @Override
            public void onItemViewClick() {
                Log.d("onItemViewClick", "onItemViewClick: " + canItemSelectable);
                if (canItemSelectable) {
                    item.setSelect(!item.isSelect());//设置item的选中状态
                    //记录选中的位置
                    if (item.isSelect()) {
                        selectedItemNums.add(position);
                        Log.d("selectItem", "SelectedItem: " + position);
                    } else {
                        selectedItemNums.remove(selectedItemNums.indexOf(position));
                        Log.d("selectItem", "deleteSelectedItem: " + selectedItemNums.indexOf(position));
                    }
                    addSelectedListener();
                } else if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });
    }


    /**
     * TODO 加载广告(按照内容的有无制定策略。)
     * txm 20170906
     * fgr 20170914 修改AdManager.getNativeAd(String placeId, ViewGroup parent)为AdManager.getNativeAd(AdConfig config, ViewGroup parent)
     * 解决大广告变小广告，小广告变大广告问题，
     */
    void setAd(final FeedDataItemView item, final int position) {
        LinearLayout adBigLl = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.ad_big_load, null);
        LinearLayout adSmallLl = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.ad_small_load, null);
        final Feed feed = mFeedList.get(position);
        item.setType(FeedDataItemView.TYPE_ITEM_VIEW_AD);
        final FrameLayout fl = (FrameLayout) item.findViewById(R.id.fl_ad_container);
        fl.removeAllViews();
        final AdConfig config = feed.getAdConfig();
        LinearLayout adDayima = null;
        //TODO 加载视图
        if (config.getType().equals(TNativeAd.typeBig)) {
            fl.addView(adBigLl);
            adDayima = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.ad_big_dayima, null);
        } else if (config.getType().equals(TNativeAd.typeSmall)) {
            fl.addView(adSmallLl);
            adDayima = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.ad_small_dayima, null);
        }
        if (feed.getAdContent() != null) {
            // TODO 有广告时候，直接展示数据即可。
            Log.d(TAG, "Lucky,list has ad content before.");
            AdManager.getNativeAd(config, fl).setAdContent(feed.getAdContent()).bindView();
        } else {
            //TODO 没有广告时候，要请求加载广告。
            TNativeAd nativeAd = AdManager.getNativeAd(config, fl);
            final LinearLayout finalAdDayima = adDayima;
            nativeAd.loadAd(new TNativeAd.ContentAdListener() {
                @Override
                public void call(AdContent adContent) {
                    Log.d(TAG, "load ad content again to feed:" + feed.getIndex());
                    //保存广告内容到实体类。
                    feed.setAdContent(adContent);
                }

                @Override
                public void onError(AdError error) {
                    Log.d(TAG, "load ad content again to feed fail:" + error.getMessage() + ";" + feed.getIndex());
                    if (feed.getAdContent() == null) {
                        fl.removeAllViews();
                        finalAdDayima.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mContext.startActivity(new Intent(mContext, ExplainActivity.class));
                            }
                        });
                        fl.addView(finalAdDayima);
                    } else {
                        AdManager.getNativeAd(config, fl).setAdContent(feed.getAdContent()).bindView();
                    }
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnMoreClickListener(OnMoreClickListener onMoreClickListener) {
        this.onMoreClickListener = onMoreClickListener;
    }

    public void setSelectedListener(SelectedListener selectedListener) {
        this.selectedListener = selectedListener;
    }


    public void setDeleteListener(DeleteListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    /**
     * Item的点击事件
     */
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    /**
     * 更多的点击事件
     */
    public interface OnMoreClickListener {
        void onMoreClick(int position, View view);
    }

    //设置是否显示选择
    public void canSelectable(boolean canItemSelectable) {
        this.canItemSelectable = canItemSelectable;
        if (!canItemSelectable) {
            selectedItemNums.clear();
        }
        notifyDataSetChanged();
    }

    //选中所有item
    public void seletAllItem(boolean allItemSelected) {
        selectedItemNums.clear();//全选的情况下不管如何，都要清空原来的list，因为判断是通过size
        if (allItemSelected) {
            for (int position = 0; position < mFeedList.size(); position++) {
                selectedItemNums.add(position);
            }
        }
        notifyDataSetChanged();
        selectedListener.onSelectAll(allItemSelected);
        //        addSelectedListener();
    }

    //删除选中项
    public void deleteSelectedItem() {
        if (selectedItemNums.size() == 0) {
            return;
        }
        ArrayList<Feed> temps = new ArrayList<>();
        //selectedItemNums中保存的是位置，这样可以减少内存的消耗。
        for (int positon : selectedItemNums) {
            temps.add(mFeedList.get(positon));
        }
        mFeedList.removeAll(temps);
        selectedItemNums.clear();
        notifyDataSetChanged();
        addSelectedListener();
    }

    //删除选中项
    public void deleteSelectedItem(DeleteListener deleteListener) {
        ArrayList<Feed> deletedFeeds = new ArrayList<>();
        for (int positon : selectedItemNums) {
            deletedFeeds.add(mFeedList.get(positon));
        }
        if (deleteListener != null) {
            deleteListener.onDelete(deletedFeeds);
        }
        deleteSelectedItem();
        addSelectedListener();
    }

    private void addSelectedListener() {
        //监听选择的操作
        if (selectedListener != null) {
            boolean isAllSelected = false;
            if (selectedItemNums.size() == mFeedList.size()) {
                isAllSelected = true;
            } else {
                isAllSelected = false;
            }
            selectedListener.onSelectAll(isAllSelected);
        }
    }


    /**
     * 选中状态的监听
     */
    public interface SelectedListener {
        /**
         * @param isAllSelected 是否全选
         */
        void onSelectAll(boolean isAllSelected);
    }


    public interface DeleteListener {
        /**
         * @param deletedFeeds 被删除的实体类列表
         */
        void onDelete(ArrayList<Feed> deletedFeeds);
    }


    public void showMoreDialog(Context context, RecyclerView rcv, int position) {
        //计算偏移量
        View view = rcv.getLayoutManager().findViewByPosition(position);

        Dialog dialog = new Dialog(context, R.style.MoreDialog);
        dialog.setContentView(R.layout.dialog);
        //获取到当前Activity的Window
        Window dialog_window = dialog.getWindow();
        //设置对话框的位置
        dialog_window.setGravity(Gravity.TOP | Gravity.LEFT);
        //获取到LayoutParams
        WindowManager.LayoutParams dialog_window_attributes = dialog_window.getAttributes();
        //设置对话框位置的偏移量
        dialog_window_attributes.x = 430;
        dialog_window_attributes.width = 650;
        dialog_window_attributes.height = 545;
        dialog_window.setAttributes(dialog_window_attributes);
        dialog.show();
    }

    int mHeight = 0;//dp2xp  202 第一次点击的时候，mPopupViewHeight有可能为0
    int mPopupViewHeight = 0;
    private Dialog reportDialog;

    public void showMorePopupWindows(final Context context, final RecyclerView rcv, final int position, View view) {
        final ImageView imageView = (ImageView) view;//more
        imageView.setImageResource(R.mipmap.ic_moremore_s);
        setHuise();//设置背景为灰色

        final View popupView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog, null);//popupWindow布局
        final ImageView iv_more_save = (ImageView) popupView.findViewById(R.id.iv_more_save);
        final TextView tv_save_title = (TextView) popupView.findViewById(R.id.tv_save_title);
        final TextView tv_save_message = (TextView) popupView.findViewById(R.id.tv_save_message);

        final Feed feed = mFeedList.get(position);

        if (feed.getSave()) {
            iv_more_save.setImageResource(R.mipmap.ic_collect_s);
            tv_save_title.setText(context.getString(R.string.more5));
            tv_save_message.setText(context.getString(R.string.more5explain));
        }

        final PopupWindow popupWindow = new PopupWindow(popupView,
                RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);//不向下传递
        popupWindow.setOutsideTouchable(false);

        popupView.findViewById(R.id.ll_save).setOnClickListener(new View.OnClickListener() {//收藏
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                //
                // 收藏网络数据
                //
                if (!feed.getSave()) {//收藏
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("id", feed.getId());
                    map.put("type", feed.getFeedType());
                    ServiceGenerator.createServcie(context, ApiService.class).mark(map).enqueue(new Callback<Basic>() {
                        @Override
                        public void onResponse(Call<Basic> call, Response<Basic> response) {
                            Basic basic = response.body();
                            if (basic.getCode() == Constants.REQUEST_SUCCESS) {
                                iv_more_save.setImageResource(R.mipmap.ic_collect_d);
                                feed.setSave(!feed.getSave());
                                notifyDataSetChanged();
                                new ToastUtil(context, context.getString(R.string.collectionSuccess), R.mipmap.ic_toast_yes).show();

                                //收藏-图片信息流 more ListPicSave
                                if (getItemViewType(position) == ITEM_TYEP.TYPE_ITEM_VIEW_ONE_PICTURE.ordinal()) {
                                    EventLogger.logEvent(EventConsts.ListPicSave);
                                } else if (getItemViewType(position) == ITEM_TYEP.TYPE_ITEM_VIEW_THREE_PICTURE.ordinal()) {
                                    EventLogger.logEvent(EventConsts.ListPicSave);
                                } else if (getItemViewType(position) == ITEM_TYEP.TYPE_ITEM_VIEW_VIDEO.ordinal()) {
                                    //收藏uv-视频信息流 more ListVideoSave
                                    EventLogger.logEvent(EventConsts.ListVideoSave);
                                }
                            } else {
                                new ToastUtil(context, context.getString(R.string.collectionFail), R.mipmap.ic_toast_no).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Basic> call, Throwable t) {
                            new ToastUtil(context, context.getString(R.string.collectionFail), R.mipmap.ic_toast_no).show();
                        }
                    });
                } else {//取消收藏
                    RemoveMark rmMark = new RemoveMark();
                    List<RemoveMark.IdListBean> idListBeans = new ArrayList<RemoveMark.IdListBean>();
                    RemoveMark.IdListBean idListBean = new RemoveMark.IdListBean();
                    idListBean.setId(feed.getId());
                    idListBean.setType(feed.getFeedType());
                    idListBeans.add(idListBean);
                    rmMark.setId_list(idListBeans);
                    ServiceGenerator.createServcie(context, ApiService.class).removeMark(rmMark).enqueue(new Callback<Basic>() {
                        @Override
                        public void onResponse(Call<Basic> call, Response<Basic> response) {
                            Basic basic = response.body();
                            if (basic.getCode() == Constants.REQUEST_SUCCESS) {
                                iv_more_save.setImageResource(R.mipmap.ic_collect_s);
                                feed.setSave(!feed.getSave());
                                notifyDataSetChanged();
                                new ToastUtil(context, context.getString(R.string.operationSuccess), R.mipmap.ic_toast_yes).show();
                            } else {
                                new ToastUtil(context, context.getString(R.string.operationFail), R.mipmap.ic_toast_no).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Basic> call, Throwable t) {
                            new ToastUtil(context, context.getString(R.string.operationFail), R.mipmap.ic_toast_no).show();
                        }
                    });
                }

            }
        });
        popupView.findViewById(R.id.ll_noinsterest).setOnClickListener(new View.OnClickListener() {//不喜欢
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();

                //用户行为-图片内容 不感兴趣
                if (getItemViewType(position) == ITEM_TYEP.TYPE_ITEM_VIEW_ONE_PICTURE.ordinal()) {
                    EventLogger.logEvent(EventConsts.ListPicMore, EventConsts.action, EventConsts.noInterested);
                } else if (getItemViewType(position) == ITEM_TYEP.TYPE_ITEM_VIEW_THREE_PICTURE.ordinal()) {
                    EventLogger.logEvent(EventConsts.ListPicMore, EventConsts.action, EventConsts.noInterested);
                } else if (getItemViewType(position) == ITEM_TYEP.TYPE_ITEM_VIEW_VIDEO.ordinal()) {
                    //用户行为-视频内容 不感兴趣
                    EventLogger.logEvent(EventConsts.ListVideoMore, EventConsts.action, EventConsts.noInterested);
                }

                //
                // 不喜欢网络数据
                //
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("id", feed.getId());
                map.put("type", feed.getFeedType());
                map.put("ctg", feed.getCtg());
                ServiceGenerator.createServcie(context, ApiService.class).notInterested(map).enqueue(new Callback<Basic>() {
                    @Override
                    public void onResponse(Call<Basic> call, Response<Basic> response) {
                        Basic basic = response.body();
                        if (basic.getCode() == Constants.REQUEST_SUCCESS) {
                            //不喜欢
                            mFeedList.remove(position);
                            notifyDataSetChanged();
                            new ToastUtil(context, context.getString(R.string.operationSuccess), R.mipmap.ic_toast_yes).show();
                        } else {
                            new ToastUtil(context, context.getString(R.string.operationFail), R.mipmap.ic_toast_no).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Basic> call, Throwable t) {
                        new ToastUtil(context, context.getString(R.string.operationFail), R.mipmap.ic_toast_no).show();
                    }
                });
            }
        });
        popupView.findViewById(R.id.ll_report).setOnClickListener(new View.OnClickListener() {//举报
            @Override
            public void onClick(View v) {
                reportDialog = new Dialog(mContext, R.style.MyDialog);
                LinearLayout root = (LinearLayout) LayoutInflater.from(mContext).inflate(
                        R.layout.dialog_report, null);
                root.findViewById(R.id.report1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reportDialog.dismiss();
                        //
                        // 举报网络数据
                        //
                        report(feed, context, context.getString(R.string.report1));
                    }
                });
                root.findViewById(R.id.report2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //
                        // 举报网络数据
                        //
                        reportDialog.dismiss();
                        report(feed, context, context.getString(R.string.report2));
                    }
                });
                root.findViewById(R.id.report3).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //
                        // 举报网络数据
                        //
                        reportDialog.dismiss();
                        report(feed, context, context.getString(R.string.report3));
                    }
                });
                root.findViewById(R.id.report4).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //
                        // 举报网络数据
                        //
                        reportDialog.dismiss();
                        report(feed, context, context.getString(R.string.report4));

                    }
                });
                root.findViewById(R.id.report5).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //
                        // 举报网络数据
                        //
                        reportDialog.dismiss();
                        report(feed, context, context.getString(R.string.report5));

                    }
                });
                root.findViewById(R.id.reportCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reportDialog.dismiss();
                    }
                });
                Window dialogWindow = reportDialog.getWindow();
                dialogWindow.setGravity(Gravity.CENTER);
                //很神奇的代码，有了这段代码，dialog就会弹到中间，否则会弹到上面
                WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
                root.measure(0, 0);
                lp.height = root.getMeasuredHeight();
                reportDialog.setContentView(root);
                reportDialog.show();
                popupWindow.dismiss();
            }
        });

        //计算pupup显示位置
        int[] viewPosition = new int[2];
        view.getLocationOnScreen(viewPosition);
        int viewHight = viewPosition[1];//more相对屏幕的高度
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        wm.getDefaultDisplay().getSize(point);
        int height = point.y;//屏幕的高度
        popupView.post(new Runnable() {//popupViwe的实际高度
            @Override
            public void run() {
                mPopupViewHeight = popupView.getHeight();
            }
        });
        if (viewHight > height / 2) {//在more在屏幕的下方
            if (mHeight > mPopupViewHeight + view.getBottom()) {//第一次点击的时候，mPopupViewHeight有可能为0
                popupWindow.showAsDropDown(view, 0, -mHeight);
            } else {
                popupWindow.showAsDropDown(view, 0, -mPopupViewHeight - view.getBottom());
            }
        } else {
            popupWindow.showAsDropDown(view);
        }

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {//popupWindow消失的时候，设置为白色
            @Override
            public void onDismiss() {
                setBaise();
                imageView.setImageResource(R.mipmap.ic_moremore);
            }
        });

    }

    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void setHuise() {
        ViewGroup parent = (ViewGroup) ((Activity) mContext).getWindow().getDecorView().getRootView();
        Drawable dim = new ColorDrawable(Color.BLACK);
        dim.setBounds(0, 0, parent.getWidth(), parent.getHeight());
        dim.setAlpha((int) (255 * 0.7));
        ViewGroupOverlay overlay = parent.getOverlay();
        overlay.add(dim);

        //        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow().getAttributes();
        //        lp.alpha = 1.0f;
        //        ((Activity) mContext).getWindow().setAttributes(lp);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void setBaise() {
        ViewGroup parent = (ViewGroup) ((Activity) mContext).getWindow().getDecorView().getRootView();
        ViewGroupOverlay overlay = parent.getOverlay();
        overlay.clear();

        //        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow().getAttributes();
        //        lp.alpha = 1.0f;
        //        ((Activity) mContext).getWindow().setAttributes(lp);
    }

    public void setOnPlayClickListener(OnPlayClickListener onPlayClickListener) {
        this.onPlayClickListener = onPlayClickListener;
    }

    public interface OnPlayClickListener {
        void onPlayClick(int position);
    }

    public void report(Feed feed, final Context context, String reason) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("id", feed.getId());
        map.put("reason", reason);
        map.put("type", feed.getFeedType());
        ServiceGenerator.createServcie(context, ApiService.class).report(map).enqueue(new Callback<Basic>() {
            @Override
            public void onResponse(Call<Basic> call, Response<Basic> response) {
                Basic basic = response.body();
                if (basic.getCode() == Constants.REQUEST_SUCCESS) {
                    new ToastUtil(context, context.getString(R.string.report_success), R.mipmap.ic_toast_yes).show();
                } else {
                    new ToastUtil(context, context.getString(R.string.report_fail), R.mipmap.ic_toast_no).show();
                }
            }

            @Override
            public void onFailure(Call<Basic> call, Throwable t) {
                new ToastUtil(context, context.getString(R.string.report_fail), R.mipmap.ic_toast_no).show();
            }
        });
    }
}