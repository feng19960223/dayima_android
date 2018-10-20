package com.taiqudong.android.enayeh.application.retrofit.bean;

/**
 * Created by tangxianming on 2017/8/23.
 * 匿名注册
 */


public class RegisterAnon extends Basic {

    /**
     * code : 0
     * data : {"token":"633fbf260efd7e3fee19ed2e04cecc49","user":{"name":"p9omry92","avatar":null,"intro":null,"uid":"e6f69bf787b16e939d039e05ca17d172"}}
     * desc : success
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
         * token : 633fbf260efd7e3fee19ed2e04cecc49
         * user : {"name":"p9omry92","avatar":null,"intro":null,"uid":"e6f69bf787b16e939d039e05ca17d172"}
         */

        private String token;
        private UserBean user;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public UserBean getUser() {
            return user;
        }

        public void setUser(UserBean user) {
            this.user = user;
        }

        public static class UserBean {
            /**
             * name : p9omry92
             * avatar : null
             * intro : null
             * uid : e6f69bf787b16e939d039e05ca17d172
             */

            private String name;
            private String avatar;
            private String intro;
            private String uid;

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

            public String getIntro() {
                return intro;
            }

            public void setIntro(String intro) {
                this.intro = intro;
            }

            public String getUid() {
                return uid;
            }

            public void setUid(String uid) {
                this.uid = uid;
            }
        }
    }
}
