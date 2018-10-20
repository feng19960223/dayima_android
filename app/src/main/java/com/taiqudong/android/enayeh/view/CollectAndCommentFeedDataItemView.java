package com.taiqudong.android.enayeh.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.taiqudong.android.enayeh.R;
import com.taiqudong.android.enayeh.utils.NumberUtil;

/**
 * Created by taiqudong on 2017/8/17.
 */

public class CollectAndCommentFeedDataItemView extends LinearLayout implements View.OnClickListener {
    public static final int TYPE_ITEM_VIEW_ONE_PICTURE = 0;
    public static final int TYPE_ITEM_VIEW_THREE_PICTURE = 1;
    public static final int TYPE_ITEM_VIEW_VIDEO = 2;
    private Context context;

    public CollectAndCommentFeedDataItemView(Context context) {
        this(context, null, 0);
    }

    public CollectAndCommentFeedDataItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollectAndCommentFeedDataItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.item_data_feed_collectandcomment_view, this);
        init();
    }

    private LinearLayout ll_itemview;

    private ImageView iv_select;//编辑按钮

    private LinearLayout ll_onePicture;//一张图片
    private TextView tv_onePicture;//一张图片文字
    private TextView tv_onePicture_content;//一张图片文字 副标题
    private ImageView iv_onePicture;//一张图片图片

    private LinearLayout ll_threePicture;//三张图片
    private TextView tv_threePicture;//三张图片文字
    private ImageView iv_threePicture1;//三张图片图片1
    private ImageView iv_threePicture2;//三张图片图片2
    private ImageView iv_threePicture3;//三张图片图片3

    private LinearLayout ll_video;//视频
    private TextView tv_video;//视频title
    private ImageView iv_thumbnail;//视频缩略图
    private ImageView iv_play;//视频播放按钮

    private TextView tv_source;//评论来源，作者。。。
    private TextView tv_comment_count;//评论数
    private ImageView iv_newcollect;

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
        iv_thumbnail = (ImageView) findViewById(R.id.iv_thumbnail);
        iv_play = (ImageView) findViewById(R.id.iv_playnew);

        iv_newcollect = (ImageView) findViewById(R.id.iv_newcollect);
        tv_source = (TextView) findViewById(R.id.tv_source);
        tv_comment_count = (TextView) findViewById(R.id.tv_comment_count);


        ll_itemview.setOnClickListener(this);

        iv_play.setOnClickListener(this);
    }

    public void setNewCollectIV(boolean isNewCollect) {
        iv_newcollect.setVisibility(isNewCollect ? VISIBLE : GONE);
    }


    //设置itemview是什么，一张图片，三张图片，视频
    public void setType(int type) {
        iv_select.setVisibility(GONE);
        ll_onePicture.setVisibility(GONE);
        ll_threePicture.setVisibility(GONE);
        ll_video.setVisibility(GONE);
        if (type == TYPE_ITEM_VIEW_ONE_PICTURE) {
            ll_onePicture.setVisibility(VISIBLE);
        } else if (type == TYPE_ITEM_VIEW_THREE_PICTURE) {
            ll_threePicture.setVisibility(VISIBLE);
        } else if (type == TYPE_ITEM_VIEW_VIDEO) {
            ll_video.setVisibility(VISIBLE);
        }
    }

    //显示选中
    public void showSelect() {
        iv_select.setImageResource(R.mipmap.ic_quan_d);
        iv_select.setVisibility(VISIBLE);
    }

    public void goneSelect() {
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

    //设置评论数
    public void setCommentCount(String commentCount) {
        if (TextUtils.isEmpty(commentCount)) {//null
            tv_comment_count.setText("0");//评论
        } else {
            try {
                long count = Long.parseLong(commentCount);
                if (count > 1) {
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

    //设置一张图片的content
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

    //设置三张图片的第2张图片
    public void setVideoThumbnailIV(String url) {
        if (!TextUtils.isEmpty(url)) {
            Glide.with(context).load(url).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(R.mipmap.bg_pic3).error(R.mipmap.bg_pic3).into(iv_thumbnail);
        }
    }

    //设置三张图片的第3张图片
    public void setThreePictureIV3(String url) {
        if (!TextUtils.isEmpty(url)) {
            Glide.with(context).load(url).centerCrop().diskCacheStrategy(DiskCacheStrategy.RESULT).placeholder(R.mipmap.bg_pic3).error(R.mipmap.bg_pic3).into(iv_threePicture3);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_itemview://点击view
                if (getOnItemViewClickListener() != null) {
                    getOnItemViewClickListener().onItemViewClick();
                }
                break;
            case R.id.iv_playnew:
                setSelect(!isSelect);
                if (getOnItemViewPlayListener() != null) {
                    getOnItemViewPlayListener().onPlay();
                }
                break;
            default:
        }
    }


    private OnItemViewClickListener onItemViewClickListener;//点击view

    public OnItemViewClickListener getOnItemViewClickListener() {
        return onItemViewClickListener;
    }

    public void setOnItemViewClickListener(OnItemViewClickListener onItemViewClickListener) {
        this.onItemViewClickListener = onItemViewClickListener;
    }

    public interface OnItemViewClickListener {
        void onItemViewClick();
    }

    private OnItemViewPlayListener onItemViewPlayListener;

    public void setOnItemViewPlayListener(OnItemViewPlayListener onItemViewPlayListener) {
        this.onItemViewPlayListener = onItemViewPlayListener;
    }

    public OnItemViewPlayListener getOnItemViewPlayListener() {
        return onItemViewPlayListener;
    }

    public interface OnItemViewPlayListener {
        void onPlay();
    }

}
