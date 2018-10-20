package com.taiqudong.android.enayeh.utils;

import android.content.ContentValues;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.taiqudong.android.enayeh.application.AppLogic;
import com.taiqudong.android.enayeh.application.retrofit.ServiceGenerator;
import com.taiqudong.android.enayeh.application.retrofit.api.ApiService;
import com.taiqudong.android.enayeh.application.retrofit.bean.ArticleInfo;
import com.taiqudong.android.enayeh.application.retrofit.bean.MenstrualLog;
import com.taiqudong.android.enayeh.application.retrofit.bean.MenstrualLogs;
import com.taiqudong.android.enayeh.application.retrofit.bean.MenstrualUser;
import com.taiqudong.android.enayeh.application.retrofit.bean.MenstrualUserInfo;
import com.taiqudong.android.enayeh.bean.Basic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by tangxianming on 2017/8/25.
 * 网络请求的工具类
 */

public class ApiUtil {
    public ApiUtil() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 月经及其他用户数据记录上传
     */
    public static void postMenstrualLog(Context context) {
        //上传经期数据
        List<Basic> basics = AppLogic.getInstance().selectAllBasic();
        List<MenstrualLog> dataBeans = new ArrayList<>();
        for (com.taiqudong.android.enayeh.bean.Basic basic : basics) {
            MenstrualLog rowsBean = new MenstrualLog();
            rowsBean.setComming(basic.isComing());
            rowsBean.setDay(basic.getTime());
            rowsBean.setDefecation(basic.isDefecation());
            rowsBean.setDrink(basic.isDrink());
            rowsBean.setDysmenorhea(basic.getDysmenorrhea());
            rowsBean.setFruit(basic.isFruit());
            rowsBean.setMenstruation(basic.getMenstruation());
            rowsBean.setMood(basic.getMood());
            rowsBean.setRunning(basic.isRunning());
            rowsBean.setSexInfo(basic.getSex());
            rowsBean.setDefecation(basic.isDefecation());
            rowsBean.setVer(Integer.valueOf(String.valueOf(System.currentTimeMillis() / 1000)));
            rowsBean.setWeight(Float.valueOf(basic.getWeight() == null ? "0" : basic.getWeight()));
            dataBeans.add(rowsBean);
        }
        ServiceGenerator.createServcie(context, ApiService.class).menstrualLog(dataBeans).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() == null) {
                    return;
                }
                try {
                    Log.d("apiutil", "postMenstrualLog: " + response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("apiutil", "postMenstrualLog:" + t.getMessage());
            }
        });
    }

    public static void menstrualLogGet(Context context) {
        menstrualLogGet(context, null);
    }

    /**
     * 月经及其他用户数据记录获取
     */
    public static void menstrualLogGet(Context context, final ApiCallback<MenstrualLogs> callback) {
        ServiceGenerator.createServcie(context, ApiService.class).menstrualLogs().enqueue(new Callback<MenstrualLogs>() {
            @Override
            public void onResponse(Call<MenstrualLogs> call, Response<MenstrualLogs> response) {
                MenstrualLogs menstrualLogses = response.body();
                if (menstrualLogses.getCode() == Constants.REQUEST_SUCCESS) {
                    MenstrualLogs.DataBean dataBean = menstrualLogses.getData();
                    //更新经期数据到本地
                    List<MenstrualLogs.DataBean.RowsBean> rowsBeens = dataBean.getRows();
                    if (callback != null) {
                        if (menstrualLogses == null) {
                            callback.run(false, null);
                            return;
                        }

                        if (dataBean == null) {
                            callback.run(false, null);
                            return;
                        }

                        if (rowsBeens.size() == 0) {
                            callback.run(false, null);
                            return;
                        }
                    } else {
                        if (menstrualLogses == null || dataBean == null || rowsBeens.size() == 0) {
                            return;
                        }
                    }
                    for (MenstrualLogs.DataBean.RowsBean rowsBean : rowsBeens) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("time", rowsBean.getDay());
                        contentValues.put("menstruation", rowsBean.getMenstruation());
                        contentValues.put("dysmenorrhea", rowsBean.getDysmenorhea());
                        contentValues.put("coming", rowsBean.isComming());
                        contentValues.put("sex", rowsBean.getSexInfo());
                        contentValues.put("weight", rowsBean.getWeight());
                        contentValues.put("running", rowsBean.isRunning());
                        contentValues.put("drink", rowsBean.isDrink());
                        contentValues.put("fruit", rowsBean.isFruit());
                        contentValues.put("defecation", rowsBean.isDefecation());
                        contentValues.put("mood", rowsBean.getMood());
                        AppLogic.getInstance().updateBasic(rowsBean.getDay(), contentValues);
                    }
                    Log.d("apiutil", "menstrualLogGet: " + menstrualLogses.toString());
                    if (callback != null) {
                        callback.run(true, menstrualLogses);
                    }
                } else {
                    if (menstrualLogses.getCode() == 210011) {//no content.
                        if (callback != null) {
                            callback.run(true, menstrualLogses);
                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<MenstrualLogs> call, Throwable t) {
                Log.d("apiutil", "response: " + t.getMessage());
                if (callback != null) {
                    callback.run(false, null);
                }
            }
        });
    }

    /**
     * 用户信息上传
     */
    public static void postUserinfo(Context context) {
        //用户信息上传
        boolean isBeginning = AppLogic.getInstance().getReminderBEGINNING();
        boolean isEnd = AppLogic.getInstance().getReminderEND();
        boolean isMedication = AppLogic.getInstance().getReminderMEDICATION();
        boolean isDrink = AppLogic.getInstance().getReminderDRINK();
        Calendar calendar = AppLogic.getInstance().getLastMemstrualDate();
        MenstrualUser dataBean = new MenstrualUser();
        if (calendar != null) {
            dataBean.setLastMenstrual(DateUtil.date2int(calendar));
        }
        dataBean.setMedicineNotify(isMedication);
        dataBean.setMenstrualCycle(AppLogic.getInstance().getMentrualCicle());
        dataBean.setMenstrualEndNotify(isEnd);
        dataBean.setMenstrualStartNotify(isBeginning);
        dataBean.setMenstrualTime(AppLogic.getInstance().getMenstrualTime());
        dataBean.setVer(Integer.valueOf(String.valueOf(System.currentTimeMillis() / 1000)));
        dataBean.setUserStatus(AppLogic.getInstance().getStatus());
        dataBean.setWaterNotify(isDrink);
        ServiceGenerator.createServcie(context, ApiService.class).menstrualUserInfo(dataBean).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() == null) {
                    return;
                }
                try {
                    Log.d("apiutil", "postUserinfo: " + response.body().string().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("apiutil", "postUserinfo: " + t.getMessage());
            }
        });
    }


    /**
     * 用户信息获取
     *
     * @param context
     */
    public static void userinfoGet(Context context) {
        userinfoGet(context, null);
    }

    /**
     * 用户信息获取
     *
     * @param {Context}                        context
     * @param {ApiCallback<MenstrualUserInfo>} callback
     */
    public static void userinfoGet(Context context, final ApiCallback<MenstrualUserInfo> callback) {
        ServiceGenerator.createServcie(context, ApiService.class).menstrualUserinfos().enqueue(new Callback<MenstrualUserInfo>() {
            @Override
            public void onResponse(Call<MenstrualUserInfo> call, Response<MenstrualUserInfo> response) {
                MenstrualUserInfo menstrualUserInfo = response.body();
                if (menstrualUserInfo.getCode() == Constants.REQUEST_SUCCESS) {
                    MenstrualUserInfo.DataBean dataBean = menstrualUserInfo.getData();
                    if (callback != null) {
                        if (menstrualUserInfo == null) {
                            callback.run(false, null);
                            return;
                        }

                        if (dataBean == null) {
                            callback.run(false, null);
                            return;
                        }
                    } else {
                        if (menstrualUserInfo == null || dataBean == null) {
                            return;
                        }
                    }
                    AppLogic.getInstance().setStatus(dataBean.getUserStatus());
                    AppLogic.getInstance().setLastMenstrualDate(dataBean.getLastMenstrual());
                    AppLogic.getInstance().setMenstrualTime(dataBean.getMenstrualTime());
                    AppLogic.getInstance().setMentrualCicle(dataBean.getMenstrualCycle());
                    AppLogic.getInstance().setReminderDRINK(dataBean.isWaterNotify());
                    AppLogic.getInstance().setReminderMEDICATION(dataBean.isMedicineNotify());
                    AppLogic.getInstance().setReminderBEGINNING(dataBean.isMenstrualStartNotify());
                    AppLogic.getInstance().setReminderEND(dataBean.isMenstrualEndNotify());
                    AppLogic.getInstance().afterInit(true);//用户初始化过数据
                    if (dataBean.getLastMenstrual() == 0 || dataBean.getMenstrualTime() == 0 || dataBean.getMenstrualCycle() == 0) {
                        AppLogic.getInstance().afterInit(false);
                    }
                    Log.d("apiutil", "userinfoGet: " + menstrualUserInfo.toString());
                    if (callback != null) {
                        callback.run(true, menstrualUserInfo);
                    }
                } else {
                    AppLogic.getInstance().afterInit(false);
                    if (menstrualUserInfo.getCode() == 210011) {//no content.
                        if (callback != null) {
                            callback.run(true, menstrualUserInfo);
                        }
                    }
                    Log.d("apiutil", "onResponse: " + menstrualUserInfo.toString());
                }
            }

            @Override
            public void onFailure(Call<MenstrualUserInfo> call, Throwable t) {
                Log.d("apiutil", "userinfoGet: " + t.getMessage());
                if (callback != null) {
                    callback.run(false, null);
                }
            }
        });
    }


    public interface ApiCallback<T> {
        void run(boolean isSuccess, T resp);
    }

}
