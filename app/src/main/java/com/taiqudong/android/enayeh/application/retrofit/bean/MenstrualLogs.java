package com.taiqudong.android.enayeh.application.retrofit.bean;

import java.util.List;

/**
 * Created by tangxianming on 2017/8/26.
 * 经期数据
 */

public class MenstrualLogs extends Basic {

    /**
     * data : {"rows":[{"ver":1503683486,"dysmenorhea":0,"sexInfo":0,"comming":true,"running":false,"drink":false,"fruit":false,"defecation":false,"mood":0,"weight":0,"menstruation":0,"day":"20175126"}]}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private List<RowsBean> rows;

        public List<RowsBean> getRows() {
            return rows;
        }

        public void setRows(List<RowsBean> rows) {
            this.rows = rows;
        }

        public static class RowsBean {
            /**
             * ver : 1503683486
             * dysmenorhea : 0
             * sexInfo : 0
             * comming : true
             * running : false
             * drink : false
             * fruit : false
             * defecation : false
             * mood : 0
             * weight : 0
             * menstruation : 0
             * day : 20175126
             */

            private int ver;
            private int dysmenorhea;
            private int sexInfo;
            private boolean comming;
            private boolean running;
            private boolean drink;
            private boolean fruit;
            private boolean defecation;
            private int mood;
            private float weight;
            private int menstruation;
            private String day;

            public int getVer() {
                return ver;
            }

            public void setVer(int ver) {
                this.ver = ver;
            }

            public int getDysmenorhea() {
                return dysmenorhea;
            }

            public void setDysmenorhea(int dysmenorhea) {
                this.dysmenorhea = dysmenorhea;
            }

            public int getSexInfo() {
                return sexInfo;
            }

            public void setSexInfo(int sexInfo) {
                this.sexInfo = sexInfo;
            }

            public boolean isComming() {
                return comming;
            }

            public void setComming(boolean comming) {
                this.comming = comming;
            }

            public boolean isRunning() {
                return running;
            }

            public void setRunning(boolean running) {
                this.running = running;
            }

            public boolean isDrink() {
                return drink;
            }

            public void setDrink(boolean drink) {
                this.drink = drink;
            }

            public boolean isFruit() {
                return fruit;
            }

            public void setFruit(boolean fruit) {
                this.fruit = fruit;
            }

            public boolean isDefecation() {
                return defecation;
            }

            public void setDefecation(boolean defecation) {
                this.defecation = defecation;
            }

            public int getMood() {
                return mood;
            }

            public void setMood(int mood) {
                this.mood = mood;
            }

            public float getWeight() {
                return weight;
            }

            public void setWeight(float weight) {
                this.weight = weight;
            }

            public int getMenstruation() {
                return menstruation;
            }

            public void setMenstruation(int menstruation) {
                this.menstruation = menstruation;
            }

            public String getDay() {
                return day;
            }

            public void setDay(String day) {
                this.day = day;
            }

            @Override
            public String toString() {
                return "RowsBean{" +
                        "ver=" + ver +
                        ", dysmenorhea=" + dysmenorhea +
                        ", sexInfo=" + sexInfo +
                        ", comming=" + comming +
                        ", running=" + running +
                        ", drink=" + drink +
                        ", fruit=" + fruit +
                        ", defecation=" + defecation +
                        ", mood=" + mood +
                        ", weight=" + weight +
                        ", menstruation=" + menstruation +
                        ", day='" + day + '\'' +
                        '}';
            }
        }
    }
}
