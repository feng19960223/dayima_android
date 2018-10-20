package com.taiqudong.android.enayeh.application.retrofit.bean;

/**
 * Created by tangxianming on 2017/8/26.
 * 用户数据
 */

public class MenstrualUserInfo extends Basic{


    /**
     * data : {"lastMenstrual":20170831,"menstrualTime":7,"menstrualCycle":28,"waterNotify":true,"medicineNotify":true,"menstrualStartNotify":true,"menstrualEndNotify":true,"userStatus":1,"birthday":0,"height":0,"weight":0}
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
         * lastMenstrual : 20170831
         * menstrualTime : 7
         * menstrualCycle : 28
         * waterNotify : true
         * medicineNotify : true
         * menstrualStartNotify : true
         * menstrualEndNotify : true
         * userStatus : 1
         * birthday : 0
         * height : 0
         * weight : 0
         */

        private int lastMenstrual;
        private int menstrualTime;
        private int menstrualCycle;
        private boolean waterNotify;
        private boolean medicineNotify;
        private boolean menstrualStartNotify;
        private boolean menstrualEndNotify;
        private int userStatus;
        private int birthday;
        private int height;
        private int weight;
        private int ver;

        public int getVer() {
            return ver;
        }

        public void setVer(int ver) {
            this.ver = ver;
        }

        public int getLastMenstrual() {
            return lastMenstrual;
        }

        public void setLastMenstrual(int lastMenstrual) {
            this.lastMenstrual = lastMenstrual;
        }

        public int getMenstrualTime() {
            return menstrualTime;
        }

        public void setMenstrualTime(int menstrualTime) {
            this.menstrualTime = menstrualTime;
        }

        public int getMenstrualCycle() {
            return menstrualCycle;
        }

        public void setMenstrualCycle(int menstrualCycle) {
            this.menstrualCycle = menstrualCycle;
        }

        public boolean isWaterNotify() {
            return waterNotify;
        }

        public void setWaterNotify(boolean waterNotify) {
            this.waterNotify = waterNotify;
        }

        public boolean isMedicineNotify() {
            return medicineNotify;
        }

        public void setMedicineNotify(boolean medicineNotify) {
            this.medicineNotify = medicineNotify;
        }

        public boolean isMenstrualStartNotify() {
            return menstrualStartNotify;
        }

        public void setMenstrualStartNotify(boolean menstrualStartNotify) {
            this.menstrualStartNotify = menstrualStartNotify;
        }

        public boolean isMenstrualEndNotify() {
            return menstrualEndNotify;
        }

        public void setMenstrualEndNotify(boolean menstrualEndNotify) {
            this.menstrualEndNotify = menstrualEndNotify;
        }

        public int getUserStatus() {
            return userStatus;
        }

        public void setUserStatus(int userStatus) {
            this.userStatus = userStatus;
        }

        public int getBirthday() {
            return birthday;
        }

        public void setBirthday(int birthday) {
            this.birthday = birthday;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }


        @Override
        public String toString() {
            return "DataBean{" +
                    "lastMenstrual=" + lastMenstrual +
                    ", menstrualTime=" + menstrualTime +
                    ", menstrualCycle=" + menstrualCycle +
                    ", waterNotify=" + waterNotify +
                    ", medicineNotify=" + medicineNotify +
                    ", menstrualStartNotify=" + menstrualStartNotify +
                    ", menstrualEndNotify=" + menstrualEndNotify +
                    ", userStatus=" + userStatus +
                    ", birthday=" + birthday +
                    ", height=" + height +
                    ", weight=" + weight +
                    ", ver=" + ver +
                    '}';
        }
    }
}
