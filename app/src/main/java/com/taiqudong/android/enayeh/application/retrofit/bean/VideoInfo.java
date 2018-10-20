package com.taiqudong.android.enayeh.application.retrofit.bean;

import java.util.List;

/**
 * Created by tangxianming on 2017/8/25.
 */

public class VideoInfo extends Basic {

    /**
     * data : {"type":"V","id":"96a57eed193e0ee9b9592bad29ac8951","ctg":"1001","target_id":"QJbynBQHEZY","source":"Duniati","image":"https://i.ytimg.com/vi/QJbynBQHEZY/hqdefault.jpg","views":9195,"likes":117,"dislikes":64,"commentCount":0,"publish_time":"2014-12-24","likeStatus":"","suggest":[{"type":"V","id":"1abd8c98a277f312581ebd64a14d8540","ctg":"1001","target_id":"TdKRK6VMaNo","title":"حركات لشد المؤخرة | مع دومينيك","source":"Duniati","share_url":"https://www.youtube.com/watch?v=TdKRK6VMaNo","cover":"https://i.ytimg.com/vi/TdKRK6VMaNo/hqdefault.jpg","duration":"02:20","views":1161,"commentCount":0,"commentsCount":0,"publish_time":"2015-01-19"},{"type":"V","id":"7a1686c6d08d185d2f04ad919706892c","ctg":"1001","target_id":"AhRcDD0iOH4","title":"تمارين رياضية لحرق الدهون في الجسم | مع حنين","source":"Duniati","share_url":"https://www.youtube.com/watch?v=AhRcDD0iOH4","cover":"https://i.ytimg.com/vi/AhRcDD0iOH4/hqdefault.jpg","duration":"02:01","views":4669,"commentCount":0,"commentsCount":0,"publish_time":"2014-11-21"},{"type":"V","id":"4b2a65837a0439582e55dc348d2d1a0a","ctg":"1001","target_id":"4Phlnn4rmH0","title":"شدّ عضلات الذراعين بتمارين رياضية بسيطة | مع دوريس","source":"Duniati","share_url":"https://www.youtube.com/watch?v=4Phlnn4rmH0","cover":"https://i.ytimg.com/vi/4Phlnn4rmH0/hqdefault.jpg","duration":"02:27","views":9963,"commentCount":0,"commentsCount":0,"publish_time":"2014-05-21"},{"type":"V","id":"c6aa87289077db02246eafd147977906","ctg":"1001","target_id":"Ktn21dHrOKY","title":"تنظيف البشرة الدهنية | مع داني","source":"Duniati","share_url":"https://www.youtube.com/watch?v=Ktn21dHrOKY","cover":"https://i.ytimg.com/vi/Ktn21dHrOKY/hqdefault.jpg","duration":"02:41","views":6827,"commentCount":0,"commentsCount":0,"publish_time":"2015-01-18"},{"type":"V","id":"942ee98802ef42bd5d516b39f9535f66","ctg":"1001","target_id":"N0-_oOdBV8A","title":"راشيل تغسل وجهها بالماء بعد المكياج... شاهدي النتيجة","source":"Duniati","share_url":"https://www.youtube.com/watch?v=N0-_oOdBV8A","cover":"https://i.ytimg.com/vi/N0-_oOdBV8A/hqdefault.jpg","duration":"06:03","views":4211,"commentCount":0,"commentsCount":0,"publish_time":"2016-10-14"}],"ad1":{"placeId":"Video_Detail","type":"type2","sources":[{"adKey":"1336833246413944_1387325998031335","adType":"fb"},{"adKey":"ca-app-pub-9477775406918793/5442594990","adType":"gg"}]}}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * type : V
         * id : 96a57eed193e0ee9b9592bad29ac8951
         * ctg : 1001
         * target_id : QJbynBQHEZY
         * source : Duniati
         * image : https://i.ytimg.com/vi/QJbynBQHEZY/hqdefault.jpg
         * views : 9195
         * likes : 117
         * dislikes : 64
         * commentCount : 0
         * publish_time : 2014-12-24
         * likeStatus :
         * suggest : [{"type":"V","id":"1abd8c98a277f312581ebd64a14d8540","ctg":"1001","target_id":"TdKRK6VMaNo","title":"حركات لشد المؤخرة | مع دومينيك","source":"Duniati","share_url":"https://www.youtube.com/watch?v=TdKRK6VMaNo","cover":"https://i.ytimg.com/vi/TdKRK6VMaNo/hqdefault.jpg","duration":"02:20","views":1161,"commentCount":0,"commentsCount":0,"publish_time":"2015-01-19"},{"type":"V","id":"7a1686c6d08d185d2f04ad919706892c","ctg":"1001","target_id":"AhRcDD0iOH4","title":"تمارين رياضية لحرق الدهون في الجسم | مع حنين","source":"Duniati","share_url":"https://www.youtube.com/watch?v=AhRcDD0iOH4","cover":"https://i.ytimg.com/vi/AhRcDD0iOH4/hqdefault.jpg","duration":"02:01","views":4669,"commentCount":0,"commentsCount":0,"publish_time":"2014-11-21"},{"type":"V","id":"4b2a65837a0439582e55dc348d2d1a0a","ctg":"1001","target_id":"4Phlnn4rmH0","title":"شدّ عضلات الذراعين بتمارين رياضية بسيطة | مع دوريس","source":"Duniati","share_url":"https://www.youtube.com/watch?v=4Phlnn4rmH0","cover":"https://i.ytimg.com/vi/4Phlnn4rmH0/hqdefault.jpg","duration":"02:27","views":9963,"commentCount":0,"commentsCount":0,"publish_time":"2014-05-21"},{"type":"V","id":"c6aa87289077db02246eafd147977906","ctg":"1001","target_id":"Ktn21dHrOKY","title":"تنظيف البشرة الدهنية | مع داني","source":"Duniati","share_url":"https://www.youtube.com/watch?v=Ktn21dHrOKY","cover":"https://i.ytimg.com/vi/Ktn21dHrOKY/hqdefault.jpg","duration":"02:41","views":6827,"commentCount":0,"commentsCount":0,"publish_time":"2015-01-18"},{"type":"V","id":"942ee98802ef42bd5d516b39f9535f66","ctg":"1001","target_id":"N0-_oOdBV8A","title":"راشيل تغسل وجهها بالماء بعد المكياج... شاهدي النتيجة","source":"Duniati","share_url":"https://www.youtube.com/watch?v=N0-_oOdBV8A","cover":"https://i.ytimg.com/vi/N0-_oOdBV8A/hqdefault.jpg","duration":"06:03","views":4211,"commentCount":0,"commentsCount":0,"publish_time":"2016-10-14"}]
         * ad1 : {"placeId":"Video_Detail","type":"type2","sources":[{"adKey":"1336833246413944_1387325998031335","adType":"fb"},{"adKey":"ca-app-pub-9477775406918793/5442594990","adType":"gg"}]}
         */

