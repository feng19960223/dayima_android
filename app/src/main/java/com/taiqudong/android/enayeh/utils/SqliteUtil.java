package com.taiqudong.android.enayeh.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.taiqudong.android.ad.AdConfig;
import com.taiqudong.android.enayeh.bean.Feed;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangxianming on 2017/9/10.
 */

public class SqliteUtil {
    private static final String TAG = "SqliteUtil";
    private static SysDatabaseHelper dbHelper;
    private Context mContext;
    private static final String INSERT_TO_FEED = "insert into feed(" +
            "id," +
            "feedType," +
            "totalType," +
            "type," +
            "title," +
            "onePicUrl," +
            "threePicUrl1," +
            "threePicUrl2," +
            "threePicUrl3," +
            "videoUrl," +
            "videoListUrl," +
            "content," +
            "likeCount," +
            "commitCount," +
            "unLikeCount," +
            "lookCount," +
            "duration," +
            "dateTime," +
            "author," +
            "canLoadVideo," +//boolean 0|1
            "coverUrl," +
            "ctg," +
            "url," +
            "shareUrl," +
            "save," +//boolean 0|1
            "intro," +
            "indexx," +
            "adContent," +//广告内容的json
            "adConfig," +//广告配置的json
            "nextToken)" +
            "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private final static String SELECT_FEED = "select * from feed where totalType=?";
    private final static String DELETE_FEED = "delete from feed where totalType=?";
    private final static String SELECT_FEED_DETAIL = "select nextToken from feed where totalType=?";

    private SqliteUtil(Context context) {
        if (dbHelper == null) {
            dbHelper = new SysDatabaseHelper(context, "dayima.db", null, 2);
        }
        mContext = context;
    }

    public static SqliteUtil getInstance(Context context) {
        return new SqliteUtil(context);
    }

    /**
     * 获取数据操作对象
     *
     * @return
     */
    public SQLiteDatabase obtainDatabase() {
        return dbHelper.getWritableDatabase();
    }


