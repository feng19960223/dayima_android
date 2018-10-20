package com.taiqudong.android.enayeh.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.taiqudong.android.enayeh.R;
import com.taiqudong.android.enayeh.bean.Feed;
import com.taiqudong.android.enayeh.view.CollectAndCommentFeedDataItemView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by taiqudong on 2017/7/13.
 */

public class CollectAndCommentFeedAdapter extends RecyclerView.Adapter<CollectAndCommentFeedAdapter.FeedViewHoloder> {
    public enum ITEM_TYEP {
        TYPE_ITEM_VIEW_ONE_PICTURE, TYPE_ITEM_VIEW_THREE_PICTURE, TYPE_ITEM_VIEW_VIDEO;
    }

    private boolean canItemSelectable = false;//item是否处于可选状态
    private List<Integer> selectedItemNums = new ArrayList<>();//被选中的item位置集
    private Context mContext;
    private ArrayList<Feed> mFeedList;
    private CollectAndCommentFeedAdapter.OnItemClickListener onItemClickListener;
    private CollectAndCommentFeedAdapter.SelectedListener selectedListener;
    private CollectAndCommentFeedAdapter.DeleteListener deleteListener;
    private CollectAndCommentFeedAdapter.OnPlayClickListener onPlayClickListener;

    public CollectAndCommentFeedAdapter(ArrayList<Feed> feedList, Context context) {
        mFeedList = feedList;
        mContext = context;
    }

