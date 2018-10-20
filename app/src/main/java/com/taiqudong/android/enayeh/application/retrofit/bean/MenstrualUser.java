package com.taiqudong.android.enayeh.application.retrofit.bean;

/**
 * Created by tangxianming on 2017/8/31.
 */

public class MenstrualUser {

    /**
     * lastMenstrual : 20170805
     * menstrualTime : 7
     * menstrualCycle : 28
     * waterNotify : false
     * medicineNotify : false
     * menstrualStartNotify : false
     * menstrualEndNotify : false
     * userStatus : 2
     * ver : 1
     */

    private int lastMenstrual;
    private int menstrualTime;
    private int menstrualCycle;
    private boolean waterNotify;
    private boolean medicineNotify;
    private boolean menstrualStartNotify;
    private boolean menstrualEndNotify;
    private int userStatus;
    private int ver;

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

    public int getVer() {
        return ver;
    }

    public void setVer(int ver) {
        this.ver = ver;
    }

    @Override
    public String toString() {
        return "MenstrualUser{" +
                "lastMenstrual=" + lastMenstrual +
                ", menstrualTime=" + menstrualTime +
                ", menstrualCycle=" + menstrualCycle +
                ", waterNotify=" + waterNotify +
                ", medicineNotify=" + medicineNotify +
                ", menstrualStartNotify=" + menstrualStartNotify +
                ", menstrualEndNotify=" + menstrualEndNotify +
                ", userStatus=" + userStatus +
                ", ver=" + ver +
                '}';
    }
}
