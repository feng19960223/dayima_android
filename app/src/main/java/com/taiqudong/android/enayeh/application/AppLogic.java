package com.taiqudong.android.enayeh.application;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Settings;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.taiqudong.android.ad.SPUtil;
import com.taiqudong.android.enayeh.R;
import com.taiqudong.android.enayeh.bean.Basic;
import com.taiqudong.android.enayeh.bean.UserInfo;
import com.taiqudong.android.enayeh.utils.Constants;
import com.taiqudong.android.enayeh.utils.DateUtil;
import com.taiqudong.android.enayeh.utils.Log;
import com.taiqudong.android.enayeh.utils.SqliteUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 传说中的上帝类， 重要的逻辑请谢在这里
 * 1) 用户数据的读取与写入
 * 2) 逻辑计算，如经期的计算等等
 * Created by zhangxiang on 2017/7/6.
 */
public class AppLogic {

    private static AppLogic mInstance = null;

    private SimpleDateFormat mSdf;

    private AppLogic() {
        mSdf = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);

    }

    private static Context mContext = null;

    //是否已经被初始化
    private final static String KEY_IS_INIT = "is_init";

    //设备id
    private final static String KEY_DEVICE_ID = "device_id";

    //用户登录的凭证
    private final static String KEY_TOKEN = "token";

    //用户信息
    private final static String KEY_USER_INFO = "userinfo";

    //用户状态， 想怀孕，还是记录月经
    private final static String KEY_USER_STATUS = "user_status";

    //上次月经来的第一天
    private final static String KEY_LAST_MENSTRUAL_DATE = "last_mentrual_date";

    //月经的天数
    private final static String KEY_MENSTRUAL_TIME = "mentrual_time";

    //月经的周期, 来的第一天到下次来的前一天
    private final static String KEY_MENSTRUAL_CICLE = "mentrual_cicle";

    //体重
    private final static String KEY_WEIGHT = "user_weight";

    //身高
    private final static String KEY_HEIGHT = "user_height";

    //生日
    private final static String KEY_BIRTHDAY = "user_birthday";


    //存储结构版本
    private final static String KEY_STORE_VERSION = "store_ver";

    public final static int USER_STATUS_TO_PREGNANT = 1;
    public final static int USER_STATUS_TO_TRACK = 2;

    private final static int FINAL_STORE_VERSION = 2;


    public final static int REQUEST_CODE_DEVICE_ID = 10001;

    public static int sInitCount = 0;

    private final static String TAG = "AppLogic";


    public static void init(Context context) {

        if (sInitCount == 0) {
            sInitCount++;
            mContext = context;
            SharedPreferences sp = getInstance().getPrefs(mContext);

            int store_version = sp.getInt(KEY_STORE_VERSION, 0);
            if (store_version != FINAL_STORE_VERSION) {
                initInFirstTime();
            }

        } else {
            Log.w(TAG, "AppLogin.init should be call only once.");
        }

    }

    /**
     * 第一次运行
     */
    public static void initInFirstTime() {

        SharedPreferences sp = getInstance().getPrefs(mContext);

        //存储的信息版本
        SharedPreferences.Editor ed = sp.edit();

        if (sp.getString(KEY_DEVICE_ID, null) == null) {
            ed.putString(KEY_DEVICE_ID, String.format("%x%x",
                    Math.round(Math.random() * 10000000),
                    Math.round(Math.random() * 10000000)));
        }

        ed.putInt(KEY_STORE_VERSION, FINAL_STORE_VERSION);
        ed.putBoolean(KEY_IS_INIT, false);
        ed.commit();

    }

    public SimpleDateFormat getSdf() {
        return mSdf;
    }

    public static AppLogic getInstance() {
        if (mInstance == null) {
            mInstance = new AppLogic();
        }
        return mInstance;
    }

    public MemstrualSettings getSetting() {
        return new MemstrualSettings();
    }

    public List<AppDay> getMonth() {
        Calendar c = getToday();
        return getMonth(c.get(Calendar.YEAR), c.get(Calendar.MONTH));
    }

    public boolean isInitialized() {
        Log.d(TAG, "db_path: " + getDBName(mContext));
        SharedPreferences sharedPreferences = getPrefs();
        if (!sharedPreferences.getBoolean(KEY_IS_INIT, false)) {
            //            cleanInit(mContext);
            return false;
        } else {
            return true;
        }
    }

    /**
     * 表示有没有设置过月经数据
     *
     * @param isInit
     */
    public void afterInit(boolean isInit) {
        getPrefs().edit().putBoolean(KEY_IS_INIT, isInit).commit();
    }

    public void cleanInit(Context context) {
        SharedPreferences sharedPreferences = getPrefs(context);

//        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(getDBName(context), null);
//        createTables(db);
//        db.close();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_INIT, false).commit();
    }


    public void setStatus(int userStatus) {
        SharedPreferences.Editor ed = getPrefs().edit();
        ed.putInt(KEY_USER_STATUS, userStatus).commit();
    }

    public int getStatus() {
        return getPrefs().getInt(KEY_USER_STATUS, 0);
    }

    //获得设备id
    public String getDeviceId() {
        String DEVICE_ID = Settings.Secure.getString(mContext.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        //这里获取的不唯一，所以去掉
        //        SharedPreferences sp = getPrefs(mContext);
        //        String deviceId = sp.getString(KEY_DEVICE_ID, null);
        return DEVICE_ID;
    }


    //存储客户端的用户凭证
    public void setToken(String token) {
        SharedPreferences sp = getPrefs();
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(KEY_TOKEN, token);
        ed.commit();
        //保存token到本地，以供retrofit使用。
        SPUtil.put(mContext, Constants.SP_TOKEN, token);
    }

    public String getToken() {
        return getPrefs().getString(KEY_TOKEN, null);
    }

    //保存用户信息
    public void setUserInfo(UserInfo info) {
        Gson gson = new Gson();
        String json = gson.toJson(info);
        SharedPreferences.Editor ed = getPrefs().edit();
        ed.putString(KEY_USER_INFO, json).commit();
        Log.d(TAG, "setUserInfo" + json);
    }

    public UserInfo getUserInfo() {
        String json = getPrefs().getString(KEY_USER_INFO, null);
        if (json == null) {
            return null;
        } else {
            Gson gson = new Gson();
            return gson.fromJson(json, UserInfo.class);
        }
    }

    /**
     * 上次月经的第一天
     * yyyy {1999..}
     * MM {1..12}
     * dd {1..31}
     */
    public void setLastMenstrualDate(int yyyy, int MM, int dd) {
        SharedPreferences.Editor ed = getPrefs().edit();
        int date = yyyy * 10000 + MM * 100 + dd; //8,14
        ed.putInt(KEY_LAST_MENSTRUAL_DATE, date).commit();//1-7
        Log.d(TAG, "setLastMentrualDate" + date);
    }

    /**
     * 上次月经的第一天
     *
     * @param date
     */
    public void setLastMenstrualDate(int date) {
        SharedPreferences.Editor ed = getPrefs().edit();
        ed.putInt(KEY_LAST_MENSTRUAL_DATE, date).commit();//1-7
        Log.d(TAG, "setLastMentrualDate" + date);
    }


    /**
     * 如果用户未设置，则返回null
     *
     * @return
     */
    public Calendar getLastMenstrualDate() {
        if (getPrefs() == null) {
            return getSetting().getLastBeginDate();
        } else {
            return getDate(KEY_LAST_MENSTRUAL_DATE);
        }
    }


    //月经周期
    public void setMentrualCicle(int days) {
        SharedPreferences.Editor ed = getPrefs().edit();
        ed.putInt(KEY_MENSTRUAL_CICLE, days).commit();
        Log.d(TAG, "setMentrualCicle " + days);
        getSetting().setMemstrualCicle(days);
    }

    //月经周期
    public int getMentrualCicle() {
        if (getPrefs() == null) {
            return getSetting().getMenstrualCicle();
        } else {
            return getPrefs().getInt(KEY_MENSTRUAL_CICLE, 0);
        }
    }

    //一般月经来几天
    public void setMenstrualTime(int days) {
        SharedPreferences.Editor ed = getPrefs().edit();
        ed.putInt(KEY_MENSTRUAL_TIME, days).commit();
        Log.d(TAG, "setMentrualTime " + days);
    }

    //@return days int
    public int getMenstrualTime() {
        if (getPrefs() == null) {
            return getSetting().getMenstualTime();
        } else {
            return getPrefs().getInt(KEY_MENSTRUAL_TIME, 0);
        }
    }

    //设置生日
    public void setBirthday(int yyyy, int MM, int dd) {
        int yyyyMMdd = yyyy * 10000 + MM * 100 + dd;
        getPrefs().edit().putInt(KEY_BIRTHDAY, yyyyMMdd).commit();
    }

    public Calendar getBirthday() {
        return getDate(KEY_BIRTHDAY);
    }

    public void setHeight(float height) {
        getPrefs().edit().putFloat(KEY_HEIGHT, height).commit();
    }

    //未设置则返回0
    public float getHeight() {
        return getPrefs().getFloat(KEY_HEIGHT, 0);
    }

    //将用户体重保存
    public void setWeight(float weight) {
        getPrefs().edit().putFloat(KEY_WEIGHT, weight).commit();
        Calendar today = getToday();
        ContentValues values = new ContentValues();
        values.put("weight", String.valueOf(weight));
        updateBasic(mSdf.format(today.getTime()), values);
    }

    public String getTodayStr() {
        return mSdf.format(getToday().getTime());
    }

    public float getWeight() {
        return getPrefs().getFloat(KEY_WEIGHT, 0);
    }

    //预计下一个来月经的第一天
    public Calendar findNextMenstrual() {
        Calendar cal = getLastMenstrualDate();
        if (cal == null) {
            return null;
        }
        Calendar today = getToday();
        while (cmpDate(today, cal) > 0) {
            cal.add(Calendar.DATE, getMentrualCicle());
        }
        return cal;
    }

    public Calendar getLastMemstrualDate() {
        return getDate(KEY_LAST_MENSTRUAL_DATE);
    }

    //距离下次大姨妈还有几天
    public long findNextMemstrualCount() {
        return find();
//        Calendar cal = findNextMenstrual();
//        if (cal == null) {
//            return -1;
//        }
//        return (long) (Math.ceil((cal.getTimeInMillis() - getToday().getTimeInMillis()) / 86400000.0));
    }

    //返回下次月经期的天数
    public long find() {
        List<AppDay> appdays = AppLogic.getInstance().getList(0, 30);
        for (int i = 0; i < appdays.size(); i++) {
            if (appdays.get(i).getDayType() == AppDay.DAY_TYPE_MENSTRUAL) {
                return i;
            }
        }
        return 1;
    }

    // < 0 , c1 < c2;
    // > 0 , c1 > c2;
    // = 0 , c1 = c2
    public int cmpDate(Calendar c1, Calendar c2) {
        int c1d = c1.get(Calendar.YEAR) * 10000 + c1.get(Calendar.MONTH) * 100 + c1.get(Calendar.DATE);
        int c2d = c2.get(Calendar.YEAR) * 10000 + c2.get(Calendar.MONTH) * 100 + c2.get(Calendar.DATE);
        return c1d - c2d;
    }

    private Calendar getDate(String key) {
        int yyyyMMdd = getPrefs().getInt(key, 0);
        //Log.d("TAG", "days==1 " + yyyyMMdd);
        if (yyyyMMdd == 0) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        int yyyy = yyyyMMdd / 10000;
        int MM = yyyyMMdd % 10000 / 100 - 1;
        int dd = yyyyMMdd % 100;
        //Log.d("TAG", "days==1 " + yyyy + " " + MM + " " + dd);

        calendar.set(yyyy, MM, dd, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    //创建表
    private void createTables(SQLiteDatabase db) {
        //time主键，唯一，不空
        db.execSQL("DROP TABLE IF EXISTS Basic");
        db.execSQL("CREATE TABLE Basic (time TEXT PRIMARY KEY UNIQUE NOT NULL," +
                "menstruation INTEGER," +
                "dysmenorrhea INTEGER," +
                "coming BOOLEAN," +
                "sex INTEGER," +
                "weight TEXT," +
                "running BOOLEAN," +
                "drink BOOLEAN," +
                "fruit BOOLEAN," +
                "defecation BOOLEAN," +
                "mood INTEGER)");
    }

    //time 20170717
    public boolean isExistTime(String time) {//表里是否有time 这条数据
//        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(getDBName(mContext), null);
        SQLiteDatabase db = SqliteUtil.getInstance(mContext).obtainDatabase();

        boolean result = false;
        Cursor cursor = null;
        cursor = db.rawQuery("select * from Basic where time = ?", new String[]{time});
        result = null != cursor && cursor.moveToFirst();
        if (null != cursor && !cursor.isClosed()) {
            cursor.close();
        }
//        db.close();
        return result;
    }

    /**
     * values值參考queryBasic(String time)方法
     *
     * @param time
     * @param values
     */
    public void updateBasic(String time, ContentValues values) {
//        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(getDBName(mContext), null);
        SQLiteDatabase db = SqliteUtil.getInstance(mContext).obtainDatabase();

        if (isExistTime(time)) {
            db.update("Basic", values, "time = ?", new String[]{time});
        } else {
            values.put("time", time);
            db.insert("Basic", null, values);
        }
//        db.close();
    }

    /**
     * values值參考queryBasic(String time)方法
     */
    public void clearBasic() {
//        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(getDBName(mContext), null);
        SQLiteDatabase db = SqliteUtil.getInstance(mContext).obtainDatabase();

        db.delete("Basic", null, null);
//        db.close();
    }


    /**
     * @param time 格式20170101
     */
    public Basic queryBasic(String time) {
//        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(getDBName(mContext), null);
        SQLiteDatabase db = SqliteUtil.getInstance(mContext).obtainDatabase();

        Basic basic = queryBasic(time, db);
//        db.close();
        return basic;
    }

    public Basic queryBasic(AppDay appDay) {
        return queryBasic(mSdf.format(appDay.getDay().getTime()));
    }

    private Basic queryBasic(String time, SQLiteDatabase db) {
        Cursor cursor = db.query("Basic", null, "time = ?", new String[]{time}, null, null, null);
        Basic basic = new Basic();
        if (cursor.moveToFirst()) {
            do {
                basic = fromDB(cursor);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return basic;
    }

    private Basic fromDB(Cursor cursor) {

        Basic basic = new Basic();

        int menstruation = cursor.getInt(cursor.getColumnIndex("menstruation"));//123,对应3个等级月经量
        int dysmenorrhea = cursor.getInt(cursor.getColumnIndex("dysmenorrhea"));//123,对应3个等级经痛
        boolean coming = cursor.getInt(cursor.getColumnIndex("coming")) > 0;//1true0false来月经了
        int sex = cursor.getInt(cursor.getColumnIndex("sex"));//1234,对应3个等级爱爱等级
        String weight = cursor.getString(cursor.getColumnIndex("weight"));
        boolean running = cursor.getInt(cursor.getColumnIndex("running")) > 0;//1true0false跑步
        boolean drink = cursor.getInt(cursor.getColumnIndex("drink")) > 0;//1true0false喝水
        boolean fruit = cursor.getInt(cursor.getColumnIndex("fruit")) > 0;//1true0false水果
        boolean defecation = cursor.getInt(cursor.getColumnIndex("defecation")) > 0;//1true0false排便
        int mood = cursor.getInt(cursor.getColumnIndex("mood"));//12345,对应3等级,心情

        basic.setMenstruation(menstruation);
        basic.setDysmenorrhea(dysmenorrhea);
        basic.setComing(coming);
        basic.setSex(sex);
        basic.setWeight(weight);
        basic.setRunning(running);
        basic.setDrink(drink);
        basic.setFruit(fruit);
        basic.setDefecation(defecation);
        basic.setMood(mood);

        basic.setTime(cursor.getString(cursor.getColumnIndex("time")));

        return basic;
    }

    public List<AppDay> getWeightLog() {
        Calendar end = getToday();
        end.add(Calendar.DATE, 3);

        Calendar start = getToday();
        start.add(Calendar.DATE, -9);
//        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(getDBName(mContext), null);
        SQLiteDatabase db = SqliteUtil.getInstance(mContext).obtainDatabase();

        String start_date = mSdf.format(start.getTime());
        String end_date = mSdf.format(end.getTime());

        String sql = "SELECT * FROM Basic WHERE time >= ? and time <= ?";
        String[] args = {start_date, end_date};

        Cursor cursor = db.rawQuery(sql, args);

        Map<String, Float> weights = new HashMap<>();
        if (cursor.moveToFirst()) {
            do {
                String key = cursor.getString(cursor.getColumnIndex("time"));
                String val = cursor.getString(cursor.getColumnIndex("weight"));
                if (TextUtils.isEmpty(val)) {
                    weights.put(key, 0f);
                } else {
                    weights.put(key, Float.valueOf(val));
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        List<AppDay> appDays = new ArrayList<>(10);
        Calendar today = getToday();
        for (; cmpDate(start, end) < 1; start.add(Calendar.DATE, 1)) {
            Calendar cal = (Calendar) start.clone();
            AppDay ad = new AppDay(today, cal, AppDay.DAY_TYPE_NORMAL);
            String key = mSdf.format(start.getTime());
            if (weights.containsKey(key)) {
                ad.setWeight(weights.get(key).floatValue());
            }
            appDays.add(ad);
        }
//        db.close();
        return appDays;
    }

    private String getDBName(Context context) {
        String path = context.getFilesDir().getAbsolutePath().replace("files", "databases") + File.separator + context.getResources().getString(R.string.db_name) + ".db";
        return path;
    }


    public AppDay getAppDay(Calendar day, MenstrualDB.Result result) {
        Calendar today = getToday();
        AppDay appDay = new AppDay(today, day, AppDay.DAY_TYPE_UNKNOWN);
        Calendar lastBeginDate = getLastMenstrualDate();
        long regularPeriod = getMentrualCicle();
        if (result.getStatus() == MenstrualDB.STATUS_UNKNOWN) {
            //用户数据并没有记录
            appDay = getAppDay(day);
            appDay.setPredicted(true);
        } else if (result.getStatus() == MenstrualDB.STATUS_BEFORE_MEMSTRUL) {
            appDay = getAppDay(day,
                    DateUtil.int2date(Integer.parseInt(result.getBasic().getTime())),
                    regularPeriod);
            appDay.setPredicted(true);
        } else if (result.getStatus() == MenstrualDB.STATUS_IN_MEMSTRUAL) {
            appDay.setPredicted(false);
            appDay.setDayType(AppDay.DAY_TYPE_MENSTRUAL);
            long delta_secs = day.getTimeInMillis() / 1000 -
                    DateUtil.int2date(Integer.parseInt(result.getBasic().getTime())).getTimeInMillis() / 1000;
            long days = (delta_secs) / 86400L + 1;
            //if (days == 0) {//如果突然点coming，从数据库缺结果，有可能为0
            //    days = 1;
            //}
            appDay.setDayCount((int) days);
        } else {//这一天的时间大于
            //用户数据的
            Calendar lastMark = DateUtil.int2date(Integer.parseInt(result.getBasic().getTime()));//数据库大姨妈开始的时间
            appDay = getAppDay(day, lastMark, regularPeriod);
            appDay.setPredicted(true);
        }

        return appDay;
    }

    private AppDay getAppDay(Calendar day, Calendar lastBeginDate, long regularPeriod) {

        AppDay appDay = new AppDay(Calendar.getInstance(), day, AppDay.DAY_TYPE_NORMAL);

        MemstrualSettings setting = getSetting();
        if (lastBeginDate == null || regularPeriod == 0) {
            appDay.setDayType(AppDay.DAY_TYPE_UNKNOWN);
            return appDay;
        } else {
            //传入参数
            setting.setMemstrualCicle((int) regularPeriod);
            setting.setLastBeginDate(lastBeginDate);
        }

        int memtualDays = getMenstrualTime();
        if (memtualDays != 0) {
            //传入参数
            setting.setmMenstualDays(memtualDays);
        }

        Calendar lastBeginDate_minus_cicle = Calendar.getInstance();
        lastBeginDate_minus_cicle.setTime(lastBeginDate.getTime());
        lastBeginDate_minus_cicle.add(Calendar.DATE, (int) (memtualDays - regularPeriod));//7 -28

        if (lastBeginDate_minus_cicle.after(day)) {
            return appDay;
        }

        //这是新周期的第几天
        long delta_secs = (day.getTimeInMillis() - lastBeginDate.getTimeInMillis()) / 1000;
        long period_secs = 86400L * regularPeriod;
        while (delta_secs <= 0) {
            delta_secs += period_secs;
        }
        long days = delta_secs / 86400L % regularPeriod;

        if (days < setting.getPreSecurityDaysStart()) {//月经期
            appDay.setDayType(AppDay.DAY_TYPE_MENSTRUAL);
            appDay.setDayCount((int) (days + 1));
        } else if (days < setting.getOvulationDaysStart()) {//安全期
            appDay.setDayType(AppDay.DAY_TYPE_SECURITY);
            appDay.setDayCount((int) (days - setting.getPreSecurityDaysStart()) + 1);
        } else if (days == setting.getOvulationDay()) {
            appDay.setDayType(AppDay.DAY_TYPE_OVULATION_DAY);//排卵日
            appDay.setDayCount(1);
        } else if (days <= setting.getOvulationDaysEnd()) {
            appDay.setDayType(AppDay.DAY_TYPE_OVULATION);//排卵期
            appDay.setDayCount((int) (days - setting.getOvulationDaysStart() + 1));
        } else {
            appDay.setDayType(AppDay.DAY_TYPE_SECURITY);//安全期
            appDay.setDayCount((int) (days - setting.getOvulationDaysEnd()));
        }
        return appDay;
    }

    //日期类型
    public AppDay getAppDay(Calendar day) {
        Calendar lastBeginDate = getLastMenstrualDate();
        long regularPeriod = getMentrualCicle();
        return getAppDay(day, lastBeginDate, regularPeriod);
    }

    //获得共享配置
    public SharedPreferences getPrefs(Context context) {
        if (context != null) {
            SharedPreferences sharedPref = context.getSharedPreferences(
                    context.getString(R.string.preference_key), Context.MODE_PRIVATE);
            return sharedPref;
        } else {
            return null;
        }

    }

    public SharedPreferences getPrefs() {
        return getPrefs(mContext);
    }


    private Calendar getMonthFirstDay(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public List<Basic> selectAllBasic() {
//        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(getDBName(mContext), null);
        SQLiteDatabase db = SqliteUtil.getInstance(mContext).obtainDatabase();

        List<Basic> basicList = new ArrayList<>();
        Cursor cursor = null;
        cursor = db.rawQuery("select * from Basic order by time ASC", new String[0]);
        if (cursor.moveToFirst()) {
            do {
                Basic basic = fromDB(cursor);
                basicList.add(basic);
            } while (cursor.moveToNext());
        }
        cursor.close();
//        db.close();
        return basicList;
    }


    public List<AppDay> getMonth(int year, int month) {

        int weeks = 6;//显示五周
        int maxDays = weeks * 7;
        List<AppDay> ret = new ArrayList<AppDay>(maxDays);

        Calendar today = getToday();
        Calendar monthFirstDay = getMonthFirstDay(year, month);
        MemstrualSettings settings = getSetting();

        MenstrualDB menstrualDB = new MenstrualDB();
        menstrualDB.init(selectAllBasic());

        int firstDayOfWeek = settings.getFirstDayOfWeek();

        //之前的日期
        List<AppDay> prevDays = new ArrayList<>(7);

        //noinspection WrongConstant
        if (monthFirstDay.get(Calendar.DAY_OF_WEEK) != firstDayOfWeek) {
            for (int i = 1; i < 7; ++i) {
                Calendar c = Calendar.getInstance();
                c.set(year, month, 1, 0, 0, 0);
                c.set(Calendar.MILLISECOND, 0);
                c.add(Calendar.DATE, -i);

                AppDay appDay = getAppDay(c, menstrualDB.determine(DateUtil.date2int(c)));
                prevDays.add(appDay);

                int ym1 = c.get(Calendar.YEAR) * 100 + c.get(Calendar.MONTH);
                int ym2 = monthFirstDay.get(Calendar.YEAR) * 100 + monthFirstDay.get(Calendar.MONTH);
                //noinspection WrongConstant
                if (c.get(Calendar.DAY_OF_WEEK) == firstDayOfWeek && ym1 < ym2) {
                    break;
                }
            }
        }


        //之后的日期
        List<AppDay> nextDays = new ArrayList<>(maxDays);
        for (int i = 0; i < maxDays; ++i) {
            Calendar c = Calendar.getInstance();
            c.set(year, month, 1, 0, 0, 0);
            c.add(Calendar.DATE, i);

            AppDay appDay = getAppDay(c, menstrualDB.determine(DateUtil.date2int(c)));
            int ym1 = c.get(Calendar.YEAR) * 100 + c.get(Calendar.MONTH);
            int ym2 = monthFirstDay.get(Calendar.YEAR) * 100 + monthFirstDay.get(Calendar.MONTH);

            //noinspection WrongConstant
            if (c.get(Calendar.DAY_OF_WEEK) == firstDayOfWeek && ym1 > ym2) {
                break;
            }
            nextDays.add(appDay);
        }

        for (int i = prevDays.size() - 1; i >= 0; --i) {
            prevDays.get(i).setCurrentMonth(month);
            ret.add(prevDays.get(i));
        }
        for (int i = 0; i < nextDays.size(); ++i) {
            nextDays.get(i).setCurrentMonth(month);
            ret.add(nextDays.get(i));
        }

        return ret;
    }

    //今天的appday
    public AppDay getTodayAppDay() {
        MenstrualDB menstrualDB = new MenstrualDB();
        menstrualDB.init(selectAllBasic());

        Calendar today = getToday();
        return getAppDay(today, menstrualDB.determine(DateUtil.date2int(today)));
    }

    //返回首页的信息
    public List<AppDay> getList(int prevCount, int afterCount) {
        List<AppDay> days = new ArrayList<>(prevCount + afterCount + 1);
        MenstrualDB menstrualDB = new MenstrualDB();
        menstrualDB.init(selectAllBasic());

        for (int i = -prevCount; i <= afterCount; ++i) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            cal.add(Calendar.DATE, i);
            AppDay appDay = getAppDay(cal, menstrualDB.determine(DateUtil.date2int(cal)));
            days.add(appDay);
        }

        //安全期和排卵期都会错乱
        int securityCount = 1;
        for (int i = 0; i < days.size(); i++) {
            if (days.get(i).getDayType() != AppDay.DAY_TYPE_SECURITY) {//今天不是安全期
                securityCount = 1;
            } else if (days.get(i).getDayType() == AppDay.DAY_TYPE_SECURITY) {//今天是安全期
                days.get(i).setDayCount(securityCount);
                securityCount += 1;
            }
        }
        for (int i = 0; i < days.size(); i++) {
            if (days.get(i).getDayType() != AppDay.DAY_TYPE_OVULATION) {//今天不是排卵期
                if (days.get(i).getDayType() != AppDay.DAY_TYPE_OVULATION_DAY) {//今天不是不是排卵日
                    securityCount = 1;
                } else {
                    securityCount += 1;
                }
            } else if (days.get(i).getDayType() == AppDay.DAY_TYPE_OVULATION) {//今天是排卵期
                days.get(i).setDayCount(securityCount);
                securityCount += 1;
            }
        }

        return days;
    }

    protected Calendar getToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    //全局设置
    //计算用的关键参数
    static public class MemstrualSettings {

        static MemstrualSettings mInstance = null;

        private int mRegularPeriod; //月经周期
        private Calendar mLastBeginDate; //上次来潮首日
        private int mMenstualDays; //月经天数

        private static final String TAG = "Settings";

        /**
         * 排卵期  getmOvulationDayBefore + 1 + mOvulationDayAfter,
         * 一个周期: 月经期 + 安全期1 + 排卵期 + 安全期2
         **/
        private int mOvulationDay = 13; //排卵日, 月经来的前多少天
        private int mOvulationDayBefore = 6; //排卵期
        private int mOvulationDayAfter = 4; //排卵期


        public MemstrualSettings() {
            mRegularPeriod = 28;
            mLastBeginDate = Calendar.getInstance();
            mLastBeginDate.add(Calendar.DATE, -10);
            mMenstualDays = 6;
        }

        public void setLastBeginDate(Calendar day) {
            //Log.d(TAG, String.format("上次来的第一天 %d - %d - %d",
            //        day.get(Calendar.YEAR), day.get(Calendar.MONTH), day.get(Calendar.DATE)));
            mLastBeginDate = day;
        }

        public void setMemstrualCicle(int days) {
            //Log.d(TAG, String.format("月经周期: %d", days));
            mRegularPeriod = days;
        }

        public void setmMenstualDays(int days) {
            //Log.d(TAG, String.format("月经天数: %d", days));
            mMenstualDays = days;
        }

        public int getMenstrualCicle() {
            return mRegularPeriod;
        }

        public int getMenstualTime() {
            return mMenstualDays;
        }

        public Calendar getLastBeginDate() {
            return mLastBeginDate;
        }

        //返回安全期1,开始天数
        public int getPreSecurityDaysStart() {
            return mMenstualDays;
        }

        //排卵期开始
        public int getOvulationDaysStart() {
            return mRegularPeriod - mOvulationDay - mOvulationDayBefore;
            //27-14-6=7
            //28-14-6=8
        }

        //排卵日
        public int getOvulationDay() {
            return mRegularPeriod - mOvulationDay - 1;
        }

        //排卵期结束
        public int getOvulationDaysEnd() {
            return mRegularPeriod - mOvulationDay - 1 + mOvulationDayAfter;
        }

        //一周的开始
        //@return Calendar SUNDAY
        public int getFirstDayOfWeek() {
            return Calendar.SUNDAY;
        }
    }

    private final static String KEY_REMINDER_BEGINNING = "isBeginning";//提前2天提醒来
    private final static String KEY_REMINDER_END = "isEnd";//提取2天提醒走
    private final static String KEY_REMINDER_MEDICATION = "isMedication";//提醒吃药
    private final static String KEY_REMINDER_DRINK = "isDrink";//喝水

    public void setReminderBEGINNING(boolean isBeginning) {
        getPrefs().edit().putBoolean(KEY_REMINDER_BEGINNING, isBeginning).commit();
    }

    public void setReminderEND(boolean isEnd) {
        getPrefs().edit().putBoolean(KEY_REMINDER_END, isEnd).commit();
    }

    public void setReminderMEDICATION(boolean isMedication) {
        getPrefs().edit().putBoolean(KEY_REMINDER_MEDICATION, isMedication).commit();
    }

    public void setReminderDRINK(boolean isDrink) {
        getPrefs().edit().putBoolean(KEY_REMINDER_DRINK, isDrink).commit();
    }

    public boolean getReminderBEGINNING() {
        return getPrefs().getBoolean(KEY_REMINDER_BEGINNING, true);
    }

    public boolean getReminderEND() {
        return getPrefs().getBoolean(KEY_REMINDER_END, true);
    }

    public boolean getReminderMEDICATION() {
        return getPrefs().getBoolean(KEY_REMINDER_MEDICATION, true);
    }

    public boolean getReminderDRINK() {
        return getPrefs().getBoolean(KEY_REMINDER_DRINK, true);
    }

    private int FeedState;//管理个人收藏和个人评论页面的跳转

    public int getFeedState() {
        return FeedState;
    }

    public void setFeedState(int feedState) {
        FeedState = feedState;
    }
}

/**
 * 用户的数据库
 */
class MenstrualDB {


    public static final int STATUS_UNKNOWN = -1; //未定义的一天
    public static final int STATUS_IN_MEMSTRUAL = 1; //这天来了大姨妈
    public static final int STATUS_BEFORE_MEMSTRUL = 2; //这天是大姨妈来的前几天
    public static final int STATUS_AFTER_MEMSTRUAL = 3; //这是大姨妈之后


    private List<Basic> mBasicList;
    private List<MemstrualSpan> mRegion;

    public MenstrualDB() {
        mBasicList = null;
        mRegion = new ArrayList<>();
    }

    //默认 list 是按照时间升序排列的
    //把连续的月经期放入到mRegion中
    public void init(List<Basic> list) {

        mBasicList = list;
        MemstrualSpan memstrualSpan = new MemstrualSpan();
        for (int i = 0; i < list.size(); ++i) {
            Basic basic = list.get(i);
            if (basic.getMenstruation() > 0 || basic.isComing()) {
                if (memstrualSpan.start == null) {
                    memstrualSpan.start = basic;
                } else {
                    int d0 = 0;
                    if (memstrualSpan.end == null) {
                        d0 = Integer.parseInt(memstrualSpan.start.getTime());
                    } else {
                        d0 = Integer.parseInt(memstrualSpan.end.getTime());
                    }
                    int d1 = Integer.parseInt(basic.getTime());
                    //
                    if (isAdj(d0, d1)) {//两天相邻
                        memstrualSpan.end = basic;
                    } else {
                        if (memstrualSpan.end == null) {
                            memstrualSpan.end = memstrualSpan.start;
                        }
                        mRegion.add(memstrualSpan);
                        memstrualSpan = new MemstrualSpan();
                        memstrualSpan.start = basic;
                    }
                }
            }
        }
        if (memstrualSpan.end == null) {
            memstrualSpan.end = memstrualSpan.start;
        }
        if (memstrualSpan.start != null) {
            mRegion.add(memstrualSpan);
        }
    }

    private class MemstrualSpan {

        private Basic start;
        private Basic end;

        public MemstrualSpan() {
            start = null;
            end = null;
        }
    }


    //是不是临近的两天
    public boolean isAdj(int d1, int d2) {
        Calendar c1 = DateUtil.int2date(d1);
        Calendar c2 = DateUtil.int2date(d2);

        long span = Math.abs(c1.getTimeInMillis() - c2.getTimeInMillis());
        long max = 86400000 * 2;
        return span < max;

    }

    /**
     * 找到最近的相关的月经期
     *
     * @param yyyyMMdd 指定日期
     * @return
     */
    public Result determine(int yyyyMMdd) {

        Result r = new Result();
        r.setStatus(STATUS_UNKNOWN);
        for (int i = 0; i < mRegion.size(); ++i) {
            MemstrualSpan span = mRegion.get(i);
            int start = Integer.parseInt(span.start.getTime());
            int end = Integer.parseInt(span.end.getTime());
            if (yyyyMMdd < start) {
                r.setStatus(STATUS_BEFORE_MEMSTRUL);
                r.setBasic(span.start);
                r.setLastBasic(span.end);
                return r;
            } else if (yyyyMMdd <= end) {
                r.setStatus(STATUS_IN_MEMSTRUAL);
                r.setBasic(span.start);
                r.setLastBasic(span.end);
                return r;
            } else {
                r.setStatus(STATUS_AFTER_MEMSTRUAL);
                r.setBasic(span.start);
                r.setLastBasic(span.end);
            }
        }
        return r;
    }

    //大姨妈周期
    public static class Result {

        private int status;
        //大姨妈开始的日期
        private Basic basic;
        //大姨妈结束的日期
        private Basic lastBasic;

        protected Result() {
            basic = null;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public Basic getBasic() {
            return basic;
        }

        public void setBasic(Basic basic) {
            this.basic = basic;
        }

        public void setLastBasic(Basic lastBasic) {
            this.lastBasic = lastBasic;
        }

        public Basic getLastBasic() {
            return lastBasic;
        }
    }

}