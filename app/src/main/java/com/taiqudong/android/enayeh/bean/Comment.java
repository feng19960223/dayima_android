package com.taiqudong.android.enayeh.bean;

import java.io.Serializable;

/**
 * Created by taiqudong on 2017/8/20.
 */

public class Comment implements Serializable {
    String id;
    String url;
    String name;
    String content;
    boolean isLike;
    Integer likeNum;
    String likeStatus; //"", "like", "dislike"


    public String getLikeStatus() {
        return likeStatus;
    }

    public void setLikeStatus(String likeStatus) {
        this.likeStatus = likeStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(Integer likeNum) {
        this.likeNum = likeNum;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    @Override
    public String toString() {
        return "Comment{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", isLike=" + isLike +
                ", likeNum='" + likeNum + '\'' +
                '}';
    }
}
