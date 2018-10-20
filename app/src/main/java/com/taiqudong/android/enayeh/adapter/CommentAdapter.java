package com.taiqudong.android.enayeh.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.taiqudong.android.enayeh.R;
import com.taiqudong.android.enayeh.bean.Comment;
import com.taiqudong.android.enayeh.utils.NumberUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by taiqudong on 2017/8/20.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    Context context;

    List<Comment> comments;

    OnItemClickListener onItemClickListener;
    OnLikeClickListener onLikeClickListener;

    public CommentAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @Override
    public CommentAdapter.CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CommentViewHolder(LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(CommentAdapter.CommentViewHolder holder, final int position) {
        final Comment comment = comments.get(position);
        holder.contentTv.setText(comment.getContent());
        holder.nameTv.setText(comment.getName());

        //设置点赞的状态
        if (comment.getLikeNum() == 0) {
            holder.likeTv.setText(context.getString(R.string.like));
        } else {
//            holder.likeTv.setText(String.valueOf(comment.getLikeNum()));
            holder.likeTv.setText(NumberUtil.getChangeCount(String.valueOf(comment.getLikeNum())));
        }
        if (comment.isLike()) {
            holder.likeTv.setTextColor(Color.parseColor("#ff189f"));
            holder.likeIv.setImageResource(R.drawable.ic_like_press);
        } else {
            holder.likeTv.setTextColor(Color.parseColor("#666666"));
            holder.likeIv.setImageResource(R.drawable.ic_like_nopress);
        }
        Glide.with(context).load(comment.getUrl()).diskCacheStrategy(DiskCacheStrategy.RESULT).error(R.drawable.avatar_default).into(holder.avatarCiv);
        holder.commentLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(comment);
                }
            }
        });
        //点赞点击
        holder.likeRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onLikeClickListener != null) {
                    if (comment.isLike()) {
                        onLikeClickListener.onDisLikeClick(position);
                    } else {
                        onLikeClickListener.onLikeClick(position);
                    }
                }
            }
        });
    }

    /**
     * 喜欢或不喜欢
     *
     * @param postion
     */
    public void likeOrDislike(int postion) {
        Comment comment = comments.get(postion);
        if (comment.isLike()) {
            comment.setLikeNum(Integer.valueOf(comment.getLikeNum()) - 1);
        } else {
            comment.setLikeNum(Integer.valueOf(comment.getLikeNum()) + 1);
        }
        comment.setLike(!comment.isLike());
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return comments.size();
    }


    class CommentViewHolder extends RecyclerView.ViewHolder {
        CircleImageView avatarCiv;
        TextView nameTv;
        TextView likeTv;
        ImageView likeIv;
        TextView contentTv;
        LinearLayout commentLl;
        RelativeLayout likeRl;

        public CommentViewHolder(View itemView) {
            super(itemView);
            avatarCiv = (CircleImageView) itemView.findViewById(R.id.civ_avatar);
            nameTv = (TextView) itemView.findViewById(R.id.tv_name);
            likeTv = (TextView) itemView.findViewById(R.id.tv_like);
            likeIv = (ImageView) itemView.findViewById(R.id.iv_like);
            contentTv = (TextView) itemView.findViewById(R.id.tv_content);
            commentLl = (LinearLayout) itemView.findViewById(R.id.ll_comment);
            likeRl = (RelativeLayout) itemView.findViewById(R.id.rl_like);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public void setOnLikeClickListener(OnLikeClickListener onLikeClickListener) {
        this.onLikeClickListener = onLikeClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(Comment comment);
    }

    public interface OnLikeClickListener {
        void onLikeClick(int position);

        void onDisLikeClick(int position);
    }


}
