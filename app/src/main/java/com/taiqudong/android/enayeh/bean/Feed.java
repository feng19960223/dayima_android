package com.taiqudong.android.enayeh.bean;

import com.taiqudong.android.ad.AdConfig;
import com.taiqudong.android.ad.AdContent;

import java.io.Serializable;

/**
 * Created by taiqudong on 2017/7/13.
 */

public class Feed implements Serializable {
    String id;
    String feedType;
    String totalType;
    int type;
    String title;//标题
    String onePicUrl;
    String threePicUrl1;
    String threePicUrl2;
    String threePicUrl3;
    String videoUrl;
    String videoListUrl;
    String content;//内容
    int likeCount;//点赞数
    int commitCount;//评论数
    int unLikeCount;//不喜欢数
    int lookCount;//查看数
    String duration;//持续时间
    String dateTime;//日期
    String author;//作者
    boolean canLoadVideo;//能否加载视频
    String coverUrl;//视频预览图
    String ctg;
    String url;
    String shareUrl;
    boolean save = false;
    String intro;
    int index;
    AdContent adContent;//广告内容
    AdConfig adConfig;//广告配置
    String nextToken;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getTotalType() {
        return totalType;
    }

    public void setTotalType(String totalType) {
        this.totalType = totalType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getCtg() {
        return ctg;
    }

    public void setCtg(String ctg) {
        this.ctg = ctg;
    }

    public String getFeedType() {
        return feedType;
    }

    public void setFeedType(String feedType) {
        this.feedType = feedType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }


    public void setSave(boolean save) {
        this.save = save;
    }

    public boolean getSave() {
        return save;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOnePicUrl() {
        return onePicUrl;
    }

    public void setOnePicUrl(String onePicUrl) {
        this.onePicUrl = onePicUrl;
    }

    public String getThreePicUrl1() {
        return threePicUrl1;
    }

    public void setThreePicUrl1(String threePicUrl1) {
        this.threePicUrl1 = threePicUrl1;
    }

    public String getThreePicUrl2() {
        return threePicUrl2;
    }

    public void setThreePicUrl2(String threePicUrl2) {
        this.threePicUrl2 = threePicUrl2;
    }

    public String getThreePicUrl3() {
        return threePicUrl3;
    }

    public void setThreePicUrl3(String threePicUrl3) {
        this.threePicUrl3 = threePicUrl3;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoListUrl() {
        return videoListUrl;
    }

    public void setVideoListUrl(String videoListUrl) {
        this.videoListUrl = videoListUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommitCount() {
        return commitCount;
    }

    public void setCommitCount(int commitCount) {
        this.commitCount = commitCount;
    }

    public int getUnLikeCount() {
        return unLikeCount;
    }

    public void setUnLikeCount(int unLikeCount) {
        this.unLikeCount = unLikeCount;
    }

    public int getLookCount() {
        return lookCount;
    }

    public void setLookCount(int lookCount) {
        this.lookCount = lookCount;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }


    public boolean getCanLoadVideo() {
        return canLoadVideo;
    }

    public void setCanLoadVideo(boolean canLoadVideo) {
        this.canLoadVideo = canLoadVideo;
    }

    public AdContent getAdContent() {
        return adContent;
    }

    public void setAdContent(AdContent adContent) {
        this.adContent = adContent;
    }

    public AdConfig getAdConfig() {
        return adConfig;
    }

    public void setAdConfig(AdConfig adConfig) {
        this.adConfig = adConfig;
    }

    public String getNextToken() {
        return nextToken;
    }

    public void setNextToken(String nextToken) {
        this.nextToken = nextToken;
    }

    @Override
    public String toString() {
        return "Feed{" +
                "totalType='" + totalType + '\'' +
                ", type=" + type +
                ", index=" + index +
                ", token=" + nextToken +
                '}';
    }
}
