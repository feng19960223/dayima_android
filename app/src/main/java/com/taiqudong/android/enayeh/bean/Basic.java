package com.taiqudong.android.enayeh.bean;

/**
 * 数据库存储的对象
 * Created by taiqudong on 2017/7/18.
 */

public class Basic {

    private String time;//yyyyMMdd 时间

    private int menstruation;//123,对应3个等级月经量
    private int dysmenorrhea;//123,对应3个等级经痛
    private boolean coming;//1true0false来月经了
    private int sex;//12345,对应5个等级爱爱等级
    private String weight;//无kg，纯小数字符串
    private boolean running;//1true0false跑步
    private boolean drink;//1true0false喝水
    private boolean fruit;//1true0false水果
    private boolean defecation;//1true0false排便
    private int mood;//12345,对应3等级,心情
    private boolean isInMenstruation; //是否是月经期

    public Basic(int menstruation, int dysmenorrhea, boolean coming, int sex, String weight,
                 boolean running, boolean drink, boolean fruit, boolean defecation, int mood) {
        this.menstruation = menstruation;
        this.dysmenorrhea = dysmenorrhea;
        this.coming = coming;
        this.sex = sex;
        this.weight = weight;
        this.running = running;
        this.drink = drink;
        this.fruit = fruit;
        this.defecation = defecation;
        this.mood = mood;
    }

    public Basic() {
    }

    public String getTime() {
        return time;
    }

    public boolean isInMenstruation() {
        return isInMenstruation;
    }

    public void setInMenstruation(boolean inMenstruration) {
        isInMenstruation = inMenstruration;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getMenstruation() {
        return menstruation;
    }

    public void setMenstruation(int menstruation) {
        this.menstruation = menstruation;
    }

    public int getDysmenorrhea() {
        return dysmenorrhea;
    }

    public void setDysmenorrhea(int dysmenorrhea) {
        this.dysmenorrhea = dysmenorrhea;
    }

    public boolean isComing() {
        return coming;
    }

    public void setComing(boolean coming) {
        this.coming = coming;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
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
}
