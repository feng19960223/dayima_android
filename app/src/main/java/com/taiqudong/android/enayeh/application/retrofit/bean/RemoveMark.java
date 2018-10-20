package com.taiqudong.android.enayeh.application.retrofit.bean;

import java.util.List;

/**
 * Created by tangxianming on 2017/8/24.
 */

public class RemoveMark extends Basic {


    private List<IdListBean> id_list;

    public List<IdListBean> getId_list() {
        return id_list;
    }

    public void setId_list(List<IdListBean> id_list) {
        this.id_list = id_list;
    }

    public static class IdListBean {
        /**
         * id : 96a57eed193e0ee9b9592bad29ac8951
         * type : A
         */

        private String id;
        private String type;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
