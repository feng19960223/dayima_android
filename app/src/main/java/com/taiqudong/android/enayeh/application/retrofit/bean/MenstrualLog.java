package com.taiqudong.android.enayeh.application.retrofit.bean;

/**
 * Created by tangxianming on 2017/8/31.
 *
 */

public class MenstrualLog {
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
        return "MenstrualLog{" +
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