    @Override
    public CollectAndCommentFeedAdapter.FeedViewHoloder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CollectAndCommentFeedAdapter.FeedViewHoloder(LayoutInflater.from(mContext).inflate(R.layout.item_feed_collectandcomment, null));
    }

    @Override
    public void onBindViewHolder(CollectAndCommentFeedAdapter.FeedViewHoloder holder, int position) {
        CollectAndCommentFeedDataItemView item = holder.itemView;
        //分类设置4种数据：一张图片文字、三张图片文字、视频文字、视频文字list
        if (getItemViewType(position) == CollectAndCommentFeedAdapter.ITEM_TYEP.TYPE_ITEM_VIEW_ONE_PICTURE.ordinal()) {
            setViewOnePicture(item, position);
        } else if (getItemViewType(position) == CollectAndCommentFeedAdapter.ITEM_TYEP.TYPE_ITEM_VIEW_THREE_PICTURE.ordinal()) {
            setViewThreePicture(item, position);
        } else if (getItemViewType(position) == CollectAndCommentFeedAdapter.ITEM_TYEP.TYPE_ITEM_VIEW_VIDEO.ordinal()) {
            setViewVideo(item, position);
        } else {
            throw new UnsupportedOperationException("please set your item's type!");
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
        CollectAndCommentFeedDataItemView itemView;

        FeedViewHoloder(View view) {
            super(view);
            itemView = (CollectAndCommentFeedDataItemView) view.findViewById(R.id.cac_fdiv);
        }
    }

    /**
     * 设置一张图片文字
     *
     * @param item
     */
    void setViewOnePicture(final CollectAndCommentFeedDataItemView item, final int position) {
        item.setType(CollectAndCommentFeedDataItemView.TYPE_ITEM_VIEW_ONE_PICTURE);
        if (canItemSelectable) {//显示可选状态
            item.showSelect();
        } else {
            item.goneSelect();
        }
        item.setSelect(selectedItemNums.contains(position));//设置选择的状态
        item.setNewCollectIV(mFeedList.get(position).getSave());
        item.setOnePictureIV(mFeedList.get(position).getOnePicUrl());
        item.setOnePictureTV(mFeedList.get(position).getTitle());
        item.setOnePictureTVContent(mFeedList.get(position).getIntro());
        // TODO: 2017/8/29  intro
        item.setCommentCount(String.valueOf(mFeedList.get(position).getCommitCount()));
        item.setSource(mFeedList.get(position).getAuthor());
        item.setOnItemViewClickListener(new CollectAndCommentFeedDataItemView.OnItemViewClickListener() {
            @Override
            public void onItemViewClick() {
                if (canItemSelectable) {
                    item.setSelect(!item.isSelect());//设置item的选中状态
                    //记录选中的位置
                    if (item.isSelect()) {
                        selectedItemNums.add(position);
                    } else {
                        selectedItemNums.remove(selectedItemNums.indexOf(position));
                    }
                    addSelectedListener();
                } else if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });
    }

    /**
     * 设置三张图片文字
     *
     * @param item
     */
    void setViewThreePicture(final CollectAndCommentFeedDataItemView item, final int position) {
        item.setType(CollectAndCommentFeedDataItemView.TYPE_ITEM_VIEW_THREE_PICTURE);
        if (canItemSelectable) {//显示可选状态
            item.showSelect();
        } else {
            item.goneSelect();
        }
        item.setNewCollectIV(mFeedList.get(position).getSave());
        item.setSelect(selectedItemNums.contains(position));//设置选择的状态
        item.setThreePictureIV1(mFeedList.get(position).getThreePicUrl1());
        item.setThreePictureIV2(mFeedList.get(position).getThreePicUrl2());
        item.setThreePictureIV3(mFeedList.get(position).getThreePicUrl3());
        item.setThreePictureTV(mFeedList.get(position).getTitle());
        item.setCommentCount(String.valueOf(mFeedList.get(position).getCommitCount()));
        item.setSource(mFeedList.get(position).getAuthor());
        item.setOnItemViewClickListener(new CollectAndCommentFeedDataItemView.OnItemViewClickListener() {
            @Override
            public void onItemViewClick() {
                if (canItemSelectable) {
                    item.setSelect(!item.isSelect());//设置item的选中状态
                    //记录选中的位置
                    if (item.isSelect()) {
                        selectedItemNums.add(position);
                    } else {
                        selectedItemNums.remove(selectedItemNums.indexOf(position));
                    }
                    addSelectedListener();
                } else if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });

    }

    /**
     * 设置视频文字
     *
     * @param item
     */
    void setViewVideo(final CollectAndCommentFeedDataItemView item, final int position) {
        item.setType(CollectAndCommentFeedDataItemView.TYPE_ITEM_VIEW_VIDEO);
        if (canItemSelectable) {//显示可选状态
            item.showSelect();
        } else {
            item.goneSelect();
        }
        item.setNewCollectIV(mFeedList.get(position).getSave());
        item.setVideoThumbnailIV(mFeedList.get(position).getCoverUrl());
        item.setSelect(selectedItemNums.contains(position));//设置选择的状态
        item.setVideoTV(mFeedList.get(position).getTitle());
        item.setCommentCount(String.valueOf(mFeedList.get(position).getCommitCount()));
        item.setSource(mFeedList.get(position).getAuthor());
        item.setOnItemViewClickListener(new CollectAndCommentFeedDataItemView.OnItemViewClickListener() {
            @Override
            public void onItemViewClick() {
                if (canItemSelectable) {
                    item.setSelect(!item.isSelect());//设置item的选中状态
                    //记录选中的位置
                    if (item.isSelect()) {
                        selectedItemNums.add(position);
                    } else {
                        selectedItemNums.remove(selectedItemNums.indexOf(position));
                    }
                    addSelectedListener();
                } else if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });
        item.setOnItemViewPlayListener(new CollectAndCommentFeedDataItemView.OnItemViewPlayListener() {
            @Override
            public void onPlay() {
                if (canItemSelectable) {
                    return;
                }
                if (onPlayClickListener != null) {
                    onPlayClickListener.onPlayClick(position);
                }
            }
        });
    }


    public void setOnItemClickListener(CollectAndCommentFeedAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setSelectedListener(CollectAndCommentFeedAdapter.SelectedListener selectedListener) {
        this.selectedListener = selectedListener;
    }


    public void setDeleteListener(CollectAndCommentFeedAdapter.DeleteListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    /**
     * Item的点击事件
     */
    public interface OnItemClickListener {
        void onItemClick(int position);
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
        deleteListener.onDelete(temps);
        addSelectedListener();
    }

    //删除选中项
    public void deleteSelectedItem(CollectAndCommentFeedAdapter.DeleteListener deleteListener) {
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
            if (mFeedList.size() == 0) {
                return;
            }
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


    public void setOnPlayClickListener(CollectAndCommentFeedAdapter.OnPlayClickListener onPlayClickListener) {
        this.onPlayClickListener = onPlayClickListener;
    }

    public interface OnPlayClickListener {
        void onPlayClick(int position);
    }
}