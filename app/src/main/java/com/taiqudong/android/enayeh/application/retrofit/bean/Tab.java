package com.taiqudong.android.enayeh.application.retrofit.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tangxianming on 2017/8/24.
 * 信息类别
 */

public class Tab extends Basic implements Serializable{

    /**
     * data : {"video_tabs":[{"tid":"1001","name":"beauty"},{"tid":"1002","name":"health"},{"tid":"1003","name":"cook"}],"article_tabs":[{"tid":"2001","name":"beauty"},{"tid":"2002","name":"health"},{"tid":"2003","name":"cook"},{"tid":"2004","name":"martial relationship"}]}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private List<VideoTabsBean> video_tabs;
        private List<ArticleTabsBean> article_tabs;

        public List<VideoTabsBean> getVideo_tabs() {
            return video_tabs;
        }

        public void setVideo_tabs(List<VideoTabsBean> video_tabs) {
            this.video_tabs = video_tabs;
        }

        public List<ArticleTabsBean> getArticle_tabs() {
            return article_tabs;
        }

        public void setArticle_tabs(List<ArticleTabsBean> article_tabs) {
            this.article_tabs = article_tabs;
        }

        public static class VideoTabsBean implements Serializable{
            /**
             * tid : 1001
             * name : beauty
             */

            private String tid;
            private String name;

            public String getTid() {
                return tid;
            }

            public void setTid(String tid) {
                this.tid = tid;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public static class ArticleTabsBean implements Serializable{
            /**
             * tid : 2001
             * name : beauty
             */

            private String tid;
            private String name;

            public String getTid() {
                return tid;
            }

            public void setTid(String tid) {
                this.tid = tid;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }
}
