package com.taiqudong.android.enayeh.application.retrofit.bean;

import java.util.List;

/**
 * Created by tangxianming on 2017/8/25.
 */

public class Comments extends Basic {

    /**
     * data : {"rows":[{"id":"c81aab66c373887c33f51511b7e034b1","content":"This is contentghjkjhghjkjhg","publish_time":"2017-08-25","user":{"uid":"c6aa87289077db02246eafd147977906","name":"enayeh_hzvgegifk","avatar":null}}],"maxRows":20,"nextToken":"c81aab66c373887c33f51511b7e034b1"}
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
         * rows : [{"id":"c81aab66c373887c33f51511b7e034b1","content":"This is contentghjkjhghjkjhg","publish_time":"2017-08-25","user":{"uid":"c6aa87289077db02246eafd147977906","name":"enayeh_hzvgegifk","avatar":null}}]
         * maxRows : 20
         * nextToken : c81aab66c373887c33f51511b7e034b1
         */

        private int maxRows;
        private String nextToken;
        private List<RowsBean> rows;

        public int getMaxRows() {
            return maxRows;
        }

        public void setMaxRows(int maxRows) {
            this.maxRows = maxRows;
        }

        public String getNextToken() {
            return nextToken;
        }

        public void setNextToken(String nextToken) {
            this.nextToken = nextToken;
        }

        public List<RowsBean> getRows() {
            return rows;
        }

        public void setRows(List<RowsBean> rows) {
            this.rows = rows;
        }

        public static class RowsBean {
            /**
             * id : c81aab66c373887c33f51511b7e034b1
             * content : This is contentghjkjhghjkjhg
             * publish_time : 2017-08-25
             * user : {"uid":"c6aa87289077db02246eafd147977906","name":"enayeh_hzvgegifk","avatar":null}
             */

            private String id;
            private String content;
            private String publish_time;
            private UserBean user;
            private Integer likes;
            private String likeStatus; //"", "like", "dislike"


            public String getLikeStatus() {
                return likeStatus;
            }

            public void setLikeStatus(String likeStatus) {
                this.likeStatus = likeStatus;
            }

            public Integer getLikes() {
                return likes;
            }

            public void setLikes(Integer likes) {
                this.likes = likes;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getPublish_time() {
                return publish_time;
            }

            public void setPublish_time(String publish_time) {
                this.publish_time = publish_time;
            }

            public UserBean getUser() {
                return user;
            }

            public void setUser(UserBean user) {
                this.user = user;
            }

            public static class UserBean {
                /**
                 * uid : c6aa87289077db02246eafd147977906
                 * name : enayeh_hzvgegifk
                 * avatar : null
                 */

                private String uid;
                private String name;
                private String avatar;

                public String getUid() {
                    return uid;
                }

                public void setUid(String uid) {
                    this.uid = uid;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getAvatar() {
                    return avatar;
                }

                public void setAvatar(String avatar) {
                    this.avatar = avatar;
                }
            }
        }
    }
}