        private String type;
        private String id;
        private String ctg;
        private String target_id;
        private String source;
        private String image;
        private int views;
        private int likes;
        private int dislikes;
        private int commentCount;
        private String publish_time;
        private String likeStatus;
        private Ad1Bean ad1;
        private List<SuggestBean> suggest;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCtg() {
            return ctg;
        }

        public void setCtg(String ctg) {
            this.ctg = ctg;
        }

        public String getTarget_id() {
            return target_id;
        }

        public void setTarget_id(String target_id) {
            this.target_id = target_id;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public int getViews() {
            return views;
        }

        public void setViews(int views) {
            this.views = views;
        }

        public int getLikes() {
            return likes;
        }

        public void setLikes(int likes) {
            this.likes = likes;
        }

        public int getDislikes() {
            return dislikes;
        }

        public void setDislikes(int dislikes) {
            this.dislikes = dislikes;
        }

        public int getCommentCount() {
            return commentCount;
        }

        public void setCommentCount(int commentCount) {
            this.commentCount = commentCount;
        }

        public String getPublish_time() {
            return publish_time;
        }

        public void setPublish_time(String publish_time) {
            this.publish_time = publish_time;
        }

        public String getLikeStatus() {
            return likeStatus;
        }

        public void setLikeStatus(String likeStatus) {
            this.likeStatus = likeStatus;
        }

        public Ad1Bean getAd1() {
            return ad1;
        }

        public void setAd1(Ad1Bean ad1) {
            this.ad1 = ad1;
        }

        public List<SuggestBean> getSuggest() {
            return suggest;
        }

        public void setSuggest(List<SuggestBean> suggest) {
            this.suggest = suggest;
        }

        public static class Ad1Bean {
            /**
             * placeId : Video_Detail
             * type : type2
             * sources : [{"adKey":"1336833246413944_1387325998031335","adType":"fb"},{"adKey":"ca-app-pub-9477775406918793/5442594990","adType":"gg"}]
             */

            private String placeId;
            private String type;
            private List<SourcesBean> sources;

            public String getPlaceId() {
                return placeId;
            }

            public void setPlaceId(String placeId) {
                this.placeId = placeId;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public List<SourcesBean> getSources() {
                return sources;
            }

            public void setSources(List<SourcesBean> sources) {
                this.sources = sources;
            }

            public static class SourcesBean {
                /**
                 * adKey : 1336833246413944_1387325998031335
                 * adType : fb
                 */

                private String adKey;
                private String adType;

                public String getAdKey() {
                    return adKey;
                }

                public void setAdKey(String adKey) {
                    this.adKey = adKey;
                }

                public String getAdType() {
                    return adType;
                }

                public void setAdType(String adType) {
                    this.adType = adType;
                }
            }
        }

        public static class SuggestBean {
            /**
             * type : V
             * id : 1abd8c98a277f312581ebd64a14d8540
             * ctg : 1001
             * target_id : TdKRK6VMaNo
             * title : حركات لشد المؤخرة | مع دومينيك
             * source : Duniati
             * share_url : https://www.youtube.com/watch?v=TdKRK6VMaNo
             * cover : https://i.ytimg.com/vi/TdKRK6VMaNo/hqdefault.jpg
             * duration : 02:20
             * views : 1161
             * commentCount : 0
             * commentsCount : 0
             * publish_time : 2015-01-19
             */

            private String type;
            private String id;
            private String ctg;
            private String target_id;
            private String title;
            private String source;
            private String share_url;
            private String cover;
            private String duration;
            private int views;
            private int commentCount;
            private int commentsCount;
            private String publish_time;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getCtg() {
                return ctg;
            }

            public void setCtg(String ctg) {
                this.ctg = ctg;
            }

            public String getTarget_id() {
                return target_id;
            }

            public void setTarget_id(String target_id) {
                this.target_id = target_id;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getSource() {
                return source;
            }

            public void setSource(String source) {
                this.source = source;
            }

            public String getShare_url() {
                return share_url;
            }

            public void setShare_url(String share_url) {
                this.share_url = share_url;
            }

            public String getCover() {
                return cover;
            }

            public void setCover(String cover) {
                this.cover = cover;
            }

            public String getDuration() {
                return duration;
            }

            public void setDuration(String duration) {
                this.duration = duration;
            }

            public int getViews() {
                return views;
            }

            public void setViews(int views) {
                this.views = views;
            }

            public int getCommentCount() {
                return commentCount;
            }

            public void setCommentCount(int commentCount) {
                this.commentCount = commentCount;
            }

            public int getCommentsCount() {
                return commentsCount;
            }

            public void setCommentsCount(int commentsCount) {
                this.commentsCount = commentsCount;
            }

            public String getPublish_time() {
                return publish_time;
            }

            public void setPublish_time(String publish_time) {
                this.publish_time = publish_time;
            }
        }
    }
}