    /**
     * 插入feed流的数据
     *
     * @param feeds
     */
    public void insertFeedBatch(List<Feed> feeds) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Gson gson = new Gson();
        for (Feed feed : feeds) {
            String adConfigJson = gson.toJson(feed.getAdConfig());
            db.execSQL(INSERT_TO_FEED, new String[]{feed.getId(), feed.getFeedType(), feed.getTotalType(), String.valueOf(feed.getType()), feed.getTitle(),
                    feed.getOnePicUrl(), feed.getThreePicUrl1(), feed.getThreePicUrl2(), feed.getThreePicUrl3(), feed.getVideoUrl(), feed.getVideoListUrl(), feed.getContent(),
                    String.valueOf(feed.getLikeCount()), String.valueOf(feed.getCommitCount()), String.valueOf(feed.getUnLikeCount()), String.valueOf(feed.getLookCount()), feed.getDuration(), feed.getDateTime(),
                    feed.getAuthor(), String.valueOf(feed.getCanLoadVideo() ? 1 : 0), feed.getCoverUrl(), feed.getCtg(), feed.getUrl(), feed.getShareUrl(), String.valueOf(feed.getSave() ? 1 : 0),
                    feed.getIntro(), String.valueOf(feed.getIndex()), null, adConfigJson, feed.getNextToken()});
            Log.d(TAG, "feed inserted successful:" + feed.getId());
            android.util.Log.i(TAG, "insertFeedBatch: " + feed.getShareUrl());
        }
    }

    /**
     * 批量删除数据
     *
     * @param tip
     */
    public void deleteFeedBatch(String tip) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(DELETE_FEED, new String[]{tip});
    }

    /**
     * @param tip
     * @return
     */
    public String getNextToken(String tip) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String nextToken = null;
        Cursor cursor = db.rawQuery(SELECT_FEED_DETAIL, new String[]{tip});
        if (cursor.moveToFirst()) {
            nextToken = cursor.getString(cursor.getColumnIndex("nextToken"));
        }
        cursor.close();
        android.util.Log.d(TAG, "nextToken in sqlite is:" + nextToken);
        return nextToken;
    }


    /**
     * 获取所有满足条件的数据
     *
     * @param tid feed所属类型
     */
    public List<Feed> obtainFeedAll(String tid) {
        List<Feed> feeds = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(SELECT_FEED, new String[]{tid});
        if (cursor.moveToFirst()) {
            Gson gson = new Gson();
            do {
                //新建数据
                Feed feed = new Feed();
                feed.setId(cursor.getString(cursor.getColumnIndex("id")));
                feed.setIndex(Integer.valueOf(cursor.getString(cursor.getColumnIndex("indexx"))));
                //转换json to bean.
                AdConfig adConfig = gson.fromJson(cursor.getString(cursor.getColumnIndex("adConfig")), AdConfig.class);
                feed.setAdConfig(adConfig);
                feed.setAdContent(null);//缓存中不保存广告
                feed.setAuthor(cursor.getString(cursor.getColumnIndex("author")));
                feed.setCanLoadVideo(Integer.valueOf(cursor.getString(cursor.getColumnIndex("canLoadVideo"))) == 1 ? true : false);
                feed.setCommitCount(Integer.valueOf(cursor.getString(cursor.getColumnIndex("commitCount"))));
                feed.setContent(cursor.getString(cursor.getColumnIndex("content")));
                feed.setCoverUrl(cursor.getString(cursor.getColumnIndex("coverUrl")));
                feed.setCtg(cursor.getString(cursor.getColumnIndex("ctg")));
                feed.setDateTime(cursor.getString(cursor.getColumnIndex("dateTime")));
                feed.setDuration(cursor.getString(cursor.getColumnIndex("duration")));
                feed.setFeedType(cursor.getString(cursor.getColumnIndex("feedType")));
                feed.setIntro(cursor.getString(cursor.getColumnIndex("intro")));
                feed.setNextToken(cursor.getString(cursor.getColumnIndex("nextToken")));
                feed.setLikeCount(Integer.valueOf(cursor.getString(cursor.getColumnIndex("likeCount"))));
                feed.setOnePicUrl(cursor.getString(cursor.getColumnIndex("onePicUrl")));
                feed.setThreePicUrl1(cursor.getString(cursor.getColumnIndex("threePicUrl1")));
                feed.setThreePicUrl2(cursor.getString(cursor.getColumnIndex("threePicUrl2")));
                feed.setThreePicUrl3(cursor.getString(cursor.getColumnIndex("threePicUrl3")));
                feed.setVideoUrl(cursor.getString(cursor.getColumnIndex("videoUrl")));
                feed.setUrl(cursor.getString(cursor.getColumnIndex("url")));
                feed.setVideoListUrl(cursor.getString(cursor.getColumnIndex("videoListUrl")));
                feed.setUnLikeCount(Integer.valueOf(cursor.getString(cursor.getColumnIndex("unLikeCount"))));
                feed.setType(Integer.valueOf(cursor.getString(cursor.getColumnIndex("type"))));
                feed.setTotalType(cursor.getString(cursor.getColumnIndex("totalType")));
                feed.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                feed.setSave(Integer.valueOf(cursor.getString(cursor.getColumnIndex("save"))) == 1 ? true : false);
                feed.setLookCount(Integer.valueOf(cursor.getString(cursor.getColumnIndex("lookCount"))));
                feed.setShareUrl(cursor.getString(cursor.getColumnIndex("shareUrl")));
                feeds.add(feed);
                android.util.Log.i(TAG, "obtainFeedAll: cursor" + cursor.getString(cursor.getColumnIndex("shareUrl")));
                android.util.Log.i(TAG, "obtainFeedAll: feed" + feed.getShareUrl());
            } while (cursor.moveToNext());
        }
        cursor.close();
        return feeds;
    }

    /**
     * 该类型的feed数据是否为空
     *
     * @param tid
     * @return
     */
    public boolean isEmpty(String tid) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(SELECT_FEED, new String[]{tid});
        boolean isEmpty = !cursor.moveToFirst();
        android.util.Log.d(TAG, "sqlite when tid=" + tid + "is empty:" + isEmpty);
        cursor.close();
        return isEmpty;
    }
}