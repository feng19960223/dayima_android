package com.taiqudong.android.enayeh.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.youtube.player.YouTubePlayerView;
import com.taiqudong.android.enayeh.R;
import com.taiqudong.android.enayeh.utils.NumberUtil;

/**
 * Created by taiqudong on 2017/8/17.
 */

public class FeedDataItemView extends LinearLayout implements View.OnClickListener {
    public static final int TYPE_ITEM_VIEW_ONE_PICTURE = 0;
    public static final int TYPE_ITEM_VIEW_THREE_PICTURE = 1;
    public static final int TYPE_ITEM_VIEW_VIDEO = 2;
    public static final int TYPE_ITEM_VIEW_VIDEOLIST = 3;
    public static final int TYPE_ITEM_VIEW_PICLIST = 4;
    public static final int TYPE_ITEM_VIEW_AD = 5;
    private Context context;

    public FeedDataItemView(Context context) {
        this(context, null, 0);
    }

    public FeedDataItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FeedDataItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.item_data_feed_view, this);
        init();
    }

    private LinearLayout ll_itemview;

    private ImageView iv_select;//编辑按钮

    private LinearLayout ll_onePicture;//一张图片
    private TextView tv_onePicture;//一张图片文字
    private TextView tv_onePicture_content;//一张图片文字
    private ImageView iv_onePicture;//一张图片图片

    private LinearLayout ll_threePicture;//三张图片
    private TextView tv_threePicture;//三张图片文字
    private ImageView iv_threePicture1;//三张图片图片1
    private ImageView iv_threePicture2;//三张图片图片2
    private ImageView iv_threePicture3;//三张图片图片3

    private LinearLayout ll_video;//视频
    private TextView tv_video;//视频title
    private YouTubePlayerView youtube;//youtube视频
    private ImageView iv_thumbnail;//视频缩略图
    private ImageView iv_play;//视频播放按钮

    private LinearLayout ll_videolist;
    private TextView tv_videolist;
    private TextView tv_videolist_count;
    private ImageView iv_videolist;
    private TextView tv_videolist_time;
    private View divider;
    private FrameLayout fl_ad;

    private LinearLayout ll_comment;//评论
    private ImageView iv_newcollect;//收藏
    private TextView tv_source;//评论来源，作者。。。
    private ImageView iv_comment;//评论图片
    private TextView tv_comment_count;//评论数
    private ImageView tv_more;//评论更多

    private void init() {
        ll_itemview = (LinearLayout) findViewById(R.id.ll_itemview);//view

        iv_select = (ImageView) findViewById(R.id.iv_select);//选中

        ll_onePicture = (LinearLayout) findViewById(R.id.ll_onePicture);//一张图片
        tv_onePicture = (TextView) findViewById(R.id.tv_onePicture);
        tv_onePicture_content = (TextView) findViewById(R.id.tv_onePicture_content);
        iv_onePicture = (ImageView) findViewById(R.id.iv_onePicture);

        ll_threePicture = (LinearLayout) findViewById(R.id.ll_threePicture);//三张图片
        tv_threePicture = (TextView) findViewById(R.id.tv_threePicture);
        iv_threePicture1 = (ImageView) findViewById(R.id.iv_threePicture1);
        iv_threePicture2 = (ImageView) findViewById(R.id.iv_threePicture2);
        iv_threePicture3 = (ImageView) findViewById(R.id.iv_threePicture3);

        ll_video = (LinearLayout) findViewById(R.id.ll_video);//视频
        tv_video = (TextView) findViewById(R.id.tv_video);
        //        youtube = (YouTubePlayerView) findViewById(R.id.youtube);
        iv_thumbnail = (ImageView) findViewById(R.id.iv_thumbnail);
        iv_play = (ImageView) findViewById(R.id.iv_play);

        ll_videolist = (LinearLayout) findViewById(R.id.ll_videolist);//视频列表
        tv_videolist = (TextView) findViewById(R.id.tv_videolist);
        tv_videolist_count = (TextView) findViewById(R.id.tv_videolist_count);
        iv_videolist = (ImageView) findViewById(R.id.iv_videolist);
        tv_videolist_time = (TextView) findViewById(R.id.tv_videolist_time);

        ll_comment = (LinearLayout) findViewById(R.id.ll_comment);//评论
        iv_newcollect = (ImageView) findViewById(R.id.iv_newcollect);
        tv_source = (TextView) findViewById(R.id.tv_source);
        iv_comment = (ImageView) findViewById(R.id.iv_comment);
        tv_comment_count = (TextView) findViewById(R.id.tv_comment_count);
        tv_more = (ImageView) findViewById(R.id.tv_more);
        divider = (View) findViewById(R.id.view_line);
        fl_ad = (FrameLayout) findViewById(R.id.fl_ad_container);

        ll_itemview.setOnClickListener(this);
        iv_comment.setOnClickListener(this);
        tv_comment_count.setOnClickListener(this);
        tv_more.setOnClickListener(this);
        iv_newcollect.setOnClickListener(this);

        iv_play.setOnClickListener(this);
    }

    public void setNewCollectVisibility(boolean visibility) {
        iv_newcollect.setVisibility(visibility ? VISIBLE : GONE);
    }

    //设置itemview是什么，一张图片，三张图片，视频,广告
    public void setType(int type) {
        iv_select.setVisibility(GONE);
        ll_onePicture.setVisibility(GONE);
        ll_threePicture.setVisibility(GONE);
        ll_video.setVisibility(GONE);
        ll_videolist.setVisibility(GONE);
        if (type == TYPE_ITEM_VIEW_ONE_PICTURE) {
            ll_onePicture.setVisibility(VISIBLE);
            ll_comment.setVisibility(VISIBLE);
        } else if (type == TYPE_ITEM_VIEW_THREE_PICTURE) {
            ll_threePicture.setVisibility(VISIBLE);
            ll_comment.setVisibility(VISIBLE);
        } else if (type == TYPE_ITEM_VIEW_VIDEO) {
            ll_video.setVisibility(VISIBLE);
            ll_comment.setVisibility(VISIBLE);
        } else if (type == TYPE_ITEM_VIEW_VIDEOLIST) {
            ll_videolist.setVisibility(VISIBLE);
            ll_comment.setVisibility(GONE);
        } else if (type == TYPE_ITEM_VIEW_PICLIST) {
            ll_videolist.setVisibility(VISIBLE);
            ll_comment.setVisibility(GONE);
            tv_videolist_time.setVisibility(GONE);
        } else if (type == TYPE_ITEM_VIEW_AD) {
            fl_ad.setVisibility(View.VISIBLE);
            ll_comment.setVisibility(GONE);
            tv_videolist_time.setVisibility(GONE);
        }
    }

    //设置分割线的显示与否
    public void setDividerVisible(int visibleable) {
        divider.setVisibility(visibleable);
    }

    //显示选中
    public void showSelect() {
        iv_select.setVisibility(VISIBLE);
    }

    //显示选中
    public void goneSelect() {
        iv_select.setImageResource(R.mipmap.ic_quan_s);
        iv_select.setVisibility(GONE);
    }

    private boolean isSelect = false;

    //设置选中
    public void setSelect(boolean select) {
        isSelect = select;
        iv_select.setImageResource(isSelect ? R.mipmap.ic_quan_s : R.mipmap.ic_quan_d);
    }

    public boolean isSelect() {
        return isSelect;
    }

    //显示评论条
    public void showComment() {
        ll_comment.setVisibility(VISIBLE);
    }

    public void goneComment() {//隐藏评论条
        ll_comment.setVisibility(GONE);
    }

    //设置评论数
    public void setCommentCount(String commentCount) {
        if (TextUtils.isEmpty(commentCount)) {//null
            tv_comment_count.setText("0");//评论
        } else {
            try {
                long count = Long.parseLong(commentCount);
                if (count >= 1) {
                    tv_comment_count.setText(NumberUtil.getChangeCount(count));
                } else {
                    tv_comment_count.setText("0");
                }
            } catch (Exception e) {
                tv_comment_count.setText("0");
            }
        }
    }

    //设置来源
    public void setSource(String source) {
        if (!TextUtils.isEmpty(source)) {
            tv_source.setText(source);
        }
    }

    //设置一张图片的title
    public void setOnePictureTV(String title) {
        if (!TextUtils.isEmpty(title)) {
            tv_onePicture.setText(title);
        }
    }

    //设置一张图片的title
    public void setOnePictureTVContent(String content) {
        if (!TextUtils.isEmpty(content)) {
            tv_onePicture_content.setText(content);
        }
    }

    //设置三张图片的title
    public void setThreePictureTV(String title) {
        if (!TextUtils.isEmpty(title)) {
            tv_threePicture.setText(title);
        }
    }

    //设置视频的title
    public void setVideoTV(String title) {
        if (!TextUtils.isEmpty(title)) {
            tv_video.setText(title);
        }
    }

    //设置一张图片的图片
    public void setOnePictureIV(String url) {
        if (!TextUtils.isEmpty(url)) {
            Glide.with(context).load(url).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(R.mipmap.bg_pic1).error(R.mipmap.bg_pic1).into(iv_onePicture);
        }
    }

    //设置三张图片的图片，给一个3length的string[]
    public void setThreePictureIVs(String[] urls) {
        if (urls == null || urls.length != 3) {
            return;
        }
        if (!TextUtils.isEmpty(urls[0])) {
            Glide.with(context).load(urls[0]).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(R.mipmap.bg_pic3).error(R.mipmap.bg_pic3).into(iv_threePicture1);
        }
        if (!TextUtils.isEmpty(urls[1])) {
            Glide.with(context).load(urls[1]).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(R.mipmap.bg_pic3).error(R.mipmap.bg_pic3).into(iv_threePicture2);
        }
        if (!TextUtils.isEmpty(urls[2])) {
            Glide.with(context).load(urls[3]).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(R.mipmap.bg_pic3).error(R.mipmap.bg_pic3).into(iv_threePicture3);
        }
    }

    //设置三张图片的第1张图片
    public void setThreePictureIV1(String url) {
        if (!TextUtils.isEmpty(url)) {
            Glide.with(context).load(url).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(R.mipmap.bg_pic3).error(R.mipmap.bg_pic3).into(iv_threePicture1);
        }
    }

    //设置三张图片的第2张图片
    public void setThreePictureIV2(String url) {
        if (!TextUtils.isEmpty(url)) {
            Glide.with(context).load(url).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(R.mipmap.bg_pic3).error(R.mipmap.bg_pic3).into(iv_threePicture2);
        }
    }

    //设置三张图片的第3张图片
    public void setThreePictureIV3(String url) {
        if (!TextUtils.isEmpty(url)) {
            Glide.with(context).load(url).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(R.mipmap.bg_pic3).error(R.mipmap.bg_pic3).into(iv_threePicture3);
        }
    }

    //设置视频列表的title
    public void setVideolistTV(String title) {
        if (!TextUtils.isEmpty(title)) {
            tv_videolist.setText(title);
        }
    }

    //设置视频列表的观看数
    public void setVideolistCountTV(String count) {
        if (TextUtils.isEmpty(count)) {//null
            tv_videolist_count.setText("0");
        } else {
            try {
                long c = Long.parseLong(count);
                if (c > 1) {
                    tv_videolist_count.setText(NumberUtil.getChangeCount(c));
                } else {
                    tv_videolist_count.setText("0");
                }
            } catch (Exception e) {
                tv_videolist_count.setText("0");
            }
        }
    }

    //设置视频列表的图片
    public void setVideoListIV(String url) {
        if (!TextUtils.isEmpty(url)) {
            Glide.with(context).load(url).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(R.mipmap.bg_pic1).error(R.mipmap.bg_pic1).into(iv_videolist);
        }
    }


    /**
     * 设置视频的缩略图
     *
     * @param cover
     */
    public void setCover(String cover) {
        Glide.with(context).load(cover).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(R.mipmap.bg_video).error(R.mipmap.bg_video).into(iv_thumbnail);
    }


    //设置视频列表的时间
    public void setVideolistTimeTV(String time) {
        if (!TextUtils.isEmpty(time)) {
            tv_videolist_time.setText(time);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_comment://点击评论
                if (iv_select.getVisibility() == VISIBLE) {
                    setSelect(!isSelect);
                    return;
                }
                if (getOnCommentClickListener() != null) {
                    getOnCommentClickListener().onCommentClick();
                }
                break;
            case R.id.tv_comment_count://点击评论数
                if (iv_select.getVisibility() == VISIBLE) {
                    setSelect(!isSelect);
                    return;
                }
                if (getOnCommentClickListener() != null) {
                    getOnCommentClickListener().onCommentClick();
                }
                break;
            case R.id.tv_more://点击更多
                if (iv_select.getVisibility() == VISIBLE) {
                    setSelect(!isSelect);
                    return;
                }
                if (getOnMoreClickListener() != null) {
                    getOnMoreClickListener().onMoreClick(tv_more);
                }
                break;
            case R.id.ll_itemview://点击view
                if (getOnItemViewClickListener() != null) {
                    getOnItemViewClickListener().onItemViewClick();
                }
                break;
            case R.id.iv_play:
                if (getOnItemPlayClickListener() != null) {
                    onItemPlayClickListener.onItemPlayClick();
                }
                break;
            default:
        }
    }

    private OnCommentClickListener onCommentClickListener;//评论，评论数
    private OnMoreClickListener onMoreClickListener;//更多
    private OnItemViewClickListener onItemViewClickListener;//点击view
    private OnItemPlayClickListener onItemPlayClickListener;//播放的回调

    public OnCommentClickListener getOnCommentClickListener() {
        return onCommentClickListener;
    }

    public void setOnCommentClickListener(OnCommentClickListener onCommentClickListener) {
        this.onCommentClickListener = onCommentClickListener;
    }

    public OnMoreClickListener getOnMoreClickListener() {
        return onMoreClickListener;
    }

    public void setOnMoreClickListener(OnMoreClickListener onMoreClickListener) {
        this.onMoreClickListener = onMoreClickListener;
    }

    public OnItemViewClickListener getOnItemViewClickListener() {
        return onItemViewClickListener;
    }

    public void setOnItemViewClickListener(OnItemViewClickListener onItemViewClickListener) {
        this.onItemViewClickListener = onItemViewClickListener;
    }

    public void setOnItemPlayClickListener(OnItemPlayClickListener onItemPlayClickListener) {
        this.onItemPlayClickListener = onItemPlayClickListener;
    }

    public OnItemPlayClickListener getOnItemPlayClickListener() {
        return onItemPlayClickListener;
    }

    public interface OnCommentClickListener {
        void onCommentClick();
    }

    public interface OnMoreClickListener {
        void onMoreClick(View view);
    }

    public interface OnSelectClickListener {
        void onSelectClick(boolean select);
    }

    public interface OnItemViewClickListener {
        public void onItemViewClick();
    }

    public interface OnItemPlayClickListener {
        public void onItemPlayClick();
    }
}
