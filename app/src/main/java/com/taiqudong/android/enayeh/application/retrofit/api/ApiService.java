package com.taiqudong.android.enayeh.application.retrofit.api;

import com.taiqudong.android.enayeh.application.retrofit.bean.AdConfig;
import com.taiqudong.android.enayeh.application.retrofit.bean.ArticleInfo;
import com.taiqudong.android.enayeh.application.retrofit.bean.Basic;
import com.taiqudong.android.enayeh.application.retrofit.bean.Comments;
import com.taiqudong.android.enayeh.application.retrofit.bean.ContentList;
import com.taiqudong.android.enayeh.application.retrofit.bean.MenstrualLog;
import com.taiqudong.android.enayeh.application.retrofit.bean.MenstrualLogs;
import com.taiqudong.android.enayeh.application.retrofit.bean.MenstrualUser;
import com.taiqudong.android.enayeh.application.retrofit.bean.MenstrualUserInfo;
import com.taiqudong.android.enayeh.application.retrofit.bean.RemoveMark;
import com.taiqudong.android.enayeh.application.retrofit.bean.Tab;
import com.taiqudong.android.enayeh.application.retrofit.bean.VideoInfo;

import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by tangxianming on 2017/8/24.
 */

public interface ApiService {
    /**
     * 文章录入
     *
     * @return
     */
    @POST("api/article/add.action")
    Call<ResponseBody> articleAdd();

    /**
     * 文章列表
     *
     * @return
     */
    @GET("api/article/index_list.action")
    Call<ResponseBody> articleIndexList();

    /**
     * 文章列表
     *
     * @return
     */
    @GET("article/get.html")
    Call<ResponseBody> articleGet();

    /**
     * tab配置，各种配置， **后台切前台 app重启的时候抓取
     *
     * @return
     */
    @GET("api/app/cfg.action")
    Call<Tab> AppCfg();

    /**
     * 月经及其他用户数据记录上传
     *
     * @return
     */
    @POST("api/menstrual/log.action")
    Call<ResponseBody> menstrualLog(@Body List<MenstrualLog> rowsBean);


    /**
     * 月经及其他用户数据记录获取
     *
     * @return
     */
    @GET("api/menstrual/logs.action")
    Call<MenstrualLogs> menstrualLogs();

    /**
     * 用户行为数据上传
     *
     * @return
     */
    @POST("api/menstrual/userinfo.action")
    Call<ResponseBody> menstrualUserinfo(@Body MenstrualUserInfo menstrualUserInfo);


    /**
     * 用户行为数据获取
     *
     * @return
     */
    @GET("api/menstrual/userinfo.action")
    Call<MenstrualUserInfo> menstrualUserinfos();


    /**
     * 月经及其他用户数据记录
     *
     * @return
     */
    @POST("api/menstrual/userinfo.action")
    Call<ResponseBody> menstrualUserInfo(@Body MenstrualUser menstrualUserInfo);

    /**
     * 获得数据列表
     *
     * @return
     */
    @GET("api/content/list.action")
    Call<ContentList> contentList(@Query("ctg") String ctg, @Query("nextToken") String nextToken, @Query("action") String action);

    /**
     * 评论内容
     *
     * @return
     */
    @POST("api/content/comment.action")
    Call<Basic> comment(@Body HashMap<String, String> data);

    /**
     * 获得评论列表
     *
     * @return
     */
    @GET("api/content/comments.action")
    Call<Comments> comments(@Query("id") String id, @Query("type") String type, @Query("nextToken") String nextToken);

    /**
     * 不感兴趣
     *
     * @return
     */
    @POST("api/content/not_interested.action")
    Call<Basic> notInterested(@Body HashMap<String, String> data);

    /**
     * 收藏
     *
     * @return
     */
    @POST("api/content/mark.action")
    Call<Basic> mark(@Body HashMap<String, String> data);

    /**
     * 评论过的文章
     *
     * @return
     */
    @GET("api/content/comment_list.action")
    Call<ContentList> commentList(@Query("nextToken") String nextToken);

    /**
     * 举报
     *
     * @return
     */
    @POST("api/content/report.action")
    Call<Basic> report(@Body HashMap<String, String> data);

    /**
     * 移除收藏
     *
     * @return
     */
    @POST("api/content/remove_mark.action")
    Call<Basic> removeMark(@Body RemoveMark data);

    /**
     * 查看收藏的列表
     *
     * @return
     */
    @GET("api/content/mark_list.action")
    Call<ContentList> markList(@Query("nextToken") String nextToken, @Query("action") String action);

    /**
     * 获得视频详情
     *
     * @return
     */
    @GET("api/content/video_info.action")
    Call<VideoInfo> videoInfo(@Query("id") String id);

    /**
     * 获得文章详情
     *
     * @return
     */
    @GET("api/content/article_info.action")
    Call<ArticleInfo> articleInfo(@Query("id") String id);

    /**
     * 喜欢评论
     *
     * @return
     */
    @POST("api/content/comment/like.action")
    Call<Basic> likeComment(@Body HashMap<String, String> data);


    /**
     * 不喜欢评论
     *
     * @return
     */
    @POST("api/content/comment/dislike.action")
    Call<Basic> disLikeComment(@Body HashMap<String, String> data);


    /**
     * 喜欢视频
     *
     * @return
     */
    @POST("api/content/video/like.action")
    Call<Basic> likeVideo(@Body HashMap<String, String> data);


    /**
     * 不喜欢视频
     *
     * @return
     */
    @POST("api/content/video/dislike.action")
    Call<Basic> disLikeVideo(@Body HashMap<String, String> data);

    @Headers("app-num: 2")
    @GET("api/adcfg.action")
    Call<AdConfig> adCfg();
}
