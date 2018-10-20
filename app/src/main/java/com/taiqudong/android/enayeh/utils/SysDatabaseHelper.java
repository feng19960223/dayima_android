package com.taiqudong.android.enayeh.utils;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.*;

/**
 * Created by tangxianming on 2017/9/10.
 */

public class SysDatabaseHelper extends SQLiteOpenHelper {

    private Context mContext;

    private static final String TAG = "SysDatabaseHelper";

    public static final String CREATE_FEED = "create table feed (" +
            "uid integer primary key autoincrement," +
            "id text," +//id 可能会一样，所以不作为主键。
            "feedType text," +
            "totalType text," +
            "type text," +
            "title text," +
            "onePicUrl text," +
            "threePicUrl1 text," +
            "threePicUrl2 text," +
            "threePicUrl3 text," +
            "videoUrl text," +
            "videoListUrl text," +
            "content text," +
            "likeCount text," +
            "commitCount text," +
            "unLikeCount text," +
            "lookCount text," +
            "duration text," +
            "dateTime text," +
            "author text," +
            "canLoadVideo text," +//boolean 0|1
            "coverUrl text," +
            "ctg text," +
            "url text," +
            "shareUrl text," +
            "save text," + //boolean 0|1
            "intro text," +
            "indexx text," +//排序（index 在sqlite中不能使用）
            "adContent text," +//广告内容的json
            "adConfig text," +//广告配置的json
            "nextToken text)";

    //创建Basic数据作为经期数据表
    public static final String CREATE_BASIC = "CREATE TABLE Basic (time TEXT PRIMARY KEY UNIQUE NOT NULL," +
            "menstruation INTEGER," +
            "dysmenorrhea INTEGER," +
            "coming BOOLEAN," +
            "sex INTEGER," +
            "weight TEXT," +
            "running BOOLEAN," +
            "drink BOOLEAN," +
            "fruit BOOLEAN," +
            "defecation BOOLEAN," +
            "mood INTEGER)";


    private static final String DROP_FEED = "DROP TABLE IF EXISTS feed";
    private static final String DROP_BASIC = "DROP TABLE IF EXISTS Basic";

    public SysDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建feed流类型的数据表
        db.execSQL(CREATE_FEED);
        //创建经期数据库
        db.execSQL(CREATE_BASIC);
        //创建
        android.util.Log.d(TAG, "sqlite is created success!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_FEED);
        db.execSQL(DROP_BASIC);
        db.execSQL(CREATE_FEED);
        db.execSQL(CREATE_BASIC);
        android.util.Log.d(TAG, "sqlite is update success!");
    }
}
